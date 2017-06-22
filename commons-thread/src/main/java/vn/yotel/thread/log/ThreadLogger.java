package vn.yotel.thread.log;

import vn.yotel.thread.ManageableThread;

public interface ThreadLogger {
	void log(ManageableThread thread, int level, String message);
}
