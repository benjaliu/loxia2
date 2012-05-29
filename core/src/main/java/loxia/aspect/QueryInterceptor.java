package loxia.aspect;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import loxia.annotation.DynamicQuery;
import loxia.annotation.NamedQuery;
import loxia.annotation.NativeQuery;
import loxia.annotation.NativeUpdate;
import loxia.annotation.Query;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.service.VelocityTemplateService;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryInterceptor implements MethodInterceptor {
	
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
	
	@PostConstruct
	public void init() throws Exception {
		namedQueryHandler = new NamedQueryHandler(daoService);
		queryHandler = new QueryHandler(daoService);
		dynamicQueryHandler = new DynamicQueryHandler(daoService, templateService, dnqProvider);
		nativeQueryHandler = new NativeQueryHandler(daoService, templateService, dnqProvider);
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method m = invocation.getMethod();
		Query query = m.getAnnotation(Query.class);
		NamedQuery namedQuery = m.getAnnotation(NamedQuery.class);
		DynamicQuery dynamicQuery = m.getAnnotation(DynamicQuery.class);
		NativeQuery nativeQuery = m.getAnnotation(NativeQuery.class);
		NativeUpdate nativeUpdate = m.getAnnotation(NativeUpdate.class);
		
		if(namedQuery !=null){
			return namedQueryHandler.handleNamedQuery(namedQuery, invocation);
		}else if(query != null){
			return queryHandler.handleQuery(query,invocation);
		}else if(dynamicQuery != null){
			return dynamicQueryHandler.handleDynamicQuery(dynamicQuery, invocation);
		}else if(nativeQuery != null){
			return nativeQueryHandler.handleNativeQuery(nativeQuery, invocation);
		}else if(nativeUpdate != null){
			return nativeQueryHandler.handleNativeUpdate(nativeUpdate, invocation);
		}else
			return invocation.proceed();
	}

}
