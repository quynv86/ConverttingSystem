package vn.yotel.admin.dao;

import vn.yotel.commons.dao.IGenericDao;
import vn.yotel.thread.domain.ThreadConfig;
import vn.yotel.thread.domain.ThreadConfigs;

public interface ThreadConfigDao  extends IGenericDao<ThreadConfig, String> {
	public ThreadConfigs loadThreadConfigs();
}
