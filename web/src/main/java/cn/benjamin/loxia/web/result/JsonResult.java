package cn.benjamin.loxia.web.result;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.benjamin.loxia.support.json.AbstractJSONObject;
import cn.benjamin.loxia.support.json.JSONArray;
import cn.benjamin.loxia.support.json.JSONObject;

import com.opensymphony.xwork2.ActionInvocation;

public class JsonResult extends StrutsResultSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4328612208758924273L;
	
	private static final Logger logger = LoggerFactory.getLogger(JsonResult.class);
	
	private String charSet;

	@SuppressWarnings("unchecked")
	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		logger.debug("Handling JSON Result...");
		Charset charset = null;
        if (charSet != null) {
            if (Charset.isSupported(charSet)) {
                charset = Charset.forName(charSet);
            }
            else {
                logger.warn("charset ["+charSet+"] is not recognized ");
                charset = null;
            }
        }
		HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
 
        if (charset != null) {
            response.setContentType("text/plain; charset="+charSet);
        }
        else {
            response.setContentType("text/plain");
        }
        response.setHeader("Content-Disposition", "inline");


        PrintWriter writer = response.getWriter();
        AbstractJSONObject jo = null;
        
        Map request  = (Map)invocation.getInvocationContext().get("request");
        Map<String,Object> exceptionMap = (Map<String,Object>)request.get("exception");
        if(exceptionMap != null){
        	logger.debug("Exception found:" + exceptionMap);
        	Map<String,Object> resultMap = new HashMap<String, Object>();
        	Map<String,Object> eMap = new HashMap<String, Object>();
        	eMap.put("obj", new JSONObject(exceptionMap.get("exception")));
        	eMap.put("message", new JSONArray((List<String>)exceptionMap.get("errorMessages")));
        	resultMap.put("exception", new JSONObject(eMap));
        	if(exceptionMap.get("invalidToken") != null){
        		resultMap.put("invalidToken", true);
        		resultMap.put("token", exceptionMap.get("token"));
        	}
        	jo = new JSONObject(resultMap);
        }else{        
	        jo = (AbstractJSONObject)request.get("json");	        
        }
        
        if(jo != null){
        	logger.debug(jo.toString());
        	writer.write(jo.toString());        	
        }
        
        if (writer != null) {
            writer.flush();
            writer.close();
        }        
	}

	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	
}
