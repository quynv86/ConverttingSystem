package vn.yotel.admin.bo;

import java.util.List;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.admin.jpa.HeaderConfig;
public interface HeaderConfigBo extends GenericBo<HeaderConfig,Integer>{
	HeaderConfig findByThread(Integer threadId);
	void updateHeader(Integer threadId, List<String> addColumns, List<String> removeColumns);
}
