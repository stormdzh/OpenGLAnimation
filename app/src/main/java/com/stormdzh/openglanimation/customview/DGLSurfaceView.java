package com.stormdzh.openglanimation.customview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.stormdzh.openglanimation.util.opgl.DrawLine;
import com.stormdzh.openglanimation.util.opgl.DrawPoint;
import com.stormdzh.openglanimation.util.opgl.DrawTriangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:31
 */
public class DGLSurfaceView extends GLSurfaceView {

    public DGLSurfaceView(Context context) {
        this(context, null);

    }

    public DGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        //设置渲染器此处自定Renderer
        setRenderer(new MyRenderer());
        //设置渲染模式

        setRenderMode(DGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //渲模式 有两种 和 被动
        //RENDERMODE_WHEN_DIRTY 被动渲染  也是 等待渲用面 染 刷新界requestRender(),
        //RENDERMODE_CONTINUOUSLY 主动渲染 onDrawFrame 消耗性能
    }


    //自定义 renderer 实现需求功能  实现三个方法
    public class MyRenderer implements Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            //关闭抗抖动 对于颜色较少的系统 可以牺牲分辨率 通过抖动来增加颜色数量
            gl.glDisable(GL10.GL_DITHER);
            //设置 hint 模式 此处为 快速模式
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
            //设置背景颜色 此处为黑色 RGBA
            gl.glClearColor(0, 0, 0, 0);
            //开启深度检测
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {


            // 此方法中设置视口  因为 opengl是没有窗口的
            //该方法参数意义
            /**
             * 1，2 为 手机屏幕 x y坐标 00 表左上角
             * 3，4 表示 视口的宽和高
             * 四个参数 则能绘制出一个举行视口
             */
            gl.glViewport(0, 0, width, height);

            //设置投影矩阵
            gl.glMatrixMode(GL10.GL_PROJECTION);
            //矩阵单位化
            gl.glLoadIdentity();

            //获取视口的宽高比

            float r = (float) width / height;
            //然后 设置 是视角大小
            //参数意义
            /**
             *
             * 参数1表示 在近平面上 原点到左屏幕的距离
             * 参数2 表示在近平面上 原点到又屏幕的距离
             * 参数3 4 表示。。。上/下。。。
             * r 是 屏幕宽高比例  正常情况r<1 即是 高大于宽
             * 把高 设置为1 那么 原点到 左右屏幕距离就是r
             * 参数5 近平面距离
             * 参数6 是远平面距离 严格意义上可以 任意给值
             * 但实际情况 为了屏幕正常显示自己想要的图形应适当给值
             */
            gl.glFrustumf(-r, r, -1, 1, 1, 10);

        }

        @Override
        public void onDrawFrame(GL10 gl) {

            //此方法是绘制的图形的
            //首先清除颜色和深度缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //设置模型矩阵
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            //如果 你运行时 看不全自己要绘制的图形 这时候你可以 平移（缩放）一下
            /**
             * 参数解释
             * 1，2  为 x，y轴上的平移量
             * 3 为 z轴的平移量  注意  是float
             */
            gl.glTranslatef(0, 0, -3.0f);

            //绘制图形
            //此处把绘制的功能 写成一个类

//              DrawPoint point = new DrawPoint();
//              point.mDraw(gl);

//            DrawLine line = new DrawLine();
//            line.mDraw(gl);

            DrawTriangle drawTriangle = new DrawTriangle();
            drawTriangle.mDraw(gl);
        }
    }
}
