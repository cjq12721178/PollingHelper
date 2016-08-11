package com.example.kat.pollinghelper.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

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
                byteBuffer.close();
            } catch (Exception e) {
                Log.d("PollingHelper", e.getMessage());
            }
        }

        return result;
    }

    //不处理异常
    public static int stringToInt(String src, int defValue) {
        return src != null ? Integer.parseInt(src) : defValue;
    }

    public static int minuteToMillisecond(int minutes) {
        return minutes * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
    }

    public static int secondToMillisecond(int seconds) {
        return seconds * MILLISECONDS_PER_SECOND;
    }

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
}
