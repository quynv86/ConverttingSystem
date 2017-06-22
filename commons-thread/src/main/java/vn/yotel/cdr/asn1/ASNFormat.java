package vn.yotel.cdr.asn1 ;

import java.io.* ;
import java.util.* ;
import vn.yotel.thread.ManageableThread;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Thai Hoang Hiep
 * @version 1.0
 */

public abstract class ASNFormat
{
	////////////////////////////////////////////////////////
	// Data
	////////////////////////////////////////////////////////
	public ASNData mdatRoot = null ;
	public ASNDefinition mdefRoot = null ;
	public String strSeperator = ";" ;
	public String strReplace = "," ;
	////////////////////////////////////////////////////////
	// Constant
	////////////////////////////////////////////////////////
	public static int HUAWEI_CHAR = -1 ;
	public static int HUAWEI_NUMBER = 0 ;
	public static int HUAWEI_DATE = 1 ;
	public static int HUAWEI_TIME = 2 ;
	public static int HUAWEI_DATE_TIME = 3 ;
	public static int HUAWEI_TIME_STAMP = 4 ;
	public static int HUAWEI_TBCD = 5 ;
	public static int HUAWEI_ADDR_STR = 6 ;
	public static int HUAWEI_IP = 7 ;
	public static int HUAWEI_OCTET = 8 ;
	public static int HUAWEI_CELL_ID = 9 ;
	public static int HUAWEI_IC_ROUTE = 10 ;
	public static int HUAWEI_OG_ROUTE = 11 ;
	public static int HUAWEI_OG_NUMBER = 12 ;
	public static int HUAWEI_ADDRESS_STRING = 13 ;
	public static int HUAWEI_TKGP_Name = 14 ;
	public static int NETWORK_CALL_REFERENCE = 15 ;
	// add IA5String format: haimt
	public static int IA5_STRING = 16;
	public static int BOOLEAN = 17;
	// end

	////////////////////////////////////////////////////////
	// 2013/11/25
	// Them truong dung chung trong BSSConvertHuaweiGPRS
	////////////////////////////////////////////////////////
	public String mstrTempDir;
	public String p_LocDir;
	public String p_mstrHeader;
	public String mstrFileNameFormat = "";

	public static int BUF_SIZE = 500 * 1024;
	////////////////////////////////////////////////////////
	// Abstract function
	////////////////////////////////////////////////////////
	public abstract String format(ASNDefinition def,ASNData dat) ;

	public abstract byte[] parse(ASNDefinition def,String strData) ;

	//////////////////////////////////////////////////////
	// add: HaiMT
	// datasource param
	/////////////////////////////////////////////////////
	protected String mstrDatasourceID;

	////////////////////////////////////////////////////////
	// Open data source
	////////////////////////////////////////////////////////
	public void open(ASNData dat)
	{
		mdatRoot = dat ;
		mdefRoot.compile(mdatRoot) ;
	}

