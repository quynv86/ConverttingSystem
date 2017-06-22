package vn.yotel.admin.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import vn.yotel.admin.dao.AuthUserDao;
import vn.yotel.admin.jpa.AuthUser;
import vn.yotel.commons.dao.impl.GenericHibernateDao;

@Repository("authUserDaoImpl")
public class AuthUserDaoImpl extends GenericHibernateDao<AuthUser, Integer> implements AuthUserDao {

    public AuthUserDaoImpl() {
        this(AuthUser.class);
    }

    public AuthUserDaoImpl(Class<AuthUser> type) {
        this.setClazz(AuthUser.class);
    }

    @Override
    public AuthUser findByUsername(String username) {
		List<?> result = getCurrentSession()
				.createQuery("SELECT user FROM AuthUser user WHERE user.userName = :username")
				.setParameter("username", username).list();
        if (result != null && result.size() > 0) {
        	AuthUser user = (AuthUser) result.get(0);
            Hibernate.initialize(user.getAuthRoles());
            Hibernate.initialize(user.getAuthUsermetas());
            return user;
        }
        return null;
    }


}
