package vn.yotel.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.StringUtils;
import vn.yotel.thread.Constants.ManageableThreadState;


public abstract class ManageableThread implements Runnable {
	
	protected String id;
	protected String name;
	protected boolean status;
	protected boolean requireStop = false;
	private byte state = ManageableThreadState.NORMAL;
	protected int delayTime = 30;
	private int order = 0;
	protected JSONObject params = new JSONObject();
	private ExecutorService exeService = Executors.newFixedThreadPool(1);
	private Logger LOG = LoggerFactory.getLogger(ManageableThread.class);
	protected ApplicationContext appCtx;
	
	//HA
	protected boolean active = false;
	protected Object activeMonitorObj = new Object();
	
	protected int mLoglevel = Constants.LOGLEVEL.DEFAUL;
	
	private ThreadManager threadManager;
	
	
	public ManageableThread() {
		this.requireStop = false;
		this.setState(ManageableThreadState.NORMAL);
		params = new JSONObject();
	}
	public ManageableThread setApplicationContext(ApplicationContext appCtx){
		this.appCtx  = appCtx;
		return this;
	}
	public boolean isRunning() {
		return status;
	}

	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}

	public ThreadManager getManager() {
		return threadManager;
	}

	public void setManager(ThreadManager manager) {
		this.threadManager = manager;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public void startJob() {
    	if (status) {
			LOG.warn(String.format("Thread %s, name = %s is already started!", this.getId(), this.getName()));
		} else {
			LOG.info("Submit request to start this thread.");
			exeService.submit(this);
		}
    }
    
	public void stopJob() {
		if (!status) {
			LOG.warn(String.format("Thread %s, name = %s is not running yet!", this.getId(), this.getName()));
		} else {
			this.requireStop = true;
		}
	}
	
	protected void loadParameters()  throws AppException {
		try{
			String logLevel = this.getParamAsString("log-level");
			if(Constants.LOGLEVEL.DEBUG_ST.equalsIgnoreCase(logLevel)){
				mLoglevel = Constants.LOGLEVEL.DEBUG;
			}else if(Constants.LOGLEVEL.INFOR_ST.equalsIgnoreCase(logLevel)){
				mLoglevel = Constants.LOGLEVEL.INFOR;
			}else if(Constants.LOGLEVEL.WARNING_ST.equalsIgnoreCase(logLevel)){
				mLoglevel = Constants.LOGLEVEL.WARNING;
			}
			else if(Constants.LOGLEVEL.ERROR_ST.equalsIgnoreCase(logLevel)){
				mLoglevel = Constants.LOGLEVEL.ERROR;
			}else if(Constants.LOGLEVEL.FATAL_ST.equalsIgnoreCase(logLevel)){
				mLoglevel = Constants.LOGLEVEL.FATAL;
			}else{
				mLoglevel=Constants.LOGLEVEL.DEFAUL;
			}
		}catch(Exception ex){
			
		}
	}
	
    protected void initializeSession() throws AppException {
    }
    
	protected abstract boolean processSession() throws AppException;
	
	protected void completeSession() throws AppException {
	}
	
	protected void storeConfig() {
	}

	@Override
	public void run() {
		try {
			this.status = true;
			while (!requireStop) {
				try {
					this.loadParameters();
					this.validateParameters();
					this.initializeSession();
					if (this.processSession()) {
					}
				} catch (Exception e) {
					LOG.error("", e);
				} finally {
					try{
					this.completeSession();
					}catch(Exception exCompl){
						LOG.error("Error in completeSession. Detail: {}", exCompl.getMessage());
					}
				}
				try {
					for (int iIndex = 0; (iIndex < this.getDelayTime() * 10) && !requireStop; iIndex++) {
						safeSleep(100L);
					}
				} catch (Exception e) {
				}
				
			}
		} catch (Exception e) {
			LOG.error("Error: ", e.getMessage());
		} finally {
			this.status = false;
			this.requireStop = false;
		}
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}
	
	public int getParamAsInt(String keyVal) {
		return this.params.getInt(keyVal);
	}
	public long getParamAsLong(String keyVal){
		return this.params.getLong(keyVal);
	}
	
	public String getParamAsString(String keyVal) {
		return this.params.optString(keyVal, "");
	}
	
	public boolean getParamAsBoolean(String keyVal) {
		return this.params.getBoolean(keyVal);
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * 
	 * @param millis
	 */
	protected void safeSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
		}
	}
	
	protected void safeWait(Object locker, long millis) {
		try {
			locker.wait(millis);
		} catch (Exception e) {
		}
	}
	protected abstract void validateParameters() throws AppException;
	public void notifyEnteringNewMode(String serverOldMode, String serverNewMode) {
	}
	
	public Object getBean(String beanName){
		try{
			return appCtx.getBean(beanName);
		}catch(BeansException beanException){
			LOG.warn("Can not get bean for given name [{}]", beanName);
			return null;
		}	
	}
	
	public Object getParamsAsBean(String key){
		String beanName = this.getParamAsString(key);
		if(!StringUtils.nvl(beanName, "").equals("")){
			return getBean(beanName);
		}
		return null;
		
	}
	protected final void logDebug(String logMsg){
		log(Constants.LOGLEVEL.DEBUG, logMsg);
	}
	
	protected final void logInfo(String logMsg){
		log(Constants.LOGLEVEL.INFOR, logMsg);
	}
	protected final void logWarning(String logMsg){
		log(Constants.LOGLEVEL.WARNING, logMsg);
	}
	protected final void logError(String logMsg){
		log(Constants.LOGLEVEL.ERROR, logMsg);
	}
	protected final void logFatal(String logMsg){
		log(Constants.LOGLEVEL.FATAL, logMsg);
	}
	private void log(int level, String logMsg){
		if(getLoglevel()<=level){
			threadManager.logThread(this,level, logMsg);
		}
	}
	public int getLoglevel(){
		return this.mLoglevel;
	}
	public ApplicationContext getAppCtx(){
		return appCtx;
	}
}
