package cn.benjamin.loxia.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.benjamin.loxia.support.LoxiaSupportConstants;
import cn.benjamin.loxia.support.LoxiaSupportSettings;
import cn.benjamin.loxia.utils.DateUtil;

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
