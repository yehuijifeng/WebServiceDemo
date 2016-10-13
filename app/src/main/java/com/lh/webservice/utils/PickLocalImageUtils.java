package com.lh.webservice.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 通过系统相册的回调获得图片的url
 */
public class PickLocalImageUtils {

    public static final int CODE_FOR_ALBUM = 2000;

    public static void toAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        activity.startActivityForResult(intent, CODE_FOR_ALBUM);
    }

    public static String getPath(Uri uri, ContentResolver resolver) {
        String path;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } else {
            path = uri.getPath();
        }
        return path;
    }
}