	////////////////////////////////////////////////////////
	// Purpose: Create cursor from query command
	// Inputs: String store query command
	// Outputs: ASNCursor created
	// Author: HiepTH
	////////////////////////////////////////////////////////
	public void createCursor(ASNCursor cursor,String strQuery) throws IOException
	{
		// Get table name
		int iIndex = 0 ;
		int iLastIndex = iIndex ;
		iIndex = strQuery.indexOf('(') ;
		String strTableName = strQuery.substring(iLastIndex,iIndex) ;
		iLastIndex = iIndex + 1 ;
		int iResult = strTableName.indexOf('!') ;
		if(iResult >= 0)
		{
			String strChoiceName = strTableName.substring(iResult + 1,strTableName.length()) ;
			strTableName = strTableName.substring(0,iResult) ;
			cursor.mdefCursor = buildCursorList(strTableName) ;
			cursor.mdefChoice = cursor.mdefCursor[cursor.mdefCursor.length - 1].getChild(strChoiceName) ;
		}
		else
		{
			cursor.mdefCursor = buildCursorList(strTableName) ;
			cursor.mdefChoice = cursor.mdefCursor[cursor.mdefCursor.length - 1] ;
		}

		if(cursor.mdefCursor == null || cursor.mdefCursor.length <= 0)
		{
			throw new IOException("Empty cursor") ;
		}

		// Get field list
		iIndex = strQuery.indexOf(')') ;
		String strFieldList = strQuery.substring(iLastIndex,iIndex) ;
		iIndex = 0 ;
		iLastIndex = iIndex ;

		// Build field list
		Vector vctFieldList = new Vector() ;
		String strFieldName ;
		while((iIndex = strFieldList.indexOf(',',iLastIndex)) >= 0)
		{
			strFieldName = strFieldList.substring(iLastIndex,iIndex) ;
			vctFieldList.add(strFieldName) ;
			iLastIndex = iIndex + 1 ;
		}
		strFieldName = strFieldList.substring(iLastIndex,strFieldList.length()) ;
		vctFieldList.add(strFieldName) ;

		// Analyse field name
		int iFieldCount = vctFieldList.size() ;
		cursor.mdefFieldList = new ASNDefinition[iFieldCount] ;
		for(iIndex = 0 ;iIndex < vctFieldList.size() ;iIndex++)
		{
			strFieldName = (String)vctFieldList.elementAt(iIndex) ;
			if(strFieldName.toUpperCase().equals("NULL"))
			{
				cursor.mdefFieldList[iIndex] = null ;
			}
			else if(strFieldName.startsWith("'") && strFieldName.endsWith("'"))
			{
				ASNDefinition def = new ASNDefinition() ;
				def.miType = ASNDefinition.ASN_CONST ;
				def.mstrValue = strFieldName.substring(1,strFieldName.length() - 1) ;
				cursor.mdefFieldList[iIndex] = def ;
			}
			else
			{
				cursor.mdefFieldList[iIndex] = cursor.mdefChoice.getChild(strFieldName) ;
				if(cursor.mdefFieldList[iIndex] == null)
				{
					throw new IOException("Field " + strFieldName + " not found in asn description") ;
				}
			}
		}

		// Initialize
		cursor.mdatFieldList = new ASNData[cursor.mdefFieldList.length] ;
		cursor.mstrFieldList = new String[cursor.mdefFieldList.length] ;
		cursor.mdatCursor = new ASNData[cursor.mdefCursor.length] ;
		cursor.mfmt = this ;
	}

	////////////////////////////////////////////////////////
	// Purpose: Build cursor list
	// Inputs: String contain cursor path
	// Outputs: Exception throw if error occured
	// Author: HiepTH
	////////////////////////////////////////////////////////
	private ASNDefinition[] buildCursorList(String strCursor) throws IOException
	{
		Vector vtReturn = new Vector() ;
		ASNDefinition def = null ;
		int iIndex = 0 ;
		while((iIndex = strCursor.indexOf('.')) >= 0)
		{
			String strCursorName = strCursor.substring(0,iIndex) ;
			strCursor = strCursor.substring(iIndex + 1,strCursor.length()) ;
			if(def == null)
			{
				def = mdefRoot.getChild(strCursorName) ;
			}
			else
			{
				def = def.getChild(strCursorName) ;
			}
			if(def == null)
			{
				throw new IOException(strCursorName + " not found in path") ;
			}
			vtReturn.addElement(def) ;
		}
		if(def == null)
		{
			def = mdefRoot.getChild(strCursor) ;
		}
		else
		{
			def = def.getChild(strCursor) ;
		}
		if(def == null)
		{
			throw new IOException(strCursor + " not found in path") ;
		}
		vtReturn.addElement(def) ;
		ASNDefinition[] defReturn = new ASNDefinition[vtReturn.size()] ;
		for(iIndex = 0 ;iIndex < defReturn.length ;iIndex++)
		{
			defReturn[iIndex] = (ASNDefinition)vtReturn.elementAt(iIndex) ;
		}
		return defReturn ;
	}

	////////////////////////////////////////////////////////
	// Purpose: Build query command
	// Inputs: ASNCursor store query statement
	// Outputs: Exception throw if error occured
	// Author: HiepTH
	////////////////////////////////////////////////////////
	public void query(ASNCursor cursor) throws IOException
	{
		// Contruct cursor
		cursor.mfmt = this ;
		if(cursor.mdefCursor.length <= 0)
		{
			throw new IOException("Emty cursor") ;
		}

		// Construct first element
		cursor.mdatCursor[0] = mdatRoot.getChildOneLevel(cursor.mdefCursor[0]) ;
		cursor.miLevel = 0 ;
	}

