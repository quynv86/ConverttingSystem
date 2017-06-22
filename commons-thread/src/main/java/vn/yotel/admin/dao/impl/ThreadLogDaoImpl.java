package vn.yotel.admin.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import vn.yotel.admin.dao.ThreadLogDao;
import vn.yotel.admin.jpa.ThreadLog;
import vn.yotel.commons.dao.impl.GenericHibernateDao;

@Repository("threadLogDaoImp")
public class ThreadLogDaoImpl extends GenericHibernateDao<ThreadLog, Long> implements ThreadLogDao {
	
	@Override
	public List<ThreadLog> loadLog(String threadId) {
		Criteria cr = getCurrentSession().createCriteria(ThreadLog.class);
		cr.add(Restrictions.eq("threadId", Long.parseLong(threadId))).addOrder(Order.desc("logDate"));
		return cr.list();
	}

}
