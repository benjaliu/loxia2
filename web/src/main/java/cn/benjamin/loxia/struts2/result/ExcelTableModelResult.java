package cn.benjamin.loxia.struts2.result;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.benjamin.loxia.struts2.table.TableModel;
import cn.benjamin.loxia.struts2.table.TableModelUtils;

import com.opensymphony.xwork2.ActionInvocation;

public class ExcelTableModelResult extends StrutsResultSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -664529035966407899L;
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelTableModelResult.class);
	
	public static final String DEFAULT_CONTENT_TYPE = "application/vnd.ms-excel";

	@SuppressWarnings("unchecked")
	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		Map request  = (Map)invocation.getInvocationContext().get("request");
		TableModel tableModel = (TableModel)request.get("exceltable");
		
		HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
		response.setHeader("Content-Disposition", "attachment;filename=" + tableModel.getModelName() + ".xls");
		
		
		if(tableModel != null){
			logger.debug("Output Excel {}.xls", tableModel.getModelName());
			TableModelUtils.outputExcel(response.getOutputStream(), tableModel);
		}
	}

}
