package loxia.aspect;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import loxia.annotation.QueryParam;
import loxia.dao.DaoService;
import loxia.dao.Page;
import loxia.dao.Sort;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQueryHandler {
	protected static final Logger logger = LoggerFactory.getLogger(QueryAspect.class);

	protected DaoService daoService;
	
	public AbstractQueryHandler(DaoService daoService){
		this.daoService = daoService;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getParams(Method m, Object[] args){
		Map<String, Object> params = new HashMap<String, Object>();
		Annotation[][] paramAnnos = m.getParameterAnnotations();
		for(int i=0; i < paramAnnos.length; i++){
			for(int j=0; j< paramAnnos[i].length; j++){
				if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof QueryParam){
					if(args[i] != null && args[i] instanceof Map){
						params.putAll((Map<String,Object>)args[i]);
					}else{
						QueryParam qp = (QueryParam)paramAnnos[i][j];
						params.put(qp.value(), args[i]);
					}
					
				}
			}
		}
		return params;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object[]> getParamsEx(Method m, Object[] args){
		Map<String, Object[]> params = new HashMap<String, Object[]>();
		Annotation[][] paramAnnos = m.getParameterAnnotations();
		Class<?>[] ptypes = m.getParameterTypes();
		for(int i=0; i < paramAnnos.length; i++){
			for(int j=0; j< paramAnnos[i].length; j++){
				if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof QueryParam){
					if(args[i] != null && args[i] instanceof Map){
						Map<String,Object> map = (Map<String,Object>)args[i];
						for(String key: map.keySet()){
							Object val = map.get(key);
							params.put(key, new Object[]{val,val == null?
									String.class : val.getClass()});
						}
					}
					QueryParam qp = (QueryParam)paramAnnos[i][j];
					params.put(qp.value(), new Object[]{args[i], ptypes[i]});
				}
			}
		}
		return params;
	}
	
	protected Page getPage(Object[] args){
		Page page = null;
		for(Object arg: args){
			if(arg instanceof Page){
				if(page == null)
					page = (Page)arg;
				else
					throw new IllegalArgumentException("More than one definitions found for Page.");
			}
		}
		return page;
	}
	
	protected Sort[] getSorts(Object[] args){
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
	
	private Object[] getParamValueInternal(String key, Object[] valObj){
		if(key == null || key.trim().length() ==0) return valObj;
		Object val = valObj[0];
		Class<?> clazz = (Class<?>)valObj[1];
		
		if(val == null){
			PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(clazz);
			Class<?> c = String.class;
			for(PropertyDescriptor p: props){
				if(p.getName().equals(key))
					c = p.getPropertyType();
			}
			return new Object[]{null, c};
		}else if(key.indexOf('.') < 0){
			try {
				return new Object[]{PropertyUtils.getProperty(val, key),
						PropertyUtils.getPropertyDescriptor(val, key).getPropertyType()};
			} catch (Exception e) {
				logger.error("Get Query Param Error: {} in {}", 
						key, val.getClass());
				throw new RuntimeException("Query Param Error");
			} 
		}else{
			int delim = key.indexOf('.');
			
			String pname = key.substring(0,delim);
			String cname = key.substring(delim+1);
			try{
				return getParamValueInternal(cname, new Object[]{PropertyUtils.getProperty(val, pname),
						PropertyUtils.getPropertyDescriptor(val, pname).getPropertyType()});
			} catch (Exception e) {
				logger.error("Get Query Param Error: {} in {}", 
						key, val.getClass());
				throw new RuntimeException("Query Param Error");
			} 			
		}
		
	}
	
	protected Object[] getParamValueAndType(String key, Map<String, Object[]> paramMap){
		int delim = key.indexOf('.');
		if(delim <0)
			return paramMap.get(key);
		
		String pname = key.substring(0,delim);
		String cname = key.substring(delim+1);
		return getParamValueInternal(cname, paramMap.get(pname));
	}
}
