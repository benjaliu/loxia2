package cn.benjamin.loxia.support.encryptor;

public class EncryptException extends Exception{
    /**
	 *
	 */
	private static final long serialVersionUID = -105804760357492438L;

	public EncryptException() {
    }

    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(Throwable cause) {
        super(cause);
    }

    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
