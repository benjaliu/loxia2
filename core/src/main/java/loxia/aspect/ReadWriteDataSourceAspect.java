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
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

@Aspect
public class ReadWriteDataSourceAspect implements Ordered{
	protected static final Logger logger = LoggerFactory.getLogger(ReadWriteDataSourceAspect.class);
	
	@Autowired
	TransactionAttributeSource transactionAttributeSouce;
	
	@Around("this(loxia.dao.ReadWriteSupport)")
	public Object doQuery(ProceedingJoinPoint pjp) throws Throwable{	
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		TransactionAttribute ta = transactionAttributeSouce.getTransactionAttribute(ms.getMethod(), pjp.getTarget().getClass());
		if(ta == null || (ReadWriteStatusHolder.getReadWriteStatus() != null && ta.getPropagationBehavior() != TransactionDefinition.PROPAGATION_REQUIRES_NEW)){
			return pjp.proceed(pjp.getArgs());
		}
		
		logger.debug("determine datasource for query:{}.{}",ms.getDeclaringType().getName(),ms.getMethod().getName());		
		logger.debug("Current operation's transaction status: {}", ta == null ? "null": ta.toString());
		
		String currentStatus = ReadWriteStatusHolder.getReadWriteStatus();
		if(ta != null){
			if(ta.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW){
				logger.debug("New writable connection is required for new transaction.");
				ReadWriteStatusHolder.setReadWriteStatus(ReadWriteSupport.WRITE);
			}else
				ReadWriteStatusHolder.setReadWriteStatus((ta != null && ta.isReadOnly()) ? ReadWriteSupport.READ: ReadWriteSupport.WRITE);
		}
		try {
			Object rtn = pjp.proceed(pjp.getArgs());
			return rtn;
		} catch (Throwable e) {
			throw e;
		} finally {
			if(ta != null){
				if(ta.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW){
					logger.debug("Fallback to previous Read/Write Status: {}", currentStatus);
					if(currentStatus == null)
						ReadWriteStatusHolder.clearReadWriteStatus();
					else
						ReadWriteStatusHolder.setReadWriteStatus(currentStatus);
				}else{
					logger.debug("Clear Read/Write Status");
					ReadWriteStatusHolder.clearReadWriteStatus();					
				}				
			}
		}
	}

	public int getOrder() {
		return 0;
	}
}
