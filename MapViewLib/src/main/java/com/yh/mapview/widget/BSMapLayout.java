package com.yh.mapview.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yh.mapview.listener.OnGetMapViewListener;
import com.yh.mapview.log.LogTool;

/**
 * Created by yaoh on 2019/1/17.
 */

public class BSMapLayout extends FrameLayout {

    private static final String TAG = "BSMapLayout";
    private BSMapView mMapView;

    private int mMapViewWidth;
    private int mMapViewHeight;

    private int mViewWidth;
    private int mViewHeight;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public BSMapLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogTool.LogE_DEBUG(TAG, " onLayout ------------>" );
    }

    private void addMapView() {
        mViewWidth = getRight() - getLeft();
        mViewHeight = getBottom() - getTop();
        mMapViewWidth = mViewWidth + mViewHeight;
        mMapViewHeight = mMapViewWidth;

        LogTool.LogD(TAG, "addMapView -------> "
                + " getLeft(): " + getLeft()
                + " getRight(): " + getRight()
                + " getTop(): " + getTop()
                + " getBottom(): " + getBottom()
                + " mMapViewWidth: " + mMapViewWidth);

        mMapView = new BSMapView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width = mMapViewWidth;
        layoutParams.height = mMapViewHeight;
        int leftMargin = (layoutParams.width - getWidth()) / 2;
        int topMargin = (layoutParams.height - getHeight()) / 2;
        layoutParams.leftMargin = -leftMargin;
        layoutParams.topMargin = -topMargin;
        mMapView.setLayoutParams(layoutParams);

        addView(mMapView);
    }

    public void getMapView(final OnGetMapViewListener listener) {
        if (listener == null) {
            return;
        }

        if (mMapView != null) {
            listener.onGetMapView(mMapView);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogTool.LogD(TAG, " getMapView --->" + " getLeft(): " + getLeft() + " getTop(): " + getTop());
                    addMapView();
                    listener.onGetMapView(mMapView);
                }
            });
        }
    }

}
