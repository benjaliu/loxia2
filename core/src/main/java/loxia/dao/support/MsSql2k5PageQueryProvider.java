package loxia.dao.support;

import loxia.dao.PageQueryProvider;

public class MsSql2k5PageQueryProvider implements PageQueryProvider {

	public String getPagableQuery(String sql, int begin, int count) {
		sql = sql.trim();
		int delim = sql.toLowerCase().lastIndexOf("order by");
		if(delim <0) throw new RuntimeException("Pagable query should have order clause");
		
		int pdelim = sql.toLowerCase().indexOf("select");
		StringBuffer sb = new StringBuffer();
		sb.append("select top " + count + " p.* from (select ");
		sb.append("ROW_NUMBER() over(" + sql.substring(delim) + ") as rownum,");
		sb.append(sql.substring(pdelim+6,delim));
		sb.append(") p where rownum >= " + begin);
		return sb.toString();
	}

}
