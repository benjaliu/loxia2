package loxia.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loxia.annotation.DynamicQuery;
import loxia.annotation.NamedQuery;
import loxia.annotation.NativeQuery;
import loxia.annotation.Query;
import loxia.annotation.QueryParam;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.dao.ModelClassSupport;
import loxia.dao.Pagination;
import loxia.dao.Sort;
import loxia.dao.support.BaseRowMapper;
import loxia.service.VelocityTemplateService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.RowMapper;


@Aspect
public class QueryAspect implements Ordered {

	private static final Logger logger = LoggerFactory.getLogger(QueryAspect.class);

	@Autowired
	private DaoService daoService;
	
	@Autowired
	private VelocityTemplateService templateService;
	
	@Autowired
	private DynamicNamedQueryProvider dnqProvider;
	
	public int getOrder() {
		return 20;
	}
	
	private class MapRowMapper extends BaseRowMapper<Object>{
		public MapRowMapper(String[] alias, Class<?>[] clazzes){
			this.alias = alias;
			this.clazzes = clazzes;
		}
		private String[] alias;
		private Class<?>[] clazzes;

		public Object mapRow(ResultSet rs, int index) throws SQLException {
			if(alias.length == 1){
				return getResultFromRs(rs, alias[0], clazzes[0]);
			}else{
				Map<String, Object> result = new HashMap<String, Object>();
				for(int i=0; i< alias.length; i++){
					result.put(alias[i], getResultFromRs(rs, alias[i], clazzes[i]));
				}
				return result;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Object> getParams(Method m, Object[] args){
		Map<String, Object> params = new HashMap<String, Object>();
		Annotation[][] paramAnnos = m.getParameterAnnotations();
		for(int i=0; i < paramAnnos.length; i++){
			for(int j=0; j< paramAnnos[i].length; j++){
				if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof QueryParam){
					if(args[i] != null && args[i] instanceof Map)
						params.putAll((Map<String,Object>)args[i]);
					QueryParam qp = (QueryParam)paramAnnos[i][j];
					params.put(qp.value(), args[i]);
				}
			}
		}
		return params;
	}
	
	private Sort[] getSorts(Object[] args){
		Sort[] sorts = null;
		for(Object arg: args){
			if(arg instanceof Sort[]){
				if(sorts == null)
					sorts = (Sort[])arg;
				else
					throw new IllegalArgumentException("More than one definitions found for sort.");
			}
		}
		return sorts;
	}
	
	@SuppressWarnings("unchecked")
	private RowMapper<?> getRowMapper(Object[] args){
		RowMapper<?> rowMapper = null;
		for(Object arg: args){
			if(arg instanceof RowMapper){
				if(rowMapper == null)
					rowMapper = (RowMapper<?>)arg;
				else
					throw new IllegalArgumentException("More than one definitions found for RowMapper.");
			}
		}
		return rowMapper;
	}
	
	private Object handleNamedQuery(NamedQuery namedQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		boolean pagable = namedQuery.pagable();		
		String queryName = namedQuery.value();
		if(queryName.equals("")){
			if(!(pjp.getThis() instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)pjp.getThis();
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + ms.getMethod().getName();				
		}
		logger.debug("Named Query[{}] will be executed.",queryName);
					
		Sort[] sorts = getSorts(pjp.getArgs());
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByNamedQuery(queryName, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1]);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				if(sorts == null)
					return daoService.findByNamedQuery(queryName, params);
				else
					return daoService.findByNamedQuery(queryName, params, sorts);
			}
		}else if(Pagination.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByNamedQuery(queryName, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], namedQuery.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByNamedQuery(queryName, params, sorts);
	}
	
