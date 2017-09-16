package vn.yotel.admin.dao;

import java.util.List;

import vn.yotel.admin.jpa.HeaderConfig;
import vn.yotel.commons.dao.IGenericDao;

public interface HeaderConfigDao  extends IGenericDao<HeaderConfig, Integer>{
	HeaderConfig findByThread(Integer threadId);
	void updateHeader(Integer threadId, List<String> addColumns, List<String> removeColumns);
}
