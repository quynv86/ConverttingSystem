package vn.yotel.commons.bo;

import java.io.Serializable;
import java.util.List;

import vn.yotel.commons.dao.IGenericDao;


public interface GenericBo<T extends Serializable, PK extends Serializable> {

	public <E extends IGenericDao<T, PK>> E getDAO();

	T findOne(PK id);
	
	List<T> findAll();
    /**
     * CRUD
     */
    public abstract void create(T record);
    
    public abstract T read(PK id);

    public abstract void update(T record);

    public abstract void delete(T record);
    
    void deleteById(PK entityId);

}
