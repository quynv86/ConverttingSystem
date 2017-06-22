package vn.yotel.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {
	public static final SimpleDateFormat SDF_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat SDF_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

	public static synchronized Date parse(String dateStr, SimpleDateFormat dateFormat) throws ParseException {
		return dateFormat.parse(dateStr);
	}
}
