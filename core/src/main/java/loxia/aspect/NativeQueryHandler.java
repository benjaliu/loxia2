package loxia.aspect;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loxia.annotation.NativeQuery;
import loxia.annotation.NativeQuery.DEFAULT;
import loxia.annotation.NativeUpdate;
import loxia.core.utils.HibernateUtil;
import loxia.dao.ColumnTranslator;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.dao.ModelClassSupport;
import loxia.dao.Page;
import loxia.dao.Pagination;
import loxia.dao.Sort;
import loxia.dao.support.BaseRowMapper;
import loxia.dao.support.CommonBeanRowMapper;
import loxia.dao.support.DummyColumnTranslator;
import loxia.service.VelocityTemplateService;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.type.Type;
import org.springframework.jdbc.core.RowMapper;

public class NativeQueryHandler extends DynamicQueryHandler {

	public NativeQueryHandler(DaoService daoService, VelocityTemplateService templateService, 
			DynamicNamedQueryProvider dnqProvider) {
		super(daoService, templateService, dnqProvider);
	}
	
	public Object handleNativeQuery(NativeQuery nativeQuery, MethodInvocation invocation){
		return handleNativeQueryNative(nativeQuery, invocation.getThis(), invocation.getMethod(), invocation.getArguments());
	}

	public Object handleNativeQuery(NativeQuery nativeQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		return handleNativeQueryNative(nativeQuery, pjp.getThis(), ms.getMethod(), pjp.getArgs());
	}
	
	private Object handleNativeQueryNative(NativeQuery nativeQuery, Object obj, Method m, Object[] args){
		Map<String, Object[]> paramsEx = getParamsEx(m, args);
		Map<String,Object> templateParams = new HashMap<String, Object>();
		for(String key: paramsEx.keySet())
			templateParams.put(key, paramsEx.get(key)[0]);
		
		Page page = getPage(args);
		boolean pagable = (page != null) ||nativeQuery.pagable();		
		
		String queryName = nativeQuery.value();
		if(queryName.equals("")){
			if(!(obj instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)obj;
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + m.getName();				
		}
		
		String queryStringWithName = getDynamicQuery(queryName, templateParams);		
		List<Object> conditions = new ArrayList<Object>();
		String queryString = getNativeQuery(queryStringWithName, paramsEx, conditions);
		
		if(logger.isDebugEnabled()){
			logger.debug("NativeQuery[{}] will be executed", queryName);
			logger.debug("{}",queryString);
		}
		
		Sort[] sorts = getSorts(args);
		RowMapper<?> rowMapper = getRowMapper(args);
		if(rowMapper == null && (!nativeQuery.model().equals(DEFAULT.class))){
			ColumnTranslator t = null;
			try {
				if(!DummyColumnTranslator.class.equals(nativeQuery.translator())){
					t = nativeQuery.translator().newInstance();
					t.setModelClass(nativeQuery.model());
				}
			} catch (Exception e) {
				//do nothing
			}
			rowMapper = new CommonBeanRowMapper(nativeQuery.model(), t, nativeQuery.alias());
		}
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
		
		if(List.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
							page.getStart(), page.getSize(), rowMapper);
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
							(Integer)args[0], (Integer)args[1], rowMapper);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
						-1, -1, rowMapper);
			}
		}else if(Pagination.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
							page.getStart(), page.getSize(), nativeQuery.withGroupby(), rowMapper);
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByNativeQuery(queryString, conditions.toArray(), sorts, 
							(Integer)args[0], (Integer)args[1], nativeQuery.withGroupby(), rowMapper);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByNativeQuery(queryString, conditions.toArray(), rowMapper, sorts);
	}
	
	public int handleNativeUpdate(NativeUpdate nativeUpdate, MethodInvocation invocation){
		return handleNativeUpdateNative(nativeUpdate, invocation.getThis(), invocation.getMethod(), invocation.getArguments());
	}
	
	public int handleNativeUpdate(NativeUpdate nativeUpdate, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		return handleNativeUpdateNative(nativeUpdate, pjp.getThis(), ms.getMethod(), pjp.getArgs());
	}
	
	private int handleNativeUpdateNative(NativeUpdate nativeUpdate, Object obj, Method m, Object[] args){
		Map<String, Object[]> paramsEx = getParamsEx(m, args);	
		Map<String,Object> templateParams = new HashMap<String, Object>();
		for(String key: paramsEx.keySet())
			templateParams.put(key, paramsEx.get(key)[0]);
		
		String queryName = nativeUpdate.value();
		if(queryName.equals("")){
			if(!(obj instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)obj;
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + m.getName();				
		}
		
		String queryStringWithName = getDynamicQuery(queryName, templateParams);		
		List<Object> conditions = new ArrayList<Object>();
		List<Type> types = new ArrayList<Type>();
		String queryString = getNativeUpdateQuery(queryStringWithName, paramsEx, 
				conditions,types);
		
		logger.debug("Update[{}] will be executed.",queryString);
		return daoService.batchUpdateByNativeQuery(queryString, conditions.toArray(), types.toArray(new Class<?>[]{}));		
	}
	
	private String getNativeQuery(String queryName, Map<String, Object[]> paramsEx,
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
						conditions.add(getParamValueAndType(paramNameSb.toString(), paramsEx)[0]);
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
	
	private String getNativeUpdateQuery(String queryName, Map<String, Object[]> paramsEx, 
			List<Object> conditions, List<Type> types) {
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
						Object[] v = getParamValueAndType(paramNameSb.toString(), paramsEx);
						conditions.add(v[0]);
						types.add(HibernateUtil.translateClass((Class<?>)v[1]));
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
