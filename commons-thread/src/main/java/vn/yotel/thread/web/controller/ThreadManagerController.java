package vn.yotel.thread.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.yotel.admin.bo.ThreadConfigBo;
import vn.yotel.admin.bo.ThreadLogBo;
import vn.yotel.admin.jpa.ThreadLog;
import vn.yotel.commons.context.AppContext;
import vn.yotel.thread.ManageableThread;
import vn.yotel.thread.ThreadManager;
import vn.yotel.thread.domain.ThreadConfig;
import vn.yotel.thread.domain.ThreadParams;

@Controller
@RequestMapping(value = { "/thread" })
public class ThreadManagerController {

	private final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("threadConfigLoader")
	ThreadConfigBo threadConfigService;

	@Autowired
	@Qualifier("threadLogBoImpl")
	ThreadLogBo threadLogLoader;
	
    @RequestMapping(value = {"/list.html"})
    public String listThreads(Model model) {
        ThreadManager threadManager = (ThreadManager) AppContext.getBean("threadManager");
        Collection<ManageableThread> collect = threadManager.getThreadList().values();
        List<ManageableThread> list = new ArrayList<ManageableThread>(collect);
		Collections.sort(list, new Comparator<ManageableThread>() {
			@Override
			public int compare(ManageableThread thread1, ManageableThread thread2) {
				return (thread1.getOrder() - thread2.getOrder());
			}
		});
        model.addAttribute("threads", list);
        model.addAttribute("haMode", "MASTER");
        return "thread/list";
    }
    
    @RequestMapping(value = {"/{id}/start.html"})
	public String startThread(Model model, @PathVariable(value = "id") String threadId) {
        ThreadManager threadManager = (ThreadManager) AppContext.getBean("threadManager");
        threadManager.startThread(threadId);
        return "redirect:/thread/list.html";
    }
    
    @RequestMapping(value = {"/{id}/stop.html"})
	public String stopThread(Model model, @PathVariable(value = "id") String threadId) {
        ThreadManager threadManager = (ThreadManager) AppContext.getBean("threadManager");
        threadManager.stopThread(threadId);
        return "redirect:/thread/list.html";
    }
    
    @RequestMapping(value = {"/{id}/settings.html"})
	public String settings(Model model, @PathVariable(value = "id") String threadId) {
    	model.addAttribute("threadName", "Demo Thread");
    	ThreadConfig threadConfig = threadConfigService.findOne(threadId);
    	model.addAttribute("threadParams",threadConfig);
        return "thread/settings";
    }

    @RequestMapping(value = "/edit_params", method = RequestMethod.POST)
    public String saveSettings(ThreadConfig threadParams){
    	threadConfigService.update(threadParams);
    	ThreadManager threadManager = (ThreadManager) AppContext.getBean("threadManager");
    	try {
			threadManager.reloadThreadParams(threadParams.getId());
		} catch (Exception e) {
			LOG.error("Error when reload thread params");
		}
    	System.out.println(threadParams.getName());
    	return "redirect:/thread/list.html";
    }
    
    @RequestMapping(value="/{id}/viewlog.html", method=RequestMethod.GET)
    public String loadLog(Model model, @PathVariable(value="id") String threadId){
    	ThreadConfig thread = threadConfigService.findOne(threadId);
    	
    	List<ThreadLog> logrecs = threadLogLoader.loadLog(threadId);
    	model.addAttribute("logrecs", logrecs);
    	model.addAttribute("thread", thread);
    	return "thread/viewlog";
    }
}
