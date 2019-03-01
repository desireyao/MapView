package com.yh.mapview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yh.mapview.listener.OnMapBitmapLoadListener;
import com.yh.mapview.log.LogTool;
import com.yh.mapview.utils.BitmapUtil;
import com.yh.mapview.utils.ScreenUtil;
import com.yh.mapview.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BSMapView extends RelativeLayout {

    private static final String TAG = "BSMapView";

    private int MAX_PROPER_SIDE_WIDTH = 1500;

    // 显示 地图 底图和线 的 控件
    private BSMapAndLines mMapAndLines;

    // 地图 原点的左上角
    private View original;

    // 地图图片的压缩倍数
    private float mBitmapScale = 1.f;

    // 合适的撑满视图的缩放大小
    private float mProperScale;
    private float mCurScale;

    //经过缩放后的 地图图片的宽高
    private int mScaledBitmapWith;
    private int mScaledBitmapHeight;

    private int mTouchslop; // 最小的触摸距离

    // 是否是跟随模式
    private boolean isFlowMode;

    // 所有点的集合
    private List<BSMapPointView> mapPoints = new ArrayList<>();

    public BSMapView(Context context) {
        this(context, null);
    }

    public BSMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BSMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 初始化
    private void init() {
        mTouchslop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        setBackgroundColor(getResources().getColor(R.color.red_dot_text));

        mMapAndLines = new BSMapAndLines(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mMapAndLines.setLayoutParams(params);
        mMapAndLines.setScaleType(ImageView.ScaleType.MATRIX);
        addView(mMapAndLines);

        original = new View(getContext());
    }


    /**
     * 设置跟随模式
     */
    public void setFlowMode(boolean flowMode) {
        isFlowMode = flowMode;
    }


    /**
     * 更新 点的位置
     *
     * @param mapPoint
     */
    public void updatePointPosition(BSMapPointView mapPoint) {
        // 由于图片的压缩
        double pointX = mapPoint.getPoint()[0] * mBitmapScale;
//        double pointY = mScaledBitmapHeight - mapPoint.getPoint()[1] * mBitmapScale;
        double pointY = mapPoint.getPoint()[1] * mBitmapScale;
        mapPoint.setPoint(pointX, pointY);

        // 由于手势的缩放或拖动
        float showX = (float) (pointX * mCurScale + original.getX());
        float showY = (float) (pointY * mCurScale + original.getY());

        mapPoint.setShowX(showX);
        mapPoint.setShowY(showY);
    }

    /**
     * 添加一个点 并显示
     */
    public void addMapPoint(BSMapPointView mapPoint) {
        // 先转化坐标点
        updatePointPosition(mapPoint);
        addView(mapPoint);
        mapPoints.add(mapPoint);
    }


    /**
     * 更新到居中位置
     */
    public void update2Center() {
        BSMapPointView mapPoint = mapPoints.get(0);
        LogTool.LogE_DEBUG(TAG, "update2Center ---------> "
                + " original.getX(): " + original.getX()
                + " original.getY(): " + original.getY()
                + " mapPoint.getPoint()[0]: " + mapPoint.getPoint()[0]
                + " mapPoint.getPoint()[1]: " + mapPoint.getPoint()[1]
                + " mMapAndLines.getWidth(): " + mMapAndLines.getWidth()
                + " mMapAndLines.getHeight(): " + mMapAndLines.getHeight());

        float dx = (getRight() - getLeft()) / 2f - mapPoint.getShowX();
        float dy = (getBottom() - getTop()) / 2f - mapPoint.getShowY();
        LogTool.LogE_DEBUG(TAG, "update2Center ---------> dx: " + dx + " dy: " + dy);

        mMatrix.set(mMapAndLines.getImageMatrix());
        mMatrix.postTranslate(dx, dy);
        updateBaseMapAndLines();
    }

    public void update2Center2() {
//        mCurMatrix = mMapAndLines.getImageMatrix();
        float dx = 50;
        float dy = 50;
//        LogTool.LogE_DEBUG(TAG, "ACTION_MOVE---> dx: " + dx + " dy: " + dy);
//
//        // 在没有移动之前的位置上进行移动
        mMatrix.set(mCurMatrix);
        mMatrix.postTranslate(dx, dy);
        updateBaseMapAndLines();
    }

    /**
     * 设置 地图 底图 并显示
     *
     * @param bitmap
     */
    public void initMap(final Bitmap bitmap, final OnMapBitmapLoadListener listener) {
        mMapAndLines.post(new Runnable() {
            @Override
            public void run() {
                // todo
                // 将图片控制最大边长为 1500
                int max_side_length = Math.max(bitmap.getWidth(), bitmap.getHeight());
                mBitmapScale = MAX_PROPER_SIDE_WIDTH / max_side_length;
                LogTool.LogE_DEBUG(TAG, "initMap---------->"
                        + " max_side_length:" + max_side_length
                        + " mBitmapScale: " + mBitmapScale);

                final Bitmap properBitmap = BitmapUtil.scaleBitmap(bitmap, mBitmapScale);
                mScaledBitmapWith = properBitmap.getWidth();
                mScaledBitmapHeight = properBitmap.getHeight();
                /**
                 * 最大占满屏幕的宽度 但是mapview的宽高都是 maplayout的宽高之和
                 */
                float widthScale = ScreenUtil.getScreenWidth(getContext()) * 1.0f / mScaledBitmapWith;
                float heightScale = (getBottom() - getTop()) * 1.0f / mScaledBitmapHeight;

                // 取能占满一边屏幕 最小的倍数
                mProperScale = Math.min(widthScale, heightScale);
                mCurScale = mProperScale;

                float initPostTransX = getWidth() / 2.f - (mScaledBitmapWith * mProperScale) / 2.f;
                float initPostTransY = getHeight() / 2.f - (mScaledBitmapHeight * mProperScale) / 2.f;

                Matrix matrix = new Matrix();
                matrix.preScale(mProperScale, mProperScale);
                matrix.postTranslate(initPostTransX, initPostTransY);
                mMapAndLines.setImageMatrix(matrix);
                mMapAndLines.setImageBitmap(properBitmap);

                /**初始化 地图原点的坐标 */
                original.setX(initPostTransX);
                original.setY(initPostTransY);

                LogTool.LogE_DEBUG(TAG, "initMap----------> "
                        + " initPostTransX:" + initPostTransX
                        + " initPostTransY: " + initPostTransY
                        + " mProperScale: " + mProperScale);

                if (listener != null) {
                    listener.onMapBitmapLoaded();
                }
            }
        });
    }


    private float mDownX;
    private float mDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
//                LogTool.LogE_DEBUG(TAG, "onInterceptTouchEvent---> MotionEvent.ACTION_DOWN");
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
//                LogTool.LogE_DEBUG(TAG, " onInterceptTouchEvent---> MotionEvent.ACTION_MOVE");
                if (Math.abs(event.getX() - mDownX) > mTouchslop
                        || Math.abs(event.getY() - mDownY) > mTouchslop) {
                    return true;
                } else {
                    return false;
                }
            }
            case MotionEvent.ACTION_UP: {
//                LogTool.LogE_DEBUG(TAG, " onInterceptTouchEvent---> MotionEvent.ACTION_UP");
                break;
            }
        }
        return false;
    }

    private int mode = 0;// 初始状态, 记录是拖拉照片模式还是放大缩小照片模式
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;

    private PointF mStartPoint = new PointF();  // 用于记录 开始时候的坐标位置
    private Matrix mMatrix = new Matrix();      // 用于记录 拖拉图片移动的坐标位置
    private Matrix mCurMatrix = new Matrix();   // 用于记录 图片要进行拖拉时候的坐标位置

    private float mStartDis;                    // 两个手指的开始距离
    private PointF midPoint;                    // 两个手指的中间点

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 手指压下屏幕
            case MotionEvent.ACTION_DOWN: {
                mode = MODE_DRAG;
                mCurMatrix.set(mMapAndLines.getImageMatrix());
                mStartPoint.set(event.getX(), event.getY());
                break;
            }
            // 手指在屏幕上移动，改事件会被不断触发
            case MotionEvent.ACTION_MOVE: {
                // 拖拉图片
                if (mode == MODE_DRAG) {
                    // 最小倍数 不支持拖动
//                    if (matrixValues[0] == mProperScale) {
//                        super.onTouchEvent(event);
//                        return false;
//                    }
                    float dx = event.getX() - mStartPoint.x; // 得到x轴的移动距离
                    float dy = event.getY() - mStartPoint.y; // 得到x轴的移动距离
                    LogTool.LogE_DEBUG(TAG, "ACTION_MOVE---> dx: " + dx + " dy: " + dy);
                    // 在没有移动之前的位置上进行移动
                    mMatrix.set(mCurMatrix);
                    mMatrix.postTranslate(dx, dy);
                } else if (mode == MODE_ZOOM) {
                    // 放大缩小图片
                    float endDis = distance(event);          // 结束距离
                    if (endDis > mTouchslop) {               // 两个手指并拢在一起的时候像素大于10
                        float scale = endDis / mStartDis;    // 得到缩放倍数
                        mMatrix.set(mCurMatrix);
                        mMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
            }
            // 手指离开屏幕
            case MotionEvent.ACTION_UP: {
//                LogTool.LogE_DEBUG(TAG, "onTouchEvent---> MotionEvent.ACTION_UP");
                // 两个手指离开屏幕, 当缩小到小于 最小倍数时，重置为居中最小状态
                if (mCurScale < mProperScale) {
                    float scale = mProperScale / mCurScale;
                    mCurScale = mProperScale;

                    float initPostTransX = getWidth() / 2.f - (mScaledBitmapWith * mCurScale) / 2.f;
                    float initPostTransY = getHeight() / 2.f - (mScaledBitmapHeight * mCurScale) / 2.f;

                    float offsetX = -original.getX() + initPostTransX;
                    float offsetY = -original.getY() + initPostTransY;

                    mMatrix.preScale(scale, scale);
                    mMatrix.postTranslate(offsetX, offsetY);
                    updateBaseMapAndLines();
                }
                break;
            }
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN: {
//                LogTool.LogE_DEBUG(TAG, "onTouchEvent ---> MotionEvent.ACTION_POINTER_DOWN");
                mode = MODE_ZOOM; //设置为缩放模式

                /** 计算两个手指间的距离 */
                mStartDis = distance(event);

                /** 计算两个手指间的中间点 */
                if (mStartDis > mTouchslop) {  // 两个手指并拢在一起的时候像素大于 mTouchslop
                    midPoint = mid(event);
                    // 记录当前ImageView的缩放倍数
                    mCurMatrix.set(mMapAndLines.getImageMatrix());
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                // 当触点离开屏幕，但是屏幕上还有触点(手指)
//                LogTool.LogE_DEBUG(TAG, "onTouchEvent---> MotionEvent.ACTION_POINTER_UP");
                mode = 0;
                break;
            }
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                // 移动 地图
                updateBaseMapAndLines();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }

        /**
         * 点击在一定范围时 才触发点击事件
         */
        if (Math.abs(event.getX() - mDownX) < mTouchslop
                && Math.abs(event.getY() - mDownY) < mTouchslop) {
            super.onTouchEvent(event);
        }

        return true;
    }


    /**
     * 更新地图上点的位置
     */
    private void updateBaseMapAndLines() {
        mMapAndLines.setImageMatrix(mMatrix);

        float[] matrixValues = new float[9];
        mMatrix.getValues(matrixValues);
        mCurScale = matrixValues[0];

        // 地图原点的坐标
        original.setX(0 * matrixValues[0] + matrixValues[2]);
        original.setY(0 * matrixValues[4] + matrixValues[5]);

        // 移动点
        for (int i = 0; i < mapPoints.size(); i++) {
            double scaleX = mapPoints.get(i).getPoint()[0] * matrixValues[0];
            double scaleY = mapPoints.get(i).getPoint()[1] * matrixValues[4];
            mapPoints.get(i).setShowX((float) (scaleX + matrixValues[2]));
            mapPoints.get(i).setShowY((float) (scaleY + matrixValues[5]));
        }
//        mMapAndLines.invalidate();
    }

    /**
     * 计算两个手指间的距离
     */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两个手指间的中间点
     */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

}
