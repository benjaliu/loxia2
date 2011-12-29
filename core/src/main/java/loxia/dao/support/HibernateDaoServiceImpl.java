package loxia.dao.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import loxia.core.utils.StringUtil;
import loxia.dao.Pagination;
import loxia.dao.Sort;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.ForeignKeys;
import org.hibernate.engine.Status;
import org.hibernate.impl.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateDaoServiceImpl extends AbstractHibernateDaoServiceImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4979034935314898726L;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public <T> T save(T model) {
		Session session = getSession();
		if (EntityStatus.TRANSIENT == getStatus(model)) {
			session.persist(model);
			return model;
		} else {
			return (T) session.merge(model);
		}
	}
	
	public <T> void delete(T model) {
		Session session = getSession();
		session.delete(model);
	}

	public <T> void deleteByPrimaryKey(Class<T> clazz, Serializable pk) {
		T entity = getByPrimaryKey(clazz, pk);
		if(entity != null){
			Session session = getSession();
			session.delete(entity);
		}else throw new PersistenceException("The entity you want to delete is not existed.");
	}

	@SuppressWarnings("unchecked")
	public <T> T getByPrimaryKey(Class<T> clazz,
			Serializable pk) {
		Session session = getSession();
		return (T) session.get(clazz, pk);
	}
	
	protected Session getSession() {
		//return SessionFactoryUtils.getSession(sessionFactory, true);
		return sessionFactory.getCurrentSession();
	}
	
	private EntityStatus getStatus(Object model){
		SessionImpl simpl = (SessionImpl)getSession();		
		EntityEntry entry = simpl.getPersistenceContext().getEntry(model);
		if(entry != null){
			//Persistent Object
			logger.debug("current {} is one Entity with entry in PersistenceContext.", model);
			if (entry.getStatus() != Status.DELETED) {
				logger.debug("EntityStatus: {}", EntityStatus.PERSISTENT );
				return EntityStatus.PERSISTENT;
			} else {
				logger.debug("EntityStatus: {}", EntityStatus.REMOVED );
				return EntityStatus.REMOVED;
			}
		}else{
			//Detached or Transient Object
			logger.debug("current {} is one Entity without entry in PersistenceContext.", model);
			if (ForeignKeys.isTransient(null, model, null, simpl)) {
				logger.debug("EntityStatus: {}", EntityStatus.TRANSIENT );
				return EntityStatus.TRANSIENT;
			} else {
				logger.debug("EntityStatus: {}", EntityStatus.DETACHED );
				return EntityStatus.DETACHED;
			}
		}
	}
	
	public int batchUpdateByNamedQuery(String queryName, Map<String,Object> params) {
		Session session = getSession();
		Query query = session.getNamedQuery(queryName);
		logger.debug("Batch Update[{}]",query.getQueryString());
		if(params != null && !params.keySet().isEmpty()){
			logger.debug("Parameter List:");
			int i=1;
			for(String key: params.keySet()){
				logger.debug("{}) [{}] : {}", new Object[]{i++, key, params.get(key)});
				query.setParameter(key, params.get(key));
			}
		}
		return query.executeUpdate();
	}
	
	public int batchUpdateByQuery(String queryString, Map<String, Object> params) {
		Session session = getSession();
		Query query = session.createQuery(queryString);
		logger.debug("Batch Update[{}]", queryString);
		if(params != null && !params.keySet().isEmpty()){
			logger.debug("Parameter List:");
			int i=1;
			for(String key: params.keySet()){
				logger.debug("{}) [{}] : {}", new Object[]{i++, key, params.get(key)});
				query.setParameter(key, params.get(key));
			}
		}
		return query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> findByQueryNative(Query query, Map<String,Object> params, int start, int pageSize){
		if(params != null && !params.keySet().isEmpty()){
			logger.debug("Parameter List:");
			int i=1;
			for(String key: params.keySet()){
				logger.debug("{}) [{}] : {}", new Object[]{i++, key, params.get(key)});
				if(params.get(key) instanceof Collection<?>){
					query.setParameterList(key,(Collection<?>)params.get(key));
				}else
					query.setParameter(key, params.get(key));
			}
		}
		if(start > 0)
			query.setFirstResult(start);
		if(pageSize > 0)
			query.setMaxResults(pageSize);
		return query.list();
	}
	
	protected <T> List<T> findByNamedQueryNative(String queryName, Map<String,Object> params, Sort[] sorts,  int start, int pageSize){
		Session session = getSession();
		Query query = session.getNamedQuery(queryName);		
		if(sorts == null || sorts.length == 0){
			logger.debug("Find[{}]", query.getQueryString());
			return findByQueryNative(query, params, start, pageSize);
		}else{
			return findByQueryNative(query.getQueryString(), params, sorts, start, pageSize);
		}		
	}	
	
	protected <T> List<T> findByQueryNative(String query, Map<String,Object> params, Sort[] sorts, int start, int pageSize){		
		Session session = getSession();
		if(sorts != null && sorts.length > 0){
			query += " order by " + StringUtil.join(sorts);
		}
		logger.debug("Find[{}]", query);
		return findByQueryNative(session.createQuery(query), params, start, pageSize);
	}
	
	protected <T> Pagination<T> findByNamedQueryNative(String queryName, Map<String,Object> params, 
			Sort[] sorts, int start, int pageSize, boolean withGroupby){
		Session session = getSession();
		Query query = session.getNamedQuery(queryName);		
		String queryString = query.getQueryString();		
		if(sorts == null || sorts.length == 0){
			logger.debug("Find[{}]", queryString);
			Pagination<T> p = new Pagination<T>();
			List<T> list = findByQueryNative(query, params,  start, pageSize);
			p.setItems(list);	
			String countQueryString = getCountQueryStringForHql(queryString);
			if(withGroupby)
				p.setCount((long)findByQueryNative(session.createQuery(countQueryString), params, -1, -1).size());
			else
				p.setCount((Long)findByQueryNative(session.createQuery(countQueryString), params, -1, -1).iterator().next());
			return setPagination(p, start, pageSize, sorts);
		}else{
			return findByQueryNative(queryString, params, sorts, start, pageSize, withGroupby);
		}		
	}
	
	protected <T> Pagination<T> findByQueryNative(String query, Map<String,Object> params, 
			Sort[] sorts, int start, int pageSize, boolean withGroupby){
		Session session = getSession();
		Pagination<T> p = new Pagination<T>();
		List<T> list = findByQueryNative(query, params, sorts, start, pageSize);
		p.setItems(list);
		String countQueryString = getCountQueryStringForHql(query);
		if(withGroupby)
			p.setCount((long)findByQueryNative(session.createQuery(countQueryString), params, -1, -1).size());
		else
			p.setCount((Long)findByQueryNative(session.createQuery(countQueryString), params, -1, -1).iterator().next());
		return setPagination(p, start, pageSize, sorts);
	}	
	
	@SuppressWarnings("unchecked")
	private <T> List<T> findByQueryExNative(String queryString, Map<String, Object> params,
			Sort[] sorts, int start, int pageSize) {
		if(sorts != null && sorts.length > 0){
			queryString += " order by " + StringUtil.join(sorts);
		}
		logger.debug("Find[{}]", queryString);
		Session session = getSession();
		Query query = session.createQuery(queryString);		
		String [] paramNames = query.getNamedParameters();
		if(paramNames != null && paramNames.length >0){
			logger.debug("Parameter List:");
			int i=1;
			for(String key: paramNames){
				logger.debug("{}) [{}] : {}", new Object[]{i++, key, params.get(key)});
				query.setParameter(key, params.get(key));
			}
		}
		
		if(start > 0)
			query.setFirstResult(start);
		if(pageSize > 0)
			query.setMaxResults(pageSize);
		return (List<T>)query.list();
	}
	
	public <T> List<T> findByQueryEx(String queryString, Map<String, Object> params,
			Sort[] sorts, int start, int pageSize) {
		return findByQueryExNative(queryString, params, sorts, start, pageSize);
	}
	
	public <T> Pagination<T> findByQueryEx(String queryString, Map<String, Object> params,
			Sort[] sorts, int start, int pageSize, boolean withGroupby) {
		Pagination<T> p = new Pagination<T>();
		List<T> list = findByQueryExNative(queryString, params, sorts, start, pageSize);
		p.setItems(list);
		String countQueryString = getCountQueryStringForHql(queryString);
		Session session = getSession();
		Query query = session.createQuery(countQueryString);	
		Map<String,Object> paramsNew = new HashMap<String, Object>();		
		String [] paramNames = query.getNamedParameters();
		if(paramNames != null && paramNames.length >0){
			for(String key: paramNames){
				paramsNew.put(key, params.get(key));
			}
		}
		if(withGroupby)
			p.setCount((long)findByQueryNative(query, paramsNew, -1, -1).size());
		else
			p.setCount((Long)findByQueryNative(query, paramsNew, -1, -1).iterator().next());		
		return setPagination(p, start, pageSize, sorts);
	}
	
	public <T> T findOneByQueryEx(String queryString, Map<String,Object> params, Sort[] sorts){
		List<T> list = findByQueryExNative(queryString, params, sorts, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}		
	
	public void flush(){
		getSession().flush();
	}
	
	public <T> void evict(T model){
		if(model != null)
			getSession().evict(model);
		else
			getSession().clear();
	}
}
