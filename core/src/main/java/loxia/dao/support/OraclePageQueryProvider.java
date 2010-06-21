package loxia.dao.support;

import loxia.dao.PageQueryProvider;

public class OraclePageQueryProvider implements PageQueryProvider {

	public String getPagableQuery(String sql, int begin, int count) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (select pagequery.*, rownum rowno from(");
		sb.append(sql);
		sb.append(") pagequery where rownum <" + (begin + count));
		sb.append(") where rowno >= " + begin);
		return sb.toString();
	}

}
