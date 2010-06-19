package loxia.dao;

import java.util.List;

public class Pagination<T> {
	public Pagination(){}
	public Pagination(List<T> items, long count){
		this.items = items;
		this.count = count;
	}
	private List<T> items;
	private long count;
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
