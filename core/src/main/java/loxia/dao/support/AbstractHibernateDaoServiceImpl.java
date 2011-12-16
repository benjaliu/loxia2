package loxia.dao.support;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import loxia.core.utils.StringUtil;
import loxia.dao.DaoService;
import loxia.dao.PageQueryProvider;
import loxia.dao.Pagination;
import loxia.dao.Sort;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;


public abstract class AbstractHibernateDaoServiceImpl implements DaoService, InitializingBean,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4364940509322932338L;

	protected static final Logger logger = LoggerFactory.getLogger(DaoService.class);
	
	@Resource
	private ApplicationContext ac;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	protected DataSource dataSource;
	
	protected PageQueryProvider pageQueryProvider;	
	
	public void afterPropertiesSet() throws Exception {
		try{
			pageQueryProvider = ac.getBean(PageQueryProvider.class);
		}catch (NoSuchBeanDefinitionException e) {
			//do nothing
		}
		if(pageQueryProvider == null)
			logger.warn("PageQueryProvider is not set so sql page query will not effected.");
	}

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
        if(delim1 <0){
        	if(logger.isDebugEnabled()){
        		logger.debug("It seemed that current hql is not one valid one.");
        		logger.debug("HQL:{}",hql);
        	}
        	return null;
        }
        String fieldlist = hql.substring(7,delim1-1);
        int delim3 = fieldlist.indexOf(",");
        if(delim3 == -1) delim3 = fieldlist.length();
        String countOQL =  "select count(" + fieldlist.substring(0,delim3) + ") " + hql.substring(delim1,hql.length());
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
        	countSQL = "select count(1) as num from (" + sql.substring(0,delim2) + ") tmp_tbl";
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
	
	public <T> T findOneByNamedQuery(String queryName, Map<String,Object> params, Sort[] sorts) {
		List<T> list = findByNamedQueryNative(queryName, params, sorts, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	public <T> T findOneByQuery(String queryString, Map<String,Object> params, Sort[] sorts){
		List<T> list = findByQueryNative(queryString, params, sorts, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	//sql part		
	private <T> List<T> findByNativeQueryNative(String queryString, Object[] params, 
			Sort[] sorts, int start, int pageSize, RowMapper<T> rowMapper){
		if(sorts != null && sorts.length > 0){
			queryString += " order by " + StringUtil.join(sorts);
		}
		if(logger.isDebugEnabled()){
			logger.debug("NativeFind[{}]", queryString);
			logger.debug("parameters: {}", params == null? "null" : "[" + params.length + "]");			
			if(params != null){
				int index = 1;
				for(Object p: params){
					logger.debug("{}): {}", index++, p);
				}
			}
		}
		if(start < 0 || pageSize < 0)
			return jdbcTemplate.query(queryString, params, rowMapper);
		else
			return jdbcTemplate.query(pageQueryProvider == null? queryString : 
				pageQueryProvider.getPagableQuery(queryString, start, pageSize), 
				params,
				rowMapper);		
	}
	
	protected <T> Pagination<T> findByNativeQueryNative(String queryString, Object[] params,
			Sort[] sorts, int start, int pageSize, boolean withGroupby, RowMapper<T> rowMapper) {
		Pagination<T> p = new Pagination<T>();
		List<T> list = findByNativeQueryNative(queryString, params, sorts, start, pageSize, rowMapper);
		p.setItems(list);
		String countQueryString = getCountQueryStringForSql(queryString, withGroupby);		
		p.setCount((Integer)findByNativeQueryNative(countQueryString, params, null, -1, -1, 
				new RowMapper<Integer>() {
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				}).iterator().next());
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
	
	public <T> T findOneByNativeQuery(String queryString, Object[] params, RowMapper<T> rowMapper, Sort[] sorts) {
		List<T> list = findByNativeQueryNative(queryString, params, sorts, -1, -1, rowMapper);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	public int batchUpdateByNativeQuery(String queryString, Object[] params, Type[] types) {
		logger.debug("Batch Native Update[{}]",queryString);
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(queryString);
		if(params != null && params.length > 0){
			for(int i=0; i< params.length; i++){
				logger.debug("{}) : {} [{}]", new Object[]{i+1, params[i], types[i]});
				query.setParameter(i, params[i], types[i]);
			}
		}		
		return query.executeUpdate();
	}

	public void executeDDL(String queryString) {
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(queryString);
		query.executeUpdate();
	}	
	
	private class InnerStoreProcedure extends StoredProcedure {
    	public InnerStoreProcedure(String spName, SqlParameter[] parameters){
    		setSql(spName);
    		if(parameters != null)
	    		for(SqlParameter param: parameters)
	    			declareParameter(param);
    	}
    }
	
	private Map<String,Object> executeSpNative(String spName, SqlParameter[] sqlParameters, Map<String,Object> params){
    	StoredProcedure sp = new InnerStoreProcedure(spName, sqlParameters);
    	sp.setDataSource(dataSource);
    	sp.compile();
    	return sp.execute(params);
    }
    
    public Map<String,Object> executeSP(String spName) {
        return executeSpNative(spName, null, new HashMap<String, Object>());
    }   
    
    public Map<String,Object> executeSp(String spName, SqlParameter[] sqlParameters, Map<String,Object> params){
    	return executeSpNative(spName, sqlParameters, params);
    }	
}
