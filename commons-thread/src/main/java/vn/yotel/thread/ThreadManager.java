package vn.yotel.thread;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import vn.yotel.admin.bo.ThreadConfigBo;
import vn.yotel.thread.domain.ThreadConfig;
import vn.yotel.thread.domain.ThreadConfigs;
import vn.yotel.thread.log.ThreadLogger;

public class ThreadManager implements Runnable {
	
	private static Logger LOG = LoggerFactory.getLogger(ThreadManager.class);

	private Map<String, ManageableThread> threadList = new ConcurrentHashMap<String, ManageableThread>();
	//Map to store future to control the scheduled thread.
	private boolean runningStatus = false;
	private boolean requireStop = false;

	@Autowired
	@Qualifier("DBLogger")
	private ThreadLogger threadLogger;
	
	@Autowired
	private ApplicationContext appCtx;

	@Autowired
	@Qualifier("threadConfigLoader")
	private ThreadConfigBo threadConfigLoader;
	
	public ThreadManager(ApplicationContext appCtx, ThreadConfigBo threadConfigLoader) {
		super();
		this.appCtx = appCtx;
		this.threadConfigLoader = threadConfigLoader;

	}
	public ThreadManager(){
		super();
	}
	public void startManager() {
		this.requireStop = false;
		if (!runningStatus) {
			Executors.newFixedThreadPool(1).submit(this);
		} else {
			LOG.warn("ThreadManager is already running ...");
		}
	}
	
	public void stopManager() {
		LOG.info("Require to stop ThreadManager");
		this.requireStop = true;
		Collection<ManageableThread> threads = threadList.values();
		for (ManageableThread manageableThread : threads) {
			manageableThread.stopJob();
		}
	}
	
	public Map<String, ManageableThread> getThreadList() {
		return threadList;
	}

	public void setThreadList(Map<String, ManageableThread> threadList) {
		this.threadList = threadList;
	} 

	public ManageableThread getThread(String threadId) {
		ManageableThread thread = threadList.get(threadId);
		return thread;
	}
	
	public String stopThread(String threadId) {
		String message = "";
		ManageableThread thread = threadList.get(threadId);
		if (thread != null) {
			if (thread.isRunning()) {
				thread.stopJob();				
			} else {
				message = "Thread is not running yet.";
			}
		} else {
			message = "Could not find the thread";
		}
		return message;
	}
	
	public String startThread(String threadId) {
		String message = "";
		ManageableThread thread = threadList.get(threadId);
		if (thread != null) {
			if (!thread.isRunning()) {
				thread.startJob();				
			} else {
				message = "Thread is already running.";
			}
		} else {
			message = "Could not find the thread";
		}
		return message;
	}

	@Override
	public void run() {
		LOG.debug("Starting ThreadManager");
		runningStatus = true;
		while (!requireStop) {
			try {
				synchronized(this){
					LOG.debug("Loading thread config .....");
					ThreadConfigs threadCfgs = loadThreadConfigs();
					LOG.debug("Total loaded thread(s) : {} . ", threadCfgs.getThreads().size());
					LOG.debug("Init thread .....");
					processThreadConfigs(threadCfgs);
					LOG.debug("Threads are loaded...");
				}
			} catch (Exception e) {
				LOG.error("ThreadManager.run", e);
			} finally {
				try {
					for (int i = 0; i < 60 * 10 && !requireStop; i++) {
						Thread.sleep(100l);
					}
				} catch (InterruptedException e) {
				}
			}
		}
		runningStatus = false;
	}

	public void processThreadConfigs(ThreadConfigs threadCfgs) {
		List<ThreadConfig> threads = null;
		if ((threadCfgs != null) && ((threads = threadCfgs.getThreads()) != null)) {
			for (ThreadConfig threadConfig : threads) {
				ManageableThread thread = threadList.get(threadConfig.getId());
				if(thread == null) {
					try {
						thread = (ManageableThread) Class.forName(threadConfig.getClassName()).newInstance();
						thread.setId(threadConfig.getId());
						thread.setName(threadConfig.getName());
						thread.setStatus(false);
						thread.setDelayTime(threadConfig.getDelayTime());
						thread.setOrder(threadConfig.getOrder());
						thread.setParams(threadConfig.getParams());
						thread.setManager(this);
						thread.setApplicationContext(appCtx);
						this.getThreadList().put(thread.getId(), thread);
						if (threadConfig.isAutoStart()) {
							this.startThread(thread.getId());
						}
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						LOG.error("", e);
					}
				} else {
					thread.setName(threadConfig.getName());
					thread.setDelayTime(threadConfig.getDelayTime());
					thread.setParams(threadConfig.getParams());
					this.getThreadList().put(thread.getId(), thread);
				}
			}
		}
	}

	public synchronized ThreadConfigs loadThreadConfigs(){
		return threadConfigLoader.loadThreadConfigs();
	}
	public void reloadThreadParams(String threadId) throws Exception{
		synchronized(this){
			ThreadConfig threadConfig = threadConfigLoader.findOne(threadId);
			ManageableThread thread = threadList.get(threadConfig.getId());
			if(thread!=null){
				thread.setName(threadConfig.getName());
				thread.setDelayTime(threadConfig.getDelayTime());
				thread.setParams(threadConfig.getParams());
				this.getThreadList().put(thread.getId(), thread);
			}else{
				LOG.warn("Cant not reload thread param for null thread object");
			}
		}
		
	}
	
	protected void logThread(ManageableThread thread, int level, String logMsg){
		threadLogger.log(thread, level, logMsg);
	}
}
