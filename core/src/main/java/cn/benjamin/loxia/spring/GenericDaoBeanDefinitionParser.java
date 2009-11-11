package cn.benjamin.loxia.spring;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

public class GenericDaoBeanDefinitionParser extends
		AbstractBeanDefinitionParser {
	
	public static final String BASE_BEAN_NAME = "genericDaoBeanDefinitionParser_baseBean";
	public static final AbstractBeanDefinition BASE_BEAN = new RootBeanDefinition(
			Object.class);
	
	public static final String INTERFACE_ATTRIBUTE = "interface";

	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {		
		if(!element.hasAttribute(BeanDefinitionParserDelegate.PARENT_ATTRIBUTE)){
			if(!parserContext.getRegistry().containsBeanDefinition(BASE_BEAN_NAME)){
				parserContext.getRegistry().registerBeanDefinition(BASE_BEAN_NAME, BASE_BEAN);
			}
			element.setAttribute(BeanDefinitionParserDelegate.PARENT_ATTRIBUTE, BASE_BEAN_NAME);
		}
		
		AbstractBeanDefinition beanDefinition = parserContext.getDelegate()
			.parseBeanDefinitionElement(element, null, null);
		
		Class<?>[] interfaces = null;
		if(element.hasAttribute(BeanDefinitionParserDelegate.CLASS_ATTRIBUTE)){
			try {
				Class<?> clazz = beanDefinition.resolveBeanClass(ClassUtils.getDefaultClassLoader());
				interfaces = clazz.getInterfaces();
				
				//override abstract method
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (Modifier.isAbstract(method.getModifiers())) {
						beanDefinition.getMethodOverrides().addOverride(
								new ReplaceOverride(method.getName(), null));
					}
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(beanDefinition.getBeanClassName() + " is not found");
			}
		}else{
			try {
				Class<?> iClazz = ClassUtils.forName(element.getAttribute(INTERFACE_ATTRIBUTE));				
				interfaces = new Class<?>[]{iClazz};
			} catch (Exception e) {
				throw new IllegalArgumentException(element.getAttribute(INTERFACE_ATTRIBUTE) + " is not found");
			} 
		}

		if (interfaces == null) {
			return new LoxiaBeanDefinition(beanDefinition);
		}

		AbstractBeanDefinition rootDefinition = new RootBeanDefinition(
				ProxyFactoryBean.class);
		rootDefinition.getPropertyValues().addPropertyValue("interfaces",
				interfaces);
		rootDefinition.getPropertyValues().addPropertyValue("target",
				beanDefinition);

		return new LoxiaBeanDefinition(rootDefinition);
	}

}
