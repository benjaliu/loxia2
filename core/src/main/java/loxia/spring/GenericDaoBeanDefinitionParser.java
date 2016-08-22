package loxia.spring;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

import loxia.aspect.SimpleModelClassSupport;
import loxia.dao.GenericEntityDao;
import loxia.dao.ModelClassSupport;

public class GenericDaoBeanDefinitionParser extends
		AbstractBeanDefinitionParser {
	
	public static final String INTERFACE_ATTRIBUTE = "interface";

	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {		
		AbstractBeanDefinition beanDefinition = parserContext.getDelegate()
			.parseBeanDefinitionElement(element, null, null);
		Class<?>[] interfaces = null;
		if(beanDefinition.getBeanClassName() != null){
			//with CLASS_ATTRIBUTE
			interfaces = beanDefinition.getBeanClass().getInterfaces();
			
			if(! GenericEntityDao.class.isAssignableFrom(beanDefinition.getBeanClass()))
				throw new BeanCreationException("{} is not a valid GenericEntityDao bean.", element.getAttribute(ID_ATTRIBUTE));
			
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
					
					if (GenericEntityDao.class.isAssignableFrom(iClazz)) {
						beanDefinition = new RootBeanDefinition(SimpleModelClassSupport.class);
						beanDefinition.getPropertyValues().addPropertyValue("modelClass",getGenericModelClass(iClazz));
					}else{
						throw new BeanCreationException("{} is not a valid GenericEntityDao Interface.", element.getAttribute(ID_ATTRIBUTE));
					}
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
		
		AbstractBeanDefinition rootDefinition = new GenericBeanDefinition();
		rootDefinition.setParentName("parentGenericDaoProxy");
		rootDefinition.getPropertyValues().addPropertyValue("proxyInterfaces",interfaces);
		rootDefinition.getPropertyValues().addPropertyValue("target",beanDefinition);		
		//rootDefinition.getPropertyValues().addPropertyValue("interceptorNames", new String[]{"queryInterceptor"});
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
