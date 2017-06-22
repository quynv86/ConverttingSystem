package vn.yotel.report.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/report")
public class CDRReportController {
	
	@RequestMapping("/cdrreport.html")
	public String processCDRReport(){
		return "report/cdrreport";
	}
}
