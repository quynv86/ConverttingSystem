package vn.yotel.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPFile;

import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.DateUtil;
import vn.yotel.commons.util.FileUtil;
import vn.yotel.commons.util.StringUtils;
import vn.yotel.commons.util.WildcardUtil;

public class FTPReceiver extends FTPThread {
	// //////////////////////////////////////////////////////
	// Member variables
	// //////////////////////////////////////////////////////
	protected String mstrTempDir;

	private String mstrDirectBackupDir;

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	public void loadParameters() throws AppException {
		super.loadParameters();
		mstrTempDir = getPramsAsFolderAndCreate("TempDir");
	}

	// //////////////////////////////////////////////////////

	public void validateParameters() throws AppException {
		if (mstrRemoteStyle != null && mstrRemoteStyle.length() > 0
				&& !mstrRemoteStyle.equals("Directly")) {
			if (mstrProcessDate == null || mstrProcessDate.length() == 0)
				throw new AppException("App_FTP_001",
						"ProcessDate cannot be null when FTPStyle='"
								+ mstrRemoteStyle + "'");

			if (mstrDateFormat == null || mstrDateFormat.length() == 0)
				throw new AppException("App_FTP_001",
						"DateFormat cannot be null when FTPStyle='"
								+ mstrRemoteStyle + "'");
			if (!DateUtil.isDate(mstrProcessDate, mstrDateFormat))
				throw new AppException("App_FTP_001",
						"ProcessDate does not match DateFormat");
			if (mstrRemoteStyle.equals("Daily")
					&& mstrDateFormat.indexOf("dd") < 0)
				throw new AppException("App_FTP_001",
						"DateFormat must contain 'dd' when FTPStyle='"
								+ mstrRemoteStyle + "'");
			else if (mstrRemoteStyle.equals("Monthly")
					&& mstrDateFormat.indexOf("MM") < 0)
				throw new AppException("App_FTP_001",
						"DateFormat must contain 'MM' when FTPStyle='"
								+ mstrRemoteStyle + "'");
			else if (mstrRemoteStyle.equals("Yearly")
					&& mstrDateFormat.indexOf("yyyy") < 0)
				throw new AppException("App_FTP_001",
						"DateFormat must contain 'yyyy' when FTPStyle='"
								+ mstrRemoteStyle + "'");
		}
	}

	// //////////////////////////////////////////////////////
	/**
	 * Process one file
	 * 
	 * @param iFileIndex
	 *            int
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	public void process(int iFileIndex) throws AppException {
		try {
			// Get file
			FTPFile ffl = (FTPFile) mvtFileList.elementAt(iFileIndex);
			String strValidateResult = validateFile(ffl);
			boolean bResult = (strValidateResult == null || strValidateResult
					.length() == 0);
			if (!bResult)
				logInfo(strValidateResult);
			else
				getFile(ffl);
		} catch (Exception ex) {
			if (ex instanceof AppException) {
				throw (AppException) ex;
			} else {
				throw new AppException("App_FTP_002", ex.getMessage());
			}
		}
	}

	// //////////////////////////////////////////////////////
	/**
	 * Change next process date
	 */
	// //////////////////////////////////////////////////////
	public void changeProcessDate() throws AppException {
		if (mstrRemoteStyle != null && mstrRemoteStyle.length() > 0
				&& !mstrRemoteStyle.equals("Directly")) {
			java.util.Date dt = DateUtil
					.toDate(mstrProcessDate, mstrDateFormat);
			do {
				if (mstrRemoteStyle.equals("Daily"))
					dt = DateUtil.addDay(dt, 1);
				else if (mstrRemoteStyle.equals("Monthly"))
					dt = DateUtil.addMonth(dt, 1);
				else if (mstrRemoteStyle.equals("Yearly"))
					dt = DateUtil.addYear(dt, 1);
				String strNextProcessDate = StringUtils.format(dt,
						mstrDateFormat);
				try {
					if (mftpMain.changeWorkingDirectory(mstrRemoteDir
							+ strNextProcessDate)) {
						// Store config
						mbNewDirectory = true;
						setParameter("NewDirectory", true);
						mstrProcessDate = strNextProcessDate;
						setParameter("ProcessDate",mstrProcessDate);
						persitParams();
						return;
					}
				} catch (IOException ex) {
					throw new AppException("App_FTP_001", ex.getMessage());

				}
			} while (dt.getTime() < System.currentTimeMillis());
		}
	}

