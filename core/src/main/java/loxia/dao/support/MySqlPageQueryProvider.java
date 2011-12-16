package loxia.dao.support;

import loxia.dao.PageQueryProvider;

public class MySqlPageQueryProvider implements PageQueryProvider {

	public String getPagableQuery(String sql, int begin, int count) {
		return sql + " limit " + begin + " , " + count;
	}

}
