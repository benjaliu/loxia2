package cn.benjamin.loxia.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.benjamin.loxia.model.BaseModel;

public interface DaoService {
	<T extends BaseModel> T getByPrimaryKey(Class<T> clazz, Serializable pk);
	
	<T extends BaseModel> T save(T model);
	<T extends BaseModel> void delete(T model);
	<T extends BaseModel> void deleteByPrimaryKey(Class<T> clazz,Serializable pk);
	
	<T> T findOneByNamedQuery(String queryName, Map<String,Object> params);
	<T> T findOneByQuery(String queryString, Map<String,Object> params);
	
	/**
	 * Similiar with findOneByQuery, the difference is that the params can contain other definition do not used by query 
	 * @param <T>
	 * @param queryString
	 * @param params
	 * @return
	 */
	<T> T findOneByQueryEx(String queryString, Map<String,Object> params);
	
	<T> T findOneByNativeQuery(String queryString, Object[] params, String resultSetMapping);
	
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params);
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, int start, int pageSize);
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts);
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts, int start, int pageSize);
	
	<T> List<T> findByQuery(String queryString, Map<String,Object> params);
	<T> List<T> findByQuery(String queryString, Map<String,Object> params, int start, int pageSize);
	<T> List<T> findByQuery(String queryString, Map<String,Object> params, Sort[] sorts);
	<T> List<T> findByQuery(String queryString, Map<String,Object> params, Sort[] sorts, int start, int pageSize);
	
	/**
	 * Similiar with findByQuery, the difference is that the params can contain other definition do not used by query 
	 * @param <T>
	 * @param queryString
	 * @param params
	 * @param sorts
	 * @param start
	 * @param pageSize
	 * @return
	 */
	<T> List<T> findByQueryEx(String queryString, Map<String,Object> params, Sort[] sorts, int start, int pageSize);
	
	<T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping);
	<T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping, int start, int pageSize);
	<T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping, Sort[] sorts);
	<T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping, Sort[] sorts, int start, int pageSize);
	
	int batchUpdateByNamedQuery(String queryName, Map<String,Object> params);
	int batchUpdateByQuery(String queryString, Map<String,Object> params);
}
