package vn.yotel.admin.bo.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import vn.yotel.admin.bo.ThreadLogBo;
import vn.yotel.admin.dao.ThreadLogDao;
import vn.yotel.admin.jpa.ThreadLog;
import vn.yotel.commons.bo.impl.GenericBoImpl;

@Service("threadLogBoImpl")
@Transactional
public class ThreadLogBoImpl extends GenericBoImpl<ThreadLog, Long> implements ThreadLogBo{

	@Autowired
	@Qualifier("threadLogDaoImp")
	private ThreadLogDao dao;
	
	@SuppressWarnings("unchecked")
	@Override
	public ThreadLogDao getDAO() {
		return dao;
	}
	
	@Override
	public List<ThreadLog> loadLog(String threadId) {
		return dao.loadLog(threadId);
	}
}
