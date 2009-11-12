package cn.benjamin.loxia.service;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityTemplateService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5003667862282402287L;

static final Logger logger = LoggerFactory.getLogger(VelocityTemplateService.class);
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String DEFAULT_TEMPLATE_PLACE = "template/vm/";

    private boolean initFlag = false;

    public VelocityTemplateService() {
        Velocity.setProperty("resource.loader", "class");
        Velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        try {
            Velocity.init();
            initFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Init Velocity Error");
        }
    }

    public String parseVMContent(String templateContent, Map<String,Object> contextParameters) {
        if (!initFlag)
            throw new RuntimeException("Velocity initialize failed");
        if (logger.isDebugEnabled()) {
        	logger.debug("Start parsing velocity template");
        	logger.debug("Template content: {}", templateContent);
        	logger.debug("Parameters: {}", contextParameters);
        }
        try {
            VelocityContext context = new VelocityContext();
            for (String key : contextParameters.keySet()) {
                context.put(key, contextParameters.get(key));
            }

            StringWriter writer = new StringWriter();
            Velocity.evaluate(context, writer, "loxia", templateContent);

            String result = writer.getBuffer().toString();
            if (logger.isDebugEnabled()) {
            	logger.debug("Parse result is: ");
            	logger.debug(result);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Parse Velocity Template Error");
        }
    }

    public String parseVMTemplate(String templateFileName, Map<String,Object> contextParameters) {
        if (!initFlag)
            throw new RuntimeException("Velocity initialize failed");
        if (logger.isDebugEnabled()) {
        	logger.debug("Start parsing velocity template");
        	logger.debug("Template name: {}", templateFileName);
        	logger.debug("Parameters: {}", contextParameters);
        }
        try {
            Template template = Velocity.getTemplate(DEFAULT_TEMPLATE_PLACE + templateFileName, 
            		DEFAULT_ENCODING);
            VelocityContext context = new VelocityContext();
            for (String key : contextParameters.keySet()) {
                context.put(key, contextParameters.get(key));
            }
            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            String result = writer.getBuffer().toString();
            if (logger.isDebugEnabled()) {
            	logger.debug("Parse result is: ");
            	logger.debug(result);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Parse Velocity Template Error");
        }
    }
}
