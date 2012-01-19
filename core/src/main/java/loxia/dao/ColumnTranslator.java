package loxia.dao;

public interface ColumnTranslator {
	void setModelClass(Class<?> clazz);
	String toColumnName(String attribute);
}
