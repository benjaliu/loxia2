package loxia.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import loxia.annotation.NamedQuery;
import loxia.dao.DaoService;
import loxia.dao.ModelClassSupport;
import loxia.dao.Pagination;
import loxia.dao.Sort;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class NamedQueryHandler extends AbstractQueryHandler{
	
	public NamedQueryHandler(DaoService daoService){
		super(daoService);
	}

	public Object handleNamedQuery(NamedQuery namedQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		boolean pagable = namedQuery.pagable();		
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
		}else if(Pagination.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByNamedQuery(queryName, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], namedQuery.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByNamedQuery(queryName, params, sorts);
	}
}
