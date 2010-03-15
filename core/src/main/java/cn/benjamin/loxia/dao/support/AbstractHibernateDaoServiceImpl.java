package cn.benjamin.loxia.dao.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.benjamin.loxia.dao.DaoService;
import cn.benjamin.loxia.dao.Pagination;
import cn.benjamin.loxia.dao.RowMapper;
import cn.benjamin.loxia.dao.Sort;
import cn.benjamin.loxia.utils.StringUtil;

public abstract class AbstractHibernateDaoServiceImpl implements DaoService,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4364940509322932338L;

	protected static final Logger logger = LoggerFactory.getLogger(DaoService.class);
	
	/**
	 * TRANSIENT(NEW)
	 * PERSISTENT(MANAGED)
	 * DETACHED
	 * REMOVED(DELETED)
	 * @author benjamin
	 *
	 */
	protected static enum EntityStatus {
		TRANSIENT,PERSISTENT,DETACHED,REMOVED
	}
	protected abstract Session getSession();
	
	protected abstract <T> List<T> findByNamedQueryNative(String queryName, Map<String,Object> params, Sort[] sorts, int start, int pageSize);
	protected abstract <T> List<T> findByQueryNative(String query, Map<String,Object> params, Sort[] sorts, int start, int pageSize);	
	
	protected String getCountQueryStringForHql(String hql){
		if(hql == null) return null;
		hql = hql.trim();
        String lowercaseOQL = hql.toLowerCase();
        int delim1 = lowercaseOQL.indexOf("from");
        int delim2 = lowercaseOQL.indexOf("order by");
        if(delim1 <0){
        	if(logger.isDebugEnabled()){
        		logger.debug("It seemed that current hql is not one valid one.");
        		logger.debug("HQL:{}",hql);
        	}
        	return null;
        }
        if (delim2 == -1) delim2 = hql.length();
        String fieldlist = hql.substring(7,delim1-1);
        int delim3 = fieldlist.indexOf(",");
        if(delim3 == -1) delim3 = fieldlist.length();
        String countOQL =  "select count(" + fieldlist.substring(0,delim3) + ") " + hql.substring(delim1,delim2);
        logger.debug("Count OQL:" +  countOQL);
        return countOQL;
	}
	
	protected String getCountQueryStringForSql(String sql, boolean withGroupby){
		if(sql == null) return null;
		sql = sql.trim();
        String lowercaseSQL = sql.toLowerCase();
        int delim1 = lowercaseSQL.indexOf("from");
        int delim2 = lowercaseSQL.indexOf("order by");
        if(delim1 <0){
        	if(logger.isDebugEnabled()){
        		logger.debug("It seemed that current sql is not one valid one.");
        		logger.debug("SQL:{}", sql);
        	}
        	return null;
        }
        if (delim2 == -1) delim2 = sql.length();
        String countSQL = "";
        if(withGroupby){
        	countSQL = "select count(1) from (" + sql.substring(0,delim2) + ")";
        }else{
        	countSQL =  "select count(1) as num " + sql.substring(delim1,delim2);
        }
        logger.debug("Count SQL:{}", countSQL);
        return countSQL;
	}
		
	protected abstract <T> Pagination<T> findByNamedQueryNative(String queryName, Map<String,Object> params, Sort[] sorts, int start, int pageSize, boolean withGroupby);
	protected abstract <T> Pagination<T> findByQueryNative(String query, Map<String,Object> params, Sort[] sorts, int start, int pageSize, boolean withGroupby);	
	
	public <T> List<T> findByNamedQuery(String queryName, Map<String,Object> params) {
		return findByNamedQueryNative(queryName, params, null, -1, -1);
	}
	
	public <T> List<T> findByNamedQuery(String queryName,
			Map<String, Object> params, Sort[] sorts) {
		return findByNamedQueryNative(queryName, params, sorts, -1, -1);
	}
	
	public <T> List<T> findByNamedQuery(String queryName, Map<String,Object> params, int start, int pageSize) {
		return findByNamedQueryNative(queryName, params, null, start, pageSize);
	}
	
	public <T> List<T> findByNamedQuery(String queryName,
			Map<String, Object> params, Sort[] sorts, int start, int pageSize) {
		return findByNamedQueryNative(queryName, params, sorts, start, pageSize);
	}
	
	public <T> Pagination<T> findByNamedQuery(String queryName, Map<String,Object> params, int start, int pageSize, boolean withGroupby) {
		return findByNamedQueryNative(queryName, params, null, start, pageSize, withGroupby);
	}
	
	public <T> Pagination<T> findByNamedQuery(String queryName,
			Map<String, Object> params, Sort[] sorts, int start, int pageSize, boolean withGroupby) {
		return findByNamedQueryNative(queryName, params, sorts, start, pageSize, withGroupby);
	}
	
	public <T> List<T> findByQuery(String queryString, Map<String, Object> params) {
		return findByQueryNative(queryString, params, null, -1, -1);
	}
	
	public <T> List<T> findByQuery(String queryString, Map<String, Object> params,
			Sort[] sorts) {
		return findByQueryNative(queryString, params, sorts, -1, -1);
	}
	
	public <T> List<T> findByQuery(String queryString, Map<String, Object> params, int start, int pageSize) {
		return findByQueryNative(queryString, params, null, start, pageSize);
	}
	
	public <T> List<T> findByQuery(String queryString, Map<String, Object> params,
			Sort[] sorts, int start, int pageSize) {		
		return findByQueryNative(queryString, params, sorts, start, pageSize);
	}
	
	public <T> Pagination<T> findByQuery(String queryString, Map<String, Object> params, int start, int pageSize, boolean withGroupby) {
		return findByQueryNative(queryString, params, null, start, pageSize, withGroupby);
	}
	
	public <T> Pagination<T> findByQuery(String queryString, Map<String, Object> params,
			Sort[] sorts, int start, int pageSize, boolean withGroupby) {
		return findByQueryNative(queryString, params, sorts, start, pageSize, withGroupby);
	}
	
	public <T> T findOneByNamedQuery(String queryName, Map<String,Object> params) {
		List<T> list = findByNamedQueryNative(queryName, params, null, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	public <T> T findOneByQuery(String queryString, Map<String,Object> params){
		List<T> list = findByQueryNative(queryString, params, null, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	//sql part
	@SuppressWarnings("unchecked")
	protected <T> List<T> findByNativeQueryNative(String queryString, Object[] params, String resultSetMapping,
			Sort[] sorts, int start, int pageSize) {
		if(sorts != null && sorts.length > 0){
			queryString += " order by " + StringUtil.join(sorts);
		}
		logger.debug("NativeFind[{}]", queryString);
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(queryString);
		query.setResultSetMapping(resultSetMapping);
		if(params != null && params.length > 0){
			for(int i=0; i< params.length; i++){
				logger.debug("{}) : {}", i+1, params[i]);
				query.setParameter(i, params[i]);
			}
		}
		if(start > 0)
			query.setFirstResult(start);
		if(pageSize > 0)
			query.setMaxResults(pageSize);
		return (List<T>)query.list();
	}	
	
	protected <T> Pagination<T> findByNativeQueryNative(String queryString, Object[] params, String resultSetMapping,
			Sort[] sorts, int start, int pageSize, boolean withGroupby) {
		Pagination<T> p = new Pagination<T>();
		List<T> list = findByNativeQueryNative(queryString, params, resultSetMapping, sorts, start, pageSize);
		p.setItems(list);
		String countQueryString = getCountQueryStringForSql(queryString, withGroupby);		
		p.setCount((Integer)findByNativeQueryNative(countQueryString, params, "sqlcount", null, -1, -1).iterator().next());
		return p;
	}	
	
	public <T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping) {
		return findByNativeQueryNative(queryString, params, resultSetMapping, null, -1, -1);
	}

	public <T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping,
			int start, int pageSize) {
		return findByNativeQueryNative(queryString, params, resultSetMapping, null, start, pageSize);
	}

	public <T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping,
			Sort[] sorts) {
		return findByNativeQueryNative(queryString, params, resultSetMapping, sorts, -1, -1);
	}
	
	public <T> List<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping,
			Sort[] sorts, int start, int pageSize) {
		return findByNativeQueryNative(queryString, params, resultSetMapping, sorts, start, pageSize);
	}
	
	public <T> Pagination<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping,
			int start, int pageSize, boolean withGroupby) {
		return findByNativeQueryNative(queryString, params, resultSetMapping, null, start, pageSize, withGroupby);
	}
	
	public <T> Pagination<T> findByNativeQuery(String queryString, Object[] params, String resultSetMapping,
			Sort[] sorts, int start, int pageSize, boolean withGroupby) {
		return findByNativeQueryNative(queryString, params, resultSetMapping, sorts, start, pageSize, withGroupby);
	}
	
	public <T> T findOneByNativeQuery(String queryString, Object[] params, String resultSetMapping) {
		List<T> list = findByNativeQueryNative(queryString, params, resultSetMapping, null, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> findByNativeQueryNative(String queryString, Object[] params, 
			Sort[] sorts, int start, int pageSize, RowMapper<T> rowMapper){
		if(sorts != null && sorts.length > 0){
			queryString += " order by " + StringUtil.join(sorts);
		}
		logger.debug("NativeFind[{}]", queryString);
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(queryString);
		if(params != null && params.length > 0){
			for(int i=0; i< params.length; i++){
				logger.debug("{}) : {}", i+1, params[i]);
				query.setParameter(i, params[i]);
			}
		}
		if(start > 0)
			query.setFirstResult(start);
		if(pageSize > 0)
			query.setMaxResults(pageSize);
		List<Object[]> list = query.list();
		List<T> result = new ArrayList<T>();
		int i = 0;
		for(Object[] objs: list){
			result.add(rowMapper.mapRow(objs, i++));
		}
		return result;
	}
	
	protected <T> Pagination<T> findByNativeQueryNative(String queryString, Object[] params,
			Sort[] sorts, int start, int pageSize, boolean withGroupby, RowMapper<T> rowMapper) {
		Pagination<T> p = new Pagination<T>();
		List<T> list = findByNativeQueryNative(queryString, params, sorts, start, pageSize, rowMapper);
		p.setItems(list);
		String countQueryString = getCountQueryStringForSql(queryString, withGroupby);		
		p.setCount((Integer)findByNativeQueryNative(countQueryString, params, "sqlcount", null, -1, -1).iterator().next());
		return p;
	}	
	
	public <T> List<T> findByNativeQuery(String queryString, Object[] params, RowMapper<T> rowMapper) {
		return findByNativeQueryNative(queryString, params, null, -1, -1, rowMapper);
	}

	public <T> List<T> findByNativeQuery(String queryString, Object[] params,
			int start, int pageSize, RowMapper<T> rowMapper) {
		return findByNativeQueryNative(queryString, params, null, start, pageSize, rowMapper);
	}

	public <T> List<T> findByNativeQuery(String queryString, Object[] params,
			Sort[] sorts, RowMapper<T> rowMapper) {
		return findByNativeQueryNative(queryString, params, sorts, -1, -1, rowMapper);
	}
	
	public <T> List<T> findByNativeQuery(String queryString, Object[] params,
			Sort[] sorts, int start, int pageSize, RowMapper<T> rowMapper) {
		return findByNativeQueryNative(queryString, params, sorts, start, pageSize, rowMapper);
	}
	
	public <T> Pagination<T> findByNativeQuery(String queryString, Object[] params,
			int start, int pageSize, boolean withGroupby, RowMapper<T> rowMapper) {
		return findByNativeQueryNative(queryString, params, null, start, pageSize, withGroupby, rowMapper);
	}
	
	public <T> Pagination<T> findByNativeQuery(String queryString, Object[] params,
			Sort[] sorts, int start, int pageSize, boolean withGroupby, RowMapper<T> rowMapper) {
		return findByNativeQueryNative(queryString, params, sorts, start, pageSize, withGroupby, rowMapper);
	}
	
	public <T> T findOneByNativeQuery(String queryString, Object[] params, RowMapper<T> rowMapper) {
		List<T> list = findByNativeQueryNative(queryString, params, null, -1, -1, rowMapper);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	public int batchUpdateByNativeQuery(String queryString, Object[] params) {
		logger.debug("Batch Native Update[{}]",queryString);
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(queryString);
		if(params != null && params.length > 0){
			for(int i=0; i< params.length; i++){
				logger.debug("{}) : {}", i+1, params[i]);
				query.setParameter(i+1, params[i]);
			}
		}		
		return query.executeUpdate();
	}

	public void executeDDL(String queryString) {
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(queryString);
		query.executeUpdate();
	}	
}
