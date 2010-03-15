package cn.benjamin.loxia.dao;


public interface RowMapper<T> {
	T mapRow(Object[] result, int index);
}
