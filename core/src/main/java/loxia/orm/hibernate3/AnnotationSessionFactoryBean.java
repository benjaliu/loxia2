package loxia.orm.hibernate3;

import javax.annotation.Resource;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class AnnotationSessionFactoryBean extends org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean {

	@Resource
	private ApplicationContext ac;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			ConfigLocation config = ac.getBean(ConfigLocation.class);
			setPackagesToScan(config.getPackagesToScan());
			setMappingLocations(config.getMappingLocations());
		} catch (NoSuchBeanDefinitionException e) {
			//do nothing
		}
		super.afterPropertiesSet();
	}
	
	
}
