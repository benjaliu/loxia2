package cn.benjamin.loxia.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

public class LoxiaBeanDefinition extends AbstractBeanDefinition{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1539645591780956525L;
	
	private AbstractBeanDefinition beanDefinition;
	
	public LoxiaBeanDefinition(AbstractBeanDefinition beanDefinition){
		super((BeanDefinition)beanDefinition);
		this.beanDefinition = beanDefinition;
	}

	@Override
	public AbstractBeanDefinition cloneBeanDefinition() {
		return beanDefinition.cloneBeanDefinition();
	}

	public String getParentName() {
		return null;
	}

	public void setParentName(String parentName) {
		//TODO
	}

}
