package vn.yotel.admin.bo;

import java.util.List;

import vn.yotel.admin.jpa.ThreadLog;
import vn.yotel.commons.bo.GenericBo;

public interface ThreadLogBo  extends GenericBo<ThreadLog,Long>{
	List<ThreadLog> loadLog(String threadId);
}
