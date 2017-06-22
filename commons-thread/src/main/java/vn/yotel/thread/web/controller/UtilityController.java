package vn.yotel.thread.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.util.RestMessage.RestMessageBuilder;
import vn.yotel.thread.ManageableThread;
import vn.yotel.thread.ThreadManager;
import vn.yotel.thread.domain.ThreadConfigs;

@Controller
@RequestMapping(value = { "/utility" })
public class UtilityController {
	
	private Logger LOG = LoggerFactory.getLogger(UtilityController.class);
	
	Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
	
	ThreadManager threadManager;
	
	public UtilityController() {
		threadManager = (ThreadManager) AppContext.getBean("threadManager");
	}
	
	@RequestMapping(value = { "/settings.html" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String showSettings(Model model) {
		return "utility/settings";
	}
	
	@RequestMapping(value = { "/reload_thead_config.html" }, method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String reloadThreadConfigs() {
//		try {
//			ThreadConfigs threadCfgs = threadManager.loadThreadConfigs();
//			threadManager.processThreadConfigs(threadCfgs);
//		} catch (IOException e) {
//			LOG.error("", e);
//			return gson.toJson(RestMessageBuilder.FAIL("0001", e.getMessage()));
//		}
		return gson.toJson(RestMessageBuilder.SUCCESS());
	}
	
	@PreAuthorize(value = "hasRole('ROLE_MANAGE_THREADS')")
	@RequestMapping(value = { "/stop_threads.html" }, method = {RequestMethod.GET, RequestMethod.POST})
	public String shutdownAllThreads() {
		threadManager.stopManager(); 
		return "redirect:/";
	}
	
	@PreAuthorize(value = "hasRole('ROLE_MANAGE_THREADS')")
	@RequestMapping(value = { "/start_threads.html" }, method = {RequestMethod.GET, RequestMethod.POST})
	public String startAllThreads() {
		threadManager.startManager();
        Collection<ManageableThread> collect = threadManager.getThreadList().values();
        List<ManageableThread> list = new ArrayList<ManageableThread>(collect);
        for (ManageableThread manageableThread : list) {
			threadManager.startThread(manageableThread.getId());
		}
		return "redirect:/";
	}
}
