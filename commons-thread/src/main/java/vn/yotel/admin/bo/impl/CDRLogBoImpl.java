package vn.yotel.admin.bo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import vn.yotel.admin.bo.CDRLogBo;
import vn.yotel.admin.dao.CDRLogDao;
import vn.yotel.admin.jpa.CDRLog;
import vn.yotel.commons.bo.impl.GenericBoImpl;

@Service("CDRLogBoImpl")
public class CDRLogBoImpl  extends GenericBoImpl<CDRLog, Long>  implements CDRLogBo{

	@Autowired
	@Qualifier("CDRLogDaoImpl")
	private CDRLogDao dao;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public CDRLogDao getDAO() {
		return dao;
	}
}
