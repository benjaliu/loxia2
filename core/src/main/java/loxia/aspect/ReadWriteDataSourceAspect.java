package loxia.aspect;

import java.lang.reflect.Method;

import loxia.dao.ReadWriteStatusHolder;
import loxia.dao.ReadWriteSupport;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;

@Aspect
public class ReadWriteDataSourceAspect implements Ordered{
	protected static final Logger logger = LoggerFactory.getLogger(ReadWriteDataSourceAspect.class);
	
	@Around("this(loxia.dao.ReadWriteSupport)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{			
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Method m = pjp.getTarget().getClass().getMethod(ms.getMethod().getName(), ms.getParameterTypes());
		logger.debug("determine datasource for query:{}.{}",ms.getDeclaringType().getName(),ms.getMethod().getName());
		Transactional tx = m.getAnnotation(Transactional.class);
		
		if(tx == null){
			logger.debug("Transaction annotation is not found at method.");
			tx = pjp.getTarget().getClass().getAnnotation(Transactional.class);			
		}
		logger.debug("Current operation's transaction status: {}-{}", tx==null?"F":"T", tx!=null&&tx.readOnly()?"R":"W");
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
			if(needSet){
				ReadWriteStatusHolder.clearReadWriteStatus();
				logger.debug("Clear Read/Write Status");
			}
		}
	}

	public int getOrder() {
		return 0;
	}
}
