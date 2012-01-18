package loxia.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loxia.annotation.QueryParam;
import loxia.core.utils.HibernateUtil;
import loxia.dao.DaoService;
import loxia.dao.Page;
import loxia.dao.Sort;

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
					if(args[i] != null && args[i] instanceof Map)
						params.putAll((Map<String,Object>)args[i]);
					QueryParam qp = (QueryParam)paramAnnos[i][j];
					params.put(qp.value(), args[i]);
				}
			}
		}
		return params;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Type> getParamClazzes(Method m, Object[] args){
		Map<String, Type> params = new HashMap<String, Type>();
		Class<?>[] classes = m.getParameterTypes();
		Annotation[][] paramAnnos = m.getParameterAnnotations();
		for(int i=0; i < paramAnnos.length; i++){
			for(int j=0; j< paramAnnos[i].length; j++){
				if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof QueryParam){
					if(args[i] != null && args[i] instanceof Map){
						Map<String,Object> argMap = (Map<String,Object>)args[i];
						for(String key: argMap.keySet()){
							//set to string type if value is null in map params
							params.put(key, HibernateUtil.translateClass(
									argMap.get(key) == null? String.class: argMap.get(key).getClass()));
						}
					}
					QueryParam qp = (QueryParam)paramAnnos[i][j];
					params.put(qp.value(), HibernateUtil.translateClass(classes[i]));
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
}
