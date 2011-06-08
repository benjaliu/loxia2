package loxia.support.jasperreport;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public abstract class BasicJasperPrinter implements JasperPrinter, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7993163436803016218L;
	
	protected static final Logger logger = LoggerFactory.getLogger(BasicJasperPrinter.class);
	
	protected Map<String,Object> parameterMap = new HashMap<String, Object>();
	protected JRDataSource ds;
	protected JasperReport report;
	
	protected abstract JasperReport getJasperReport() throws JasperReportNotFoundException;

	public void initializeReport(Map<String, Object> initParams, JRDataSource ds) throws JasperReportNotFoundException {
		parameterMap.putAll(initParams);
		this.ds = ds;
		report = getJasperReport();
	}

	public JasperPrint print() throws JasperPrintFailureException{
		try {
			logger.debug("Begin print...");
			JasperPrint print = JasperFillManager.fillReport(report, parameterMap, ds);
			logger.debug("Data preparation done.");
			return print;
		} catch (JRException e) {
			throw new JasperPrintFailureException(e);
		}
	}

	public void print(OutputStream os) throws JasperPrintFailureException{
		JasperPrint print = print();
		logger.debug("Data preparation done.");
        try {
			JasperExportManager.exportReportToPdfStream(print, os);
			logger.debug("Print completed.");
		} catch (JRException e) {
			throw new JasperPrintFailureException(e);
		}
	}

}
