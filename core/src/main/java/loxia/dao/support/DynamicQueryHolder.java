package loxia.dao.support;

public class DynamicQueryHolder {
	private String dynamicQueryStr;

	public DynamicQueryHolder(String dynamicQueryStr) {
		this.dynamicQueryStr = dynamicQueryStr;
	}	
	
	public String getDynamicQueryStr() {
		return dynamicQueryStr;
	}

	public void setDynamicQueryStr(String dynamicQueryStr) {
		this.dynamicQueryStr = dynamicQueryStr;
	}

}
