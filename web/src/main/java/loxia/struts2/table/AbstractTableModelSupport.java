package loxia.struts2.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loxia.dao.Sort;
import loxia.support.json.JSONArray;
import loxia.support.json.JSONObject;

public abstract class AbstractTableModelSupport<T> implements TableModel {
	
	protected boolean pagable = false;
	protected int itemPerPage;
	protected int currentPage = 1;
	protected Sort[] sorts;
	
	protected String modelName;
	protected String[] columnNames;
	protected String[] columns;	

	public abstract long getCount();

	/**
	 * Model name, will be used in data export
	 * @return
	 */
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * Column names, will be used in data export
	 * @return
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String... columnNames) {
		this.columnNames = columnNames;
	}
	
	/**
	 * Column value's property string, will be used in data export
	 */
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String... columns) {
		this.columns = columns;
	}
	
	/**
	 * Query data for table model
	 * @param showAll query all data or only current page
	 * @return
	 */
	public abstract TableModel query(boolean showAll);	

	public abstract List<T> getItems();

	public JSONObject getModel() {
		return getModel(null);
	}
	
	public JSONObject getModel(String filterStr) {
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("sort", getSortString());
		result.put("page", isPagable());
		result.put("pageSize", getItemPerPage());
		result.put("currentPage", getCurrentPage());
		result.put("itemCount", getCount());
		if(filterStr == null || filterStr.trim().length() ==0)
			result.put("data", new JSONArray(getItems()));
		else
			result.put("data", new JSONArray(getItems(),filterStr));
		return new JSONObject(result);
	}

	public boolean isPagable() {
		return pagable;
	}
	public void setPagable(boolean pagable) {
		this.pagable = pagable;
	}
	public int getItemPerPage() {
		return itemPerPage;
	}
	public void setItemPerPage(int itemPerPage) {
		this.itemPerPage = itemPerPage;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public Sort[] getSorts() {
		return sorts;
	}
	public void setSorts(Sort[] sorts) {
		this.sorts = sorts;
	}
	public String getSortString(){
		if(sorts == null || sorts.length == 0) return "";
		StringBuffer sb = new StringBuffer();
		for(Sort sort: sorts)
			sb.append("," + sort.toString());
		return sb.toString().substring(1);
	}
	public void setSortString(String sortStr){
		if(sortStr == null || sortStr.trim().length() == 0) setSorts(null);
		else{
			List<Sort> sortList = new ArrayList<Sort>();
			for(String str: sortStr.split(","))
				sortList.add(new Sort(str));
			setSorts(sortList.toArray(new Sort[]{}));
		}
	}
}
