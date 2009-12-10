package cn.benjamin.loxia.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.benjamin.loxia.model.BaseModel;

public interface GenericEntityDao<T extends BaseModel, PK extends Serializable> {
	@Transactional
	T save(T model);

	@Transactional
	void delete(T model);

	@Transactional
	void deleteAll(List<T> models);

	@Transactional
	void deleteByPrimaryKey(PK id);

	@Transactional
	void deleteAllByPrimaryKey(List<PK> ids);

	@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	T getByPrimaryKey(PK id);
	
	@Transactional
	int updateByNamedQuery(String queryName, Map<String,Object> params);
	
	@Transactional
	int updateByQuery(String query, Map<String,Object> params);
	
	@Transactional
	void executeDDL(String ddl);
}
