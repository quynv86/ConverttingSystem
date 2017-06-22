/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.yotel.admin.bo;

import vn.yotel.admin.jpa.AuthUser;
import vn.yotel.commons.bo.GenericBo;

/**
 *
 * @author SP01
 */
public interface AuthUserBo extends GenericBo<AuthUser, Integer> {

    AuthUser findByUsername(String username);
}
