package vn.yotel.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BufferUtil
{
  public static byte[] replaceAll(byte[] bufSrc, byte[] bufFind, byte[] bufReplace)
    throws IOException
  {
    int iLocation = 0;
    int iPrevLocation = 0;
    ByteArrayOutputStream buf = new ByteArrayOutputStream(bufSrc.length);
    while ((iLocation = indexOf(bufSrc, iLocation, bufFind)) >= 0)
    {
      buf.write(bufSrc, iPrevLocation, iLocation - iPrevLocation);
      buf.write(bufReplace);
      iLocation += bufFind.length;
      iPrevLocation = iLocation;
    }
    buf.write(bufSrc, iPrevLocation, bufSrc.length - iPrevLocation);
    byte[] bufReturn = new byte[buf.size()];
    System.arraycopy(buf.toByteArray(), 0, bufReturn, 0, bufReturn.length);
    return bufReturn;
  }
  
  public static int indexOf(byte[] btBuff, int iOffset, byte[] btSearch)
  {
    int iLength = btBuff.length - btSearch.length + 1;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btSearch[0] == btBuff[iIndex])
      {
        int i = 1;
        while (i < btSearch.length)
        {
          if (btSearch[i] != btBuff[(iIndex + i)]) {
            break;
          }
          i++;
        }
        if (i >= btSearch.length) {
          return iIndex;
        }
      }
    }
    return -1;
  }
  
  public static int indexOf(byte[] btBuff, int iOffset, byte btSearch)
  {
    int iLength = btBuff.length;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btBuff[iIndex] == btSearch) {
        return iIndex;
      }
    }
    return -1;
  }
  
  public static int indexOfSpace(byte[] btBuff, int iOffset)
  {
    int iLength = btBuff.length;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btBuff[iIndex] <= 32) {
        return iIndex;
      }
    }
    return -1;
  }
  
  public static int indexOfLetter(byte[] btBuff, int iOffset)
  {
    int iLength = btBuff.length;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btBuff[iIndex] > 32) {
        return iIndex;
      }
    }
    return -1;
  }
  
  public static int lastIndexOfSequence(byte[] btBuff, int iOffset, byte btSearch)
  {
    while ((iOffset < btBuff.length) && (btBuff[iOffset] == btSearch)) {
      iOffset++;
    }
    if (iOffset >= btBuff.length) {
      return -1;
    }
    return iOffset;
  }
  
  public static int lastIndexOfSequenceSpace(byte[] btBuff, int iOffset)
  {
    while ((iOffset < btBuff.length) && (btBuff[iOffset] <= 32)) {
      iOffset++;
    }
    if (iOffset >= btBuff.length) {
      return -1;
    }
    return iOffset;
  }
  
  public static int lastIndexOfSequenceLetter(byte[] btBuff, int iOffset)
  {
    while ((iOffset < btBuff.length) && (btBuff[iOffset] > 32)) {
      iOffset++;
    }
    if (iOffset >= btBuff.length) {
      return -1;
    }
    return iOffset;
  }
  
  public static byte[] getData(byte[] bt, int iFirstIndex, int iLastIndex)
    throws IllegalArgumentException
  {
    if ((iLastIndex > bt.length) || (iFirstIndex < 0) || (iFirstIndex > iLastIndex)) {
      throw new IllegalArgumentException();
    }
    int iLength = iLastIndex - iFirstIndex;
    byte[] btReturn = new byte[iLength];
    System.arraycopy(bt, iFirstIndex, btReturn, 0, iLength);
    return btReturn;
  }
  
  public static int indexOf(byte[] btBuff, int iOffset, byte btSearch, byte btEscape)
  {
    int iLength = btBuff.length;
    int iEscapeIndex = -1;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btBuff[iIndex] == btEscape)
      {
        if (iEscapeIndex < 0) {
          iEscapeIndex = iIndex;
        }
      }
      else if (btBuff[iIndex] == btSearch)
      {
        if ((iEscapeIndex < 0) || ((iIndex - iEscapeIndex) % 2 == 0)) {
          return iIndex;
        }
      }
      else if (iEscapeIndex >= 0) {
        iEscapeIndex = -1;
      }
    }
    return -1;
  }
  
  public static int indexOfSpace(byte[] btBuff, int iOffset, byte btEscape)
  {
    int iLength = btBuff.length;
    int iEscapeIndex = -1;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btBuff[iIndex] == btEscape)
      {
        if (iEscapeIndex < 0) {
          iEscapeIndex = iIndex;
        }
      }
      else if (btBuff[iIndex] <= 32)
      {
        if ((iEscapeIndex < 0) || ((iIndex - iEscapeIndex) % 2 == 0)) {
          return iIndex;
        }
      }
      else if (iEscapeIndex >= 0) {
        iEscapeIndex = -1;
      }
    }
    return -1;
  }
  
  public static int indexOfLetter(byte[] btBuff, int iOffset, byte btEscape)
  {
    int iLength = btBuff.length;
    int iEscapeIndex = -1;
    for (int iIndex = iOffset; iIndex < iLength; iIndex++) {
      if (btBuff[iIndex] == btEscape)
      {
        if (iEscapeIndex < 0) {
          iEscapeIndex = iIndex;
        }
      }
      else if (btBuff[iIndex] > 32)
      {
        if ((iEscapeIndex < 0) || ((iIndex - iEscapeIndex) % 2 == 0)) {
          return iIndex;
        }
      }
      else if (iEscapeIndex >= 0) {
        iEscapeIndex = -1;
      }
    }
    return -1;
  }
}
