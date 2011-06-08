package loxia.support.jasperreport;

public class JasperPrintFailureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4144390816574152645L;

	public JasperPrintFailureException() {
		super();
	}

	public JasperPrintFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public JasperPrintFailureException(String message) {
		super(message);
	}

	public JasperPrintFailureException(Throwable cause) {
		super(cause);
	}
}
