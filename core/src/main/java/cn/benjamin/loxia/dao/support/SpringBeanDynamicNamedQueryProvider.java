package cn.benjamin.loxia.dao.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.benjamin.loxia.dao.DynamicNamedQueryProvider;

public class SpringBeanDynamicNamedQueryProvider implements
		DynamicNamedQueryProvider, ApplicationContextAware {

	private ApplicationContext ac;
	
	public String getDynamicQueryByName(String queryName) {
		Object obj = ac.getBean(queryName);
		if(obj == null || !(obj instanceof DynamicQueryHolder))
			throw new IllegalArgumentException("Do not find DynamicQuery[" + queryName + "]");
		DynamicQueryHolder holder = (DynamicQueryHolder)obj;
		return holder.getDynamicQueryStr();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ac = applicationContext;
	}

}