	// //////////////////////////////////////////////////////
	/**
	 * Filter file
	 * 
	 * @param fl
	 *            File
	 * @return File
	 */
	// //////////////////////////////////////////////////////
	public FTPFile createListItem(FTPFile ffl) {
		// Some ftp server not support ls [wildcard] -> need to check
		if (!ffl.isFile() && !WildcardUtil.match(mstrWildcard,ffl.getName()))
			return null;
		return ffl;
	}

	// //////////////////////////////////////////////////////
	/**
	 * List file from remote folder
	 * 
	 * @throws Exception
	 */
	////////////////////////////////////////////////////////
	protected void listFile() throws AppException {
		mbListing = true;
		try {
			beforeListFile();
			// Get scandir
			if(mstrRemoteStyle != null &&
			   mstrRemoteStyle.length() > 0 &&
			   !mstrRemoteStyle.equals("Directly"))
				mstrScanDir = mstrRemoteDir + mstrProcessDate + "/";
			else
				mstrScanDir = mstrRemoteDir;

			// Get storage dir
			mstrStorageDir = mstrLocalDir;
			if(mstrLocalStyle.equals("Daily"))
				mstrStorageDir += StringUtils.format(new java.util.Date(),"yyyyMMdd") + "/";
			else if(mstrLocalStyle.equals("Monthly"))
				mstrStorageDir += StringUtils.format(new java.util.Date(),"yyyyMM") + "/";
			else if(mstrLocalStyle.equals("Yearly"))
				mstrStorageDir += StringUtils.format(new java.util.Date(),"yyyy") + "/";
			FileUtil.forceFolderExist(mstrStorageDir);

			// Direct backup dir
			if(mstrBackupDir.length() > 0 && !mstrBackupStyle.equals("Delete file"))
			{
				mstrDirectBackupDir = mstrBackupDir;
				if(mstrBackupStyle.equals("Daily"))
					mstrDirectBackupDir += StringUtils.format(new java.util.Date(),"yyyyMMdd") + "/";
				else if(mstrBackupStyle.equals("Monthly"))
					mstrDirectBackupDir += StringUtils.format(new java.util.Date(),"yyyyMM") + "/";
				else if(mstrBackupStyle.equals("Yearly"))
					mstrDirectBackupDir += StringUtils.format(new java.util.Date(),"yyyy") + "/";
				if(!mftpMain.changeWorkingDirectory(mstrDirectBackupDir))
				{
					if(!mftpMain.makeDirectory(mstrDirectBackupDir))
					{
						if(!mftpMain.changeWorkingDirectory(mstrDirectBackupDir))
							throw new Exception("Could not create foler " + mstrDirectBackupDir + " on ftp server");
					}
				}
			}
			// List file
			mprtDirectoryList = new Hashtable();
			mvtFileList = new Vector();
			listFile("");

			afterListFile();
		} catch (Exception ex) {
			if (ex instanceof AppException) {
				throw (AppException) ex;
			} else {
				throw new AppException("App_FTP_002", ex.getMessage());
			}
		} finally {
			mbListing = false;
		}
	}

	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "unchecked" })
	protected void listFile(String strAdditionPath) throws Exception {
		// Get list of file in folder
		if (!mftpMain.changeWorkingDirectory(mstrScanDir + strAdditionPath))
			throw new Exception(
					"Could not change working directory to remote directory ("
							+ mstrScanDir + strAdditionPath + ")");
		FTPFile[] fflFileList = mftpMain.listFiles();

		if (fflFileList != null) {
			for (int iFileIndex = 0; iFileIndex < fflFileList.length; iFileIndex++) {
				if (fflFileList[iFileIndex].isDirectory()) {
					if (mbRecursive) {
						String strAdditionChildPath = strAdditionPath
								+ fflFileList[iFileIndex].getName() + "/";
						FileUtil.forceFolderExist(mstrStorageDir
								+ strAdditionChildPath);

						// Create backup dir
						if (mstrBackupDir.length() > 0
								&& !mstrBackupStyle.equals("Delete file")) {
							if (!mftpMain
									.changeWorkingDirectory(mstrDirectBackupDir
											+ strAdditionChildPath)) {
								if (!mftpMain.makeDirectory(mstrDirectBackupDir
										+ strAdditionChildPath)) {
									if (!mftpMain
											.changeWorkingDirectory(mstrDirectBackupDir
													+ strAdditionChildPath))
										throw new Exception(
												"Could not create foler "
														+ mstrDirectBackupDir
														+ strAdditionChildPath
														+ " on ftp server");
								}
							}
						}

						listFile(strAdditionChildPath);
					}
				} else {
					FTPFile ffl = createListItem(fflFileList[iFileIndex]);
					if (ffl != null) {
						mvtFileList.addElement(ffl);
						mprtDirectoryList.put(ffl, strAdditionPath);
					}
				}
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	/**
	 * After list file event
	 * 
	 * @throws Exception
	 */
	// /////////////////////////////////////////////////////////////////////////
	protected void afterListFile() throws Exception {
		Collections.sort(mvtFileList, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				return ((FTPFile) obj1).getName().compareTo(
						((FTPFile) obj2).getName());
			}
		});
	}

	// //////////////////////////////////////////////////////
	/**
	 * Before list file event
	 * 
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	protected void beforeListFile() throws Exception {
	}

	// //////////////////////////////////////////////////////
	/**
	 * Validate file
	 * 
	 * @param fl
	 *            File
	 * @throws Exception
	 * @return String
	 */
	// //////////////////////////////////////////////////////
	protected String validateFile(FTPFile ffl) throws Exception {
		// TODO : Need to implement later
		return "";
	}

	// //////////////////////////////////////////////////////
	/**
	 * Before get file event
	 * 
	 * @param ffl
	 *            FTPFile
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	protected void beforeGetFile(FTPFile ffl) throws Exception {
		// Log start
		logInfo("Start getting file " + ffl.getName() + " from ftp server");
	}

	// //////////////////////////////////////////////////////
	/**
	 * After get file event
	 * 
	 * @param ffl
	 *            FTPFile
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	protected void afterGetFile(FTPFile ffl) throws Exception {
		// Log completed
		logInfo("Getting file " + ffl.getName() + " completed");
	}

	// //////////////////////////////////////////////////////
	/**
	 * Error get file event
	 * 
	 * @param ffl
	 *            FTPFile
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	protected void errGetFile(FTPFile ffl, String strErrorDescription) {
		try {
			// Write log
			logError(String.format("Error when getting file %s. Detail %s",
					ffl.getName(), strErrorDescription));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// //////////////////////////////////////////////////////
	/**
	 * Get file from ftp server
	 * 
	 * @param ffl
	 *            FTPFile
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	protected void getFile(FTPFile ffl) throws Exception {
		try {
			// Before get file event
			beforeGetFile(ffl);

			// Get file
			String strAdditionPath = StringUtils.nvl(
					mprtDirectoryList.get(ffl), "");
			String strRemoteFilePath = mstrScanDir + strAdditionPath
					+ ffl.getName();
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(mstrTempDir + ffl.getName());
				if (!mftpMain.retrieveFile(strRemoteFilePath, os))
					throw new Exception("Download file failed:\r\n\t\t"
							+ mftpMain.getReplyString());
			} finally {
				FileUtil.safeClose(os);
			}

			// Validate file size
			File fl = new File(mstrTempDir + ffl.getName());
			if (!fl.exists())
				throw new Exception("Download file failed, file does not exist");
			if (fl.length() != ffl.getSize())
				throw new Exception(
						"Getted file size does not equals to ftp file size");

			// Backup file
			if (mstrBackupStyle.equals("Delete file")) {
				if (!mftpMain.deleteFile(mstrScanDir + strAdditionPath
						+ ffl.getName()))
					throw new Exception("Cannot delete file " + mstrScanDir
							+ strAdditionPath + ffl.getName());
			} else if (mstrBackupDir.length() > 0) {
				String strBackupPath = mstrDirectBackupDir
						+ strAdditionPath
						+ FileUtil.formatFileName(ffl.getName(),
								mstrBackupFileFormat);
				mftpMain.deleteFile(strBackupPath);
				if (!mftpMain.rename(
						mstrScanDir + strAdditionPath + ffl.getName(),
						strBackupPath))
					throw new Exception("Cannot rename file " + mstrScanDir
							+ strAdditionPath + ffl.getName() + " to "
							+ strBackupPath);
			}

			// Make local file
			String strGettedFilePath = FileUtil
					.backup(mstrTempDir, mstrLocalDir, ffl.getName(),
							FileUtil.formatFileName(ffl.getName(),
									mstrLocalFileFormat), mstrLocalStyle,
							strAdditionPath);
			try {
				// After get file event
				afterGetFile(ffl);
			} catch (Exception e) {
				FileUtil.deleteFile(strGettedFilePath);
				throw e;
			}
		} catch (Exception e) {
			errGetFile(ffl, e.getMessage());
			throw e;
		}
	}
}
