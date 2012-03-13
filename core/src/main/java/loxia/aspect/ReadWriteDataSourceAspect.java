package loxia.aspect;

import loxia.dao.ReadWriteStatusHolder;
import loxia.dao.ReadWriteSupport;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

@Aspect
public class ReadWriteDataSourceAspect implements Ordered{
	protected static final Logger logger = LoggerFactory.getLogger(ReadWriteDataSourceAspect.class);
	
	@Autowired
	TransactionAttributeSource transactionAttributeSouce;
	
	@Around("this(loxia.dao.ReadWriteSupport)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{	
		if(ReadWriteStatusHolder.getReadWriteStatus() != null){
			return pjp.proceed(pjp.getArgs());
		}
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		logger.debug("determine datasource for query:{}.{}",ms.getDeclaringType().getName(),ms.getMethod().getName());
		TransactionAttribute ta = transactionAttributeSouce.getTransactionAttribute(ms.getMethod(), pjp.getTarget().getClass());		
		
		logger.debug("Current operation's transaction status: {}", ta == null ? "null": ta.toString());
		
		if(ta != null){
			ReadWriteStatusHolder.setReadWriteStatus((ta != null && ta.isReadOnly()) ? ReadWriteSupport.READ: ReadWriteSupport.WRITE);
		}
		try {
			Object rtn = pjp.proceed(pjp.getArgs());
			return rtn;
		} catch (Throwable e) {
			throw e;
		} finally {
			if(ta != null){
				ReadWriteStatusHolder.clearReadWriteStatus();
				logger.debug("Clear Read/Write Status");
			}
		}
	}

	public int getOrder() {
		return 0;
	}
}
