package loxia.dao;

public class InvalidSortException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8153540158589616966L;
	
	public InvalidSortException() {
		super();
	}

	public InvalidSortException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSortException(String message) {
		super(message);
	}

	public InvalidSortException(Throwable cause) {
		super(cause);
	}	

}
