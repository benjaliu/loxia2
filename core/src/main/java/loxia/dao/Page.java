package loxia.dao;

import java.io.Serializable;

public class Page implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6900835810451940829L;
	
	private int start;
	private int size = 0;
	
	public Page(){}
	
	public Page(int startPage, int size){
		this.start = (startPage - 1)*size;
		this.size = size;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getStartPage() {
		return size == 0? 0: (start/size) + 1;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
