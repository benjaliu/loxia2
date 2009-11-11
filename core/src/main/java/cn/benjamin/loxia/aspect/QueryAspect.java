package cn.benjamin.loxia.aspect;

import java.lang.annotation.Annotation;
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

import cn.benjamin.loxia.annotation.Query;
import cn.benjamin.loxia.annotation.QueryParam;
import cn.benjamin.loxia.dao.DaoService;
import cn.benjamin.loxia.dao.ModelClassSupport;

@Aspect
public class QueryAspect implements Ordered {

	private static final Logger logger = LoggerFactory.getLogger(QueryAspect.class);

	@Autowired
	private DaoService daoService;
	
	public int getOrder() {
		return 20;
	}

	@Around("this(cn.benjamin.loxia.dao.GenericEntityDao)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Query query = ms.getMethod().getAnnotation(Query.class);
		if(query !=null){
			String queryName = query.value();
			if(queryName.equals("")){
				if(!(pjp.getThis() instanceof ModelClassSupport)) 
					throw new RuntimeException("QueryName can not be empty");
				ModelClassSupport mcs = (ModelClassSupport)pjp.getThis();
				queryName += mcs.getModelClass().getSimpleName();
				queryName += "." + ms.getMethod().getName();				
			}
			logger.debug("Named Query[{}] will be executed.",queryName);
			Map<String, Object> params = new HashMap<String, Object>();
			Annotation[][] paramAnnos = ms.getMethod().getParameterAnnotations();
			for(int i=0; i < paramAnnos.length; i++){
				for(int j=0; j< paramAnnos[i].length; j++){
					if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof QueryParam){
						QueryParam qp = (QueryParam)paramAnnos[i][j];
						params.put(qp.value(), pjp.getArgs()[i]);
					}
				}
			}
			if(logger.isDebugEnabled()){
				if(!params.keySet().isEmpty()){
					logger.debug("Query Parameters:");
					int index = 0;
					for(String key: params.keySet()){
						logger.debug("{}) [{}]: {}", new Object[]{++index,key,params.get(key)});
					}
				}
			}
			
			if(List.class.isAssignableFrom(ms.getMethod().getReturnType()))
				return daoService.findByNamedQuery(queryName, params);
			else
				return daoService.findOneByNamedQuery(queryName, params);
		}else
			return pjp.proceed(pjp.getArgs());
	}
}
