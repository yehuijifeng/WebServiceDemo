/**
 * LAB139 com.alsfox.lab139.utils 2015
 */
package com.lh.webservice.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.widget.TextView;

import com.lh.webservice.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author 权兴
 * @version 1.0
 * @date 2015年2月26日下午1:47:38
 */
public class CImageGetter implements ImageGetter {

    private Context context;

    private TextView tv;

    private int drawableWidth;

    private int drawableHeight;

    private String cacheImageName;

    private Resources res;

    private String source;

    private Drawable defaultDrawable;

    private int windowsWidth;

    /**
     * @param context      上下文对象
     * @param tv           TextView对象
     * @param windowsWidth 设置图片的宽度
     */
    public CImageGetter(Context context, TextView tv, int windowsWidth) {
        this.context = context;
        this.tv = tv;
        this.windowsWidth = windowsWidth;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCacheImageName() {
        return cacheImageName;
    }

    public void setCacheImageName(String cacheImageName) {
        this.cacheImageName = cacheImageName;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (!TextUtils.isEmpty(source)) {
            String cachePath = context.getCacheDir().getAbsolutePath();
            if ("data".equalsIgnoreCase(source.substring(0, 4))) {
                try {
                    String savePath = cachePath + "/"
                            + context.getPackageName() + "/" + "IMGINFO"
                            + DateUtils.getNow("yyyyMMddHHmmssS") + "." + "jpg";
                    source = source.substring(source.indexOf("base64") + 7,
                            source.length());
                    Base64Utils.decoderBase64File(source, savePath);
                    BitmapDrawable bitmapDrawable = getBitmapDrawable(savePath);
                    return setDrawableBounds(bitmapDrawable);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                source = source.replace(" ", "");
                cacheImageName = Common.md5(source);
                String[] ss = source.split("\\.");
                String ext = ss[ss.length - 1];
                String savePath = cachePath + "/" + context.getPackageName()
                        + "/" + cacheImageName + "." + ext;
                File file = new File(savePath);
                if (file.exists()) {
                    try {
                        BitmapDrawable bitmapDrawable = getBitmapDrawable(savePath);
                        return setDrawableBounds(bitmapDrawable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                res = context.getResources();
                defaultDrawable = res.getDrawable(R.mipmap.ic_launcher);
                URLDrawable drawable = new URLDrawable(defaultDrawable);
                new ImageAsync(drawable).execute(savePath, source);
                return drawable;
            }
        } else {
            res = context.getResources();
            defaultDrawable = res.getDrawable(R.mipmap.ic_launcher);
            URLDrawable drawable = new URLDrawable(defaultDrawable);
            new ImageAsync(drawable).execute("", source);
            return drawable;
        }
    }

    private Drawable setDrawableBounds(Drawable bitmapDrawable) {
        drawableWidth = bitmapDrawable.getIntrinsicWidth() * 3;
        drawableHeight = bitmapDrawable.getIntrinsicHeight() * 3;
        float ratio = (float) drawableWidth / (float) drawableHeight;
        drawableWidth = windowsWidth - 25;
        drawableHeight = (int) (drawableWidth / ratio);
//        if (drawableWidth < (windowsWidth / 2)) {
//            drawableHeight = drawableHeight
//                    * (windowsWidth / 2) / drawableWidth;
//            drawableWidth = windowsWidth / 2;
//        } else if (drawableWidth > windowsWidth) {
//            drawableHeight = windowsWidth * drawableHeight
//                    / drawableWidth;
//            drawableWidth = windowsWidth;
//        }
        bitmapDrawable.setBounds(0, 0, drawableWidth,
                drawableHeight);
        return bitmapDrawable;
    }

    /**
     * @param savePath
     *
     * @return
     */
    private BitmapDrawable getBitmapDrawable(String savePath) {
        Bitmap bitmap;
        if (BitmapCache.existsBitmap(savePath)) {
            bitmap = BitmapCache.getBitmap(savePath);
        } else {
            bitmap = BitmapUtil
                    .decodeSampledBitmapFromFile(savePath, 720, 1280);
            BitmapCache.putBitmap(savePath, bitmap);
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(res, bitmap);
        return bitmapDrawable;
    }

    private class ImageAsync extends AsyncTask<String, Integer, Drawable> {

        private URLDrawable drawable;

        public ImageAsync(URLDrawable drawable) {
            this.drawable = drawable;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            InputStream in = null;
            try {
                String savePath = params[0];
                String url = params[1];
                HttpGet http = new HttpGet(url);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(http);
                BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
                        response.getEntity());
                in = bufferedHttpEntity.getContent();
                if (in == null)
                    return defaultDrawable;
                File file = new File(savePath);
                String basePath = file.getParent();
                File basePathFile = new File(basePath);
                if (!basePathFile.exists()) {
                    basePathFile.mkdirs();
                }
                file.createNewFile();
                FileOutputStream fileout = new FileOutputStream(file);
                byte[] buffer = new byte[1024 * 1024];
                while (in.read(buffer) != -1) {
                    fileout.write(buffer);
                }
                fileout.flush();
                fileout.close();
                return setDrawableBounds(getBitmapDrawable(savePath));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return defaultDrawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if (result != null) {
                drawable.setDrawable(result);
                tv.setText(Html.fromHtml(source, CImageGetter.this,
                        new CTagHandler(context))); // 更新UI
                tv.invalidate();
            }
        }
    }

    public class URLDrawable extends BitmapDrawable {

        private Drawable drawable;

        public URLDrawable(Drawable defaultDraw) {
            setDrawable(defaultDraw);
        }

        private void setDrawable(Drawable nDrawable) {
            drawable = nDrawable;
            setDrawableBounds(drawable);
        }

        @Override
        public void draw(Canvas canvas) {
            drawable.draw(canvas);
        }
    }
}