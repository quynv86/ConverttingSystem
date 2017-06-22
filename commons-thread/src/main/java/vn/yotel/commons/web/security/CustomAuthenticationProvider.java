/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.yotel.commons.web.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.admin.bo.AuthUserBo;
import vn.yotel.admin.jpa.AuthUser;
import vn.yotel.admin.jpa.Role;
import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.util.PasswordUtil;

/**
 *
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private AuthUserBo authUserBo;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        try {
            authUserBo = (AuthUserBo) AppContext.getBean("authUserBo");
            AuthUser user = authUserBo.findByUsername(username);
            LOG.debug("User: {}", user);
            if (user != null) {
                String encrypedPass = PasswordUtil.encryptPassword(password, user.getSalt());
                if (encrypedPass.equals(user.getPassword())) {
                    List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
                    List<Role> lstRoles = user.getAuthRoles();
                    for (Role role : lstRoles) {
                        grantedAuths.add(new SimpleGrantedAuthority(role.getName()));
                    }
                    Authentication auth = new UsernamePasswordAuthenticationToken(user, "", grantedAuths);
                    return auth;
                } else {
                    throw new BadCredentialsException("Invalid username or password");
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
