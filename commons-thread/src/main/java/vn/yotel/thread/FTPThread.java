package vn.yotel.thread;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import vn.yotel.commons.exception.AppException;

/**
 * <p>Title: </p>
 * <p>Description: Thread get file from ftp server</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: FPT</p>
 * @author Thai Hoang Hiep
 * @version 1.0
 */

public abstract class FTPThread extends ManageableThread
{
	////////////////////////////////////////////////////////
	// Member variables
	////////////////////////////////////////////////////////
	protected FTPClient mftpMain;

	// FTP variables
	protected String mstrHost;
	protected int miPort;
	protected String mstrUser;
	protected String mstrPassword;
	protected String mstrTransferType;
	protected String mstrConnectMode;
	protected int miTimeout;
	protected String mstrLocalDir;
	protected String mstrLocalStyle;
	protected String mstrLocalFileFormat;
	protected String mstrRemoteDir;
	protected String mstrRemoteStyle;
	protected String mstrRemoteFileFormat;
	protected String mstrBackupDir;
	protected String mstrBackupStyle;
	protected String mstrBackupFileFormat;
	protected String mstrWildcard;
	protected String mstrSQLValidateCommand;
	protected boolean mbRecursive;
	protected String mstrDateFormat;
	protected String mstrProcessDate;
	protected boolean mbNewDirectory;
	protected String mstrScanDir;
	protected String mstrStorageDir;

	// Common used variables
	protected boolean mbListing = false;
	protected int miListItemCount;

	// DirectoryListItem
	protected Vector mvtFileList;
	protected Hashtable mprtDirectoryList;

	////////////////////////////////////////////////////////
	// Member function
	////////////////////////////////////////////////////////
	protected abstract void process(int iFileIndex) throws AppException;
	protected abstract void listFile() throws AppException;
	protected abstract void changeProcessDate() throws AppException;
	protected void beforeProcessFileList() throws AppException
	{
	}
	protected void afterProcessFileList() throws AppException
	{
	}

	////////////////////////////////////////////////////////
	// Override
	////////////////////////////////////////////////////////
	public void loadParameters() throws AppException
	{
		// Fill parameter
		mstrHost = this.getParamAsString("Host");
		miPort =  this.getParamAsInt("Port");
		mstrUser = this.getParamAsString("User");
		mstrPassword = this.getParamAsString("Password");		
		mstrTransferType = this.getParamAsString("TransferType");
		
		mstrConnectMode = getParamAsString("ConnectMode");
		
		miTimeout = getParamAsInt("Timeout") * 1000;
		
		mstrLocalDir = getPramsAsFolderAndCreate("LocalDir");
		
		mstrLocalStyle = getParamAsString("LocalStyle");

		mstrLocalFileFormat =  getParamAsString("LocalFileFormat");
		
		mstrRemoteDir = getParamAsString("FtpDir");
		
		mstrRemoteStyle = getParamAsString("FtpStyle");
		
		mstrRemoteFileFormat = getParamAsString("FtpFileFormat");
		
		mstrBackupDir = getParamAsString("BackupDir");

		mstrBackupStyle = getParamAsString("BackupStyle");
		mstrBackupFileFormat = getParamAsString("BackupFileFormat");
		mstrWildcard = getParamAsString("Wildcard");
		mstrSQLValidateCommand = getParamAsString("SQLValidateCommand");
		mstrDateFormat = getParamAsString("DateFormat");
		mstrProcessDate = getParamAsString("ProcessDate");
		mbRecursive = getParamAsBoolean("Recursive");
		mbNewDirectory = getParamAsBoolean("NewDirectory");
		super.loadParameters();
	}

	////////////////////////////////////////////////////////
	public void initializeSession() throws AppException
	{
		
		super.initializeSession();

		// Make sure ftp session is closed
		try
		{
			if(mftpMain != null)
				mftpMain.disconnect();
		}
		catch(Exception e)
		{
		}

		// Open FTP Connection
		mftpMain = new FTPClient();
		mftpMain.setDefaultTimeout(miTimeout);
		mftpMain.setDataTimeout(miTimeout);
		try{
			mftpMain.connect(mstrHost,miPort);
			if(!FTPReply.isPositiveCompletion(mftpMain.getReplyCode()))
			throw new AppException("App_FTP_01","FTP server refused connection");
		
			if(!mftpMain.login(mstrUser,mstrPassword))
				throw new AppException("App_FTP_02","Could not log in to ftp server. Invalid user name or password");
			mftpMain.setFileTransferMode(FTPClient.COMPRESSED_TRANSFER_MODE);
			if(mstrTransferType.equalsIgnoreCase("ASCII"))
				mftpMain.setFileType(FTPClient.ASCII_FILE_TYPE);
			else
				mftpMain.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(mstrConnectMode.equalsIgnoreCase("ACTIVE"))
				mftpMain.enterLocalActiveMode();
			else
				mftpMain.enterLocalPassiveMode();
		}catch(Exception ex){
			if(ex instanceof AppException){
				throw (AppException)ex;
			}else{
				throw new AppException("App_FTP_01", ex.getMessage());
			}
		}
	}
	////////////////////////////////////////////////////////
	public void completeSession() throws AppException
	{
		// Release ftp connection
		try
		{
			mftpMain.disconnect();
			mftpMain = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	////////////////////////////////////////////////////////
	public boolean processSession() throws AppException
	{
		// Fill receive file list
		listFile();

		// Receive list of file
		miListItemCount = mvtFileList.size();
		if(miListItemCount > 0)
		{
			beforeProcessFileList();
			for(int iIndex = 0;iIndex < miListItemCount && !requireStop;iIndex++)
			{
				process(iIndex);
//				if(mbNewDirectory)
//				{
//					mbNewDirectory = false;
//					this.store
//					mprtParam.put("NewDirectory","N");
//					storeConfig();
//				}
			}
			afterProcessFileList();
		}

		// Change next process date
		if(miListItemCount == 0 && !requireStop)
		   changeProcessDate();
		return true;
	}
}
