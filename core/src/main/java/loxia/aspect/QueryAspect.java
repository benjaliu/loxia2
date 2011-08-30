package loxia.aspect;

import loxia.annotation.DynamicQuery;
import loxia.annotation.NamedQuery;
import loxia.annotation.NativeQuery;
import loxia.annotation.NativeUpdate;
import loxia.annotation.Query;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.service.VelocityTemplateService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;


@Aspect
public class QueryAspect implements Ordered, InitializingBean {

	@Autowired
	private DaoService daoService;
	
	@Autowired
	private VelocityTemplateService templateService;
	
	@Autowired
	private DynamicNamedQueryProvider dnqProvider;
	
	private NamedQueryHandler namedQueryHandler;
	private QueryHandler queryHandler;
	private DynamicQueryHandler dynamicQueryHandler;
	private NativeQueryHandler nativeQueryHandler;
	
	public void afterPropertiesSet() throws Exception {
		namedQueryHandler = new NamedQueryHandler(daoService);
		queryHandler = new QueryHandler(daoService);
		dynamicQueryHandler = new DynamicQueryHandler(daoService, templateService, dnqProvider);
		nativeQueryHandler = new NativeQueryHandler(daoService, templateService, dnqProvider);
	}
	
	public int getOrder() {
		return 20;
	}

	@Around("this(loxia.dao.GenericEntityDao)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Query query = ms.getMethod().getAnnotation(Query.class);
		NamedQuery namedQuery = ms.getMethod().getAnnotation(NamedQuery.class);
		DynamicQuery dynamicQuery = ms.getMethod().getAnnotation(DynamicQuery.class);
		NativeQuery nativeQuery = ms.getMethod().getAnnotation(NativeQuery.class);
		NativeUpdate nativeUpdate = ms.getMethod().getAnnotation(NativeUpdate.class);
		
		if(namedQuery !=null){
			return namedQueryHandler.handleNamedQuery(namedQuery, pjp);
		}else if(query != null){
			return queryHandler.handleQuery(query,pjp);
		}else if(dynamicQuery != null){
			return dynamicQueryHandler.handleDynamicQuery(dynamicQuery, pjp);
		}else if(nativeQuery != null){
			return nativeQueryHandler.handleNativeQuery(nativeQuery, pjp);
		}else if(nativeUpdate != null){
			return nativeQueryHandler.handleNativeUpdate(nativeUpdate, pjp);
		}else
			return pjp.proceed(pjp.getArgs());
	}
}
