package com.yh.mapview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yh.mapview.listener.OnMapPointClickListener;

/**
 * 自定义地图 坐标 点
 */
public class BSMapPointView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "BSMapPointView";

    private ImageView mPointView;
    private Bitmap mPointBitmap;

    // 坐标
    private double[] mPoint = new double[2];

    private OnMapPointClickListener onMapPointClickListener;

    public BSMapPointView(Context context) {
        super(context);
    }

    public BSMapPointView(Context context, double pointX, double pointY, Bitmap bitmap) {
        super(context);
        mPoint[0] = pointX;
        mPoint[1] = pointY;
        mPointBitmap = bitmap;
        init();
    }

    public void init() {
        mPointView = new ImageView(getContext());
        mPointView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        addView(mPointView);
        mPointView.setImageBitmap(mPointBitmap);

        // 设置 监听
//        setOnClickListener(this);
    }


    public void setOnMapPointClickListener(OnMapPointClickListener onMapPointClickListener) {
        this.onMapPointClickListener = onMapPointClickListener;
    }

    public void setShowX(float x) {
        x = x - mPointBitmap.getWidth() / 2.f;
        setX(x);
    }

    public void setShowY(float y) {
        y = y - mPointBitmap.getHeight() / 2.f;
//        y = y - mPointBitmap.getHeight();
        setY(y);
    }

    public float getShowX() {
        float showX = getX() + mPointBitmap.getWidth() / 2.f;
        return showX;
    }

    public float getShowY() {
        float showY = getY() + mPointBitmap.getHeight() / 2.f;
//        float showY = getY() + mPointBitmap.getHeight();
        return showY;
    }

    public double[] getPoint() {
        return mPoint;
    }

    public void setPoint(double pointX, double pointY) {
        mPoint[0] = pointX;
        mPoint[1] = pointY;
    }

    @Override
    public void onClick(View v) {
        if (onMapPointClickListener != null) {
            onMapPointClickListener.onMapPointClick(this);
        }
    }
}
