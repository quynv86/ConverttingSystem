package vn.yotel.admin.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import vn.yotel.admin.dao.ThreadLogDao;
import vn.yotel.admin.jpa.ThreadLog;
import vn.yotel.commons.dao.impl.GenericHibernateDao;

@Repository("threadLogDaoImp")
public class ThreadLogDaoImpl extends GenericHibernateDao<ThreadLog, Long> implements ThreadLogDao {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	@Override
	public List<ThreadLog> loadLog(String threadId) {
		LOG.info("Start loading log for threadId {}", threadId);
		List<ThreadLog> retList = null;
		try {
			Query query = getCurrentSession().getNamedQuery("loadLogByThread").setParameter("thread_id",threadId);
			retList = query.list();
			
		} catch (Exception e) {
			LOG.error("Error when loading thread log. Detail {}", e.getMessage());
		}
		LOG.info("End of loading log for threadId {}", threadId);
		return retList;

	}

}
