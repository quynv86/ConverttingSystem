package vn.yotel.admin.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "", "/" })
public class MainUIController {

    @RequestMapping(value = {""})
    public String mainUI() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((auth != null) && (auth.getPrincipal() != null)) {
            return "redirect:/index.html";
        } else {
            return "login";
        }
    }
    
    @RequestMapping(value = {"index.html"})
    public String index() {
    	return "redirect:/thread/list.html";
    }
}
