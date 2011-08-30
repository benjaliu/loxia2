package loxia.support.jasperreport;

import java.applet.Applet;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

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
	public void previewReport(String reportURL){
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
			System.out.println("Load print objects[" + url +"]");
			List<JasperPrint> printList = (List<JasperPrint>)JRLoader.loadObject(url);
			System.out.println("Print objects loading finished.");
			if(printList.size() > 0){
				//preview first one
				final JasperPrint jasperPrint = printList.get(0);
				new Thread(new Runnable() {					
					public void run() {
						try {
							AccessController.doPrivileged(new PrivilegedExceptionAction() {
								public Object run() {
									JasperViewer.viewReport(jasperPrint,false);
									return null;
								}
							});
						} catch (PrivilegedActionException e) {							
							e.printStackTrace();
						}
					}
				}).start();				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
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
			System.out.println("Load print objects[" + url +"]");
			List<JasperPrint> printList = (List<JasperPrint>)JRLoader.loadObject(url);
			System.out.println("Print objects loading finished.");
			if(printList.size() > 0){
				//print
				System.out.println("Print " + printList.size() + " files...");
				Thread thread = new PrintThread(printList, showPrintDlg);
				thread.start();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
    
	private class PrintThread extends Thread{

		private List<JasperPrint> printList;
		private boolean showPrintDlg;
		
		public PrintThread(List<JasperPrint> printList, boolean showPrintDlg){
			this.printList = printList;
			this.showPrintDlg = showPrintDlg;
		}
		
		@Override
		public void run() {
			AccessController.doPrivileged(new PrintPrivilegeAction(printList, showPrintDlg));
		}		
	}
	
	private class PrintPrivilegeAction implements PrivilegedAction {

		private List<JasperPrint> printList;
		private boolean showPrintDlg;
		
		public PrintPrivilegeAction(List<JasperPrint> printList, boolean showPrintDlg){
			this.printList = printList;
			this.showPrintDlg = showPrintDlg;
		}
		
		public Object run() {
			System.out.println("Print Thread");
			try {
				for (JasperPrint jp: printList) {
					System.out.println("Printing... " + jp.getName());
					JasperPrintManager.printReport(jp, showPrintDlg);
				}
			} catch (JRException e) {
				e.printStackTrace();
			}
            return null;
		}
		
	}
}
