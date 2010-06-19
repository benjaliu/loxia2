package loxia.dao.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import loxia.core.utils.StringUtil;
import loxia.dao.Pagination;
import loxia.dao.Sort;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.ejb.HibernateQuery;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.ForeignKeys;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.Status;


public class HibernateJpaDaoServiceImpl extends AbstractHibernateDaoServiceImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5388572435277626633L;

	@PersistenceContext
	private EntityManager entityManager;
	
	public <T> T save(T model) {
		if (EntityStatus.TRANSIENT == getStatus(model)) {
			entityManager.persist(model);
			return model;
		} else {
			return (T) entityManager.merge(model);
		}
	}
	
	public <T> void delete(T model) {
		entityManager.remove(model);
	}

	public <T> void deleteByPrimaryKey(Class<T> clazz, Serializable pk) {
		T entity = getByPrimaryKey(clazz, pk);
		if(entity != null)
			entityManager.remove(entity);
		else throw new PersistenceException("The entity you want to delete is not existed.");
	}

	public <T> T getByPrimaryKey(Class<T> clazz,
			Serializable pk) {
		return (T) entityManager.find(clazz, pk);
	}
	
	protected Session getSession() {
		if(!(entityManager instanceof HibernateEntityManager))
			throw new PersistenceException(
                    "Current entity manager is not an instance of HibernateEntityManager");
		// http://docs.jboss.org/hibernate/stable/entitymanager/api/org/hibernate/ejb/AbstractEntityManagerImpl.html#getDelegate()
		return (Session)entityManager.getDelegate();
	}
	
	private EntityStatus getStatus(Object model){
		SessionImplementor simpl = (SessionImplementor)getSession();
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
		Query query = entityManager.createNamedQuery(queryName);
		logger.debug("Batch Update[{}]",((HibernateQuery)query).getHibernateQuery().getQueryString());
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
		Query query = entityManager.createQuery(queryString);
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
				query.setParameter(key, params.get(key));
			}
		}
		if(start > 0)
			query.setFirstResult(start);
		if(pageSize > 0)
			query.setMaxResults(pageSize);
		return query.getResultList();
	}
	
	protected <T> List<T> findByNamedQueryNative(String queryName, Map<String,Object> params, Sort[] sorts,  int start, int pageSize){
		Query query = entityManager.createNamedQuery(queryName);				
		if(sorts == null || sorts.length == 0){
			logger.debug("Find[{}]",((HibernateQuery)query).getHibernateQuery().getQueryString());
			return findByQueryNative(query, params, start, pageSize);
		}else{
			return findByQueryNative(((HibernateQuery)query).getHibernateQuery().getQueryString(), params, sorts, start, pageSize);
		}		
	}	
	
	protected <T> List<T> findByQueryNative(String query, Map<String,Object> params, Sort[] sorts, int start, int pageSize){
		logger.debug("Find[{}]", query);
		if(sorts != null && sorts.length > 0){
			query += " order by " + StringUtil.join(sorts);
		}
		return findByQueryNative(entityManager.createQuery(query), params, start, pageSize);
	}	
	
	protected <T> Pagination<T> findByNamedQueryNative(String queryName, Map<String,Object> params, 
			Sort[] sorts, int start, int pageSize, boolean withGroupby){
		Query query = entityManager.createNamedQuery(queryName);
		String queryString = ((HibernateQuery)query).getHibernateQuery().getQueryString();		
		if(sorts == null || sorts.length == 0){
			logger.debug("Find[{}]", queryString);
			Pagination<T> p = new Pagination<T>();
			List<T> list = findByQueryNative(query, params,  start, pageSize);
			p.setItems(list);	
			String countQueryString = getCountQueryStringForHql(queryString);
			if(withGroupby)
				p.setCount((long)findByQueryNative(entityManager.createQuery(countQueryString), params, -1, -1).size());
			else
				p.setCount((Long)findByQueryNative(entityManager.createQuery(countQueryString), params, -1, -1).iterator().next());
			return p;
		}else{
			return findByQueryNative(queryString, params, sorts, start, pageSize, withGroupby);
		}		
	}
	
	protected <T> Pagination<T> findByQueryNative(String query, Map<String,Object> params, 
			Sort[] sorts, int start, int pageSize, boolean withGroupby){
		Pagination<T> p = new Pagination<T>();
		List<T> list = findByQueryNative(query, params, sorts, start, pageSize);
		p.setItems(list);
		String countQueryString = getCountQueryStringForHql(query);
		if(withGroupby)
			p.setCount((long)findByQueryNative(entityManager.createQuery(countQueryString), params, -1, -1).size());
		else
			p.setCount((Long)findByQueryNative(entityManager.createQuery(countQueryString), params, -1, -1).iterator().next());
		return p;
	}	
		
	@SuppressWarnings("unchecked")
	private <T> List<T> findByQueryExNative(String queryString, Map<String, Object> params,
			Sort[] sorts, int start, int pageSize) {
		if(sorts != null && sorts.length > 0){
			queryString += " order by " + StringUtil.join(sorts);
		}
		logger.debug("Find[{}]", queryString);
		Query query = entityManager.createQuery(queryString);
		HibernateQuery hQuery = (HibernateQuery) query;
		String [] paramNames = hQuery.getHibernateQuery().getNamedParameters();
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
		return (List<T>)query.getResultList();
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
		Query query = entityManager.createQuery(countQueryString);
		Map<String,Object> paramsNew = new HashMap<String, Object>();
		HibernateQuery hQuery = (HibernateQuery) query;
		String [] paramNames = hQuery.getHibernateQuery().getNamedParameters();
		if(paramNames != null && paramNames.length >0){
			for(String key: paramNames){
				paramsNew.put(key, params.get(key));
			}
		}
		if(withGroupby)
			p.setCount((long)findByQueryNative(query, paramsNew, -1, -1).size());
		else
			p.setCount((Long)findByQueryNative(query, paramsNew, -1, -1).iterator().next());
		return p;
	}
	
	public <T> T findOneByQueryEx(String queryString, Map<String,Object> params, Sort[] sorts){
		List<T> list = findByQueryExNative(queryString, params, sorts, -1, -1);
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
}
