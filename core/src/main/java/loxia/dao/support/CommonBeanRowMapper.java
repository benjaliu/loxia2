package loxia.dao.support;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import loxia.dao.ColumnTranslator;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.RowMapper;

public class CommonBeanRowMapper<T> extends BaseRowMapper<T> {

	private Map<String,Class<?>> attributeMap;
	private Map<String,String> columnDefinition;
	private Class<T> clazz;
	
	public CommonBeanRowMapper() { this.clazz = getGenericClass(); setAttributes(null,null);}
	public CommonBeanRowMapper(Class<T> clazz){
		this.clazz = clazz;
		setAttributes(null,null);
	}
	public CommonBeanRowMapper(String... attributes){
		this.clazz = getGenericClass();
		setAttributes(attributes,null);
	}
	public CommonBeanRowMapper(Class<T> clazz, String... attributes){
		this.clazz = clazz;
		setAttributes(attributes,null);
	}
	public CommonBeanRowMapper(Class<T> clazz, ColumnTranslator translator, String... attributes){
		this.clazz = clazz;
		setAttributes(attributes,translator);
	}
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			T returnObj = clazz.newInstance();

			ResultSetMetaData meta = rs.getMetaData();
			
			for(int i=1; i<= meta.getColumnCount(); i++){
				String name = meta.getColumnLabel(i).toUpperCase();
				if(columnDefinition.containsKey(name)){
					String attribute = columnDefinition.get(name);
					try {						
						int delim = attribute.indexOf(".");
						if(delim >0){
							String main = attribute.substring(0,delim);
							String sub = attribute.substring(delim+1);
							Object joinObj = attributeMap.get(main).newInstance();
							PropertyUtils.setProperty(joinObj, sub, getResultFromRs(rs, i, attributeMap.get(attribute)));
							PropertyUtils.setProperty(returnObj, main, joinObj);
						}else
							PropertyUtils.setProperty(returnObj, attribute, 
									getResultFromRs(rs, i, attributeMap.get(attribute)));
					} catch (Exception e) {
						logger.warn("Set property[{}] failed.", attribute);
					}
				}
			}
			return returnObj;
		} catch (Exception e) {			
			e.printStackTrace();
			throw new RuntimeException("Cannot create new instance for class:" + clazz);
		}
	}

	public void setAttributes(String[] attributes, ColumnTranslator translator) {		
		try {
			attributeMap = new HashMap<String, Class<?>>();
			columnDefinition = new HashMap<String, String>();
			translator = translator == null ? 
					(clazz.getAnnotation(Entity.class) != null ? new JpaEntityColumnTranslator(clazz)
							: new UpperCaseColumnTranslator(clazz)) 
					: translator;
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors =
			    beanInfo.getPropertyDescriptors();			
			if(attributes != null && attributes.length > 0){
				Set<String> attributeSet = new HashSet<String>();
				attributeSet.addAll(Arrays.asList(attributes));
				for(PropertyDescriptor p: propertyDescriptors){
					if(attributeSet.contains(p.getName())){
						setAttributeAndColumnDefinition(p, translator);
					}					
				}				
			}else{
				for(PropertyDescriptor p: propertyDescriptors){
					setAttributeAndColumnDefinition(p, translator);
				}
			}			
		} catch (IntrospectionException e) {
			throw new RuntimeException("Initial rowmapper failed for class:" + clazz);
		}
		if(logger.isDebugEnabled()){
			logger.debug("ColumnTranslator: {}", translator.getClass());
			logger.debug("Attribute Map for {}:", clazz);
			for(String key: attributeMap.keySet())
				logger.debug("{}:{}", key, attributeMap.get(key));
			logger.debug("Column definitions for {}:", clazz);
			for(String key: columnDefinition.keySet())
				logger.debug("{}:{}", key, columnDefinition.get(key));
		}		
	}
	
	private void setAttributeAndColumnDefinition(PropertyDescriptor p, ColumnTranslator translator) throws IntrospectionException{
		if(p.getReadMethod() == null) return;
		if(p.getReadMethod().getAnnotation(JoinColumn.class) != null){
			String subAttr = null;
			Class<?> subClass = null;
			BeanInfo joinBeanInfo = Introspector.getBeanInfo(p.getPropertyType());
			for(PropertyDescriptor p1: joinBeanInfo.getPropertyDescriptors()){
				if(p1.getReadMethod().getAnnotation(Id.class) != null){
					subAttr = p1.getName();
					subClass = p1.getPropertyType();
					break;
				}
			}
			attributeMap.put(p.getName(), p.getPropertyType());
			attributeMap.put(p.getName() + "." + subAttr, subClass);
			columnDefinition.put(translator.toColumnName(p.getName()), p.getName() + "." + subAttr);
		}else{
			attributeMap.put(p.getName(), p.getPropertyType());
			columnDefinition.put(translator.toColumnName(p.getName()), p.getName());
		}
	}

	@SuppressWarnings("unchecked")
	private Class<T> getGenericClass(){
		Class<?> clazz = this.getClass();
		Type type = clazz.getGenericSuperclass();
		while(!(type instanceof ParameterizedType) && clazz != null && clazz != Object.class){
			clazz = clazz.getSuperclass();
			type = clazz.getGenericSuperclass();
		}
		
		if(!(type instanceof ParameterizedType)){
			Class<?>[] iclazzs = clazz.getInterfaces();
			if(iclazzs.length > 0){
				int index = -1;
				for(int i=0; i< iclazzs.length; i++){
					if(RowMapper.class.isAssignableFrom(iclazzs[i])){
						index = i;
						break;
					}
				}
				if(index >=0){
					if(clazz.getGenericInterfaces()[index] instanceof ParameterizedType)
						type = clazz.getGenericInterfaces()[index];
				}
			}
						
		}
		
		if(!(type instanceof ParameterizedType)){
			throw new RuntimeException("Can not find the right Generic Class.");
		}
		
		ParameterizedType pType = (ParameterizedType)type;
		return (Class<T>)pType.getActualTypeArguments()[0];
	}
}
