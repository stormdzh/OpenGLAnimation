package com.stormdzh.openglanimation.customview.boder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.util.DeviceUtil;
import com.stormdzh.openglanimation.util.LogUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 气泡
 * @Author: dzh
 * @CreateDate: 2020-06-23 20:38
 */
public class BubbleRenderer {

    private Context mContext;

    private int ppTexture = GLESTools.NO_TEXTURE;

    private ShortBuffer drawIndecesBuffer;
    private FloatBuffer textrueBuffer;

    //气泡的宽高  x,y
    private float bubbleW, bubbleH;
    private float bublePosition = 0.8f;
    //泡泡 大小范围

    private final float minScale = 0.5f;
    private final float maxScale = 1.8f;


    //气泡方向
    private final int OrientationLeft = 1;
    private final int OrientationRight = 2;
    private final int OrientationTop = 3;
    private final int OrientationBottom = 4;
    //泡泡 随机飘动偏移范围 每帧
    private final int Scope = 50;// dp
    private int pixScope;
    //相隔 多少像素 生成一个 气泡
    private final int addPPUnit = 40;//dp
    private int pixPPUnit;
    //随机
    private Random random = new Random();

    //气泡列表
    private List<PPItem> mBubbleList = new ArrayList<>();

    private int mWidth, mHeight;
    //变换矩阵
//    private int glMatrix;
    private int glProgram;
    private int glPPTextureLoc;
    private int glPPPostionLoc;
    private int glPPTextureCoordLoc;
    private String vertexShader_filter = "" +
            "attribute vec4 aPPPosition;\n" +
            "attribute vec2 aPPTextureCoord;\n" +
            "varying vec2 vPPTextureCoord;\n" +
            "void main(){\n" +
            "   gl_Position= aPPPosition;\n" +
            "   vPPTextureCoord = aPPTextureCoord;\n" +
            "}";

    private String fragmentShader_filter = "" +
            "precision mediump float;\n" +
            "varying mediump vec2 vPPTextureCoord;\n" +
            "uniform sampler2D uPPTexture;\n" +
            "void main(){\n" +
            "   lowp vec4 c1 = texture2D(uPPTexture,vec2(vPPTextureCoord.x,1.0-vPPTextureCoord.y));\n" +
            "   lowp vec4 outputColor = c1;\n" +
            "   gl_FragColor = outputColor;\n" +
            "}";

    public BubbleRenderer(Context context) {
        this.mContext = context;
    }

    //初始化坐标顶点 与 纹理顶点
    private void initBuffer() {
        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
        textrueBuffer = GLHelper.getScreenTextureVerticesBuffer();
    }

    //生成一个气泡对象
    public PPItem getBubbleItem(int orientation, int index) {
        PPItem item = new PPItem();
        item.orientation = orientation;
        item.scaleSize = random.nextFloat() * (maxScale - minScale) + minScale;
//        LogUtil.i("adu","item.scaleSize:"+item.scaleSize);
        item.index = index;
        item.setPPLocation();
        return item;
    }

    //设置气泡数组
    private void initBubbleData() {
        mBubbleList.clear();

        int xNum = (int) Math.ceil((float) mWidth / (float) pixPPUnit) + 1;
        int yNum = (int) Math.ceil((float) mHeight / (float) pixPPUnit) + 1;

//        LogUtil.i("adu","xNum:"+xNum+"  yNum:"+yNum);
        for (int i = 0; i < yNum; i++) {
            mBubbleList.add(getBubbleItem(OrientationLeft, i));
        }
        for (int i = 0; i < yNum; i++) {
            mBubbleList.add(getBubbleItem(OrientationRight, i));
        }
        for (int i = 0; i < xNum; i++) {
            mBubbleList.add(getBubbleItem(OrientationTop, i));
        }
        for (int i = 0; i < xNum; i++) {
            mBubbleList.add(getBubbleItem(OrientationBottom, i));
        }
    }


    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        pixScope = DeviceUtil.dip2px(mContext, Scope);

        pixPPUnit = DeviceUtil.dip2px(mContext, addPPUnit);

