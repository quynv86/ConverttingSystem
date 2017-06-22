package vn.yotel.cdr.util;

import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import vn.yotel.commons.util.StringUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Vu Anh Dung
 * @version 1.0
 */

public class CDRBinUtil {
	public static final int BYTE_LIMIT = 4000000;

	// ///////////////////////////////////////////////////////////
	/**
	 * Is IMSI Kind Author : DungVA Created Date : 22/03/2004 Return true if
	 * strIMSI startwith at least 1 element of vtIMSICard
	 */
	// ///////////////////////////////////////////////////////////
	public static boolean isIMSIKind(Vector vtIMSI, String strIMSI) {
		for (int i = 0; i < vtIMSI.size(); i++) {
			if (strIMSI.indexOf(((Vector) vtIMSI.elementAt(i)).elementAt(0)
					.toString()) == 0) {
				return true;
			}
		}
		return false;
	}

	// ///////////////////////////////////////////////////////////
	/**
	 * Get subString Seperated by strSeperator at Index iIndex Author : DungVA
	 * Created Date : 22/03/2004
	 */
	// ///////////////////////////////////////////////////////////
	public static String getStringContent(String strSource,
			String strSeperator, int iIndex) {
		int iCount = 0;
		int lastIndex = 0;
		while (iCount < iIndex) {
			lastIndex = strSource.indexOf(strSeperator, lastIndex) + 1;
			iCount++;
		}
		int nextIndex = strSource.indexOf(strSeperator, lastIndex);
		if (nextIndex == -1) {
			nextIndex = lastIndex;
		}
		return strSource.substring(lastIndex, nextIndex);
	}

	// ///////////////////////////////////////////////////////////
	/**
	 * Get subString Seperated by strSeperator at Index iIndex Author : DungVA
	 * Created Date : 22/03/2004
	 */
	// ///////////////////////////////////////////////////////////
	public static String RPad(String strSource, String strSeperator, int iLength) {
		int iPad = iLength - strSource.length();
		if (iPad > 0) {
			for (int i = 0; i < iPad; i++) {
				strSource += strSeperator;
			}
		}
		return strSource;
	}

	// ///////////////////////////////////////////////////////////
	/**
	 * Get subString Seperated by strSeperator at Index iIndex Author : DungVA
	 * Created Date : 22/03/2004
	 */
	// ///////////////////////////////////////////////////////////
	public static String LPad(String strSource, String strSeperator, int iLength) {
		int iPad = iLength - strSource.length();
		if (iPad > 0) {
			for (int i = 0; i < iPad; i++) {
				strSource = strSeperator + strSource;
			}
		}
		return strSource;
	}

	// /////////////////////////////////////////////////////////////////////////
	public static int getSequenceValue(Connection cn, String strSeqName)
			throws Exception {
		// cn = ThreadProcessor.getConnection();
		String strSQL = "SELECT " + strSeqName + ".NEXTVAL FROM DUAL";
		PreparedStatement pstmt = cn.prepareStatement(strSQL);
		ResultSet rs = pstmt.executeQuery(strSQL);
		rs.next();
		int ret = rs.getInt(1);
		pstmt.close();
		rs.close();
		// cn.close();
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////
	public static String getApParamValue(Connection cn, String strParName)
			throws Exception {
		// cn = ThreadProcessor.getConnection();
		String strSQL = "SELECT PAR_VALUE FROM AP_PARAM WHERE PAR_NAME = '"
				+ strParName.toUpperCase() + "'";
		PreparedStatement pstmt = cn.prepareStatement(strSQL);
		ResultSet rs = pstmt.executeQuery(strSQL);
		rs.next();
		String ret = rs.getString(1);
		pstmt.close();
		pstmt = null;
		rs.close();
		rs = null;
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////
	public static String nvl(String strValue, String strValueIfNull) {
		if ((strValue == null) || (strValue.equals(""))) {
			return strValueIfNull;
		}
		return strValue;
	}

	public static String formatCallingNumber(String strCallingNumber) {
		// Ham nay den luc thuc te chac chan phai viet lai

		String value = StringUtils.nvl(strCallingNumber, "");
		if (!value.equals("") && value.length() > 2) {
			// Cat bot dau 19
			value = value.substring(2);
		}

		if (value.startsWith("00") && value.length() > 3
				&& !value.startsWith("00856")) {
			return value;
		}

		if (value.startsWith("00856")) {
			value = "0" + value.substring(5);
		} else if (value.startsWith("856")) {
			value = "0" + value.substring(3);
		}

		if ((!value.startsWith("00")) && (!value.startsWith("0"))) {
			if (isNationalCall(strCallingNumber)) {
				value = "00" + value;
			} else {
				value = "0" + value;
			}
		}
		return value;
	}

	public static String formatCalledNumber(String strCalledNumber) {
		// Ham nay den luc thuc te chac chan phai viet lai
		String value = StringUtils.nvl(strCalledNumber, "");
		if (!value.equals("") && value.length() > 2) {
			// Cat bot dau 19
			value = value.substring(2);
		}
		if (value.startsWith("00") && value.length() > 3
				&& !value.startsWith("00856")) {
			return value;
		}

		if (value.startsWith("00856")) {
			value = "0" + value.substring(5);
		} else if (value.startsWith("856")) {
			value = "0" + value.substring(3);
		}
	    if ((!value.startsWith("00")) && (!value.startsWith("0"))) {
	        if (isNationalCall(strCalledNumber)) {
	          value = "00" + value;
	        } else {
	          value = "0" + value;
	        }
		}
		// Xet them cac dau so VOIP nua
		return value;
	}

	public static boolean isNationalCall(String strCalledNumber) {
		if (strCalledNumber.length() > 3) {
			String strNational = strCalledNumber.substring(1, 2);
			int iToInteger = Integer.parseInt(strNational, 16);
			if ((iToInteger & 0x7) == 1) {
				return true;
			}
		}
		return false;
	}

}
