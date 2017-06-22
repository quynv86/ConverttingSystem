package vn.yotel.commons.dao;

import java.io.Serializable;
import java.util.List;

public interface IGenericDao <T extends Serializable, PK extends Serializable> {

	void setClazz(Class<T> clazzToSet);
	
	T findOne(PK id);

	List<T> findAll();

	//CRUD
	void create(final T entity);

	T read(PK id);
	
	T update(final T entity);

	void delete(final T entity);

	void deleteById(PK entityId);
}