package loxia.dao.support;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import loxia.dao.DaoService;
import loxia.dao.GenericEntityDao;
import loxia.dao.ModelClassSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;

public class GenericEntityDaoImpl<T, PK extends Serializable> implements GenericEntityDao<T, PK>,
	ModelClassSupport{
	
	protected static final Logger logger = LoggerFactory.getLogger(GenericEntityDaoImpl.class);

	@Autowired
	protected DaoService daoService;
	
	private Class<T> modelClass;
	
	@PostConstruct
	public void init(){
		if(this.modelClass == null)
			this.modelClass = getGenericModelClass();
		logger.debug("Model Class {} is set successfully.", this.modelClass);
	}	
	
	public T save(T model) {
		return daoService.save(model);
	}
	
	public void delete(T model) {
		daoService.delete(model);
	}

	public void deleteAll(List<T> models) {
		for (T model : models) {
			delete(model);
		}
	}
	
	public void deleteByPrimaryKey(PK id) {
		daoService.deleteByPrimaryKey(modelClass, id);
	}

	public void deleteAllByPrimaryKey(List<PK> ids) {
		for(PK id: ids)
			deleteByPrimaryKey(id);
	}

	public T getByPrimaryKey(PK id) {
		return daoService.getByPrimaryKey(modelClass, id);
	}

	@SuppressWarnings("unchecked")
	private Class<T> getGenericModelClass(){
		Class<?> clazz = this.getClass();
		Type type = clazz.getGenericSuperclass();
		while(!(type instanceof ParameterizedType) && clazz != null && clazz != Object.class){
			clazz = clazz.getSuperclass();
			type = clazz.getGenericSuperclass();
		}
		
		if(!(type instanceof ParameterizedType)){
			Class<?>[] iclazzs = clazz.getInterfaces();
			if(iclazzs.length > 0){
				int index = -1;
				for(int i=0; i< iclazzs.length; i++){
					if(GenericEntityDao.class.isAssignableFrom(iclazzs[i])){
						index = i;
						break;
					}
				}
				if(index >=0){
					if(clazz.getGenericInterfaces()[index] instanceof ParameterizedType)
						type = clazz.getGenericInterfaces()[index];
				}
			}
						
		}
		
		if(!(type instanceof ParameterizedType)){
			throw new RuntimeException("Can not find the right Generic Class.");
		}
		
		ParameterizedType pType = (ParameterizedType)type;
		return (Class<T>)pType.getActualTypeArguments()[0];
	}

	public Class<T> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<T> modelClass) {
		this.modelClass = modelClass;
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
	
	public void evict(T model){
		
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}
