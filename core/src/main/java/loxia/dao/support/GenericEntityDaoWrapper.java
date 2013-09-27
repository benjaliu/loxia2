package loxia.dao.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import loxia.dao.DaoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameter;

public class GenericEntityDaoWrapper {
	protected static final Logger logger = LoggerFactory.getLogger(GenericEntityDaoImpl.class);

	protected DaoService daoService;
	
	public GenericEntityDaoWrapper(){}
	
	public GenericEntityDaoWrapper(DaoService daoService){this.daoService = daoService;}
	
	public Object save(Object model) {
		return daoService.save(model);
	}
	
	public void delete(Object model) {
		daoService.delete(model);
	}

	public void deleteAll(List<?> models) {
		for (Object model : models) {
			delete(model);
		}
	}
	
	public void deleteByPrimaryKey(Class<?> modelClass, Serializable id) {
		daoService.deleteByPrimaryKey(modelClass, id);
	}

	public void deleteAllByPrimaryKey(Class<?> modelClass, List<Serializable> ids) {
		for(Serializable id: ids)
			deleteByPrimaryKey(modelClass, id);
	}

	public Object getByPrimaryKey(Class<?> modelClass, Serializable id) {
		return daoService.getByPrimaryKey(modelClass, id);
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public int updateByNamedQuery(String queryName, Map<String, Object> params) {
		return daoService.batchUpdateByNamedQuery(queryName, params);
	}

	public int updateByQuery(String query, Map<String, Object> params) {
		return daoService.batchUpdateByQuery(query, params);
	}
	
	public int updateByNativeQuery(String query, Object[] params, Class<?>[] types){
		return daoService.batchUpdateByNativeQuery(query, params, types);
	}

	public void executeDDL(String ddl) {
		daoService.executeDDL(ddl);
	}

	public Map<String, Object> executeSP(String spName) {
		return daoService.executeSP(spName);
	}

	public Map<String, Object> executeSp(String spName,
			SqlParameter[] sqlParameters, Map<String, Object> params) {
		return daoService.executeSp(spName, sqlParameters, params);
	}
	
	public void flush(){
		daoService.flush();
	}
	
	public void evict(Object model){
		daoService.evict(model);
	}
}
