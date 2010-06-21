package loxia.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loxia.support.LoxiaSupportConstants;
import loxia.support.LoxiaSupportSettings;
import loxia.utils.DateUtil;

public class InitConfigurationListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(InitConfigurationListener.class);
	
	public void contextDestroyed(ServletContextEvent sce) {
		//currently do nothing
	}

	public void contextInitialized(ServletContextEvent sce) {
		logger.debug("Initialize context for application...");
		DateUtil.applyPattern(LoxiaSupportSettings.getInstance().get(LoxiaSupportConstants.DATE_PATTERN));
	}

}
