package vn.yotel.admin.dao;

import java.util.List;

import vn.yotel.admin.jpa.ThreadLog;
import vn.yotel.commons.dao.IGenericDao;

public interface ThreadLogDao  extends IGenericDao<ThreadLog, Long>{
	List<ThreadLog> loadLog(String threadId);
}
