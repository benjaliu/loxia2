package loxia.support.jasperreport;

import java.io.InputStream;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class ClasspathJasperPrinter extends BasicJasperPrinter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8715474238395858959L;
	
	private String reportName;
	
	public ClasspathJasperPrinter(String reportName){
		this.reportName = reportName;
	}

	@Override
	protected JasperReport getJasperReport() throws JasperReportNotFoundException {
		InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResource(reportName).openStream();
            return (JasperReport) JRLoader.loadObject(is);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JasperReportNotFoundException(e);
        } finally {
            if (is != null) try {is.close();} catch (Exception e) {}
        }
	}

}
