package vn.yotel.commons.dao.impl;

import java.io.Serializable;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import vn.yotel.commons.dao.IGenericDao;


@Repository
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class GenericHibernateDao< T extends Serializable, PK extends Serializable>  extends AbstractHibernateDao<T, PK> implements IGenericDao< T, PK >{
}