package com.lh.webservice.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lh.webservice.R;
import com.lh.webservice.utils.textutils.BitmapUtil;
import com.lh.webservice.utils.textutils.CImageGetter;
import com.lh.webservice.utils.textutils.DateUtils;
import com.lh.webservice.utils.textutils.PickLocalImageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 图文混排，并且上传
 */
public class MainActivity extends AppCompatActivity {

    private EditText test_edit;
    private Button test_btn;
    private String imageLocalUrl;
    private Map<String, String> imageurls = new HashMap<>();

    public static final String PHOTO_SAVE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/WebService/Photos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test_edit = (EditText) findViewById(R.id.test_edit);
        test_btn = (Button) findViewById(R.id.test_btn);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickLocalImageUtils.toAlbum(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageLocalUrl = null;
            switch (requestCode) {
                case PickLocalImageUtils.CODE_FOR_ALBUM:
                    if (data == null) return;
                    //通过系统相册的回调获得图片的url
                    imageLocalUrl = PickLocalImageUtils.getPath(data.getData(), getContentResolver());
                    break;
            }
            handlePostImage(imageLocalUrl);
        }
    }

    /**
     * 处理图片
     *
     * @param imagePath
     */
    private void handlePostImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) return;
        //控制大小
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imagePath, 800, 600);
        //图片另存为新的地址
        imageLocalUrl = PHOTO_SAVE_PATH + DateUtils.format(System.currentTimeMillis(), "'IMG'_yyyyMMddHHmmss") + ".jpg";
        //将当前图片的网络地址和本地地址保存起来
        imageurls.put("http://192.168.1.1:8080/icon.jpg", imageLocalUrl);
        //将图片保存到新的地址
        BitmapUtil.saveBitmap(bitmap, imageLocalUrl, 50);
        if (bitmap != null) {
            insertIntoEditText(getBitmapMime(bitmap, imageLocalUrl));
        }
    }

    /**
     * 将图片变成可变的文本
     *
     * @param pic
     * @param imagePath
     * @return
     */
    private SpannableString getBitmapMime(Bitmap pic, String imagePath) {
        SpannableString ss = new SpannableString(imagePath);
        BitmapDrawable drawable;
        if (Build.VERSION.SDK_INT < 21) {
            drawable = new BitmapDrawable(getResources(), pic);
        } else {
            ImageView imageView = new ImageView(this);
            int windowWidth = 2048;
            int picWidth = pic.getWidth();
            imageView.setPadding((windowWidth - picWidth) / 2 - (windowWidth / 100), 0, windowWidth, 10);
            imageView.setImageBitmap(pic);
            drawable = loadBitmapFromView(imageView);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, imagePath);
        ss.setSpan(span, 0, imagePath.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 从view中加载bitmap对象
     *
     * @param view
     * @return
     */
    public BitmapDrawable loadBitmapFromView(ImageView view) {
        if (view == null) return null;
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(screenshot);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        return new BitmapDrawable(getResources(), screenshot);
    }

    /**
     * 插入编辑脚本
     *
     * @param ss
     */
    private void insertIntoEditText(SpannableString ss) {
        Editable et = test_edit.getText();// 先获取Edittext中的内容
        int start = test_edit.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        test_edit.setText(et);// 把et添加到Edittext中
        test_edit.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    /**
     * 上传图文
     */
    private void getPost() {
        Editable content = test_edit.getText();
        String contentStr = content.toString();
        if (!TextUtils.isEmpty(content)) {
            ImageSpan[] spans = content.getSpans(0, content.length(), ImageSpan.class);
            for (ImageSpan span : spans) {
                String source = span.getSource();
                contentStr = contentStr.replace(source, "<div><img src='" + imageurls.get(source) + "' /></div>");
            }
        }
        contentStr.replace("[图片长传失败]", "");
    }

    /**
     * 显示图文
     *
     * @param content
     */
    private void showText(String content) {
        if (!TextUtils.isEmpty(content)) {
            CImageGetter cImageGetter = new CImageGetter(this, test_edit, 2048);
            //本地图片
            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    // 获取本地图片
                    Drawable drawable = Drawable.createFromPath(source);
                    // 必须设为图片的边际,不然TextView显示不出图片
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    // 将其返回
                    return drawable;
                }
            };
            cImageGetter.setSource(content);
            test_btn.setText(Html.fromHtml(content, imageGetter, null));
        }
    }

    /**
     * 示例
     *
     * mTvOne = (TextView) this.findViewById(R.id.tv_img_label_one);
     String htmlOne = "本地图片测试：" + "<img src='/mnt/sdcard/imgLabel.jpg'>";
     mTvOne.setText(Html.fromHtml(htmlOne, new LocalImageGetter(), null));

     mTvTwo = (TextView) this.findViewById(R.id.tv_img_label_two);
     String htmlTwo = "项目图片测试：" + "<img src=""+R.drawable.imagepro+"">";
     mTvTwo.setText(Html.fromHtml(htmlTwo, new ProImageGetter(), null));


     mTvThree = (TextView) this.findViewById(R.id.tv_img_label_three);
     mImageGetter = new NetworkImageGetter();
     mTvThree.setText(Html.fromHtml(htmlThree, mImageGetter, null));
     }
     * */

    /**
     * 本地图片
     *
     * @author Susie
     */
    private final class LocalImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            // 获取本地图片
            Drawable drawable = Drawable.createFromPath(source);
            // 必须设为图片的边际,不然TextView显示不出图片
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            // 将其返回
            return drawable;
        }
    }

    /**
     * 项目资源图片
     *
     * @author Susie
     */
    private final class ProImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            // 获取到资源id
            int id = Integer.parseInt(source);
            Drawable drawable = getResources().getDrawable(id);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        }
    }

    /**
     * 网络图片
     *
     * @author Susie
     */
    private final class NetworkImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {

            Drawable drawable = null;
//            // 封装路径
//            File file = new File(Environment.getExternalStorageDirectory(), picName);
//            // 判断是否以http开头
//            if(source.startsWith("http")) {
//                // 判断路径是否存在
//                if(file.exists()) {
//                    // 存在即获取drawable
//                    drawable = Drawable.createFromPath(file.getAbsolutePath());
//                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//                } else {
//                    // 不存在即开启异步任务加载网络图片
//                    AsyncLoadNetworkPic networkPic = new AsyncLoadNetworkPic();
//                    networkPic.execute(source);
//                }
//            }
            return drawable;
        }
    }

}
