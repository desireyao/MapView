package com.yh.mapview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yaoh on 2019/2/27
 */
public class BitmapUtil {

    /**
     * 获取 asset 资源的图片
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getBitmapFormAsset(Context context, String fileName) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            InputStream is = context.getAssets().open(fileName);
            Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFormAsset(Context context, String fileName, float scale) {
        Bitmap bmp = getBitmapFormAsset(context,fileName);

        return scaleBitmap(bmp,scale);
    }

    /**
     * 缩放 bitmap
     *
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 取得想要缩放的matrix參數
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBitMap;
    }

}
