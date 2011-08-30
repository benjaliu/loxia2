package loxia.spring;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import loxia.dao.GenericEntityDao;
import loxia.dao.ModelClassSupport;
import loxia.dao.support.GenericEntityDaoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

public class GenericDaoBeanDefinitionParser extends
		AbstractBeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(GenericDaoBeanDefinitionParser.class);
	
	public static final String INTERFACE_ATTRIBUTE = "interface";

	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {		
		AbstractBeanDefinition beanDefinition = parserContext.getDelegate()
			.parseBeanDefinitionElement(element, null, null);
		Class<?>[] interfaces = null;
		Class<?> targetType = null;
		if(beanDefinition.getBeanClassName() != null){
			//with CLASS_ATTRIBUTE
			interfaces = beanDefinition.getBeanClass().getInterfaces();
			
			if(GenericEntityDao.class.isAssignableFrom(beanDefinition.getBeanClass()))
				targetType = beanDefinition.getBeanClass();
			else
				if(targetType == null) throw new BeanCreationException("{} is not a valid GenericEntityDao bean.", element.getAttribute(ID_ATTRIBUTE));
			
			//override abstract method
			Method[] methods = beanDefinition.getBeanClass().getMethods();
			for (Method method : methods) {
				if (Modifier.isAbstract(method.getModifiers())) {
					beanDefinition.getMethodOverrides().addOverride(
							new ReplaceOverride(method.getName(), null));
				}
			}
		}else if(element.hasAttribute(INTERFACE_ATTRIBUTE)){
				try {
					Class<?> iClazz = ClassUtils.forName(element.getAttribute(INTERFACE_ATTRIBUTE), getClass().getClassLoader());				
					interfaces = new Class<?>[]{iClazz, ModelClassSupport.class};
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new BeanCreationException("Loxia bean definition error with name:" + element.getAttribute(ID_ATTRIBUTE));
				} catch (LinkageError e) {
					e.printStackTrace();
					throw new BeanCreationException("Loxia bean definition error with name:" + element.getAttribute(ID_ATTRIBUTE));
				}
		}else{
			throw new BeanCreationException("Loxia bean definition error with name:" + element.getAttribute(ID_ATTRIBUTE));
		}
		
		if(targetType == null){
			for (Class<?> targetInterface : interfaces) {
				logger.debug("Add Interface '{}' to '{}'.",targetInterface,element.getAttribute(ID_ATTRIBUTE));
				if (targetInterface == null) {
					throw new NullPointerException(
							"interfaces should not be null while class is not set");
				}
				if (GenericEntityDao.class.isAssignableFrom(targetInterface)
						&& targetInterface != GenericEntityDao.class) {
					targetType = getGenericModelClass(targetInterface);
					break;
				}
			}
			if(targetType == null) throw new BeanCreationException("{} is not a valid GenericEntityDao bean.", element.getAttribute(ID_ATTRIBUTE));
			
			beanDefinition.setBeanClass(GenericEntityDaoImpl.class);
			beanDefinition.getPropertyValues().addPropertyValue("modelClass", targetType);
		}
		
		AbstractBeanDefinition rootDefinition = new RootBeanDefinition(
				ProxyFactoryBean.class);
		rootDefinition.getPropertyValues().addPropertyValue("interfaces",
				interfaces);
		rootDefinition.getPropertyValues().addPropertyValue("target",
				beanDefinition);
		
		return rootDefinition;
	}
	
	private Class<?> getGenericModelClass(Class<?> iClazz){
		for(Type type: iClazz.getGenericInterfaces()){
			if(type instanceof ParameterizedType)
				return (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
		}
		return null;
	}
}
