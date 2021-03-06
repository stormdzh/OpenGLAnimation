package com.stormdzh.openglanimation.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.entity.common.FunctionEntity;
import com.stormdzh.openglanimation.ui.activity.function.AlphaPicActivity;
import com.stormdzh.openglanimation.ui.activity.function.AlphaShapeActivity;
import com.stormdzh.openglanimation.ui.activity.function.BorderActivity;
import com.stormdzh.openglanimation.ui.activity.function.CircleActivity;
import com.stormdzh.openglanimation.ui.activity.function.EarthActivity;
import com.stormdzh.openglanimation.ui.activity.function.FBOActivity;
import com.stormdzh.openglanimation.ui.activity.function.LineActivity;
import com.stormdzh.openglanimation.ui.activity.function.PicActivity;
import com.stormdzh.openglanimation.ui.activity.function.PointActivity;
import com.stormdzh.openglanimation.ui.activity.function.RectangleActivity;
import com.stormdzh.openglanimation.ui.activity.function.ScaleActivity;
import com.stormdzh.openglanimation.ui.activity.function.StarActivity;
import com.stormdzh.openglanimation.ui.activity.function.StickersActivity;
import com.stormdzh.openglanimation.ui.activity.function.TVFocusActivity;
import com.stormdzh.openglanimation.ui.activity.function.TotateActivity;
import com.stormdzh.openglanimation.ui.activity.function.TranslateActivity;
import com.stormdzh.openglanimation.ui.activity.function.TrapezoidActivity;
import com.stormdzh.openglanimation.ui.activity.function.TriangleActivity;
import com.stormdzh.openglanimation.ui.activity.function.VBOActivity;
import com.stormdzh.openglanimation.ui.activity.function.YuvActivity;
import com.stormdzh.openglanimation.ui.activity.function.YuvPicActivity;
import com.stormdzh.openglanimation.ui.adapter.FunctionAdapter;
import com.stormdzh.openglanimation.util.hook.HookSetOnClickListenerHelper;
import com.stormdzh.openglanimation.util.notification.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FunctionAdapter mFunctionAdapter;
    private List<FunctionEntity> functionList;


    private NotificationUtil mNotificationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HookSetOnClickListenerHelper.hookActivity();
        setContentView(R.layout.activity_main);
        requestPermissions();
        initData();
        mFunctionAdapter = new FunctionAdapter(this, functionList);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFunctionAdapter);

        testNotification();
    }

    private void testNotification() {

        mNotificationUtil=new NotificationUtil();
        mNotificationUtil.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationUtil.showWx();
        }

//        mRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mNotificationUtil.cancel();
//            }
//        }, 5000);
    }


    private void requestPermissions() {
        ArrayList<String> ps = new ArrayList<>();
        int per = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (per != PackageManager.PERMISSION_GRANTED) {
            ps.add(Manifest.permission.CAMERA);
        }
        per = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (per != PackageManager.PERMISSION_GRANTED) {
            ps.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        per = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (per != PackageManager.PERMISSION_GRANTED) {
            ps.add(Manifest.permission.READ_PHONE_STATE);
        }
        per = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (per != PackageManager.PERMISSION_GRANTED) {
            ps.add(Manifest.permission.RECORD_AUDIO);
        }
        per = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (per != PackageManager.PERMISSION_GRANTED) {
            ps.add(Manifest.permission.INTERNET);
        }
        per = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (per != PackageManager.PERMISSION_GRANTED) {
            ps.add(Manifest.permission.INTERNET);
        }
        if (!ps.isEmpty()) {
            String[] ps3 = new String[ps.size()];
            ps.toArray(ps3);
            ActivityCompat.requestPermissions(this, ps3, 100);
        }
    }

    private void initData() {
        if (functionList == null) {
            functionList = new ArrayList<>();
        }

        functionList.add(new FunctionEntity("OpenGL 绘制点", PointActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制线条", LineActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制三角形", TriangleActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制矩形", RectangleActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制梯形", TrapezoidActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制星星", StarActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制圆形", CircleActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制图片", PicActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制YUV视频", YuvActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制YUV图片", YuvPicActivity.class));
        functionList.add(new FunctionEntity("OpenGL 位移动画", TranslateActivity.class));
        functionList.add(new FunctionEntity("OpenGL 动旋动画", TotateActivity.class));
        functionList.add(new FunctionEntity("OpenGL 缩放动画", ScaleActivity.class));
        functionList.add(new FunctionEntity("OpenGL 形状Alpha动画", AlphaShapeActivity.class));
        functionList.add(new FunctionEntity("OpenGL 图片Alpha动画", AlphaPicActivity.class));
        functionList.add(new FunctionEntity("OpenGL 焦点边框", BorderActivity.class));
        functionList.add(new FunctionEntity("OpenGL TV焦点页面", TVFocusActivity.class));
        functionList.add(new FunctionEntity("OpenGL 贴纸", StickersActivity.class));
        functionList.add(new FunctionEntity("OpenGL VBO", VBOActivity.class));
        functionList.add(new FunctionEntity("OpenGL FBO", FBOActivity.class));
        functionList.add(new FunctionEntity("OpenGL 绘制地球", EarthActivity.class));
    }
}
