/**
 * LAB139
 * com.alsfox.lab139.utils
 * 2015
 */
package com.lh.webservice.utils.textutils;

import java.security.MessageDigest;

/**
 * @author 权兴
 * @date 2015年2月26日下午1:51:21
 * @version 1.0 MD5加密工具类
 */
public class Common {
	public static String md5(String sourceStr) {
		byte[] source = sourceStr.getBytes();
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char[] str = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
}