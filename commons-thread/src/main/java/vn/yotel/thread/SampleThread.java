package vn.yotel.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.exception.AppException;
public class SampleThread extends ManageableThread{

	Logger LOG = LoggerFactory.getLogger(getClass());
	private String mstrSQL;
	@Override
	protected boolean processSession() throws AppException {
		// TODO Auto-generated method stub
		LOG.info(String.format("Going to procession, we will execute sql %s", mstrSQL));
		return true;
	}
	
	protected void loadParameters() throws AppException{
		try{
			mstrSQL = this.getParamAsString("SQL");
		}catch(Exception exLoadParams){
			throw new AppException("LOAD", exLoadParams.getMessage());
		}finally{
			super.loadParameters();
		}
	}

	@Override
	protected void validateParameters() throws AppException {
	}
}
