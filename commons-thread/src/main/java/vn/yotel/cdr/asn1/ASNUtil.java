package vn.yotel.cdr.asn1;

import java.text.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Thai Hoang Hiep
 * @version 1.0
 */

public class ASNUtil
{
	////////////////////////////////////////////////////////
	// Constant
	////////////////////////////////////////////////////////
	public static DecimalFormat FORMAT_00 = new DecimalFormat("00");
	public static DecimalFormat FORMAT_SHARP = new DecimalFormat("#");
	public static SimpleDateFormat fmtDateTime = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	////////////////////////////////////////////////////////
	// Format integer
	////////////////////////////////////////////////////////
	public static String formatInteger(byte[] btValue,int iOffset,int iLength)
	{
		int iLastOffset = iOffset + iLength;
		if(btValue.length < iLastOffset || iLength < 1)
		{
			return "";
		}
		int iValue = 0;
		for(int iIndex = iOffset;iIndex < iLastOffset;iIndex++)
		{
			iValue <<= 8;
			iValue |= (btValue[iIndex] & 0xFF);
		}
		return String.valueOf(iValue);
	}

	////////////////////////////////////////////////////////
	// Format boolean
	////////////////////////////////////////////////////////
	public static String formatBoolean(byte btValue)
	{
		if(btValue == -1)
		{
			return "TRUE";
		}
		return "FALSE";
	}

	////////////////////////////////////////////////////////
	// Format Hex
	////////////////////////////////////////////////////////
	public static String formatHEX(byte[] btValue,int iOffset,int iLength)
	{
		int iLastOffset = iOffset + iLength;
		if(btValue.length < iLastOffset || iLength < 1)
		{
			return "";
		}

		StringBuffer value = new StringBuffer();
		for(int i = iOffset;i < iLastOffset;i++)
		{
			byte l,h;
			h = (byte)((btValue[i] & 0xF0) >>> 4);
			if(h < 10)
			{
				h = (byte)('0' + h);
			}
			else
			{
				h = (byte)('A' + h - 10);
			}

			l = (byte)((btValue[i] & 0x0F));
			if(l < 10)
			{
				l = (byte)('0' + l);
			}
			else
			{
				l = (byte)('A' + l - 10);
			}

			value.append((char)h);
			value.append((char)l);
		}
		return value.toString();
	}

	////////////////////////////////////////////////////////
	// Format BCD
	////////////////////////////////////////////////////////
	public static String formatBCD(byte[] btValue,int iOffset,int iLength)
	{
		int iLastOffset = iOffset + iLength;
		if(btValue.length < iLastOffset || iLength < 1)
		{
			return "";
		}

		StringBuffer value = new StringBuffer();
		for(int i = iOffset;i < iLastOffset;i++)
		{
			byte l,h;
			h = (byte)((btValue[i] & 0xF0) >>> 4);
			if(h < 10)
			{
				h = (byte)('0' + h);
			}
			else
			{
				h = (byte)('A' + h - 10);
			}

			l = (byte)((btValue[i] & 0x0F));
			if(l < 10)
			{
				l = (byte)('0' + l);
			}
			else
			{
				l = (byte)('A' + l - 10);
			}

			value.append((char)h);
			value.append((char)l);
		}
		return value.toString();
	}

	////////////////////////////////////////////////////////
	// Format TBCD
	////////////////////////////////////////////////////////
	public static String formatTBCD(byte[] btValue,int iOffset,int iLength)
	{
		int iLastOffset = iOffset + iLength;
		if(btValue.length < iLastOffset || iLength < 1)
		{
			return "";
		}

		byte l,h;
		StringBuffer value = new StringBuffer();
		for(int i = iOffset;i < iLastOffset;i++)
		{
			h = (byte)((btValue[i] & 0xF0) >>> 4);
			if(h < 10)
			{
				h = (byte)('0' + h);
			}
			else
			{
				h = (byte)('A' + h - 10);
			}

			l = (byte)((btValue[i] & 0x0F));
			if(l < 10)
			{
				l = (byte)('0' + l);
			}
			else
			{
				l = (byte)('A' + l - 10);
			}

			if(l != 'F')
			{
				value.append((char)l);
			}
			if(h != 'F')
			{
				value.append((char)h);
			}
		}
		return value.toString();
	}

	////////////////////////////////////////////////////////
	// Format address string
	////////////////////////////////////////////////////////
	public static String formatAddressString(byte[] btValue,int iOffset,int iLength)
	{
		return formatTBCD(btValue,iOffset + 1,iLength - 1);
	}

	////////////////////////////////////////////////////////
	// Format formatNetworkCallReference string
	////////////////////////////////////////////////////////
	public static String formatNetworkCallReference(byte[] btValue,int iOffset,int iLength)
	{
		return formatTBCD(btValue,iOffset + 1,iLength - 1);
	}

	////////////////////////////////////////////////////////
	// Format Date
	////////////////////////////////////////////////////////
	public static String formatDate(byte[] btValue,int iOffset,int iLength)
	{
		if((btValue.length < iOffset + iLength) || (iLength < 4))
		{
			return "";
		}
		return getBCDString(btValue[iOffset + 3]) + "/" + getBCDString(btValue[iOffset + 2]) + "/" + getBCDString(btValue[iOffset + 0]) +
			getBCDString(btValue[iOffset + 1]);
	}

