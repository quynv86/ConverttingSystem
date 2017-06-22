package vn.yotel.thread;

import vn.yotel.commons.exception.AppException;

public class JvmGCThread extends ManageableThread {

//	private Logger LOG = LoggerFactory.getLogger(JvmGCThread.class);
	
	@Override
	protected void loadParameters () throws AppException {
	}
	
	@Override
	protected boolean processSession() throws AppException {
		System.gc();
		safeSleep(100L);
		return true;
	}

	@Override
	protected void validateParameters() throws AppException {
		
	}

}
