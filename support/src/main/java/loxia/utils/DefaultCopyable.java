package loxia.utils;

public class DefaultCopyable implements CopyableInterface {

	@SuppressWarnings("unchecked")
	public boolean isCopyable(String propertyName, Object propertyValue, Class clazz) {
		return true;
	}

}
