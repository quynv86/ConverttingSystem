package vn.yotel.commons.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import vn.yotel.commons.exception.AppException;

public class PasswordUtil {

    public static String encryptPassword(final String password, String salt) throws AppException {
        SecretKey secretKey = null;
        byte[] keyBytes = salt.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        try {
            Mac mac;
            mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = password.getBytes();
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(mac.doFinal(text))).trim();
        } catch (Exception e) {
            throw new AppException("", e.getMessage());
        }
    }

    public static String doHashPassword(final String password, String salt) throws AppException {
        SecretKey secretKey = null;
        byte[] keyBytes = salt.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        try {
            Mac mac;
            mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = password.getBytes();
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(mac.doFinal(text))).trim();
        } catch (Exception e) {
            throw new AppException("", e.getMessage());
        }
    }
    public static void main(String args[]) throws AppException{
    	System.out.println(encryptPassword("abc123","5876695f8e4e1811"));
    }
}
