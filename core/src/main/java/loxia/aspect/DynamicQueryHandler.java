package loxia.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import loxia.annotation.DynamicQuery;
import loxia.dao.DaoService;
import loxia.dao.DynamicNamedQueryProvider;
import loxia.dao.ModelClassSupport;
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

	public Object handleDynamicQuery(DynamicQuery dynamicQuery, ProceedingJoinPoint pjp){
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
	
	protected String getDynamicQuery(String queryName, Map<String, Object> params) {
		return templateService.parseVMContent(dnqProvider.getDynamicQueryByName(queryName), params);
	}
}
