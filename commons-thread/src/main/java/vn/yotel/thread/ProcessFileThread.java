package vn.yotel.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.FileUtil;
import vn.yotel.commons.util.StringUtils;
import vn.yotel.commons.util.WildcardFileFilter;
import vn.yotel.thread.Constants.ManageableThreadState;
import vn.yotel.thread.ManageableThread;

public abstract class ProcessFileThread extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessFileThread.class);
	
	protected String localDir;
	protected String localStyle;
	protected String localFileFormat;
	protected String backupDir;
	protected String backupStyle;
	protected String backupFileFormat;
	protected String tempDir;
	protected String wildcard;
	protected boolean recursive;
	protected String dateFormat;
	protected String processDate;
	protected boolean isNewDirectory;
	protected String scanDir;
	protected boolean isListing = false;
	protected int listFileCount;
	protected String locationName;
	
	protected HashMap<File, String> pathByFile;
	protected List<File> listedFiles;
	
	protected abstract void process(File inputFile) throws Exception ;

	protected void listFile() throws Exception {
		this.isListing = true;
		try {
			beforeListFile();
			if ((this.localStyle != null) && (this.localStyle.length() > 0) && (!this.localStyle.equals("Directly"))) {
				this.scanDir = (this.localDir + this.processDate + "/");
			} else
				this.scanDir = this.localDir;
			this.pathByFile = new HashMap<File, String>();
			this.listedFiles = new ArrayList<File>();
			listFile("");
			afterListFile();
		} finally {
			this.isListing = false;
		}
	}
	
	protected void listFile(String additionPath) throws Exception {
		File[] files = new File(this.scanDir + additionPath).listFiles(new WildcardFileFilter(this.wildcard, true));
		if (files != null) {
			for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
				if (files[fileIndex].isDirectory()) {
					if (this.recursive) {
						String additionChildPath = additionPath + files[fileIndex].getName() + "/";
						listFile(additionChildPath);
					}
				} else {
					File file = createListItem(files[fileIndex]);
					if (file != null) {
						this.listedFiles.add(file);
						if (additionPath.length() > 0)
							this.pathByFile.put(file, additionPath);
					}
				}
			}
		}
	}
	
	public File createListItem(File file) {
		return file;
	}


	protected int getListedFileCount() throws Exception {
		return this.listedFiles.size();
	}

	protected void changeProcessDate() throws Exception {
	}

	protected abstract void beforeProcessFileList() throws Exception;

	protected abstract void afterProcessFileList() throws Exception;
	
	@Override
	protected void loadParameters () throws AppException {
		if (this.params != null) {
			super.loadParameters();
			this.dateFormat = this.getParamAsString("date-format");
			this.processDate = this.getParamAsString("process-date");
			this.localDir = this.getParamAsString("local-dir");
			this.localStyle = this.getParamAsString("local-style");
			this.localFileFormat = this.getParamAsString("local-file-format");
			this.backupDir = this.getParamAsString("backup-dir");
			this.backupStyle = this.getParamAsString("backup-style");
			this.backupFileFormat = this.getParamAsString("backup-file-format");
			this.tempDir = this.getParamAsString("temp-dir");
			this.wildcard = this.getParamAsString("wildcard");
			this.recursive = this.getParamAsBoolean("recursive");
			//
			this.locationName = this.getParamAsString("location-name");
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
    }
	
	@Override
	protected void initializeSession() throws AppException {
    }
	
	@Override
	protected boolean processSession() throws AppException {
		this.setState(ManageableThreadState.NORMAL);
		try {
			listFile();
			this.listFileCount = getListedFileCount();
			if (this.listFileCount > 0) {
				beforeProcessFileList();
				for (int iIndex = 0; (iIndex < this.listFileCount) && (!shouldStopNow()); iIndex++) {
					Exception exception=null;
					File item = listedFiles.get(iIndex);
					try{
						beforeProcessFile(item);
						process(item);
					}catch(Exception exProcess){
						exception = exProcess;
					}finally{
						afterProcessFile(item,exception);
					}
				}
				afterProcessFileList();
			}
			if ((this.listFileCount == 0) && (!shouldStopNow()))
				changeProcessDate();
		} catch (Exception ex) {
			this.setState(ManageableThreadState.ERROR);
			LOG.error("", ex);
			LOG.warn( this.locationName + ": Error occured: " + ex.getMessage());
		}
		return true;
	}
	
	private boolean shouldStopNow() {
		return this.requireStop;
	}

	@Override
	protected void completeSession() throws AppException {
		try {
		} catch (Exception e) {
			LOG.error("", e);
		}
	}
	
	protected void afterListFile() throws Exception {
		Collections.sort(this.listedFiles, new FileComparator());
	}

	protected void beforeListFile() throws Exception {
	}
	
	public String formatBackupFileName(String fileName) throws Exception {
		return FileUtil.formatFileName(fileName, StringUtils.replaceAll(this.backupFileFormat, "$ProcessDate", this.processDate));
	}

	private static class FileComparator implements Comparator<File> {
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	@Override
	protected void validateParameters() throws AppException {
	}
	protected abstract void  beforeProcessFile(File inputFile) throws Exception;
	protected abstract void afterProcessFile(File inputFile, Exception exception);
}
