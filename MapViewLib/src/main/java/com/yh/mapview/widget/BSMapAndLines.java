package com.yh.mapview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 自定义 地图 地图
 * Created by desireyao on 2016/6/30.
 */
@SuppressLint("AppCompatCustomView")
public class BSMapAndLines extends ImageView {

    // 线 坐标
    public ArrayList<MapLineCoord> mapLineCoords;

    public BSMapAndLines(Context context) {
        super(context);
        mapLineCoords = new ArrayList<>();
    }

    public BSMapAndLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        mapLineCoords = new ArrayList<>();
    }

    public ArrayList<MapLineCoord> getMapLineCoords() {
        return mapLineCoords;
    }

    public int getLineSize() {
        return mapLineCoords.size();
    }

    public void clearLines() {
        mapLineCoords.clear();
        invalidate();
    }

    public void addLines(ArrayList<MapLineCoord> mapLineCoords) {
        this.mapLineCoords.addAll(mapLineCoords);
    }

    public MapLineCoord getLine(int index) {
        return mapLineCoords.get(index);
    }

    public void addLineCoord(MapLineCoord mapLineCoord) {
        mapLineCoords.add(mapLineCoord);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mapLineCoords == null || mapLineCoords.isEmpty()) {
            return;
        }

        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(10.f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

//        canvas.drawCircle(mapLineCoords.get(0).getViewX(), mapLineCoords.get(0).getViewY(), 10, paint);
//        canvas.drawCircle(
//                mapLineCoords.get(mapLineCoords.size() - 1).getViewX(),
//                mapLineCoords.get(mapLineCoords.size() - 1).getViewY(),
//                10, paint);

        // 划线
        for (int i = 1; i < mapLineCoords.size(); i++) {
            if (mapLineCoords.get(i).getType() == 1) {
                mPaint.setColor(Color.LTGRAY);
            } else {
                mPaint.setColor(Color.GREEN);
            }

            canvas.drawLine(mapLineCoords.get(i - 1).getViewX(), mapLineCoords.get(i - 1).getViewY(),
                    mapLineCoords.get(i).getViewX(), mapLineCoords.get(i).getViewY(), mPaint);
        }

//        Path linePath = new Path();
//        linePath.moveTo(mapLineCoords.get(0).getViewX(), mapLineCoords.get(0).getViewY());
//        for (int i = 1; i < mapLineCoords.size(); i++) {
//            if (mapLineCoords.get(i).getType() == 1) {
//                mPaint.setColor(Color.LTGRAY);
//            } else {
//                mPaint.setColor(Color.GREEN);
//            }
//            linePath.lineTo(mapLineCoords.get(i).getViewX(), mapLineCoords.get(i).getViewY());
//        }
//        canvas.drawPath(linePath, mPaint);
    }

    /**
     * 地图 线 拐点 坐标
     */
    public static class MapLineCoord {
        private float firstX;
        private float firstY;

        private float viewX;
        private float viewY;

        private int type;

        public MapLineCoord() {
        }

        public MapLineCoord(float firstX, float firstY, float viewX, float viewY) {
            this.firstX = firstX;
            this.firstY = firstY;
            this.viewX = viewX;
            this.viewY = viewY;
        }

        public float getFirstX() {
            return firstX;
        }

        public void setFirstX(float firstX) {
            this.firstX = firstX;
        }

        public float getFirstY() {
            return firstY;
        }

        public void setFirstY(float firstY) {
            this.firstY = firstY;
        }

        public float getViewX() {
            return viewX;
        }

        public void setViewX(float viewX) {
            this.viewX = viewX;
        }

        public float getViewY() {
            return viewY;
        }

        public void setViewY(float viewY) {
            this.viewY = viewY;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "MapLineCoord{" +
                    "firstX=" + firstX +
                    ", firstY=" + firstY +
                    ", viewX=" + viewX +
                    ", viewY=" + viewY +
                    ", type=" + type +
                    '}';
        }
    }
}
