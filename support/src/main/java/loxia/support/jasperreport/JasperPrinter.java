package loxia.support.jasperreport;

import java.io.OutputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;

public interface JasperPrinter {
	void initializeReport(Map<String,Object> initParams, JRDataSource ds) throws JasperReportNotFoundException;
	JasperPrint print() throws JasperPrintFailureException;
	void print(OutputStream os) throws JasperPrintFailureException;
}
