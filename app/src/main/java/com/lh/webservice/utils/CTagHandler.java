/**
 * LAB139
 * com.alsfox.lab139.utils
 * 2015
 */
package com.lh.webservice.utils;

import android.content.Context;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import org.xml.sax.XMLReader;

import java.util.ArrayList;

/**
 * @author 权兴
 * @date 2015年2月26日下午1:54:06
 * @version 1.0 标签处理工具类
 */
public class CTagHandler implements TagHandler {

	private Context context;
	private ArrayList<String> imgUrls = new ArrayList<String>();

	private int position = 0;

	public CTagHandler(Context context) {
		this.context = context;
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (tag.toLowerCase().equals("img")) {
			int len = output.length();
			ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
			String imgURL = images[0].getSource();
			// String imageName = Common.md5(imgURL);
			String sdcardPath = context.getCacheDir().getAbsolutePath();
			// String[] ss = imgURL.split("\\.");
			// String ext = ss[ss.length - 1];
			String savePath = sdcardPath + "/" + context.getPackageName() + "/"
					+ "IMGINFO" + DateUtils.getNow("yyyyMMddHHmmssS") + "."
					+ "jpg";
			if ("data".equalsIgnoreCase(imgURL.substring(0, 4))) {
				try {
					imgURL = imgURL.substring(imgURL.indexOf("base64") + 7,
							imgURL.length());
					Base64Utils.decoderBase64File(imgURL, savePath);
					imgUrls.add(savePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				imgUrls.add(imgURL);
			}
			output.setSpan(new ImageClick(context, position), len - 1, len,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			position++;
		}
	}

	private class ImageClick extends ClickableSpan {

		private Context context;
		private int position;

		public ImageClick(Context context, int position) {
			this.context = context;
			this.position = position;
		}

		@Override
		public void onClick(View widget) {
//			Intent intent = new Intent();
//			intent.setClass(context, LookImageActivity.class);
//			intent.putStringArrayListExtra(Constant.KEY_IMAGEURLS, imgUrls);
//			intent.putExtra(Constant.KEY_IMAGEPOSITION, position);
//			context.startActivity(intent);
		}
	}
}