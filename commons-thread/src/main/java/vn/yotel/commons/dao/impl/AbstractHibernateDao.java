package vn.yotel.commons.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDao<T extends Serializable, PK extends Serializable> {

	private Class<T> clazz;

	/*
	 * 		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		daoType = (Class) pt.getActualTypeArguments()[0];
	 * */
	

	@Autowired
	SessionFactory sessionFactory;

	public AbstractHibernateDao(){
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		clazz = (Class) pt.getActualTypeArguments()[0];
	}
	
	public final void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	@SuppressWarnings("unchecked")
	public T findOne(PK id) {
		return (T) getCurrentSession().get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return getCurrentSession().createQuery("from " + clazz.getName()).list();
	}

	public void create(T entity) {
		getCurrentSession().persist(entity);
	}
	
	@SuppressWarnings("unchecked")
	public T read(PK id) {
		return (T) getCurrentSession().get(clazz, id);
	}

	public T update(T entity) {
		getCurrentSession().merge(entity);
		return entity;
	}

	public void delete(T entity) {
		getCurrentSession().delete(entity);
	}

	public void deleteById(PK entityId) {
		T entity = findOne(entityId);
		delete(entity);
	}

	protected final Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
