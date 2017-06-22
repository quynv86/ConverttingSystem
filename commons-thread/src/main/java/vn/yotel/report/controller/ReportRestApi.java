package vn.yotel.report.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.yotel.admin.bo.CDRLogBo;
import vn.yotel.admin.jpa.CDRLog;

@RestController
@RequestMapping("/reportdata")
public class ReportRestApi {
	@Autowired
	@Qualifier("CDRLogBoImpl")
	private CDRLogBo cdrLogLoader;
	
	@RequestMapping(value="/cdrlog", method=RequestMethod.GET)
	public List<CDRLog> loadCdrLog(){
		return cdrLogLoader.findAll();
	}
	
}
