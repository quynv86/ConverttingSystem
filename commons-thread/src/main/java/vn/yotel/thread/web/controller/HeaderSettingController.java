package vn.yotel.thread.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.yotel.admin.bo.HeaderConfigBo;
import vn.yotel.admin.jpa.CDRColumn;
import vn.yotel.admin.jpa.HeaderConfig;

@Controller
@RequestMapping("/header-settings")
public class HeaderSettingController {

	@Autowired
	@Qualifier("headerConfigBoImpl")
	private HeaderConfigBo headerConfigBo;
	
	@RequestMapping("/{threadId}/view")
	public String showSettings(Model model, @PathVariable(value="threadId") Integer threadId){
		HeaderConfig oneItem = headerConfigBo.findByThread(threadId);
		model.addAttribute("headerConfig",oneItem);
		return "thread/header-setting";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateExportColumn(@ModelAttribute HeaderConfig headerConf){
		List<String> selectedAdd = headerConf.getSelectedAdd();
		List<String> selectedRemove = headerConf.getSelectedRemove();
		headerConfigBo.updateHeader(headerConf.getId(), selectedAdd, selectedRemove);
		return "redirect:/header-settings/"+headerConf.getId().intValue()+"/view";
	}
}
