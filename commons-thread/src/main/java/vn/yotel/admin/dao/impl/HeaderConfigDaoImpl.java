package vn.yotel.admin.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transaction;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import vn.yotel.admin.dao.HeaderConfigDao;
import vn.yotel.admin.jpa.HeaderConfig;
import vn.yotel.commons.dao.impl.GenericHibernateDao;

@Repository("headerConfigDaoImpl")
public class HeaderConfigDaoImpl extends
		GenericHibernateDao<HeaderConfig, Integer> implements HeaderConfigDao {
	final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());

	@Override
	public HeaderConfig findByThread(Integer threadId) {
		return this.findOne(threadId);
	}

	@Override
	public void updateHeader(Integer threadId, List<String> addColumns,
			List<String> removeColumns) {
		LOG.info("Update for threadId: {}", threadId.intValue());
		Session session = null;
		try {
			session = this.getCurrentSession();
			if(removeColumns!=null){
				for (String columnId : removeColumns) {
					SQLQuery query = session
							.createSQLQuery("delete from export_column where header_conf_id = :header_conf_id and column_id = :column_id");
					query.setParameter("header_conf_id", threadId.intValue());
					query.setParameter("column_id", columnId);
					query.executeUpdate();
				}
			}
			if(addColumns!=null){
				for (String columnId : addColumns) {
					SQLQuery query = session
							.createSQLQuery("insert into export_column values(:header_conf_id, :column_id)");
					query.setParameter("header_conf_id", threadId.intValue());
					query.setParameter("column_id", columnId);
					query.executeUpdate();
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
	}
}
