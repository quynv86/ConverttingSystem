package vn.yotel.cdr.util;

import java.io.*;
import java.util.*;
import java.text.*;

import vn.yotel.commons.util.FileUtil;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: FPT</p>
 * @author Thai Hoang Hiep
 * @version 1.0
 * Modified by : Vu Anh Dung
 * Modified Date : 02/10/2003
 */

public class TextFile
{
    protected FileOutputStream mFile = null;
    protected BufferedOutputStream mBuffer = null;
    protected int mintCount = 0;
    protected Vector mvtText;

    // DungVA 02/10/2003
    protected long lByteResult = 0; // Tong so byte ket qua
    public String mstrFilePath = new String();

    public TextFile()
    {
        mvtText = new Vector();
    }

    public void openFile(String strFilePath,int intBuffer) throws Exception
    {
        mintCount = 0;
        try
        {
            mFile = new FileOutputStream(strFilePath);
            mBuffer = new BufferedOutputStream(mFile,intBuffer); // 5M
        }
        catch(Exception e)
        {
            FileUtil.safeClose(mBuffer);
            FileUtil.safeClose(mFile);
            throw e;
        }
        mstrFilePath = strFilePath;
    }

    public void closeFile() throws Exception
    {
        mintCount = 0;
        try
        {
            if (mBuffer != null)
                mBuffer.flush();
        }
        finally
        {
            FileUtil.safeClose(mBuffer);
            FileUtil.safeClose(mFile);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // clear vector
    // Author: ThangPV
    // Modify DateTime: 17/09/2004
    ///////////////////////////////////////////////////////////////////////////
    public void clear() throws Exception
    {
        if((mstrFilePath != null) && !mstrFilePath.equals(""))
        {
            try
            {
                closeFile();
                FileUtil.deleteFile(mstrFilePath);
            }
            catch(IOException e)
            {
                FileUtil.safeClose(mBuffer);
                FileUtil.safeClose(mFile);
                throw e;
            }
        }
    }

    public void addText(String strText) throws Exception
    {
        mintCount++;
        strText += '\n';
        try
        {
            mBuffer.write(strText.getBytes());
        }
        catch(Exception e)
        {
            FileUtil.safeClose(mBuffer);
            FileUtil.safeClose(mFile);
            throw e;
        }
    }

    public long getCount()
    {
        return mintCount;
    }
}
