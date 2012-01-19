package loxia.dao.support;

import loxia.dao.ColumnTranslator;

public class DummyColumnTranslator implements ColumnTranslator {

	public void setModelClass(Class<?> clazz) {
		//do nothing
	}

	public String toColumnName(String attribute) {
		//do nothing
		return null;
	}

}
