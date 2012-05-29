package loxia.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import loxia.annotation.DynamicQuery;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.dao.ModelClassSupport;
import loxia.dao.Page;
import loxia.dao.Pagination;
import loxia.dao.Sort;
import loxia.service.VelocityTemplateService;

public class DynamicQueryHandler extends AbstractQueryHandler {
	
	private VelocityTemplateService templateService;
	
	private DynamicNamedQueryProvider dnqProvider;

	public DynamicQueryHandler(DaoService daoService, VelocityTemplateService templateService,
			DynamicNamedQueryProvider dnqProvider) {
		super(daoService);
		this.templateService = templateService;
		this.dnqProvider = dnqProvider;
	}
	
	public Object handleDynamicQuery(DynamicQuery dynamicQuery, MethodInvocation invocation){
		return handleDynamicQueryNative(dynamicQuery, invocation.getThis(), invocation.getMethod(), invocation.getArguments());
	}

	public Object handleDynamicQuery(DynamicQuery dynamicQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		return handleDynamicQueryNative(dynamicQuery, pjp.getThis(), ms.getMethod(), pjp.getArgs());
	}
	
	private Object handleDynamicQueryNative(DynamicQuery dynamicQuery, Object obj, Method m, Object[] args){
		Map<String, Object> params = getParams(m, args);
		
		Page page = getPage(args);
		boolean pagable = (page != null) ||dynamicQuery.pagable();		
		
		String queryName = dynamicQuery.value();
		if(queryName.equals("")){
			if(!(obj instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)obj;
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + m.getName();				
		}
		
		String queryString = getDynamicQuery(queryName, params);			
		logger.debug("Query[{}] will be executed.",queryString);
		
		Sort[] sorts = getSorts(args);
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByQueryEx(queryString, params, sorts, page.getStart(), page.getSize());
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByQueryEx(queryString, params, sorts, (Integer)args[0], (Integer)args[1]); 
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByQueryEx(queryString, params, sorts, -1, -1);
			}
		}else if(Pagination.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByQueryEx(queryString, params, sorts, page.getStart(), page.getSize(), dynamicQuery.withGroupby());
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByQueryEx(queryString, params, sorts, (Integer)args[0], (Integer)args[1], dynamicQuery.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByQueryEx(queryString, params, sorts);
	}
	
	protected String getDynamicQuery(String queryName, Map<String, Object> params) {
		return templateService.parseVMContent(dnqProvider.getDynamicQueryByName(queryName), params);
	}
}
