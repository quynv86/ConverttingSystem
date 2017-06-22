package vn.yotel.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.yotel.commons.exception.AppException;

public class PasswordUtilTest {

	
	public void createPassword() throws AppException {
		final String fileName = "NVXS_2017060900000103.dat";
		final String strPattern = "^(NVXS_)(\\d{4}\\d{2}\\d{2})(\\d{8}).dat$";
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(fileName);
		matcher.find();
		System.out.println(matcher.group(2));
		System.out.println(matcher.group(3));
		
//		System.out.println("NVXS_2017060900000103.dat".substring(17, 21));
		
	}
	public static void main(String args[]) throws AppException{
		new PasswordUtilTest().createPassword();
	}
	
}