        initBuffer();

//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.focus_border_pp);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.focus_bubble_red);
        ppTexture = GLESTools.loadTexture(bitmap, GLESTools.NO_TEXTURE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        glProgram = GLESTools.createProgram(vertexShader_filter, fragmentShader_filter);
        GLES20.glUseProgram(glProgram);
        glPPTextureLoc = GLES20.glGetUniformLocation(glProgram, "uPPTexture");
        glPPPostionLoc = GLES20.glGetAttribLocation(glProgram, "aPPPosition");
        glPPTextureCoordLoc = GLES20.glGetAttribLocation(glProgram, "aPPTextureCoord");
//        glMatrix = GLES20.glGetUniformLocation(glProgram, "vMatrix");

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {

//        bubbleW = 0.02f;
        bubbleH = 0.02f;
        if (mWidth!=width) {
            this.mWidth = width;
            this.mHeight = height;
//            isPerChange = false;
            bubbleW = bubbleH * height / width;
            LogUtil.i("adu", "气泡 onSurfaceChanged  bubbleW:"+bubbleW+"    bubbleH:"+bubbleH+"   mWidth:"+mWidth+"   mHeight:"+mHeight);
//            LogUtil.i("adu", "气泡 bubbleW:" + bubbleW);
            initBubbleData();
            for (PPItem ppItem : mBubbleList) {
                ppItem.setPPLocation();
            }
        }
    }

    public void onDrawFrame(GL10 gl) {

        GLES20.glClearColor(1.0f, 1.0f, 1f, 1f);

        GLES20.glUseProgram(glProgram);

        GLES20.glEnableVertexAttribArray(glPPPostionLoc);
        GLES20.glEnableVertexAttribArray(glPPTextureCoordLoc);

        for (PPItem ppItem : mBubbleList) {
            ppItem.drawPP();
        }

        GLES20.glFinish();
        GLHelper.disableVertex(glPPPostionLoc, glPPTextureCoordLoc);
    }

    public class PPItem {

        public int index;
        public float scaleSize = 1.0f;
        public int orientation = OrientationLeft;
        private float moveOffset = 0;
        private float ppVertices[];
        private FloatBuffer shapeBuffer = GLHelper.getShapeVerticesBuffer();
        private float x, y;

        public void setPPLocation() {

            //气泡顶点坐标
            ppVertices = Arrays.copyOf(GLHelper.SquareVertices, GLHelper.SquareVertices.length);

            ppVertices[0] = -bubbleW;
            ppVertices[1] = bubbleH;
            ppVertices[2] = -bubbleW;
            ppVertices[3] = -bubbleH;
            ppVertices[4] = bubbleW;
            ppVertices[5] = -bubbleH;
            ppVertices[6] = bubbleW;
            ppVertices[7] = bubbleH;
            bubbleScale(scaleSize);

//            LogUtil.i("adu", "气泡 index:"+index+"  bubbleW:"+bubbleW+"  bubbleH:"+bubbleH);

            int mideaHei = mHeight / 2;
            int mideaWid = mWidth / 2;

            if (orientation == OrientationLeft) {

                float hy = index * pixPPUnit;
                if (hy < mideaHei) {
                    x = -bublePosition;
                    y = hy / mideaHei;
                    if (y > bublePosition) {
                        y = bublePosition;
                    }
                } else {
                    x = -bublePosition;
                    y = -(hy - mideaHei) / mideaHei;
                    if (y < -bublePosition) {
                        y = -bublePosition;
                    }
                }

            } else if (orientation == OrientationRight) {

                float hy = index * pixPPUnit;
                if (hy < mideaHei) {
                    x = bublePosition;
                    y = hy / mideaHei;
                    if (y > bublePosition) {
                        y = bublePosition;
                    }
                } else {
                    x = bublePosition;
                    y = -(hy - mideaHei) / mideaHei;
                    if (y < -bublePosition) {
                        y = -bublePosition;
                    }

                }
            } else if (orientation == OrientationTop) {

                float hx = index * pixPPUnit;

                if (hx < mideaWid) {
                    x = -hx / mideaWid;
                    y = bublePosition;

                    if (x > -bublePosition) {
                        x = -bublePosition;
                    }

                } else {
                    x = (hx - mideaWid) / mideaWid;
                    y = bublePosition;

                    if (x > bublePosition) {
                        x = bublePosition;
                    }
                }

            } else if (orientation == OrientationBottom) {

                float hx = index * pixPPUnit;

                if (hx < mideaWid) {
                    x = -hx / mideaWid;
                    y = -bublePosition;
                    if (x > -bublePosition) {
                        x = -bublePosition;
                    }
                } else {
                    x = (hx - mideaWid) / mideaWid;
                    y = -bublePosition;

                    if (x > bublePosition) {
                        x = bublePosition;
                    }
                }

            }
            bubblelocation(x, y);

            shapeBuffer.clear();
            shapeBuffer.put(ppVertices);
            shapeBuffer.position(0);

        }

        private void bubbleScale(float scaleSize) {
            for (int i = 0; i < ppVertices.length; i++) {
                ppVertices[i] = ppVertices[i] * scaleSize;
            }
        }

        private void bubblelocation(float x, float y) {

            ppVertices[0] = ppVertices[0] + x;
            ppVertices[1] = ppVertices[1] + y;

            ppVertices[2] = ppVertices[2] + x;
            ppVertices[3] = ppVertices[3] + y;

            ppVertices[4] = ppVertices[4] + x;
            ppVertices[5] = ppVertices[5] + y;

            ppVertices[6] = ppVertices[6] + x;
            ppVertices[7] = ppVertices[7] + y;
        }

        public void drawPP() {

            float random = (float) (Math.random() / 500);
            float randomD = (float) (Math.random() / 320);
            int rd = Math.random() > 0.5 ? 1 : 0;
            if (orientation == OrientationLeft) {
                bubblelocation(-random, rd == 1 ? randomD : (0 - randomD));

            } else if (orientation == OrientationRight) {
                bubblelocation(random, rd == 1 ? randomD : (0 - randomD));
            } else if (orientation == OrientationTop) {
                bubblelocation(rd == 1 ? randomD : (0 - randomD), random);
            } else if (orientation == OrientationBottom) {
                bubblelocation(rd == 1 ? randomD : (0 - randomD), -random);
            }

            shapeBuffer.clear();
            shapeBuffer.put(ppVertices);
            shapeBuffer.position(0);

            if (bubbleOut()) {
                setPPLocation();
            }


            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ppTexture);
            GLES20.glUniform1i(glPPTextureLoc, 0);

            GLHelper.updateVertex(glPPPostionLoc, glPPTextureCoordLoc, shapeBuffer, textrueBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);


        }

        private boolean bubbleOut() {
            for (int i = 0; i < ppVertices.length; i++) {
                if (Math.abs(ppVertices[i]) >= 1) {
                    return true;
                }
            }
            return false;
        }
    }
}
