package vn.yotel.admin.bo;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.thread.domain.ThreadConfig;
import vn.yotel.thread.domain.ThreadConfigs;

public interface ThreadConfigBo extends GenericBo<ThreadConfig,String>{
	ThreadConfigs loadThreadConfigs();
}
