/**
 * LAB139
 * com.alsfox.lab139.utils
 * 2015
 */
package com.lh.webservice.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 权兴
 * @date 2015年5月6日下午12:51:34
 * @version 1.0
 * 
 */
public class BitmapCache {
	static Map<String, SoftReference<Bitmap>> cacheMap = new HashMap<String, SoftReference<Bitmap>>();

	public static void putBitmap(String path, Bitmap bitmap) {
		SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bitmap);
		cacheMap.put(path, softReference);
	}

	public static Bitmap getBitmap(String path) {
		SoftReference<Bitmap> softReference = cacheMap.get(path);
		return softReference.get();
	}

	public static boolean existsBitmap(String path) {
		SoftReference<Bitmap> softReference = cacheMap.get(path);
		if (softReference != null) {
			if (softReference.get() != null) {
				return true;
			} else {
				cacheMap.remove(path);
				return false;
			}
		} else {
			return false;
		}
	}
}