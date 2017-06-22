package vn.yotel.commons.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import vn.yotel.admin.jpa.AuthUser;

public class SessionFilter  implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if ((auth != null) && (auth.getPrincipal() != null) && (auth.getPrincipal() instanceof AuthUser)) {
				AuthUser principal = (AuthUser) auth.getPrincipal();
				((HttpServletRequest) request).getSession().setAttribute("principal", principal.getUserName());
			} else {
				((HttpServletRequest) request).getSession().removeAttribute("principal");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
