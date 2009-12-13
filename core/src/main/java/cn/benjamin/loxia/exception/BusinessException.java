package cn.benjamin.loxia.exception;

public class BusinessException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4638182132757700283L;

	private int errorCode;
	private Object[] args;
	private BusinessException linkedException;



	public BusinessException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public BusinessException(int errorCode, Object[] args){
		super();
		this.errorCode = errorCode;
		this.args = args;
	}

	public BusinessException(){
		super();
		errorCode = PreserveErrorCode.ERROR_NOT_SPECIFIED;
	}

	public BusinessException(int errorCode,String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public BusinessException(String message) {
		super(message);
		errorCode = PreserveErrorCode.ERROR_NOT_SPECIFIED;
	}

	public BusinessException(Throwable cause) {
		super(cause);
		errorCode = PreserveErrorCode.ERROR_NOT_SPECIFIED;
	}

	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}

	public BusinessException getLinkedException() {
		return linkedException;
	}

	public void setLinkedException(BusinessException linkedException) {
		this.linkedException = linkedException;
	}
}
