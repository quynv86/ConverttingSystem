package vn.yotel.thread.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.yotel.admin.jpa.CDRColumn;

@RestController
@RequestMapping("/headerapi")
public class HeaderSettingApi {
	@RequestMapping(value="/{id}/add", method=RequestMethod.POST, headers="Accept=application/json")
	public String onAddColumns(List<CDRColumn> columns){
		for(CDRColumn item: columns){
			System.out.println("Add column: " + item.getId().intValue());
		}
		return "DONE";
	}
}
