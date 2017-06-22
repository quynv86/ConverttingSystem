/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.yotel.commons.web.security;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 *
 */
public class MyPermissionEvaluator implements PermissionEvaluator {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        LOG.debug("target: {}, permission: {}", targetDomainObject, permission);
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        LOG.debug("target: {}, permission: {}", targetType, permission);
        return false;
    }
}
