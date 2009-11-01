package cn.benjamin.loxia.dao;

import java.io.Serializable;

import cn.benjamin.loxia.model.BaseModel;

public interface DaoService {
	<T extends BaseModel> T getByPrimaryKey(Class<T> clazz, Serializable pk);
	
	<T extends BaseModel> T save(T model);
	<T extends BaseModel> void delete(T model);
	<T extends BaseModel> void deleteByPrimaryKey(Class<T> clazz,Serializable pk);
}
