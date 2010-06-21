package loxia.springmvc.aspect;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import loxia.dao.OperatingUnitDao;
import loxia.exception.BusinessException;
import loxia.exception.PreserveErrorCode;
import loxia.model.OperatingUnit;
import loxia.springmvc.BaseProfileController;
import loxia.springmvc.annotation.CurrentOu;
import loxia.web.annotation.Acl;

@Aspect
public class AuthorizationAspect implements Ordered{
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationAspect.class);
	
	@Resource
	private ApplicationContext context;
	
	public int getOrder() {
		return 10;
	}
	
	@Around("@annotation(loxia.web.annotation.Acl)")
	public Object authorize(ProceedingJoinPoint pjp) throws Throwable{
		logger.debug("Begin authorization check...");
		BaseProfileController controller = null;
		if(pjp.getThis() instanceof BaseProfileController){
			controller = (BaseProfileController)pjp.getThis();
		}else{
			logger.info("Currently we only check privilege on controllers.");
			return pjp.proceed(pjp.getArgs());
		}
		
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		Acl acl = ms.getMethod().getAnnotation(Acl.class);
		
		logger.debug("Acl found: {}", Arrays.asList(acl.value()));
		OperatingUnitDao operatingUnitDao = (OperatingUnitDao)context.getBean("loxiaOperatingUnitDao");
		OperatingUnit currentOu = null;
		Annotation[][] paramAnnos = ms.getMethod().getParameterAnnotations();
		for(int i=0; i < paramAnnos.length; i++){
			for(int j=0; j< paramAnnos[i].length; j++){
				if(paramAnnos[i][j] != null && paramAnnos[i][j] instanceof CurrentOu){
					Long ouId = null;
					if(pjp.getArgs()[i] instanceof OperatingUnit){
						ouId = ((OperatingUnit)pjp.getArgs()[i]).getId();
					}else if(pjp.getArgs()[i] instanceof Long){
						ouId = (Long)pjp.getArgs()[i];
					}else
						throw new IllegalArgumentException("Current Ou setting error.");
					if(ouId != null)
						currentOu = operatingUnitDao.getByPrimaryKey(ouId);
					if(currentOu == null)
						throw new IllegalArgumentException("Current Ou is null.");
					break;
				}
			}
			if(currentOu != null) break;
		}			
					
		if(currentOu != null){
			logger.debug("New current ou is set:{}[{}]", currentOu.getName(), currentOu.getId());
			controller.setCurrentOperatingUnit(currentOu);
		}else{
			logger.debug("Current ou isn't changed.");
		}
		
		if(controller.getCurrentOperatingUnit() == null){
			logger.warn("Current ou is null.");
			throw new BusinessException(PreserveErrorCode.NO_SUFFICICENT_PRIVILEGE);
		}
		if(controller.checkPrivilege(acl.value())){
			logger.debug("User pass the authorization.");
			return pjp.proceed(pjp.getArgs());
		}else{
			throw new BusinessException(PreserveErrorCode.NO_SUFFICICENT_PRIVILEGE);
		}
	}
}
