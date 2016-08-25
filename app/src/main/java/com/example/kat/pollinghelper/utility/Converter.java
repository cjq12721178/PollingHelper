package com.example.kat.pollinghelper.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by KAT on 2016/7/18.
 */
public class Converter {

    private Converter() {
    }

    public static Bitmap byteArray2Bitmap(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    public static byte[] bitmap2ByteArray(Bitmap bm) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteBuffer);
        return byteBuffer.toByteArray();
    }

    public static InputStream uri2InputStream(Context context, Uri uri) {
        InputStream result = null;
        try {
            result = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 将InputStream转换成byte[]
    public static byte[] uri2ByteArray(Context context, Uri uri){
        byte[] result = null;
        InputStream inputStream = uri2InputStream(context, uri);

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

    //压缩指定byte[]图片，并得到压缩后的图像
    public static Bitmap ratioCompress(byte[] src, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(src, 0, src.length, options);
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(src, 0, src.length, options);
    }

    //quality为压缩质量，0-100
    public static byte[] qualityCompress(Bitmap src, int quality) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, byteBuffer);
        return byteBuffer.toByteArray();
    }

    public static byte[] qualityCompress(byte[] src, int quality) {
        return qualityCompress(byteArray2Bitmap(src), quality);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (targetWidth == 0 || targetHeight == 0)
            return 1;
        if (height > targetHeight || width > targetWidth) {
            final int heightRatio = Math.round((float) height/ (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //不处理异常
    public static int string2Int(String src, int defValue) {
        return src != null ? Integer.parseInt(src) : defValue;
    }

    public static int minute2Millisecond(int minutes) {
        return minutes * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
    }

    public static int second2Millisecond(int seconds) {
        return seconds * MILLISECONDS_PER_SECOND;
    }

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
}
