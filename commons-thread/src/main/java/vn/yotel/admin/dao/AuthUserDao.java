package vn.yotel.admin.dao;

import vn.yotel.admin.jpa.AuthUser;
import vn.yotel.commons.dao.IGenericDao;

public interface AuthUserDao extends IGenericDao<AuthUser, Integer> {

	AuthUser findByUsername(String username);

   

}