	////////////////////////////////////////////////////////
	// Format time
	////////////////////////////////////////////////////////
	public static String formatTime(byte[] btValue,int iOffset,int iLength)
	{
		if((btValue.length < iOffset + iLength) || (iLength < 3))
		{
			return "";
		}
		return getBCDString(btValue[iOffset + 0]) + ":" + getBCDString(btValue[iOffset + 1]) + ":" + getBCDString(btValue[iOffset + 2]);
	}

	////////////////////////////////////////////////////////
	// Format time stamp
	////////////////////////////////////////////////////////
	public static String formatTimeStamp(byte[] btValue,int iOffset,int iLength,int iUtcOffset)
	{
		if((btValue.length < iOffset + iLength) || (iLength < 9))
		{
			return "";
		}
		Date dtReturn = null;
		long lOffset = 0;
		if(btValue[iOffset + 2] == '+' || btValue[iOffset + 2] == '-')
		{
			dtReturn = new Date(getBCDValue(btValue[iOffset + 8]),
								getBCDValue(btValue[iOffset + 7]) - 1,
								getBCDValue(btValue[iOffset + 6]),
								getBCDValue(btValue[iOffset + 5]),
								getBCDValue(btValue[iOffset + 4]),
								getBCDValue(btValue[iOffset + 3]));
			lOffset = getBCDValue(btValue[iOffset + 1]) * 3600;
			lOffset += getBCDValue(btValue[iOffset]) * 60;
			lOffset *= 1000;
			if(btValue[iOffset + 2] == '-')
			{
				lOffset = -lOffset;
			}
			dtReturn.setTime(dtReturn.getTime() - lOffset + iUtcOffset);
			return fmtDateTime.format(dtReturn);
		}

		dtReturn = new Date(getBCDValue(btValue[iOffset]),
							getBCDValue(btValue[iOffset + 1]) - 1,
							getBCDValue(btValue[iOffset + 2]),
							getBCDValue(btValue[iOffset + 3]),
							getBCDValue(btValue[iOffset + 4]),
							getBCDValue(btValue[iOffset + 5]));
		lOffset = getBCDValue(btValue[iOffset + 7]) * 3600;
		lOffset += getBCDValue(btValue[iOffset + 8]) * 60;
		lOffset *= 1000;
		if(btValue[iOffset + 6] == '-')
		{
			lOffset = -lOffset;
		}
		dtReturn.setTime(dtReturn.getTime() - lOffset + iUtcOffset);
		return fmtDateTime.format(dtReturn);
	}

	////////////////////////////////////////////////////////
	// Format ip address
	////////////////////////////////////////////////////////
	public static String formatIPAddress(byte[] btValue,int iOffset,int iLength)
	{
		int iLastOffset = iOffset + iLength;
		if(btValue.length < iLastOffset || iLength < 1)
		{
			return "";
		}
		StringBuffer strReturn = new StringBuffer();
		for(int i = iOffset;i < iLastOffset;i++)
		{
			strReturn.append('.');
			strReturn.append(FORMAT_SHARP.format(btValue[i] & 0xFF));
		}
		return strReturn.substring(1,strReturn.length());
	}

	////////////////////////////////////////////////////////
	public static String formatIPAddress(ASNData dat)
	{
		if(dat.mpFirstChild != null)
		{
			dat = dat.mpFirstChild;
			if(dat.mpFirstChild.miTagID == 0 || dat.mpFirstChild.miTagID == 1)
			{
				return formatIPAddress(dat.mbtData,0,dat.mbtData.length);
			}
			else if(dat.mpFirstChild.miTagID == 2 || dat.mpFirstChild.miTagID == 3)
			{
				return new String(dat.mbtData);
			}
		}
		return "";
	}

	////////////////////////////////////////////////////////
	// Parse integer
	////////////////////////////////////////////////////////
	public static byte[] parseInteger(String strData)
	{
		int iValue = Integer.parseInt(strData);
		byte[] btValue = new byte[4];
		int iIndex = 0;
		while(iValue > 0)
		{
			btValue[iIndex++] = (byte)(iValue & 0xFF);
			iValue >>= 8;
		}
		if(iIndex == 0)
		{
			return new byte[1];
		}

		int iCount = iIndex;
		byte[] btReturn = new byte[iCount];
		while(iIndex > 0)
		{
			btReturn[iCount - iIndex--] = btValue[iIndex];
		}
		return btReturn;
	}

	////////////////////////////////////////////////////////
	// Parse BCD
	////////////////////////////////////////////////////////
	public static byte[] parseBCD(String strData)
	{
		return new byte[0];
	}

	////////////////////////////////////////////////////////
	// Parse date
	////////////////////////////////////////////////////////
	public static byte[] parseDate(String strData)
	{
		return new byte[0];
	}

