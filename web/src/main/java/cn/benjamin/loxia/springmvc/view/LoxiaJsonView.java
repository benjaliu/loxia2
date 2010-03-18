package cn.benjamin.loxia.springmvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import cn.benjamin.loxia.support.json.JSONObject;
import cn.benjamin.loxia.web.LoxiaWebConstants;

public class LoxiaJsonView extends AbstractView {

	/**
	 * Default content type. Overridable as bean property.
	 */
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	private boolean prefixJson = false;
	
	private String encoding;	

	public LoxiaJsonView(){
		setContentType(DEFAULT_CONTENT_TYPE);
		setEncoding(DEFAULT_ENCODING);
	}
	
	@Override
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(getContentType());
		response.setCharacterEncoding(encoding);
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String filterString = (String)model.get(LoxiaWebConstants.JSON_FILTER_STR);
		model.remove(LoxiaWebConstants.JSON_FILTER_STR);
		if (prefixJson)
			response.getWriter().write("{} && ");
		if(model.size() > 0){
			if (filterString == null){				
				filterString = "**";
			}
			response.getWriter().write(new JSONObject(model,filterString).toString());
		}else{
			response.getWriter().write(new JSONObject().toString());
		}				
	}
	
	/**
	 * Indicates whether the JSON output by this view should be prefixed with "{@code {} &&}". Default is false.
	 *
	 * <p> Prefixing the JSON string in this manner is used to help prevent JSON Hijacking. The prefix renders the string
	 * syntactically invalid as a script so that it cannot be hijacked. This prefix does not affect the evaluation of JSON,
	 * but if JSON validation is performed on the string, the prefix would need to be ignored.
	 */
	public void setPrefixJson(boolean prefixJson) {
		this.prefixJson = prefixJson;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}
