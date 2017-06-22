package vn.yotel.thread;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;

import vn.yotel.admin.bo.SeqGeneratorBo;
import vn.yotel.admin.jpa.CDRLog;
import vn.yotel.cdr.CDRConst;
import vn.yotel.cdr.format.HuaweiFormat;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.FileUtil;
import vn.yotel.thread.log.CDRLogger;

public class CDRConvertingThread extends ProcessFileThread {

	private final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(getClass());

	private CDRLogger cdrLogger;
	// Sequence generator
	private SeqGeneratorBo seqGen;
	private String exportDir;
	private String outputHeader;
	private CDRLog cdrInfor;
	
	private HuaweiFormat formater;
	private int mtotalRec =0;

	private boolean logIsActive = false;

	private String fileNamePattern;

	private String fileDateFormat;

	private int fileDateGroup;

	private int fileSeqGroup;
	
	private Pattern mPattern;
	
	private Matcher mMatcher;
	
	private boolean logAvaiable(){
		return logIsActive;
	}
	
	public CDRConvertingThread() {
		super();
	}
	
	@Override
	protected void process(File inputFile) throws Exception {
			logDebug(String.format("Start processfile %s", inputFile.getName()));
			mtotalRec = formater.convert(inputFile, this);
			cdrInfor.setTotalRec(new Integer(mtotalRec));
	}

	@Override
	protected void beforeProcessFileList() throws Exception {
		formater = new HuaweiFormat();
		formater.mstrTempDir = this.tempDir;
		formater.mstrFileNameFormat = this.localFileFormat;
		formater.p_mstrHeader = this.outputHeader;
		formater.p_LocDir = this.exportDir;
		formater.p_mstrHeader = this.outputHeader;
	}

	@Override
	protected void afterProcessFileList() throws Exception {
		// TODO Auto-generated method stub

	}

	protected void loadParameters() throws AppException {
		super.loadParameters();
		exportDir = this.getParamAsString("export-dir");
		outputHeader = this.getParamAsString("output-header");
		cdrLogger = (CDRLogger)this.getParamsAsBean("cdr-logger");
		seqGen = (SeqGeneratorBo)getAppCtx().getBean("SeqGeneratorBoImpl");

		fileNamePattern = this.getParamAsString("fname-pattern");
		fileDateFormat = this.getParamAsString("fdate-format");
		fileDateGroup = this.getParamAsInt("fdate-index");
		fileSeqGroup = this.getParamAsInt("fseq-index");
		mPattern = Pattern.compile(fileNamePattern);
		if(cdrLogger!=null){
			logIsActive = true;
		}else{
			logIsActive = false;
		}
	}
	
	@Override
	protected void beforeProcessFile(File inputFile) throws Exception{
		mtotalRec = 0;
		cdrInfor = new CDRLog();
		formater.setFileId(getCDRFileSeq());
		doValid(inputFile,cdrInfor);
	}

	@Override
	protected void afterProcessFile(File inputFile, Exception exception) {
		if (exception == null) {
			onSucess(inputFile);
		} else {
			onError(inputFile, exception);
		}
	}

	protected void onSucess(File inputFile) {
		logInfo(String.format("Convert file %s is sucess. Total recs: %s record(s)",inputFile.getName(),String.valueOf(mtotalRec)));
		cdrInfor.setStatus(new Integer(CDRConst.Status.SUCC));
		logCDR(cdrInfor);
		try {
			FileUtil.backup(this.localDir, this.backupDir,
					inputFile.getName(), inputFile.getName(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void onError(File inputFile, Exception exception) {
		LOG.error("Error when converting file {}. Detail {}",
				inputFile.getName(), exception.getMessage());
		logError(String.format("Error when convert file %s, detail: %s", inputFile.getName(), exception.getMessage()));
		try {
			FileUtil.backup(this.localDir, this.backupDir,
					inputFile.getName(), inputFile.getName(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void logCDR(CDRLog rec){
		if(logAvaiable()){
			cdrLogger.log(rec);
		}else{
			logDebug("CDR Logger is not avaiable. Please check your configuration");
		}
	}
	
	private void doValid(File file, CDRLog cdrInfo) throws Exception{
		mMatcher = mPattern.matcher(file.getName());
		mMatcher.find();
		// Get File Seq
		
		int seq = -1;
		try{
			String strFileSeq = mMatcher.group(fileSeqGroup);
			seq = Integer.parseInt(strFileSeq);
		}catch(Exception exParse){
			
		}
		cdrInfor.setFileSeq(seq);
		// Get file Date
		Date lcFDate = null;
		try{
			String strDate = mMatcher.group(fileDateGroup);
			lcFDate = DateUtils.parseDate(strDate, this.fileDateFormat);
		}
		catch(Exception ex){
			lcFDate = new java.util.Date();
		}
		cdrInfo.setFileDate(lcFDate);
		cdrInfo.setAction(CDRConst.Actions.CONVERT);
		cdrInfo.setFileId(Long.parseLong(formater.getFileId()));
		cdrInfo.setDataSource(CDRConst.DataSources.HUAWEI_MSC);
		cdrInfo.setFileName(file.getName());
		cdrInfo.setStatus(new Integer(CDRConst.Status.BEGIN));
		cdrInfo.setFileSize(file.length());
	}
		
	private String getCDRFileSeq(){
		return seqGen.nextVal(CDRConst.SEQS.CDR_FILE_SEQ);
	}
	
}