	////////////////////////////////////////////////////////
	// Parse Time
	////////////////////////////////////////////////////////
	public static byte[] parseTime(String strData)
	{
		return new byte[0];
	}

	////////////////////////////////////////////////////////
	// Get BCD string
	////////////////////////////////////////////////////////
	public static String getBCDString(byte btValue)
	{
		byte l,h;
		h = (byte)((btValue & 0xF0) >>> 4);
		if(h < 10)
		{
			h = (byte)('0' + h);
		}
		else
		{
			h = (byte)('A' + h - 10);
		}

		l = (byte)((btValue & 0x0F));
		if(l < 10)
		{
			l = (byte)('0' + l);
		}
		else
		{
			l = (byte)('A' + l - 10);
		}

		return String.valueOf((char)h) + String.valueOf((char)l);
	}

	////////////////////////////////////////////////////////
	// Get BCD Value
	////////////////////////////////////////////////////////
	public static int getBCDValue(byte btValue)
	{
		int iReturn;
		iReturn = ((btValue & 0xF0) >>> 4) * 10;
		iReturn += ((btValue & 0x0F));
		return iReturn;
	}

	public static int getFixedByte(byte dat)
	{
		return dat >= 0 ? dat : 256 + dat;
	}

	public static String formatBCDDirectory(byte data[],int length)
	{
		int iOctet1 = data[0];

		int iIndicator = iOctet1 >> 7;
		if(((iOctet1 >> 4) & 7) != 5)
		{
			if(iIndicator == 1 || iIndicator == -1)
			{

				return formatTBCD(data,1,length - 1);
			}
			else
			{
				return formatTBCD(data,2,length - 2);

			}
		}
		else
		{
			if(iIndicator == 1 || iIndicator == -1)
			{
				return formatPacking7bits(data,1,length - 1);
			}
			else
			{
				return formatPacking7bits(data,2,length - 2);
			}
		}
	}

	public static String formatPacking7bits(byte data[],int off,int length)
	{
		String strValues = "";

		// Chia thanh tung doan: moi doan la 7 byte

		int index = 0;

		while(index < length)
		{
			int iCur = index % 7;

			if(iCur == 0)
			{
				// Neu da het 1 phan 7byte
				// Neu la byte dau tien
				// Lay ra 7 - iCur byte cuoi cung
				int iTemp = 0;
				for(int j = 0;j < 7 - iCur;j++)
				{
					iTemp = (iTemp << 1) + 1;
				}

				int values = getFixedByte(data[off + index]) & iTemp;
				strValues += String.valueOf((char)values);
			}
			else
			{
				// Neu la' cac byte 1 2 3 4 5 6
				// 1. Lay ra 7-iCur byte cuoi cung
				int iTemp = 0;
				for(int j = 0;j < 7 - iCur;j++)
				{
					iTemp = (iTemp << 1) + 1;
				}

				// Values1 chinh la gia tri cua 7 - iCur cuoi cung cua byte
				int values1 = getFixedByte(data[off + index]) & iTemp;

				// 2 Lay ra iCur bit cua byte phia truoc
				int values2 = getFixedByte(data[off + index - 1]) >> (8 - iCur);

				//char c = (char)((values1 << iCur) + values2);
				char c = (char)(((values1 << iCur) < 0 ? (values1 << iCur) + 256 : values1 << iCur) + values2);

				strValues += String.valueOf(c);

				if(iCur == 6)
				{
					iTemp = getFixedByte(data[off + index]);
//					if(iTemp < 127) // Tuc la' 7 bit dau cua octet 6 khac FFFFFFF
					{
						c = (char)(iTemp >> 1);
						if((int)c != 127)
						{
							strValues += String.valueOf(c);
						}
					}

				}
			}
			// Tang index
			index++;

		}
		// End while
		return strValues;
	}

	public static String formatOctetString(byte[] btValue)
	{
		String strTemp = new String();
		for(int i = 0;i < btValue.length;i++)
		{
			strTemp += String.valueOf(btValue[i]);
		}
		return strTemp;
	}

	public static String formatRecordingEntity(byte data[],int length)
	{
		int iOctet1 = data[0];
		if(((iOctet1 >> 4) & 7) != 5)
		{
			return formatTBCD(data,1,length - 1);
		}
		else
		{
			return formatPacking7bits(data,1,length - 1);
		}

	}

	public static String format_TKGP_NAME(byte data[],int offset,int length)
	{
		String strReturn = "";
		for(int i = offset;i < offset + length;i++)
		{
			strReturn += (char)data[i];
		}
		return strReturn.trim().toUpperCase();
	}
	////////////////////////////////////////////////////////
	// format IA5 String
	// DungVA 16/03/2004
	////////////////////////////////////////////////////////
	public static String formatIA5String(byte[] btValue,int iOffset,int iLength)
	{
		int iLastOffset = iOffset + iLength;
		if(btValue.length < iLastOffset || iLength < 1)
		{
			return "";
		}

		byte b;
		StringBuffer value = new StringBuffer();
		for(int i = iOffset;i < iLastOffset;i++)
		{
			b = btValue[i];
			value.append((char)b);
		}
		return value.toString();
	}
	//
}
