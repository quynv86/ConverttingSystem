package vn.yotel.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;

public class FileUtil {
	public static String fileContentFromInputStream(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			return everything;
		} finally {
			br.close();
		}
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String fileContentFromClasspath(String filePath) throws IOException {
		InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
		if (is != null) {
			return fileContentFromInputStream(is);			
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String loadContentFromFile(String fileName) throws IOException {
		File f = new File(fileName);
		if (!f.exists()) {
			return null;
		} else {
			FileInputStream is = new FileInputStream(f);
			return fileContentFromInputStream(is);
		}
	}
	/**
	 * 
	 * @param content
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean storeContentToFile(String content, String fileName) {
		boolean result = true;
		RandomAccessFile m_TextFile = null;
		try {
			File f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			m_TextFile = new RandomAccessFile(f, "rw");
			m_TextFile.setLength(0);
			m_TextFile.write(content.getBytes());
			m_TextFile.close();
		} catch (Exception e) {
			result = false;
		} finally {
			if (m_TextFile != null) {
				try {
					m_TextFile.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}
	
	public static String formatFileName(String fileName, String fileFormat) {
		if ((fileName == null) || (fileName.length() == 0) || (fileFormat == null) || (fileFormat.length() == 0)) {
			return fileName;
		}
		int extensionIndex = fileName.lastIndexOf(46);
		if (extensionIndex < 0)
			extensionIndex = fileName.length();
		int baseIndex = fileName.lastIndexOf(47);
		if (baseIndex < 0)
			baseIndex = fileName.lastIndexOf(92);
		if (baseIndex < 0)
			baseIndex = 0;
		String baseFileName = fileName.substring(baseIndex, extensionIndex);
		String fileExtension = "";
		if (extensionIndex < fileName.length() - 1)
			fileExtension = fileName.substring(extensionIndex + 1, fileName.length());
		fileFormat = StringUtils.replaceAll(fileFormat, "$FileName", fileName);
		fileFormat = StringUtils.replaceAll(fileFormat, "$BaseFileName", baseFileName);
		fileFormat = StringUtils.replaceAll(fileFormat, "$FileExtension", fileExtension);
		return fileFormat;
	}
	
	public static String backup(String sourcePath, String backupPath, String sourceFile, String backupFile, String backupStyle) throws Exception {
		return backup(sourcePath, backupPath, sourceFile, backupFile, backupStyle, true);
	}

	public static String backup(String sourcePath, String backupPath, String sourceFile, String backupFile, String backupStyle, boolean replaceIfExist) throws Exception {
		return backup(sourcePath, backupPath, sourceFile, backupFile, backupStyle, "", replaceIfExist);
	}

	public static String backup(String sourcePath, String backupPath, String sourceFile, String backupFile, String backupStyle, String additionPath) throws Exception {
		return backup(sourcePath, backupPath, sourceFile, backupFile, backupStyle, additionPath, true);
	}

	public static String backup(String sourcePath, String backupPath, String sourceFile, String backupFile, String backupStyle, String additionPath, boolean replaceIfExist) throws Exception {
		if (backupStyle.equals("Delete file")) {
			if (!deleteFile(sourcePath + sourceFile))
				throw new Exception("Cannot delete file " + sourcePath + sourceFile);
		} else if (backupPath.length() > 0) {
			String currentDate = "";
			if (backupStyle.equals("Daily"))
				currentDate = StringUtils.format(new Date(), "yyyyMMdd") + "/";
			else if (backupStyle.equals("Monthly"))
				currentDate = StringUtils.format(new Date(), "yyyyMM") + "/";
			else if (backupStyle.equals("Yearly"))
				currentDate = StringUtils.format(new Date(), "yyyy") + "/";
			forceFolderExist(backupPath + currentDate + additionPath);
			if (!renameFile(sourcePath + sourceFile, backupPath + currentDate + additionPath + backupFile, replaceIfExist))
				throw new Exception("Cannot rename file " + sourcePath + sourceFile + " to " + backupPath + currentDate + backupFile);
			return backupPath + currentDate + backupFile;
		}
		return "";
	}

	public static boolean deleteFile(String source) {
		File sourceFile = new File(source);
		return sourceFile.delete();
	}

	public static boolean renameFile(String source, String destination) {
		return renameFile(source, destination, false);
	}

	public static boolean renameFile(String source, String destination, boolean deleteIfExist) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);
		return renameFile(sourceFile, destinationFile, deleteIfExist);
	}

	public static boolean renameFile(File sourceFile, File destinationFile) {
		return renameFile(sourceFile, destinationFile, false);
	}

	public static boolean renameFile(File sourceFile, File destinationFile, boolean deleteIfExist) {
		if (sourceFile.getAbsolutePath().equals(destinationFile.getAbsolutePath()))
			return false;
		if (destinationFile.exists()) {
			if (deleteIfExist) {
				if (!destinationFile.delete())
					return false;
			} else
				return false;
		}
		return sourceFile.renameTo(destinationFile);
	}

	public static void forceFolderExist(String folderPath) throws IOException {
		forceFolderExist(new File(folderPath));
	}

	public static void forceFolderExist(File folder) throws IOException {
		if (!folder.exists()) {
			if (!folder.mkdirs())
				throw new IOException("Could not create folder " + folder.getPath());
		} else if (!folder.isDirectory())
			throw new IOException("A file with name " + folder.getPath() + " already exist");
	}
	public static void safeClose(InputStream is)
	  {
	    try
	    {
	      if (is != null) {
	        is.close();
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  public static void safeClose(OutputStream os)
	  {
	    try
	    {
	      if (os != null) {
	        os.close();
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  public static void safeClose(RandomAccessFile fl)
	  {
	    try
	    {
	      if (fl != null) {
	        fl.close();
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }
	  public static boolean copyFile(String strSrc, String strDest)
	  {
	    FileInputStream isSrc = null;
	    FileOutputStream osDest = null;
	    try
	    {
	      File flDest = new File(strDest);
	      if (flDest.exists()) {
	        flDest.delete();
	      }
	      File flSrc = new File(strSrc);
	      if (!flSrc.exists()) {
	        return false;
	      }
	      isSrc = new FileInputStream(flSrc);
	      osDest = new FileOutputStream(flDest);
	      
	      byte[] btData = new byte[65536];
	      int iLength;
	      while ((iLength = isSrc.read(btData)) != -1) {
	        osDest.write(btData, 0, iLength);
	      }
	      return true;
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      return false;
	    }
	    finally
	    {
	      safeClose(isSrc);
	      safeClose(osDest);
	    }
	  }
}
