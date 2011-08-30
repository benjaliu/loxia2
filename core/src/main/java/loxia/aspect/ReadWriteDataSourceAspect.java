package loxia.aspect;

import loxia.dao.ReadWriteStatusHolder;
import loxia.dao.ReadWriteSupport;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;

@Aspect
public class ReadWriteDataSourceAspect implements Ordered{
	
	@Around("this(loxia.dao.ReadWriteSupport)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Transactional tx = ms.getMethod().getAnnotation(Transactional.class);
		if(tx == null){
			tx = ms.getClass().getAnnotation(Transactional.class);			
		}
		boolean needSet = (tx != null && ReadWriteStatusHolder.getReadWriteStatus() == null);
		if(needSet){
			ReadWriteStatusHolder.setReadWriteStatus(tx.readOnly() ? ReadWriteSupport.READ: ReadWriteSupport.WRITE);
		}
		try {
			Object rtn = pjp.proceed(pjp.getArgs());
			return rtn;
		} catch (Throwable e) {
			throw e;
		} finally {
			if(needSet)
				ReadWriteStatusHolder.clearReadWriteStatus();
		}
	}

	public int getOrder() {
		return 0;
	}
}
