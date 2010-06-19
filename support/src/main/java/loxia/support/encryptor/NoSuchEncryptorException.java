package loxia.support.encryptor;

public class NoSuchEncryptorException extends Exception{
    /**
	 *
	 */
	private static final long serialVersionUID = 2171961511125257600L;

	public NoSuchEncryptorException() {
    }

    public NoSuchEncryptorException(String message) {
        super(message);
    }

    public NoSuchEncryptorException(Throwable cause) {
        super(cause);
    }

    public NoSuchEncryptorException(String message, Throwable cause) {
        super(message, cause);
    }
}
