package vn.yotel.admin.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.yotel.admin.bo.AuthUserBo;
import vn.yotel.admin.jpa.AuthUser;
import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.util.PasswordUtil;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping(value = { "/" })
public class AuthUserController {

    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    @RequestMapping(value = "/login.html", method = RequestMethod.GET)
    public String login(Locale locale, Model model, HttpServletRequest request,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {
        logger.info("Logging in ...");
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", referrer);
        if (error != null) {
            model.addAttribute("error", "Invalid username and password!");
        }
        if (logout != null) {
            model.addAttribute("msg", "You've been logged out successfully.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((auth != null) && (auth.getPrincipal() != null)) {
            return "redirect:/index.html";
        } else {
            return "login";
        }
    }

    @RequestMapping(value = "/login-error.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String loginError(Locale locale, Model model) {
        model.addAttribute("error", true);
        return "login";
    }
    
	@RequestMapping(value = "/user/change_password.html", method = RequestMethod.GET)
	public String changePassword(Locale locale, Model model) {
		logger.info("Going to changepassword-page");
		AuthUser principal = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("username", principal.getUserName());
		model.addAttribute("old_password","");
		model.addAttribute("new_password","");
		model.addAttribute("verify_password","");
		return "change_password";
	}
	
	@RequestMapping(value = "/user/change_password.html", method = RequestMethod.POST)
	public String changePassword(Locale locale, Model model,
			@RequestParam String username,
			@RequestParam String old_password,
			@RequestParam String new_password,
			@RequestParam String verify_password) {
		logger.info("Process changing password");
		try {
			AuthUserBo authUserBo = (AuthUserBo) AppContext.getBean("authUserBo");
			AuthUser _user = authUserBo.findByUsername(username);
			String encrypedPass = PasswordUtil.encryptPassword(old_password, _user.getSalt());
			if (!encrypedPass.equals(_user.getPassword()) || !new_password.equals(verify_password)) {
				model.addAttribute("username",username);
				model.addAttribute("old_password",old_password);
				model.addAttribute("new_password",new_password);
				model.addAttribute("verify_password",verify_password);
				return "change_password";
			} else {
				String newEncryptedPassword = PasswordUtil.encryptPassword(new_password, _user.getSalt());
				_user.setPassword(newEncryptedPassword);
				authUserBo.update(_user);
				return "redirect:/j_spring_security_logout";
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return "login";
	}
	
	@RequestMapping(value = "/403-auth.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String permPage(Locale locale, Model model) {
		logger.info("Going to 403 error page");
		return "403-auth";
	}
	
	@RequestMapping(value = "/400.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String errorPage(Locale locale, Model model) {
		logger.info("Going to 400 error page");
		return "400";
	}
	
	@RequestMapping(value = "/404.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String pageNotFound(Locale locale, Model model, HttpServletRequest request) {
		logger.info("Going to 404 error page");
		return "404";
	}
}
