package vn.yotel.commons.bo.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.commons.dao.IGenericDao;

@Transactional
public abstract class GenericBoImpl<T extends Serializable, PK extends Serializable> implements GenericBo<T, PK> {

    @Override
    public abstract <E extends IGenericDao<T, PK>> E getDAO();

    @Override
    public T findOne(PK id) {
    	return this.getDAO().findOne(id);
    }
    
    @Override
    public List<T> findAll() {
    	return this.getDAO().findAll();
    }
    
    @Override
    public void create(T record) {
        this.getDAO().create(record);
    }

    @Override
    public T read(PK id) {
        return this.getDAO().read(id);
    }

    @Override
    public void update(T record) {
        this.getDAO().update(record);
    }

    @Override
    public void delete(T record) {
        this.getDAO().delete(record);
    }

    @Override
    public void deleteById(PK entityId) {
    	this.getDAO().deleteById(entityId);
    }
}
