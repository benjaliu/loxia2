package loxia.orm.hibernate5;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class AnnotationSessionFactoryBean extends org.springframework.orm.hibernate5.LocalSessionFactoryBean {

	@Resource
	private ApplicationContext ac;

	@Override
	public void afterPropertiesSet() throws IOException {
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
