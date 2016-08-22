package loxia.utils;

public interface CopyableInterface {
	@SuppressWarnings("rawtypes")
	public boolean isCopyable(String propertyName, Object propertyValue, Class clazz);
}
