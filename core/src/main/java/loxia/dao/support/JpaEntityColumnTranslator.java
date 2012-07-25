package loxia.dao.support;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

import loxia.dao.ColumnTranslator;

public class JpaEntityColumnTranslator implements ColumnTranslator {

	private Map<String,String> attributeMap = new HashMap<String, String>();
	
	public JpaEntityColumnTranslator(){}
	
	public JpaEntityColumnTranslator(Class<?> clazz){
		setModelClass(clazz);
	}
	
	public String toColumnName(String attribute) {
		return attributeMap.get(attribute);
	}

	public void setModelClass(Class<?> clazz) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors =
			    beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor p: propertyDescriptors){
				if(p.getReadMethod() == null) continue;
				if(p.getReadMethod().getAnnotation(Column.class) != null){				
					attributeMap.put(p.getName(), p.getReadMethod().getAnnotation(Column.class).name());
				}else if(p.getReadMethod().getAnnotation(JoinColumn.class) != null){
					attributeMap.put(p.getName(), p.getReadMethod().getAnnotation(JoinColumn.class).name());
				}else{
					attributeMap.put(p.getName(), p.getName().toUpperCase());
				}
			}
		} catch (IntrospectionException e) {
			//TODO
			throw new RuntimeException();
		}
	}

}
