package com.example.kat.pollinghelper.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by KAT on 2016/7/18.
 */
public class Converter {

    public static Bitmap getBitmapFromByteArray(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    public static InputStream uriToInputStream(Context context, Uri uri) {
        InputStream result = null;
        try {
            result = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 将InputStream转换成byte[]
    public static byte[] uriToByteArray(Context context, Uri uri){
        byte[] result = null;
        InputStream inputStream = uriToInputStream(context, uri);

        if (inputStream != null) {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                result = byteBuffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
