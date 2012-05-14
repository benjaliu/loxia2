package loxia.dao.support;

import java.util.HashMap;
import java.util.Map;

import loxia.dao.DynamicNamedQueryProvider;


public class MappedDynamicNamedQueryProvider implements
		DynamicNamedQueryProvider {

	protected Map<String, String> queryMap = new HashMap<String, String>();
	
	public String getDynamicQueryByName(String queryName) {
		String query = queryMap.get(queryName);
		if(query == null)
			throw new IllegalArgumentException("Do not find DynamicQuery[" + queryName + "]");
		return query;
	}

	public void setQueryMap(Map<String, String> queryMap) {
		this.queryMap = queryMap;
	}
}
