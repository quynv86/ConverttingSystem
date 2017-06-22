package vn.yotel.commons.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class JvmGCJob extends QuartzJobBean {
	
//	private Logger LOG = LoggerFactory.getLogger(JvmGCJob.class);
	
	public JvmGCJob() {
	}
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		System.gc();
	}
}

