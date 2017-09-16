package vn.yotel.admin.bo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.admin.bo.HeaderConfigBo;
import vn.yotel.admin.dao.HeaderConfigDao;
import vn.yotel.admin.jpa.HeaderConfig;
import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.commons.dao.IGenericDao;

@Service("headerConfigBoImpl")
@Transactional
public class HeaderConfigBoImpl extends GenericBoImpl<HeaderConfig, Integer> implements HeaderConfigBo{
	@Autowired
	@Qualifier("headerConfigDaoImpl")
	private HeaderConfigDao headerConfigDao;
	@SuppressWarnings("unchecked")
	@Override
	public HeaderConfigDao getDAO() {
		// TODO Auto-generated method stub
		return headerConfigDao;
	}
	@Override
	public HeaderConfig findByThread(Integer threadId) {
		// TODO Auto-generated method stub
		HeaderConfig conf = this.findOne(threadId);
		conf.getExportColumns().size();
		conf.getFixedColumns().size();
		conf.getAvaibleColumns().size();
		return conf;
	}
	@Override
	public void updateHeader(Integer threadId, List<String> addColumns, List<String> removeColumns){
		headerConfigDao.updateHeader(threadId, addColumns, removeColumns);
	}

}
