package vn.yotel.admin.bo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.admin.bo.ThreadConfigBo;
import vn.yotel.admin.dao.ThreadConfigDao;
import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.thread.domain.ThreadConfig;
import vn.yotel.thread.domain.ThreadConfigs;

@Service("threadConfigLoader")
@Transactional
public class ThreadConfigBoImpl extends GenericBoImpl<ThreadConfig, String> implements ThreadConfigBo{

	@Autowired
	@Qualifier("threadConfigDaoImpl")
	private ThreadConfigDao threadConfigDao;
	@Override
	public ThreadConfigs loadThreadConfigs() {
		// TODO Auto-generated method stub
		return threadConfigDao.loadThreadConfigs();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ThreadConfigDao getDAO() {
		// TODO Auto-generated method stub
		return threadConfigDao;
	}

}