	private Object handleQuery(Query query, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		boolean pagable = query.pagable();		
		String queryString = query.value();			
		logger.debug("Query[{}] will be executed.",queryString);
		
		Sort[] sorts = getSorts(pjp.getArgs());
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByQuery(queryString, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1]);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByQuery(queryString, params, sorts);
			}
		}else if(Pagination.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByQuery(queryString, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], query.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByQuery(queryString, params, sorts);
	}
	
	private Object handleDynamicQuery(DynamicQuery dynamicQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		boolean pagable = dynamicQuery.pagable();		
		
		String queryName = dynamicQuery.value();
		if(queryName.equals("")){
			if(!(pjp.getThis() instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)pjp.getThis();
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + ms.getMethod().getName();				
		}
		
		String queryString = getDynamicQuery(queryName, params);			
		logger.debug("Query[{}] will be executed.",queryString);
		
		Sort[] sorts = getSorts(pjp.getArgs());
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByQueryEx(queryString, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1]);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByQueryEx(queryString, params, sorts, -1, -1);
			}
		}else if(Pagination.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByQueryEx(queryString, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], dynamicQuery.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByQueryEx(queryString, params, sorts);
	}
	
	private Object handleNativeQuery(NativeQuery nativeQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		boolean pagable = nativeQuery.pagable();		
		
		String queryName = nativeQuery.value();
		if(queryName.equals("")){
			if(!(pjp.getThis() instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)pjp.getThis();
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + ms.getMethod().getName();				
		}
		
		String queryStringWithName = getDynamicQuery(queryName, params);		
		List<Object> conditions = new ArrayList<Object>();
		String queryString = getNativeQuery(queryStringWithName, params, conditions);
		
		logger.debug("Query[{}] will be executed.",queryString);
		
		Sort[] sorts = getSorts(pjp.getArgs());
		RowMapper<?> rowMapper = getRowMapper(pjp.getArgs());
		if(rowMapper == null && (nativeQuery.alias() == null || nativeQuery.clazzes() == null
				|| nativeQuery.alias().length == 0
				|| nativeQuery.clazzes().length == 0))
			throw new IllegalArgumentException("No return type definition found.");
		
		if(rowMapper == null && nativeQuery.alias().length != nativeQuery.clazzes().length)
			throw new IllegalArgumentException("Return alias and class definition are not matched.");
		
		if(rowMapper == null)
			rowMapper = new MapRowMapper(nativeQuery.alias(), nativeQuery.clazzes());
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
							(Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], rowMapper);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
						-1, -1, rowMapper);
			}
		}else if(Pagination.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
							(Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], nativeQuery.withGroupby(), rowMapper);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByNativeQuery(queryString, conditions.toArray(), rowMapper, sorts);
	}

	@Around("this(loxia.dao.GenericEntityDao)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Query query = ms.getMethod().getAnnotation(Query.class);
		NamedQuery namedQuery = ms.getMethod().getAnnotation(NamedQuery.class);
		DynamicQuery dynamicQuery = ms.getMethod().getAnnotation(DynamicQuery.class);
		NativeQuery nativeQuery = ms.getMethod().getAnnotation(NativeQuery.class);
		
		if(namedQuery !=null){
			return handleNamedQuery(namedQuery, pjp);
		}else if(query != null){
			return handleQuery(query,pjp);
		}else if(dynamicQuery != null){
			return handleDynamicQuery(dynamicQuery, pjp);
		}else if(nativeQuery != null){
			return handleNativeQuery(nativeQuery, pjp);
		}else
			return pjp.proceed(pjp.getArgs());
	}

	private String getNativeQuery(String queryName, Map<String, Object> params,
			List<Object> conditions) {
		StringBuffer sb = new StringBuffer();
		boolean inParamName = false;
		StringBuffer paramNameSb = new StringBuffer();
		for(char c: queryName.toCharArray()){
			if(c == ':'){
				if(inParamName) throw new RuntimeException("Wrong query.");
				inParamName = true;
			}else{
				if(c == ' ' || c == '\t' || c == '\n' || c == '\r'){
					if(inParamName){
						inParamName = false;
						sb.append('?');
						conditions.add(params.get(paramNameSb.toString()));
						paramNameSb = new StringBuffer();
					}
					sb.append(c);
				}else{
					if(inParamName){
						paramNameSb.append(c);
					}else
						sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	private String getDynamicQuery(String queryName, Map<String, Object> params) {
		return templateService.parseVMContent(dnqProvider.getDynamicQueryByName(queryName), params);
	}
}
