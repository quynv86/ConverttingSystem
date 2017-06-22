package vn.yotel.cdr.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import vn.yotel.cdr.asn1.ASNCursor;
import vn.yotel.cdr.asn1.ASNData;
import vn.yotel.cdr.asn1.ASNDefinition;
import vn.yotel.cdr.asn1.ASNFormat;
import vn.yotel.cdr.asn1.ASNUtil;
import vn.yotel.cdr.asn1.Buffer;
import vn.yotel.cdr.util.CDRBinUtil;
import vn.yotel.cdr.util.TextFile;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.FileUtil;
import vn.yotel.commons.util.StringUtils;
import vn.yotel.thread.ManageableThread;


/**
 * <p>
 * Title: VMS-B3
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: FPT-FSS
 * </p>
 * 
 * @author ThanhTT
 * @version 3.0
 */
public class HuaweiFormat extends ASNFormat
{
	// //////////////////////////////////////////////////////
	// Constant
	// //////////////////////////////////////////////////////
	public int mintFileID = 0;
	public int mintIndexType = 1;
	public int mintIndexCallType = 2;
	public int mintIndexIMSI = 3;
	public int mintIndexStaDatetime = 5;
	public int mintIndexDuration = 6;
	public int mintIndexCallingNumber = 7;
	public int mintIndexCalledNumber = 8;
	public int mintIndexTrunkINName = 12;
	public int mintIndexTrunkOUTName = 13;
	public int mintIndexConnectedNumber = 16;
	public int mintIndexTranslatedNumber = 17;
	public int mintIndexCauseForTerm = 25;
	public int mintIndexFieName = 27;
	public int mintNetworkCallReference = 31;
	public int mintIndexReleasetime = 33;
	public int mintIndexRecordNumber = 34;
	public int mintIndexDiagnostics = 35;

	// File Calls
	public TextFile fileCalls;
//	public TextFile fileCallsError;

	public long p_TotRec;

	public long p_AnnRec;

	public long p_Calls;
	public long p_CallsError;

	public long p_RoamRec;

	public int mRecordCount = 0;

	public int miReduceHeader = 0;

	private String mstrSourceFile = "";

	class HuaweiCursor extends ASNCursor
	{
		public String leftPad(String strValue, int intLength, String strPad)
		{
			String strResult = strValue;
			for (int j = 1; strResult.length() < intLength; j++)
			{
				strResult = strPad + strResult;
			}
			return strResult;
		}

		// //////////////////////////////
		public int getDec(byte[] bt)
		{
			int intResult = bt[0];
			int intTemp = 256;
			for (int j = 1; j <= bt.length - 1; j++)
			{
				intResult = intResult + intTemp * bt[j];
				intTemp = intTemp * 256;
			}
			return intResult;
		}

		// /////////////////////////////////
		public boolean isNationalCall(String strCalledNumber)
		{
			if (strCalledNumber.length() > 3)
			{
				String strNational = strCalledNumber.substring(1, 2);
				int iToInteger = Integer.parseInt(strNational, 16);
				if ((iToInteger & 0x07) == 1)
				{
					return true;
				}
			}
			return false;
		}

		private String formatDate(String string)
		{
			if (string.length() > 9)
			{
				String year = "20" + string.substring(0, 2);
				String month = string.substring(2, 4);
				String day = string.substring(4, 6);
				String hour = string.substring(6, 8);
				String minute = string.substring(8, 10);
				String second = string.substring(10, 12);
				return year + "/" + month + "/" + day + " " + hour + ":"
					+ minute + ":" + second;
			}
			else
			{
				return string;
			}
		}

