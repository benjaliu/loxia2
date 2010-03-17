package cn.benjamin.loxia.springmvc.aspect;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import cn.benjamin.loxia.dao.OperatingUnitDao;
import cn.benjamin.loxia.exception.BusinessException;
import cn.benjamin.loxia.exception.PreserveErrorCode;
import cn.benjamin.loxia.model.OperatingUnit;
import cn.benjamin.loxia.springmvc.BaseProfileController;
import cn.benjamin.loxia.springmvc.annotation.CurrentOu;
import cn.benjamin.loxia.web.LoxiaWebConstants;
import cn.benjamin.loxia.web.LoxiaWebSettings;
import cn.benjamin.loxia.web.annotation.Acl;

@Aspect
public class AuthorizationAspect implements ApplicationContextAware,Ordered{
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationAspect.class);
	
	private ApplicationContext ac;
	
	public int getOrder() {
		return 10;
	}
	
	@Around("@annotation(cn.benjamin.loxia.web.annotation.Acl)")
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
		OperatingUnitDao operatingUnitDao = (OperatingUnitDao)ac.getBean("loxiaOperatingUnitDao");
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
			String setting = LoxiaWebSettings.getInstance().get(LoxiaWebConstants.DEVELOPMENT);
			if(setting != null && setting.trim().toLowerCase().equals("true")){
				logger.info("Development mode is active, so the user's ou will be used as current ou");
				controller.setCurrentOperatingUnit(controller.getCurrentUser().getOu());
			}else
				throw new BusinessException(PreserveErrorCode.NO_SUFFICICENT_PRIVILEGE);
		}
		if(controller.checkPrivilege(acl.value())){
			logger.debug("User pass the authorization.");
			return pjp.proceed(pjp.getArgs());
		}else{
			throw new BusinessException(PreserveErrorCode.NO_SUFFICICENT_PRIVILEGE);
		}
	}


	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		this.ac = ac;
	}
}
