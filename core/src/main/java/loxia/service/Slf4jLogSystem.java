/**
 * Copyright (c) 2012 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package loxia.service;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogSystem implements LogSystem{
	
	public static final String RUNTIME_LOG_SLF4J_LOGGER = "runtime.log.logsystem.slf4j.logger";
	 
    private Logger LOG = null;

	@Override
	public void init(RuntimeServices rs) throws Exception {
		String name = (String) rs.getProperty(RUNTIME_LOG_SLF4J_LOGGER);
        if (name != null) {
        	LOG = LoggerFactory.getLogger(name);
        	logVelocityMessage(DEBUG_ID, "SLF4JLogChute using logger '" + LOG.getName() + '\'');
        } else {
        	LOG = LoggerFactory.getLogger(this.getClass());
        	logVelocityMessage(DEBUG_ID, "SLF4JLogChute using logger '" + LOG.getClass() + '\'');
        }
	}

	@Override
	public void logVelocityMessage(int level, String message) {
		switch (level) {
	        case WARN_ID:
	        	LOG.warn(message);
	            break;
	        case INFO_ID:
	        	LOG.info(message);
	            break;
	        case ERROR_ID:
	        	LOG.error(message);
	            break;
	        case DEBUG_ID:
	        default:
	        	LOG.debug(message);
	            break;
	    }
	}

}
