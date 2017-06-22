package vn.yotel.thread.log;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import vn.yotel.admin.bo.CDRLogBo;
import vn.yotel.admin.jpa.CDRLog;
import vn.yotel.admin.jpa.CDRLogDetail;
import vn.yotel.cdr.CDRConst;

@Component("ConvertLogger")
public class ConvertLogger implements CDRLogger {

	@Autowired
	@Qualifier("CDRLogBoImpl")
	private CDRLogBo cdrLogger;
	
	@Override
	public void log(CDRLog rec) {
		// Them may cai detail cho no.
		if(rec.getStatus().intValue() ==1){
			List<CDRLogDetail> details = new ArrayList<CDRLogDetail>();
			CDRLogDetail sucss = new CDRLogDetail();
			sucss.setCdrLog(rec);
			sucss.setFileDate(rec.getFileDate());
			sucss.setKey(CDRConst.Status.CV_SUCSS_KEY);
			sucss.setValue(rec.getTotalRec());
			details.add(sucss);
			// Error record
			CDRLogDetail err = new CDRLogDetail();
			err.setCdrLog(rec);
			err.setFileDate(rec.getFileDate());
			err.setKey(CDRConst.Status.CV_ERROR_KEY);
			err.setValue(new Integer(0));
			details.add(err);
			
			rec.setDetails(details);
		}
		cdrLogger.create(rec);
	}
}
