package loxia.struts2.table;

import java.util.List;

import loxia.dao.Sort;
import loxia.support.json.JSONObject;

public interface TableModel {
	String getModelName();
	void setModelName(String modelName);
	String[] getColumnNames();
	void setColumnNames(String... columnNames);
	TableModel query();
	String[] getColumns();
	void setColumns(String... columns);
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
