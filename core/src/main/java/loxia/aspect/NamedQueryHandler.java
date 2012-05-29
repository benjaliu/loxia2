package loxia.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import loxia.annotation.NamedQuery;
import loxia.dao.DaoService;
import loxia.dao.ModelClassSupport;
import loxia.dao.Page;
import loxia.dao.Pagination;
import loxia.dao.Sort;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class NamedQueryHandler extends AbstractQueryHandler{
	
	public NamedQueryHandler(DaoService daoService){
		super(daoService);
	}
	
	public Object handleNamedQuery(NamedQuery namedQuery, MethodInvocation invocation){
		return handleNamedQueryNative(namedQuery, invocation.getThis(), invocation.getMethod(), invocation.getArguments());
	}

	public Object handleNamedQuery(NamedQuery namedQuery, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		return handleNamedQueryNative(namedQuery, pjp.getThis(), ms.getMethod(), pjp.getArgs());		
	}
	
	private Object handleNamedQueryNative(NamedQuery namedQuery, Object obj, Method m, Object[] args){
		Map<String, Object> params = getParams(m, args);
		
		Page page = getPage(args);
		boolean pagable = (page!= null) || namedQuery.pagable();		
		String queryName = namedQuery.value();
		if(queryName.equals("")){
			if(!(obj instanceof ModelClassSupport)) 
				throw new RuntimeException("QueryName can not be empty");
			ModelClassSupport mcs = (ModelClassSupport)obj;
			queryName += mcs.getModelClass().getSimpleName();
			queryName += "." + m.getName();				
		}
		logger.debug("Named Query[{}] will be executed.",queryName);
					
		Sort[] sorts = getSorts(args);
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByNamedQuery(queryName, params, sorts, page.getStart(), page.getSize());
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByNamedQuery(queryName, params, sorts, (Integer)args[0], (Integer)args[1]);
				else 
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				if(sorts == null)
					return daoService.findByNamedQuery(queryName, params);
				else
					return daoService.findByNamedQuery(queryName, params, sorts);
			}
		}else if(Pagination.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByNamedQuery(queryName, params, sorts, page.getStart(), page.getSize(), namedQuery.withGroupby());
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByNamedQuery(queryName, params, sorts, (Integer)args[0], (Integer)args[1], namedQuery.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByNamedQuery(queryName, params, sorts);
	}
}
