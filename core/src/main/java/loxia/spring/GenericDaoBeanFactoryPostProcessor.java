package loxia.spring;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import loxia.dao.GenericEntityDao;
import loxia.dao.ModelClassSupport;
import loxia.dao.support.GenericEntityDaoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.util.ClassUtils;


public class GenericDaoBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(GenericDaoBeanFactoryPostProcessor.class);
	
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {			
			BeanDefinition beanDefinition = beanFactory
					.getBeanDefinition(beanDefinitionName);	
			if (beanDefinition instanceof LoxiaBeanDefinition) {
				logger.debug("Bean '{}' is one loxia dao bean.", beanDefinitionName);
				LoxiaBeanDefinition loxiaBeanDefinition = (LoxiaBeanDefinition) beanDefinition;
				try {
					Class<?> clazz = loxiaBeanDefinition.resolveBeanClass(ClassUtils.getDefaultClassLoader());
					
					if (ProxyFactoryBean.class.equals(clazz)) {
						Class<?>[] interfaces = new Class<?>[0];
						PropertyValue propertyValue = loxiaBeanDefinition
								.getPropertyValues().getPropertyValue("interfaces");
						if (propertyValue != null) {
							interfaces = (Class<?>[]) propertyValue.getValue();
						}

						Class<?> targetType = null;
						for (Class<?> targetInterface : interfaces) {
							logger.debug("Add Interface '{}' to '{}'.",targetInterface,beanDefinitionName);
							if (targetInterface == null) {
								throw new NullPointerException(
										"interfaces should not be null");
							}
							if (GenericEntityDao.class.isAssignableFrom(targetInterface)
									&& targetInterface != GenericEntityDao.class) {
								targetType = getGenericModelClass(targetInterface);
								AbstractBeanDefinition targetBeanDefinition = (AbstractBeanDefinition) loxiaBeanDefinition
										.getPropertyValues().getPropertyValue(
												"target").getValue();
								if (targetBeanDefinition.getBeanClassName() == null
										&& targetBeanDefinition.getParentName()
												.equals(GenericDaoBeanDefinitionParser.BASE_BEAN_NAME)) {
									targetBeanDefinition
											.setBeanClass(GenericEntityDaoImpl.class);	
									break;
								}
							}
						}

						if (!loxiaBeanDefinition.getPropertyValues()
								.contains("type")
								&& targetType != null) {
							AbstractBeanDefinition targetBeanDefinition = (AbstractBeanDefinition) loxiaBeanDefinition
									.getPropertyValues().getPropertyValue("target")
									.getValue();
							targetBeanDefinition.getPropertyValues()
									.addPropertyValue("modelClass", targetType);
							
							Class<?>[] newInterfaces = new Class<?>[interfaces.length+1];
							for(int i=0; i< interfaces.length; i++)
								newInterfaces[i] = interfaces[i];
							newInterfaces[interfaces.length] = ModelClassSupport.class;
							loxiaBeanDefinition.getPropertyValues().
								addPropertyValue("interfaces", newInterfaces);
						}
					}
				} catch (ClassNotFoundException e) {
					return;
				}
				
			}
		}
	}
	
	private Class<?> getGenericModelClass(Class<?> iClazz){
		for(Type type: iClazz.getGenericInterfaces()){
			if(type instanceof ParameterizedType)
				return (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
		}
		return null;
	}

}
