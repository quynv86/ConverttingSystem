/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.yotel.admin.bo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.admin.bo.AuthUserBo;
import vn.yotel.admin.dao.AuthUserDao;
import vn.yotel.admin.jpa.AuthUser;
import vn.yotel.commons.bo.impl.GenericBoImpl;

/**
 *
 */
@Service(value = "authUserBo")
@Transactional
public class AuthUserBoImpl extends GenericBoImpl<AuthUser, Integer> implements AuthUserBo {
	
	@Autowired
	@Qualifier("authUserDaoImpl")
    private AuthUserDao authUserDao;

    public AuthUserBoImpl() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public AuthUserDao getDAO() {
        return this.authUserDao;
    }

    @Override
    public AuthUser findByUsername(String username) {
        AuthUser user = this.authUserDao.findByUsername(username);
        return user;
    }
}
