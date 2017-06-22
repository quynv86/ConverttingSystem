package vn.yotel.admin.dao.impl;

import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import vn.yotel.admin.dao.ThreadConfigDao;
import vn.yotel.commons.dao.impl.GenericHibernateDao;
import vn.yotel.thread.domain.ThreadConfig;
import vn.yotel.thread.domain.ThreadConfigs;

@Repository("threadConfigDaoImpl")
public class ThreadConfigDaoImpl extends GenericHibernateDao<ThreadConfig, String> implements ThreadConfigDao{
	final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());
	@Override
	public ThreadConfigs loadThreadConfigs() {
		ThreadConfigs threads = new ThreadConfigs();
		List<ThreadConfig> tmpList = null;
		try {
			tmpList = this.findAll();
		} catch (Exception e) {
			LOG.error("Cant load thread configs {}", e.getMessage());
			e.printStackTrace();
		}
		if (tmpList != null) {
			for(ThreadConfig item : tmpList){
				// Convert string to jsonobject
				String json = item.getParamsAsString();
				item.setParams(new JSONObject(json));
			}
			threads.setThreads(tmpList);
		}
		return threads;
	}
	public ThreadConfig findOne(String id){
		ThreadConfig oneItem = super.findOne(id);
		String json = oneItem.getParamsAsString();
		oneItem.setParams(new JSONObject(json));
		return oneItem;
	}

}