	////////////////////////////////////////////////////////
	// Purpose: Build query command
	// Inputs: String store query command
	// Outputs: ASNCursor created, exception throw if error occured
	// Author: HiepTH
	////////////////////////////////////////////////////////
	public void query(ASNCursor cursor,String strQuery) throws IOException
	{
		createCursor(cursor,strQuery) ;
		query(cursor) ;
	}

	////////////////////////////////////////////////////////
	// Purpose: Process single file, override function
	// Inputs: File name, file id
	// Outputs: Exception throw if error occured,
	// Author: HiepTH
	////////////////////////////////////////////////////////
	public int convert(String strSrcFile,String strDesFile,Hashtable prtParameter) throws Exception
	{
		return 0 ;
	}

	////////////////////////////////////////////////////////
	// Purpose: Process single file, override function
	// Inputs: File name, file id
	// Outputs: Exception throw if error occured,
	// Author: HaiMT
	////////////////////////////////////////////////////////
	public int convert(String strSrcFile,String strDesFile,ManageableThread parentThread) throws Exception
	{
		return 0;
	}

	////////////////////////////////////////////////////////
	public void compile(InputStream inDefinition) throws IOException
	{
		mdefRoot = new ASNDefinition(inDefinition) ;
		compile(mdefRoot) ;
	}

	////////////////////////////////////////////////////////
	// Fill format id for def
	////////////////////////////////////////////////////////
	public void compile(ASNDefinition def)
	{
		ASNDefinition pChild = def.mpFirstChild ;
		while(pChild != null)
		{
			// Fill format ID
			if(pChild.isTypeOf("INTEGER") || pChild.isTypeOf("ENUMERATED"))
			{
				pChild.miFormatID = HUAWEI_NUMBER ;
			}
			else if(pChild.isTypeOf("TBCD_STRING"))
			{
				pChild.miFormatID = HUAWEI_TBCD ;
			}

			else if(pChild.isTypeOf("OriginationNumber"))
			{
				pChild.miFormatID = HUAWEI_OG_NUMBER ;
			}
			else if(pChild.isTypeOf("AddressString"))
			{
				pChild.miFormatID = HUAWEI_ADDR_STR ;
			}
			else if(pChild.isTypeOf("Date"))
			{
				pChild.miFormatID = HUAWEI_DATE ;
			}
			else if(pChild.isTypeOf("Time"))
			{
				pChild.miFormatID = HUAWEI_TIME ;
			}
			else if(pChild.isTypeOf("TimeStamp"))
			{
				pChild.miFormatID = HUAWEI_TIME_STAMP ;
			}
			else if(pChild.isTypeOf("OCTET_STRING"))
			{
				pChild.miFormatID = HUAWEI_OCTET ;
			}
			else if(pChild.isTypeOf("IMSI") || pChild.isTypeOf("IMEI"))
			{
				pChild.miFormatID = HUAWEI_TBCD ;
			}
			else if(pChild.isTypeOf("CELL_ID"))
			{
				pChild.miFormatID = HUAWEI_CELL_ID ;
			}
			else if(pChild.isTypeOf("HUAWEI_IC_ROUTE"))
			{
				pChild.miFormatID = HUAWEI_IC_ROUTE ;
			}
			else if(pChild.isTypeOf("HUAWEI_OG_ROUTE"))
			{
				pChild.miFormatID = HUAWEI_OG_ROUTE ;
			}
			else if(pChild.isTypeOf("IPAddress") ||
				pChild.mstrName.equals("iPBinV4Address") ||
				pChild.mstrName.equals("iPBinV6Address"))
			{
				pChild.miFormatID = HUAWEI_IP ;
			}
			else if(pChild.isTypeOf("RecordingEntity"))
			{
				pChild.miFormatID = HUAWEI_ADDRESS_STRING ;
			}
			else if(pChild.isTypeOf("TKGPName"))
			{
				pChild.miFormatID = HUAWEI_TKGP_Name ;
			}
			else if(pChild.isTypeOf("NetworkCallReference"))
			{
				pChild.miFormatID = NETWORK_CALL_REFERENCE ;
			}
			else if(pChild.isTypeOf("IA5String"))
			{
				pChild.miFormatID = IA5_STRING ;
			}
			else if(pChild.isTypeOf("BOOLEAN"))
			{
				pChild.miFormatID = BOOLEAN ;
			}
			else
			{
				pChild.miFormatID = HUAWEI_CHAR ;
			}

			// Compile child
			compile(pChild) ;
			pChild = pChild.mpNext ;
		}
	}
}
