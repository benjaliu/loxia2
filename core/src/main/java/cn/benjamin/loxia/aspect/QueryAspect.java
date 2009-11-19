package cn.benjamin.loxia.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import cn.benjamin.loxia.annotation.DynamicQuery;
import cn.benjamin.loxia.annotation.NamedQuery;
import cn.benjamin.loxia.annotation.NativeQuery;
import cn.benjamin.loxia.annotation.Query;
import cn.benjamin.loxia.annotation.QueryParam;
import cn.benjamin.loxia.dao.DaoService;
import cn.benjamin.loxia.dao.DynamicNamedQueryProvider;
import cn.benjamin.loxia.dao.ModelClassSupport;
import cn.benjamin.loxia.dao.Sort;
import cn.benjamin.loxia.service.VelocityTemplateService;

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
	
	private Map<String,Object> getParams(Method m, Object[] args){
		Map<String, Object> params = new HashMap<String, Object>();
		Annotation[][] paramAnnos = m.getParameterAnnotations();
		for(int i=0; i < paramAnnos.length; i++){
			for(int j=0; j< paramAnnos[i].length; j++){
				if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof QueryParam){
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

	@Around("this(cn.benjamin.loxia.dao.GenericEntityDao)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Query query = ms.getMethod().getAnnotation(Query.class);
		NamedQuery namedQuery = ms.getMethod().getAnnotation(NamedQuery.class);
		DynamicQuery dynamicQuery = ms.getMethod().getAnnotation(DynamicQuery.class);
		NativeQuery nativeQuery = ms.getMethod().getAnnotation(NativeQuery.class);
		
		boolean pagable = false;
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		if(namedQuery !=null){
			if(namedQuery.pagable()){
				pagable = true;				
			}
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
			}else
				return daoService.findOneByNamedQuery(queryName, params);
		}else if(query != null){
			if(query.pagable()){
				pagable = true;				
			}
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
			}else
				return daoService.findOneByQuery(queryString, params);
		}else if(dynamicQuery != null){
			if(dynamicQuery.pagable()){
				pagable = true;				
			}
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
			}else
				return daoService.findOneByQueryEx(queryString, params);
		}else if(nativeQuery != null){
			if(nativeQuery.pagable()){
				pagable = true;				
			}
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
			
			if(sorts != null){
				logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
			}
						
			if(List.class.isAssignableFrom(ms.getMethod().getReturnType())){
				if(pagable){
					if(pjp.getArgs()[0] instanceof Integer &&
							pjp.getArgs()[1] instanceof Integer)				
						return daoService.findByNativeQuery(queryString, conditions.toArray(), nativeQuery.sqlResultMapping(),
								sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1]);
					else
						throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
				}else{
					return daoService.findByNativeQuery(queryString, conditions.toArray(), nativeQuery.sqlResultMapping(),
							sorts, -1, -1);
				}
			}else
				return daoService.findByNativeQuery(queryString, conditions.toArray(), nativeQuery.sqlResultMapping());
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
