package loxia.support.jasperreport;

import java.applet.Applet;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.util.JRLoader;

public class JasperPrintApplet extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4084467775319294885L;

	private URL url = null;	
    private ResourceBundle bundle = ResourceBundle.getBundle("loxia/support/jasperreport/messages");
	
	public void printReprot(String reportURL){
		printReport(reportURL, false);
	}
    
	@SuppressWarnings("unchecked")
	public void printReport(String reportURL, final boolean showPrintDlg) {
		try {
			if(reportURL.indexOf("://") >0)
				url = new URL(reportURL);
			else
				url = new URL(getCodeBase(), reportURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// URL existence
		if (url == null) {
			JOptionPane.showMessageDialog(this, bundle.getString("print.nullurl"));
			return;
		}		
		
		try {
			final List<JasperPrint> printList = (List<JasperPrint>)JRLoader.loadObject(url);
			if(printList.size() > 0){
				//print
				Thread thread = new Thread(
					new Runnable() {
						public void run() {
							AccessController.doPrivileged(new PrivilegedAction() {
						        public Object run() {
						        	try {
										for (JasperPrint jp: printList) {
											JasperPrintManager.printReport(jp, showPrintDlg);
										}
									} catch (JRException e) {
										e.printStackTrace();
									}
						            return null;
						        }
						    });
						}
					});
				thread.start();
			}
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
    
}
