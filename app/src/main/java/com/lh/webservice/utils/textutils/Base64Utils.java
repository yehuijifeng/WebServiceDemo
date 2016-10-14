/**
 * LAB139
 * com.alsfox.lab139.utils
 * 2015
 */
package com.lh.webservice.utils.textutils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author 权兴
 * @date 2015年4月29日下午2:42:48
 * @version 1.0
 * 
 */
public class Base64Utils {
	/**
	 * <p>
	 * 将文件转成base64 字符串
	 * </p>
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		// return new android.util.Base64;
		return Base64.encodeToString(buffer, Base64.DEFAULT);
	}

	/**
	 * <p>
	 * 将base64字符解码保存文件
	 * </p>
	 * 
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code, String targetPath)
			throws Exception {
		byte[] baseByte = Base64
				.decode(base64Code, Base64.DEFAULT);
		// byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(baseByte);
		out.close();
	}

	/**
	 * <p>
	 * 将base64字符保存文本文件
	 * </p>
	 * 
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void toFile(String base64Code, String targetPath)
			throws Exception {
		byte[] buffer = base64Code.getBytes();
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(buffer);
		out.close();
	}

	public static void main(String[] args) {
		try {
			String base64Code = encodeBase64File("D:\\1.jpg");
			System.out.println(base64Code);
			decoderBase64File(base64Code, "D:\\2.jpg");
			toFile(base64Code, "D:\\three.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}