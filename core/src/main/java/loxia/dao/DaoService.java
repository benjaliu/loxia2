package loxia.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;

public interface DaoService {
	<T> T getByPrimaryKey(Class<T> clazz, Serializable pk);
	
	<T> T save(T model);
	<T> void delete(T model);
	<T> void deleteByPrimaryKey(Class<T> clazz,Serializable pk);
	
	<T> T findOneByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts);
	<T> T findOneByQuery(String queryString, Map<String,Object> params, Sort[] sorts);
	
	/**
	 * Similiar with findOneByQuery, the difference is that the params can contain other definition do not used by query 
	 * @param <T>
	 * @param queryString
	 * @param params
	 * @return
	 */
	<T> T findOneByQueryEx(String queryString, Map<String,Object> params, Sort[] sorts);	
	
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params);
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, int start, int pageSize);
	<T> Pagination<T> findByNamedQuery(String queryName, Map<String,Object> params, int start, int pageSize, boolean withGroupby);
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts);
	<T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts, int start, int pageSize);
	<T> Pagination<T> findByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts, int start, int pageSize, boolean withGroupby);
	
	<T> List<T> findByQuery(String queryString, Map<String,Object> params);
	<T> List<T> findByQuery(String queryString, Map<String,Object> params, int start, int pageSize);
	<T> Pagination<T> findByQuery(String queryString, Map<String,Object> params, int start, int pageSize, boolean withGroupby);
	<T> List<T> findByQuery(String queryString, Map<String,Object> params, Sort[] sorts);
	<T> List<T> findByQuery(String queryString, Map<String,Object> params, Sort[] sorts, int start, int pageSize);
	<T> Pagination<T> findByQuery(String queryString, Map<String,Object> params, Sort[] sorts, int start, int pageSize, boolean withGroupby);
	
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
	<T> Pagination<T> findByQueryEx(String queryString, Map<String,Object> params, Sort[] sorts, int start, int pageSize, boolean withGroupby);
	
	int batchUpdateByNamedQuery(String queryName, Map<String,Object> params);
	int batchUpdateByQuery(String queryString, Map<String,Object> params);
		
	<T> List<T> findByNativeQuery(String queryString, Object[] params, RowMapper<T> rowMapper);
	<T> List<T> findByNativeQuery(String queryString, Object[] params, int start, int pageSize, RowMapper<T> rowMapper);
	<T> Pagination<T> findByNativeQuery(String queryString, Object[] params, int start, int pageSize, boolean withGroupby, RowMapper<T> rowMapper);
	<T> List<T> findByNativeQuery(String queryString, Object[] params, Sort[] sorts, RowMapper<T> rowMapper);
	<T> List<T> findByNativeQuery(String queryString, Object[] params, Sort[] sorts, int start, int pageSize, RowMapper<T> rowMapper);
	<T> Pagination<T> findByNativeQuery(String queryString, Object[] params, Sort[] sorts, int start, int pageSize, boolean withGroupby, RowMapper<T> rowMapper);
	
	<T> T findOneByNativeQuery(String queryString, Object[] params, RowMapper<T> rowMapper, Sort[] sorts);
		
	int batchUpdateByNativeQuery(String queryString, Object[] params);
	
	void executeDDL(String queryString);
	
	Map<String,Object> executeSP(String spName);
	Map<String,Object> executeSp(String spName, SqlParameter[] sqlParameters, Map<String,Object> params);
}
