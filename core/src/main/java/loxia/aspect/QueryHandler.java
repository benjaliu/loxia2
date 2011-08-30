package loxia.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import loxia.annotation.Query;
import loxia.dao.DaoService;
import loxia.dao.Pagination;
import loxia.dao.Sort;

public class QueryHandler extends AbstractQueryHandler {

	public QueryHandler(DaoService daoService) {
		super(daoService);
	}

	public Object handleQuery(Query query, ProceedingJoinPoint pjp){
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Map<String, Object> params = getParams(ms.getMethod(), pjp.getArgs());
		
		boolean pagable = query.pagable();		
		String queryString = query.value();			
		logger.debug("Query[{}] will be executed.",queryString);
		
		Sort[] sorts = getSorts(pjp.getArgs());
		
		if(sorts != null){
			logger.debug("Query need be sorted with :" + Arrays.asList(sorts));
		}
		
		if(List.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByQuery(queryString, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1]);
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				return daoService.findByQuery(queryString, params, sorts);
			}
		}else if(Pagination.class.isAssignableFrom(ms.getMethod().getReturnType())){
			if(pagable){
				if(pjp.getArgs()[0] instanceof Integer &&
						pjp.getArgs()[1] instanceof Integer)				
					return daoService.findByQuery(queryString, params, sorts, (Integer)pjp.getArgs()[0], (Integer)pjp.getArgs()[1], query.withGroupby());
				else
					throw new IllegalArgumentException("Startindex and pagesize must be set for pagable query.");
			}else{
				throw new IllegalStateException("Please set pagable to true");
			}
		}else
			return daoService.findOneByQuery(queryString, params, sorts);
	}
}
