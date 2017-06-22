package vn.yotel.thread.log;

import vn.yotel.admin.jpa.CDRLog;

public interface CDRLogger {
	void log(CDRLog rec);
}
