package loxia.dao.support;

import javax.annotation.Resource;

import loxia.dao.DynamicNamedQueryProvider;


import org.springframework.context.ApplicationContext;


public class SpringBeanDynamicNamedQueryProvider implements
		DynamicNamedQueryProvider {

	@Resource
	private ApplicationContext ac;
	
	public String getDynamicQueryByName(String queryName) {
		Object obj = ac.getBean(queryName);
		if(obj == null || !(obj instanceof DynamicQueryHolder))
			throw new IllegalArgumentException("Do not find DynamicQuery[" + queryName + "]");
		DynamicQueryHolder holder = (DynamicQueryHolder)obj;
		return holder.getDynamicQueryStr();
	}
}
