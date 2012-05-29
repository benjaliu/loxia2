package loxia.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import loxia.annotation.Query;
import loxia.dao.DaoService;
import loxia.dao.Page;
import loxia.dao.Pagination;
import loxia.dao.Sort;

public class QueryHandler extends AbstractQueryHandler {

	public QueryHandler(DaoService daoService) {
		super(daoService);
	}
	
	public Object handleQuery(Query query, MethodInvocation invocation){		
		return handleQueryNative(query, invocation.getThis(), invocation.getMethod(), invocation.getArguments());
	}

	public Object handleQuery(Query query, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		return handleQueryNative(query, pjp.getThis(), ms.getMethod(), pjp.getArgs());
	}
	
	private Object handleQueryNative(Query query, Object obj, Method m, Object[] args){
		Map<String, Object> params = getParams(m, args);
		Page page = getPage(args);
		boolean pagable = (page != null) || query.pagable();		
		
		String queryString = query.value();			
		logger.debug("Query[{}] will be executed.",queryString);
		
		Sort[] sorts = getSorts(args);
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByQuery(queryString, params, sorts, page.getStart(), page.getSize());
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByQuery(queryString, params, sorts, (Integer)args[0], (Integer)args[1]);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByQuery(queryString, params, sorts);
			}
		}else if(Pagination.class.isAssignableFrom(m.getReturnType())){
			if(pagable){
				if(page != null)
					return daoService.findByQuery(queryString, params, sorts, page.getStart(), page.getSize(), query.withGroupby());
				else if(args[0] instanceof Integer &&
						args[1] instanceof Integer)				
					return daoService.findByQuery(queryString, params, sorts, (Integer)args[0], (Integer)args[1], query.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByQuery(queryString, params, sorts);
	}
}
