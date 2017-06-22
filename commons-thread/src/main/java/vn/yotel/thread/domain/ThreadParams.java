package vn.yotel.thread.domain;

public class ThreadParams {
	private String threadId;
	
	private String threadName;
	
	private String params;

	public ThreadParams(){
		super();
	}
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	
}
