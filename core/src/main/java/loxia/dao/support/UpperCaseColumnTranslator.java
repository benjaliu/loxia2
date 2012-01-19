package loxia.dao.support;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import loxia.annotation.Column;
import loxia.dao.ColumnTranslator;

public class UpperCaseColumnTranslator implements ColumnTranslator {

	private Map<String,String> columnMap = new HashMap<String, String>();
	
	public UpperCaseColumnTranslator(){}
	
	public UpperCaseColumnTranslator(Class<?> clazz){
		setModelClass(clazz);
	}
	
	public String toColumnName(String attribute) {
		return columnMap.get(attribute);
	}

	public void setModelClass(Class<?> clazz) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors =
			    beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor p: propertyDescriptors){
				if(p.getReadMethod().getAnnotation(Column.class) != null){				
					columnMap.put(p.getName(), p.getReadMethod().getAnnotation(Column.class).value());
				}else{
					columnMap.put(p.getName(), p.getName().toUpperCase());
				}
			}
		} catch (IntrospectionException e) {
			//TODO
			throw new RuntimeException();
		}
	}

}
