package loxia.struts2.table;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import loxia.dao.Sort;
import loxia.support.json.JSONObject;

public class TableModelUtilsTest {

	@Test
	public void testOutputExcel() throws Exception {
		TableModelUtils.outputExcel(new FileOutputStream(new File("D:\\test.xls")), 
				new TableModel() {
					
					public void setSorts(Sort[] sorts) {}					
					public void setSortString(String sortStr) {}					
					public void setPagable(boolean pagable) {}
					public void setModelName(String modelName) {}
					public void setItemPerPage(int itemPerPage) {}
					public void setCurrentPage(int currentPage) {}
					public void setColumnNames(String... columnNames) {}
					public void setColumns(String... columns) {}
					
					public TableModel query() {return this;	}					
					public boolean isPagable() {return false;}					
					public Sort[] getSorts() {return null;}					
					public String getSortString() {return null;}
					
					public String getModelName() {return "Excel Table Output";}
					
					public JSONObject getModel(String filterStr) {return null;}					
					public JSONObject getModel() {return null;}					
					public List<? extends Object> getItems() {
						Object[] obj = new Object[]{"abcdefg",33,new Date()};
						return Arrays.asList(obj,obj,obj);
					}
					
					public int getItemPerPage() {return 0;}					
					public int getCurrentPage() {return 0;}					
					public long getCount() {return 0;}
					
					public String[] getColumnNames() {
						return new String[]{"String","Number","Date"};
					}
					public String[] getColumns() {
						return new String[]{"top[0]","top[1]","top[2]"};
					}
				});
	}
}
