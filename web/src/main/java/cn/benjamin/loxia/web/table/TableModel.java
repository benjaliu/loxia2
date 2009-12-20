package cn.benjamin.loxia.web.table;

import java.util.List;

import cn.benjamin.loxia.dao.Sort;
import cn.benjamin.loxia.support.json.JSONObject;

public interface TableModel {
	TableModel query();
	List<? extends Object> getItems();
	boolean isPagable();
	void setPagable(boolean pagable);
	long getCount();
	int getItemPerPage();
	void setItemPerPage(int itemPerPage);
	int getCurrentPage();
	void setCurrentPage(int currentPage);
	Sort[] getSorts();
	void setSorts(Sort[] sorts);
	String getSortString();
	void setSortString(String sortStr);	
	JSONObject getModel();
	JSONObject getModel(String filterStr);
}