		public void storeRecord(OutputStream out, String strSeperator)
			throws IOException
		{
			mstrFieldList[mintIndexStaDatetime] = formatDate(mstrFieldList[mintIndexStaDatetime]);
			mstrFieldList[mintIndexReleasetime] = formatDate(mstrFieldList[mintIndexReleasetime]);

			// Fill File_Name Field
			mstrFieldList[mintIndexFieName] = mstrSourceFile;

			// Calling number
			mstrFieldList[mintIndexCallingNumber] = CDRBinUtil.formatCallingNumber(mstrFieldList[mintIndexCallingNumber].trim());

			// Called number
			mstrFieldList[mintIndexCalledNumber] = CDRBinUtil.formatCalledNumber(mstrFieldList[mintIndexCalledNumber].trim());

			// Write to string
			String strValue = "";
			for (int i = 0; i < mstrFieldList.length; i++)
			{
				strValue += mstrFieldList[i] + ";";
			}
			// Write to file
			try
			{
				/*
				 * CauseForTerm: 3 - unsuccessfulCallAttempt ->
				 * add error file khac 3 thi add to fileCalls
				 */
				// if(!mstrFieldList[mintIndexCauseForTerm].equals("3"))
				// {
				fileCalls.addText(strValue.substring(0, strValue.length() - 1));
				// }
				// else
				// {
				// fileCallsError.addText(strValue.substring(0,
				// strValue.length() - 1));
				// }
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	};

	protected HuaweiCursor curMOCALL = new HuaweiCursor();
	protected HuaweiCursor curMTCALL = new HuaweiCursor();
	protected HuaweiCursor curRoamingCALL = new HuaweiCursor();
	protected HuaweiCursor curTransitCALL = new HuaweiCursor();
	protected HuaweiCursor curIncGatewayCALL = new HuaweiCursor();
	protected HuaweiCursor curOugGatewayCALL = new HuaweiCursor();
	protected HuaweiCursor curFWCALL = new HuaweiCursor();
	protected HuaweiCursor curMOSMS = new HuaweiCursor();
	protected HuaweiCursor curMTSMS = new HuaweiCursor();

	// //////////////////////////////////////////////////////
	class HuaweiData extends ASNData
	{
		// //////////////////////////////////////////////////////
		public HuaweiData()
		{
		}

		// //////////////////////////////////////////////////////
		public HuaweiData(InputStream in) throws IOException
		{
			load(in);
		}

		// ///////////////////////////////////////////////////////
		public void load(InputStream in) throws IOException
		{
			Buffer buf = new Buffer(in);
			buf = skipHeader(buf);
			for (buf.miBufferIndex = 0; buf.miBufferIndex < buf.miLength;)
			{
				load(buf, 0);
				if (buf.miBufferIndex < buf.miLength)
				{
					if (btTerminatedSymbol >= 0)
					{
						buf.miBufferIndex = buf.lastIndexOfSymbol(
							buf.miBufferIndex, (char) btTerminatedSymbol);
						if (buf.miBufferIndex < 0)
						{
							buf.miBufferIndex = buf.miLength;
						}
					}
				}
			}

		}

		// ///////////////////////////////////////////////////
		public Buffer skipHeader(Buffer buf)
		{
			Buffer bufParent = skipTagID(buf);
			Buffer bufData = getData(bufParent);
			if (bufData != null)
			{
				return bufData;
			}
			return null;
		}

		// //////////////////////////////////////////////////
		public Buffer getData(Buffer buf)
		{
			byte btTemp = buf.mbtBuffer[buf.miBufferIndex++];
			byte btClass = (byte) ((btTemp & 0xc0) >>> 6);
			byte btConstructed = (byte) ((btTemp & 0xe0) >>> 5);
			btTemp &= 0x1f;
			int iTagID = btTemp;
			if (iTagID >= 31)
			{
				iTagID = 0;
				for (boolean bFound = false; !bFound;)
				{
					btTemp = buf.mbtBuffer[buf.miBufferIndex++];
					bFound = btTemp >>> 7 == 0;
					btTemp &= 0x7f;
					iTagID <<= 7;
					iTagID |= btTemp;
				}
			}
			btTemp = buf.mbtBuffer[buf.miBufferIndex++];
			int iLength = 0;
			if (btTemp == -128)
			{
				iLength = -1;
			}
			else if (btTemp < 0)
			{
				for (btTemp &= 0x7f; btTemp > 0; btTemp--)
				{
					iLength <<= 8;
					iLength += buf.mbtBuffer[buf.miBufferIndex++] & 0xff;
				}
			}

			else
			{
				iLength = btTemp & 0xff;
			}
			if (iTagID == 1)
			{
				return new Buffer(buf.getData(buf.miBufferIndex,
					buf.miBufferIndex += iLength));
			}
			else
			{
				return getData(new Buffer(buf.getData(
					buf.miBufferIndex += iLength, buf.miLength)));
			}
		}

		// //////////////////////////////////////////////////
		public Buffer skipTagID(Buffer buf)
		{
			byte btTemp = buf.mbtBuffer[buf.miBufferIndex++];
			byte btClass = (byte) ((btTemp & 0xc0) >>> 6);
			byte btConstructed = (byte) ((btTemp & 0xe0) >>> 5);
			btTemp &= 0x1f;
			int iTagID = btTemp;
			if (iTagID >= 31)
			{
				iTagID = 0;
				for (boolean bFound = false; !bFound;)
				{
					btTemp = buf.mbtBuffer[buf.miBufferIndex++];
					bFound = btTemp >>> 7 == 0;
					btTemp &= 0x7f;
					iTagID <<= 7;
					iTagID |= btTemp;
				}
			}
			btTemp = buf.mbtBuffer[buf.miBufferIndex++];
			int iLength = 0;
			if (btTemp == -128)
			{
				iLength = -1;
			}
			else if (btTemp < 0)
			{
				for (btTemp &= 0x7f; btTemp > 0; btTemp--)
				{
					iLength <<= 8;
					iLength += buf.mbtBuffer[buf.miBufferIndex++] & 0xff;
				}
			}

			else
			{
				iLength = btTemp & 0xff;
			}

			return new Buffer(buf.getData(buf.miBufferIndex,
				buf.miBufferIndex += iLength));
		}
	}

	// //////////////////////////////////////////////////////
	// Constructor
	// //////////////////////////////////////////////////////
	public HuaweiFormat() throws IOException
	{
		// Get file dinh nghia
		InputStream in = getClass().getResourceAsStream(
			"/vn/yotel/cdr/format/huawei.asn1");
		// fill in => mdefRoot
		compile(in);
		in.close();
		// Create cursor
		createCursor(
			curMOCALL,
			"moCallRecord('1',"
				+ // 1. FILE_ID
					// 2. TYPE; 3. CALL_TYPE
				"'O',recordType,"
				+
				// 4. IMSI; 5. IMEI; 6. STA_DATETIME
				"servedIMSI,servedIMEI,answerTime,"
				+
				// 7. DURATION; 8. CALLING_NUMBER;9.
				// CALLED_NUMBER
				"callDuration,servedMSISDN,calledNumber,"
				+
				// 10. TAR_CLASS; 11. TS_CODE;12. CELL_ID
				"Null,null,globalAreaID,"
				+
				// 13. IC_ROUTE; 14. OG_ROUTE
				"mscIncomingROUTE.mscIncoming,mscOutgoingROUTE.mscOutgoing,"
				+
				// 15. CHARGE_INFO;16. SERVICE_KEY;17.
				// CONNECTEDNUMBER;
				"additionalChgInfo.AddChargeInfo,serviceKey,connectedNumber,"
				+
				// 18. TRANSLATEDNUMBER;19.
				// ORG_CALLING_NUMBER;20.
				// ORG_CALLED_NUMBER;
				"translatedNumber,callingNumber,calledNumber,"
				+
				// 21. MSCADDRESS;22. CALLREFERENCE;23.
				// ORGMSCADDRESS
				"mscAddress,callReference,mscAddress"
				+
				// 24. CAMELDESTINATIONNUMBER;
				",cAMELCallLegInformation.CAMELInformation.cAMELDestinationNumber"
				+
				// 25. SUPP_CODE;
				",supplServicesUsed.SuppServiceUsed.ssCode,"
				+
				// 26. CAUSEFORTERM; 27. SEQUENCENUMBER;28.
				// FILE_NAME;
				// 29. location
				"causeForTerm,sequenceNumber,'file_name',location.locationAreaCode,"
				+
				// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32.
				// NETWORK_CALL_REF
				// 33. GLOBAL_CALL_REF 34. releaseTime 35.
				// recordNumber
				"mscOutgoingROUTENumber,mscIncomingROUTENumber,networkCallReference,globalCallReference,releaseTime,recordNumber,"
				// 36. Diagnostics
				+ "diagnostics.gsm0408Cause)");
		createCursor(
			curMTCALL,
			"mtCallRecord('1',"
				+ // FILE_ID
					// TYPE,CALL_TYPE
				"'I',recordType,"
				+
				// IMSI,IMEI,STA_DATETIME
				"servedIMSI,servedIMEI,answerTime,"
				+
				// DURATION;CALLING_NUMBER;CALLED_NUMBER
				"callDuration,callingNumber,servedMSISDN,"
				+
				// TAR_CLASS;TS_CODE;CELL_ID
				"Null,null,globalAreaID,"
				+
				// IC_ROUTE;OG_ROUTE
				"mscIncomingROUTE.mscIncoming,mscOutgoingROUTE.mscOutgoing,"
				+
				// CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;
				"additionalChgInfo.AddChargeInfo,serviceKey,connectedNumber,"
				+
				// TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;
				"translatedNumber,callingNumber,servedMSISDN,"
				+
				// REC_ENT(De nhu vay cho
				// roaming);CALLREFERENCE;MSCADDRESS
				"mscAddress,callReference,mscAddress,"
				+
				// CAMELDESTINATIONNUMBER;
				"null,"
				+
				// SUPP_CODE;
				"null,"
				+
				// CAUSEFORTERM;SEQUENCENUMBER;FILE_NAME;location
				"causeForTerm,sequenceNumber,'file_name',location.locationAreaCode,"
				// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32.
				// NETWORK_CALL_REF
				// 33. GLOBAL_CALL_REF 34. releaseTime 35.
				// recordNumber
				+ "mscOutgoingROUTENumber,mscIncomingROUTENumber,networkCallReference,globalCallReference,releaseTime,recordNumber,"
				// 36. Diagnostics
				+ "diagnostics.gsm0408Cause)");

		createCursor(
			curFWCALL,
			"forwardCallRecord('1',"
				+ // FILE_ID
					// TYPE,CALL_TYPE
				"'D',recordType,"
				+
				// IMSI,IMEI,STA_DATETIME
				"servedIMSI,servedIMEI,answerTime,"
				+
				// DURATION;CALLING_NUMBER;CALLED_NUMBER
				"callDuration,servedMSISDN,calledNumber,"
				+
				// TAR_CLASS;TS_CODE;CELL_ID
				"Null,null,globalAreaID,"
				+
				// IC_ROUTE;OG_ROUTE
				"mscIncomingROUTE.mscIncoming,mscOutgoingROUTE.mscOutgoing,"
				+
				// CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;
				"additionalChgInfo.AddChargeInfo,serviceKey,connectedNumber,"
				+
				// TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;
				"translatedNumber,servedMSISDN,calledNumber,"
				+
				// REC_ENT(De nhu vay cho
				// roaming);CALLREFERENCE;MSCADDRESS
				"mscAddress,callReference,null"
				+
				// CAMELDESTINATIONNUMBER;
				",cAMELCallLegInformation.CAMELInformation.cAMELDestinationNumber"
				+
				// SUPP_CODE;
				",supplServicesUsed.SuppServiceUsed.ssCode,"
				+
				// CAUSEFORTERM;SEQUENCENUMBER;FILE_NAME;location
				"causeForTerm,sequenceNumber,'file_name',location.locationAreaCode,"
				// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32.
				// NETWORK_CALL_REF
				// 33. GLOBAL_CALL_REF 34 releaseTime 35.
				// recordNumber
				+ "mscOutgoingROUTENumber,mscIncomingROUTENumber,networkCallReference,globalCallReference,null,recordNumber,"
				// 36. Diagnostics
				+ "diagnostics.gsm0408Cause)");
		createCursor(curMOSMS, "moSMSRecord('1',"
			+ // FILE_ID
				// TYPE,CALL_TYPE
			"'M',recordType,"
			+
			// IMSI,IMEI,STA_DATETIME
			"servedIMSI,servedIMEI,originationTime,"
			+
			// DURATION;CALLING_NUMBER;CALLED_NUMBER
			"'0',servedMSISDN,destinationNumber,"
			+
			// TAR_CLASS;TS_CODE;CELL_ID
			"Null,serviceCentre,globalAreaID,"
			+
			// IC_ROUTE;OG_ROUTE
			"Null,Null,"
			+
			// CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;
			"additionalChgInfo.AddChargeInfo,null,null,"
			+
			// TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;
			"null,servedMSISDN,destinationNumber,"
			+
			// REC_ENT(De nhu vay cho
			// roaming);CALLREFERENCE;MSCADDRESS
			// "serviceCentre,callReference,null," +
			"recordingEntity,callReference,serviceCentre,"
			+ // serviceCenter add by Longbtt
				// CAMELDESTINATIONNUMBER
			"null,"
			+
			// SUPP_CODE;
			"null,"
			+
			// CAUSEFORTERM;SEQUENCENUMBER;28 FILE_NAME;29 location
			"null,null,'file_name',location.locationAreaCode,"
			// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32. NETWORK_CALL_REF
			// 33. GLOBAL_CALL_REF, 34 releaseTime 35. recordNumber
			// 36. Diagnostics
			+ "null,null,null,null,null,recordNumber,null)");
		createCursor(curMTSMS, "mtSMSRecord('1',"
			+ // FILE_ID
				// TYPE,CALL_TYPE
			"'N',recordType,"
			+
			// IMSI,IMEI,STA_DATETIME
			"servedIMSI,servedIMEI,deliveryTime,"
			+
			// DURATION;CALLING_NUMBER;CALLED_NUMBER
			"'0',origination,servedMSISDN,"
			+
			// TAR_CLASS;TS_CODE;CELL_ID
			"Null,serviceCentre,globalAreaID,"
			+
			// IC_ROUTE;OG_ROUTE
			"Null,Null,"
			+
			// CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;
			"additionalChgInfo.AddChargeInfo,null,null,"
			+
			// TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;
			"null,origination,servedMSISDN,"
			+
			// REC_ENT(De nhu vay cho
			// roaming);CALLREFERENCE;MSCADDRESS
			"recordingEntity,callReference,serviceCentre,"
			+ // recordingEntity add by Longbtt
				// CAMELDESTINATIONNUMBER
			"null,"
			+
			// SUPP_CODE;
			"null,"
			+
			// CAUSEFORTERM;SEQUENCENUMBER;28 FILE_NAME;29 location
			"null,null,'file_name',location.locationAreaCode,"
			// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32. NETWORK_CALL_REF
			// 33. GLOBAL_CALL_REF 34. releaseTime 35. recordNumber
			// 36. Diagnostics
			+ "null,null,null,null,null,recordNumber,null)");
		createCursor(
			curTransitCALL,
			"transitCallRecord('1',"
				+ // FILE_ID
					// TYPE,CALL_TYPE
				"'T',recordType,"
				+
				// IMSI,IMEI,STA_DATETIME
				"null,null,answerTime,"
				+
				// DURATION;CALLING_NUMBER;CALLED_NUMBER
				"callDuration,callingNumber,calledNumber,"
				+
				// TAR_CLASS;TS_CODE;CELL_ID
				"Null,null,null,"
				+
				// IC_ROUTE;OG_ROUTE
				"mscIncomingROUTE.mscIncoming,mscOutgoingROUTE.mscOutgoing,"
				+
				// CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;
				"additionalChgInfo.AddChargeInfo,null,translatedNumber,"
				+
				// TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;
				"translatedNumber,callingNumber,calledNumber,"
				+
				// REC_ENT(De nhu vay cho
				// roaming);CALLREFERENCE;MSCADDRESS
				"null,callReference,null,"
				+
				// CAMELDESTINATIONNUMBER
				"null,"
				+
				// SUPP_CODE;
				"null,"
				+
				// CAUSEFORTERM;SEQUENCENUMBER;28 FILE_NAME;29
				// location
				"causeForTerm,sequenceNumber,'file_name',null,"
				// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32.
				// NETWORK_CALL_REF
				// 33. GLOBAL_CALL_REF 34.releaseTime 35.
				// recordNumber
				+ "mscOutgoingROUTENumber,mscIncomingROUTENumber,networkCallReference,globalCallReference,releaseTime,recordNumber,"
				// 36. Diagnostics
				+ "diagnostics.gsm0408Cause)");

		createCursor(
			curIncGatewayCALL,
			"incGatewayRecord('2',"
				+ // FILE_ID
					// TYPE,CALL_TYPE
				"'IT',recordType,"
				+
				// IMSI,IMEI,STA_DATETIME
				"null,null,answerTime,"
				+
				// DURATION;CALLING_NUMBER;CALLED_NUMBER
				"callDuration,callingNumber,calledNumber,"
				+
				// TAR_CLASS;TS_CODE;CELL_ID
				"Null,null,null,"
				+
				// IC_ROUTE;OG_ROUTE
				"mscIncomingROUTE.mscIncoming,mscOutgoingROUTE.mscOutgoing,"
				+
				// CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;
				"additionalChgInfo.AddChargeInfo,null,null,"
				+
				// TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;
				"null,callingNumber,calledNumber,"
				+
				// REC_ENT(De nhu vay cho
				// roaming);CALLREFERENCE;MSCADDRESS
				"null,callReference,null,"
				+
				// CAMELDESTINATIONNUMBER
				"null,"
				+
				// SUPP_CODE
				"null,"
				+
				// CAUSEFORTERM;SEQUENCENUMBER;28 FILE_NAME;29
				// location
				"null,null,'file_name',location.locationAreaCode,"
				// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32.
				// NETWORK_CALL_REF
				// 33. GLOBAL_CALL_REF 34. releaseTime 35.
				// recordNumber
				+ "mscOutgoingROUTENumber,mscIncomingROUTENumber,networkCallReference,globalCallReference,releaseTime,recordNumber,"
				// 36. Diagnostics
				+ "diagnostics.gsm0408Cause)");

		createCursor(
			curOugGatewayCALL,
			"outGatewayRecord('2',"
				+ // 0. FILE_ID
					// 1. TYPE, 2. CALL_TYPE
				"'OT',recordType,"
				+
				// 3. IMSI, 4. IMEI, 5. STA_DATETIME
				"null,null,answerTime,"
				+
				// 6. DURATION;7. CALLING_NUMBER; 8.
				// CALLED_NUMBER
				"callDuration,callingNumber,calledNumber,"
				+
				// 9. TAR_CLASS;10. TS_CODE; 11. CELL_ID
				"Null,null,null,"
				+
				// 12. IC_ROUTE; 13. OG_ROUTE
				"mscIncomingROUTE.mscIncoming,mscOutgoingROUTE.mscOutgoing,"
				+
				// 14. CHARGE_INFO;15. SERVICE_KEY; 16.
				// CONNECTEDNUMBER;
				"additionalChgInfo.AddChargeInfo,null,null,"
				+
				// 17. TRANSLATEDNUMBER;18.
				// ORG_CALLING_NUMBER;19.
				// ORG_CALLED_NUMBER;
				"null,callingNumber,calledNumber,"
				+
				// 20. REC_ENT(De nhu vay cho roaming);21.
				// CALLREFERENCE;22. MSCADDRESS
				"null,callReference,null,"
				+
				// 23. CAMELDESTINATIONNUMBER
				"null,"
				+
				// 24. SUPP_CODE
				"null,"
				+
				// 25. CAUSEFORTERM; 26. SEQUENCENUMBER;27.
				// FILE_NAME;
				// 28. location
				"null,null,'file_name',location.locationAreaCode,"
				// 30. OG_ROUTE_ID 31. IC_ROUTE_ID 32.
				// NETWORK_CALL_REF
				// 33. GLOBAL_CALL_REF 34.releaseTime 35.
				// recordNumber
				+ "mscOutgoingROUTENumber,mscIncomingROUTENumber,networkCallReference,globalCallReference,releaseTime,recordNumber,"
				// 36. Diagnostics
				+ "diagnostics.gsm0408Cause)");
	}

	// //////////////////////////////////////////////////////
	// convert Huawei file
	// //////////////////////////////////////////////////////
	public int convert(File inputFile,
		ManageableThread parentThread) throws Exception
	{
		String strFileFormat = "";
		try
		{
			// force exist all tempFolder
			String strFileID = this.getFileId();
			// Load file into memory
			HuaweiData root = null;
			FileInputStream in = null;
			try
			{
				in = new FileInputStream(inputFile);

				// Load file
				root = new HuaweiData(in);
				// Correct gprs file
				correct(root);
				// Decode file
				open(root);
			}
			catch (Exception ex)
			{
				throw ex;
			}
			finally
			{
				if (in != null)
				{
					in.close();
				}
			}
			// create and open file for store value

			strFileFormat = formatFileName(inputFile.getName());

			mstrSourceFile = inputFile.getName();

			// File calls
			String strFileName = mstrTempDir + strFileFormat;
			//String strFileNameError = mstrTempDir + "/ERROR/" + strFileFormat;
//			String strFileNameError = mstrTempDir + "ERROR/" + strFileFormat;
			fileCalls = new TextFile();
			fileCalls.openFile(strFileName, BUF_SIZE); // 500K
			// //////////////////////////////////////////////////////
			// Write output with format
			// //////////////////////////////////////////////////////
			int iRecordCount = 0;
			// FileOutputStream out = new
			// FileOutputStream(strDesFile);
			if (!p_mstrHeader.equals(""))
			{
				// File Call
				fileCalls.addText(p_mstrHeader);
				miReduceHeader = 1;
			}
			// //////////////////////////////////////////////////////
			curMOCALL.mdefFieldList[0].mstrValue = strFileID;
			curMTCALL.mdefFieldList[0].mstrValue = strFileID;
			// curRoamingCALL.mdefFieldList[0].mstrValue =
			// strFileID;
			curTransitCALL.mdefFieldList[0].mstrValue = strFileID;
			curIncGatewayCALL.mdefFieldList[0].mstrValue = strFileID;
			curOugGatewayCALL.mdefFieldList[0].mstrValue = strFileID;
			curFWCALL.mdefFieldList[0].mstrValue = strFileID;
			curMOSMS.mdefFieldList[0].mstrValue = strFileID;
			curMTSMS.mdefFieldList[0].mstrValue = strFileID;

			query(curMOCALL);
			iRecordCount += curMOCALL.storeText(null, strSeperator);
			query(curMTCALL);
			iRecordCount += curMTCALL.storeText(null, strSeperator);
			// query(curRoamingCALL);
			// iRecordCount +=
			// curRoamingCALL.storeText(out,strSeperator);
			query(curTransitCALL);
			iRecordCount += curTransitCALL.storeText(null, strSeperator);
			query(curIncGatewayCALL);
			iRecordCount += curIncGatewayCALL.storeText(null, strSeperator);
			query(curOugGatewayCALL);
			iRecordCount += curOugGatewayCALL.storeText(null, strSeperator);
			query(curFWCALL);
			iRecordCount += curFWCALL.storeText(null, strSeperator);
			query(curMOSMS);
			iRecordCount += curMOSMS.storeText(null, strSeperator);
			query(curMTSMS);
			iRecordCount += curMTSMS.storeText(null, strSeperator);
			// //////////////////////////////////////////////////////
			// Release
			// //////////////////////////////////////////////////////

			p_Calls = fileCalls.getCount() - miReduceHeader;
			fileCalls.closeFile();
			renameFile(mstrTempDir + strFileFormat, p_LocDir + strFileFormat, true);
//			renameFile(mstrTempDir + "ERROR/" + strFileFormat, p_LocDir + "ERROR/" + strFileFormat, true);
//			renameFile(mstrTempDir + "/ERROR/" + strFileFormat, p_LocDir + "/ERROR/" + strFileFormat, true);

			return iRecordCount;
		}
		catch (Exception exConvertFile)
		{
			throw exConvertFile;
		}
		finally
		{
			if (fileCalls != null)
			{
				fileCalls.closeFile();
			}
		}
	}

	protected void moveAllFile(String strFileName, String strSourceDir,
		String strDesDir, String[] strArrChildDir) throws Exception
	{
		// roaming la truong hop dac biet , ko move tai day
		for (int i = 0; i < strArrChildDir.length; i++)
		{
			// if(!strArrChildDir[i].equals("/Roam/"))
			// {
			if (!FileUtil.renameFile(strSourceDir + strArrChildDir[i]
				+ strFileName, strDesDir + strArrChildDir[i] + strFileName))
			{
				throw new Exception("Can not rename File " + strSourceDir
					+ strArrChildDir[i] + strFileName + " to " + strDesDir
					+ strArrChildDir[i] + strFileName);
			}
			// }
		}
	}

	protected void createAndOpenAllFile()
	{

	}

	// //////////////////////////////////////////////////////
	// correct Huawei file
	// //////////////////////////////////////////////////////
	public void correct(ASNData root)
	{
		ASNData pChild = root.mpFirstChild;
		while (pChild != null)
		{
			if (pChild.mpFirstChild != null
				&& pChild.mpFirstChild.mbtData != null)
			{
				pChild.miTagID = pChild.mpFirstChild.mbtData[0];
			}
			pChild = pChild.mpNext;
		}
	}

	// //////////////////////////////////////////////////////
	// Format data field
	// //////////////////////////////////////////////////////
	public String format(ASNDefinition def, ASNData dat)
	{
		if (dat.mbtData != null)
		{
			if (def.miFormatID == HUAWEI_NUMBER)
			{
				return ASNUtil
					.formatInteger(dat.mbtData, 0, dat.mbtData.length);
			}
			else if (def.miFormatID == HUAWEI_DATE)
			{
				return ASNUtil.formatDate(dat.mbtData, 0, dat.mbtData.length);
			}
			else if (def.miFormatID == HUAWEI_TIME)
			{
				return ASNUtil.formatTime(dat.mbtData, 0, dat.mbtData.length);
			}
			else if (def.miFormatID == HUAWEI_TBCD)
			{
				return ASNUtil.formatTBCD(dat.mbtData, 0, dat.mbtData.length);
			}
			else if (def.miFormatID == HUAWEI_ADDR_STR)
			{
				return ASNUtil.formatAddressString(dat.mbtData, 0,
					dat.mbtData.length);
			}
			else if (def.miFormatID == HUAWEI_IP)
			{
				return ASNUtil.formatIPAddress(dat.mbtData, 0,
					dat.mbtData.length);
			}
			else if (def.miFormatID == HUAWEI_OCTET)
			{
				return ASNUtil.formatBCD(dat.mbtData, 0, dat.mbtData.length);
			}
			else if (def.miFormatID == NETWORK_CALL_REFERENCE)
			{
				String strNetworkCallReference = "";
				for (int i = 0; i < 5; i++)
				{
					if (String.valueOf(
						Integer.parseInt(
							ASNUtil.formatBCD(dat.mbtData, i, 1), 16))
						.length() == 1)
					{
						strNetworkCallReference += "00"
							+ String.valueOf(Integer.parseInt(
								ASNUtil.formatBCD(dat.mbtData, i, 1),
								16));
					}
					else if (String.valueOf(
						Integer.parseInt(
							ASNUtil.formatBCD(dat.mbtData, i, 1), 16))
						.length() == 2)
					{
						strNetworkCallReference += "0"
							+ String.valueOf(Integer.parseInt(
								ASNUtil.formatBCD(dat.mbtData, i, 1),
								16));
					}
					else if (String.valueOf(
						Integer.parseInt(
							ASNUtil.formatBCD(dat.mbtData, i, 1), 16))
						.length() == 3)
					{
						strNetworkCallReference += String.valueOf(Integer
							.parseInt(ASNUtil.formatBCD(dat.mbtData, i, 1),
								16));
					}
					else
					{
						strNetworkCallReference += String.valueOf(Integer
							.parseInt(ASNUtil.formatBCD(dat.mbtData, i, 1),
								16));
					}
				}
				return strNetworkCallReference;
			}
			else if (def.miFormatID == HUAWEI_CELL_ID)
			{
				String strCountryCode = ASNUtil.formatTBCD(dat.mbtData, 0, 3);

				String strLac = ASNUtil.formatBCD(dat.mbtData, 3, 2);
				String strCellID = ASNUtil.formatBCD(dat.mbtData, 5,
					dat.mbtData.length - 5);

				strLac = String.valueOf(Integer.parseInt(strLac, 16));
				strCellID = String.valueOf(Integer.parseInt(strCellID, 16));

				return strLac + "_" + strCellID;
			}
			return StringUtils.replaceAll(new String(dat.mbtData), strSeperator,
				strReplace);
		}
		return null;
	}

	// //////////////////////////////////////////////////////
	// Parse data
	// //////////////////////////////////////////////////////
	public byte[] parse(ASNDefinition def, String strData)
	{
		return new byte[0];
	}

	// //////////////////////////////////////////////////////
	// Reverse BCD
	// //////////////////////////////////////////////////////
	public String reverseBCD(String strSource)
	{
		String strResult = "";
		char[] chSource = strSource.toCharArray();
		int i = 0;
		while (i < chSource.length)
		{
			char ch = chSource[i];
			chSource[i] = chSource[i + 1];
			chSource[i + 1] = ch;
			i = i + 2;
		}
		strResult = new String(chSource);
		return strResult;
	}

	// //////////////////////////////////////////////////////
	// Test function
	// //////////////////////////////////////////////////////
	public static void main(String[] args)
	{
		try
		{
			final HuaweiFormat fmt = new HuaweiFormat();
			long l1 = System.currentTimeMillis();
			String file = "D://Temp//BTKK20120724060100052150.dat";
			Properties prt = new Properties();
			prt.setProperty("FileID", "1");
			fmt.p_mstrHeader = "FILE_ID;TYPE;CALL_TYPE;IMSI;IMEI;STA_DATETIME;DURATION;CALLING_NUMBER;"
				+ "CALLED_NUMBER;TAR_CLASS;TS_CODE;CELL_ID;IC_ROUTE;OG_ROUTE;CHARGE_INFO;"
				+ "SERVICE_KEY;CONNECTEDNUMBER;TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;"
				+ "REC_ENT;CALLREFERENCE;MSCADDRESS;CAMELDESTINATIONNUMBER;SUPP_CODE;CAUSEFORTERM;"
				+ "SEQUENCENUMBER;FILE_NAME;AREA;OG_ROUTE_ID;IC_ROUTE_ID;NETWORKCALLREFERENCE;GLOBALCALLREFERENCE";
			fmt.mstrTempDir = "D://TempDir//";
			fmt.p_LocDir = "D://LocDir//";
			// ConvertHuaweiMobile cvhm = new ConvertHuaweiMobile
			// ();
			// cvhm.setParameter ("FileID", "1");
			// fmt.convert (file, file + "I", cvhm);
			long l2 = System.currentTimeMillis();
			System.out.println(l2 - l1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getParentFilePath(String strFilePath)
	{
		String strParentPath = strFilePath.substring(0,
			strFilePath.lastIndexOf("/") + 1);
		return strParentPath;
	}

	private String getFileName(String strFilePath)
	{
		String strFileName = strFilePath
			.substring(strFilePath.lastIndexOf("/") + 1);
		if (strFileName.lastIndexOf(".") > 0)
		{
			return strFileName.substring(0, strFileName.lastIndexOf("."));
		}
		return strFileName;
	}

	public String formatFileName(String strSrcFile) throws AppException
	{
		String strFileFormat = this.mstrFileNameFormat;
		String strFileName = strSrcFile
			.substring(strSrcFile.lastIndexOf("/") + 1);
		String strBaseFileName = strFileName.substring(0,
			strFileName.lastIndexOf("."));
		if (strFileFormat == null || strFileFormat.trim().length() == 0)
		{
			return strFileName;
		}
		strFileFormat = StringUtils.replaceAll(strFileFormat, "$FileName",
			strFileName);
		strFileFormat = StringUtils.replaceAll(strFileFormat, "$BaseFileName",
			strBaseFileName);
		return strFileFormat;
	}

	// QuyNV5 sua
	// QuyNV5 sua
	private void renameFile(String strSource, String strDestination,
			boolean blnException) throws Exception {
		if (!FileUtil.renameFile(strSource, strDestination, true)) {
			if (FileUtil.copyFile(strSource, strDestination)) {
				if (!FileUtil.deleteFile(strSource)) {
					if (blnException) {
						throw new Exception("Cannot delete source file "
								+ strSource);
					} else {
						return;
					}
				}
			} else {
				if (blnException) {
					throw new Exception("Cannot rename file " + strSource
							+ " to " + strDestination);
				} else {
					return;
				}
			}
		}
	}
	
	public long getTotalCalls()
	{
		return p_Calls;
	}

	public long getTotalCallsError()
	{
		return p_CallsError;
	}
	
	private String fileId;
	public String getFileId(){return fileId;};
	public void setFileId(String fileId){this.fileId = fileId;}
}
