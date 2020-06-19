package com.stormdzh.openglanimation.util.opgl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:24
 */
public class DrawTriangle extends OpenGLUtils {
    private int vCount;
    private FloatBuffer verBuffer;
    private FloatBuffer colorBuffer;

    public DrawTriangle() {
        //初始化 顶点 颜色 和索引
        init();
    }

    private void init() {
        //设置多边形的 长度
        int length = 45;
        //设置顶点个数
        vCount = (360 / length + 2);
        //顶点坐标容器
        float ver[] = new float[vCount * 3];
        //第一个坐标点
        //计数器
        int count = 0;

        ver[count++] = 0;
        ver[count++] = 0;
        ver[count++] = 0;
        for (int i = 0; i < 360 + length; i += length) {
            ver[count++] = (float) (Math.cos(Math.toRadians(i)) - Math.sin(Math.toRadians(i)));
            ver[count++] = (float) (Math.cos(Math.toRadians(i)) + Math.sin(Math.toRadians(i)));
            ver[count++] = 0;
        }

        verBuffer = getFloatbuffer(ver);

        //顶点颜色
        int one = 65535;//支持65535色彩通道
        //顶点颜色数据（R GB A）
        float color[] = new float[]{
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0,
                0, one, 0, 0
        };
        //创建顶点颜色缓冲
        colorBuffer = getFloatbuffer(color);


    }
//绘制

    public void mDraw(GL10 gl) {
        //启用顶点坐标数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //启用顶颜色数组
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        //设置画笔
        //为画笔指定顶点数据
        /**
         * 参数1坐标个数
         * 参数2顶点数据类型
         * 参数3 连续顶点坐标数据的间隔
         * 参数4顶点数缓冲
         */
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verBuffer);
        //为花大笔指定顶点颜色数据
        //同上
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        /**
         * 参数1 绘制模型 点 线段 三角形
         *2 数组缓存开始的位置
         * 3 顶部观点个数
         */
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vCount);
    }
}
