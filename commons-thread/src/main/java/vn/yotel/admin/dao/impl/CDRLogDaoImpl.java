package vn.yotel.admin.dao.impl;

import org.springframework.stereotype.Repository;

import vn.yotel.admin.dao.CDRLogDao;
import vn.yotel.admin.jpa.CDRLog;

@Repository("CDRLogDaoImpl")
public class CDRLogDaoImpl extends vn.yotel.commons.dao.impl.GenericHibernateDao<CDRLog, Long> implements CDRLogDao{

}
