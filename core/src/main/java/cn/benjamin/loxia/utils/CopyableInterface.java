package cn.benjamin.loxia.utils;

public interface CopyableInterface {
	@SuppressWarnings("unchecked")
	public boolean isCopyable(String propertyName, Object propertyValue, Class clazz);
}
