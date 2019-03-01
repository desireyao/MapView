package com.yh.BSMapView;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yh.mapview.listener.OnGetMapViewListener;
import com.yh.mapview.listener.OnMapBitmapLoadListener;
import com.yh.mapview.log.LogTool;
import com.yh.mapview.utils.BitmapUtil;
import com.yh.mapview.widget.BSMapLayout;
import com.yh.mapview.widget.BSMapPointView;
import com.yh.mapview.widget.BSMapView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    private BSMapLayout mMapLayout;
    private BSMapView mMapView;

    private BSMapPointView mapPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        requestPermisson();
    }

    private void initView() {
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(this);

        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(this);

        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(this);

        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(this);

        mMapLayout = findViewById(R.id.mapLayout);
    }

    private void initMapView() {
        Bitmap bitmap = BitmapUtil.getBitmapFormAsset(this, "map.png");
        mMapView.initMap(bitmap, new OnMapBitmapLoadListener() {
            @Override
            public void onMapBitmapLoaded() {
                LogTool.LogD(TAG, "onMapBitmapLoaded --->");
            }
        });
    }

    private void requestPermisson() {
        AndPermission.with(this)
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                    }
                }).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn1) {
            mMapLayout.getMapView(new OnGetMapViewListener() {
                @Override
                public void onGetMapView(BSMapView mapView) {
                    mMapView = mapView;
                    mMapView.setBackgroundColor(getResources().getColor(R.color.gray));
                    initMapView();
                }
            });
        } else if (id == R.id.btn2) {
            Bitmap bitmap = BitmapUtil.getBitmapFormAsset(this,
                    "icon_user_point.png", 1f);
            mapPoint = new BSMapPointView(this, 0, 0, bitmap);
            mMapView.addMapPoint(mapPoint);
        } else if (id == R.id.btn3) {
            mapPoint.setPoint(500, 500);
            mMapView.updatePointPosition(mapPoint);
            mMapView.update2Center();
        }
    }
}
