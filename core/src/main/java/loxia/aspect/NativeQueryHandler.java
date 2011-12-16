package loxia.aspect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loxia.annotation.NativeQuery;
import loxia.annotation.NativeUpdate;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.dao.ModelClassSupport;
import loxia.dao.Pagination;
import loxia.dao.Sort;
import loxia.dao.support.BaseRowMapper;
import loxia.service.VelocityTemplateService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.type.Type;
import org.springframework.jdbc.core.RowMapper;

public class 
NativeQueryHandler extends DynamicQueryHandler {

	public NativeQueryHandler(DaoService daoService, VelocityTemplateService templateService, 
			DynamicNamedQueryProvider dnqProvider) {
		super(daoService, templateService, dnqProvider);
	}

	public Object handleNativeQuery(NativeQuery nativeQuery, ProceedingJoinPoint pjp){
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
	
	public int handleNativeUpdate(NativeUpdate nativeUpdate, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());		
		Map<String, Type> paramClazzes = getParamClazzes(ms.getMethod(), pjp.getArgs());
		
		String queryName = nativeUpdate.value();
		if(queryName.equals("")){
			if(!(pjp.getThis() instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)pjp.getThis();
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + ms.getMethod().getName();				
		}
		
		String queryStringWithName = getDynamicQuery(queryName, params);		
		List<Object> conditions = new ArrayList<Object>();
		List<Type> types = new ArrayList<Type>();
		String queryString = getNativeUpdateQuery(queryStringWithName, params, paramClazzes, 
				conditions,types);
		
		
		
		logger.debug("Update[{}] will be executed.",queryString);
		return daoService.batchUpdateByNativeQuery(queryString, conditions.toArray(), types.toArray(new Type[]{}));		
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
				if(c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == ',' || c == ')'){
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
	
	private String getNativeUpdateQuery(String queryName, Map<String, Object> params,
			Map<String,Type> typeParams, List<Object> conditions, List<Type> types) {
		StringBuffer sb = new StringBuffer();
		boolean inParamName = false;
		StringBuffer paramNameSb = new StringBuffer();
		for(char c: queryName.toCharArray()){
			if(c == ':'){
				if(inParamName) throw new RuntimeException("Wrong query.");
				inParamName = true;
			}else{
				if(c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == ',' || c == ')'){
					if(inParamName){
						inParamName = false;
						sb.append('?');
						conditions.add(params.get(paramNameSb.toString()));
						types.add(typeParams.get(paramNameSb.toString()));
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
}
