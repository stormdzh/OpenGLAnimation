package com.stormdzh.openglanimation.customview.gt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.View;

import com.stormdzh.openglanimation.util.DeviceUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by lifuhao on 19/6/18.
 * gl 渲染 气泡
 *
 */
public class RenderBorderPP {

    private Context mContext;
    private View drawView;
    
    private int ppTexture =  GLESTools.NO_TEXTURE;

    private ShortBuffer drawIndecesBuffer;
    private FloatBuffer textrueBuffer;

    //计算 物体实际的渲染位置
    private float pixVerticesW,pixVerticesH;

    //气泡方向
    private final int OrientationLeft = 1;
    private final int OrientationRight = 2;
    private final int OrientationTop = 3;
    private final int OrientationBottom = 4;
    //动画100帧
    private final int duration = 100;
    //比例
    private float screenWidth,screenHeight;
    //泡泡 大小范围
    private int baseSizePix;//气泡原始大小
    private final float minScale = 0.5f;
    private final float maxScale = 1.8f;
    //泡泡 随机飘动偏移范围 每帧
    private final float minMoveOffset = 0.001f;
    private final float maxMoveOffset = 0.02f;
    // 飘动范围限制 基于焦点view  drawView
    private RectF showRectF = new RectF();
    private RectF hiddenRectF = new RectF();
    //泡泡移动的范围
    private final int Scope = 50;// dp
    private int pixScope;
    //相隔 多少像素 生成一个 气泡
    private final int addPPUnit = 20;//dp
    private int pixPPUnit;
    //随机
    private Random random = new Random();

    //气泡列表
    private List<PPItem> ppList = new ArrayList<>();



    private long drawCont = 0;
    private boolean updatePPMoveOffset = true;
    //变换矩阵
    private int glMatrix;
    private int glProgram;
    private int glPPTextureLoc;
    private int glPPPostionLoc;
    private int glPPTextureCoordLoc;
    private String vertexShader_filter = "" +
            "attribute vec4 aPPPosition;\n" +
            "attribute vec2 aPPTextureCoord;\n" +
            "varying vec2 vPPTextureCoord;\n" +
            "uniform mat4 vMatrix;"+
            "void main(){\n" +
            "   gl_Position= vMatrix * aPPPosition;\n" +
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


    public void onInit(Context context, int width, int height, Bitmap bitmap) {

        mContext = context;
        screenWidth = width;
        screenHeight = height;

        pixVerticesW = 2.0f / width;
        pixVerticesH =  2.0f / height;
        pixScope = DeviceUtil.dip2px(mContext,Scope);
        pixPPUnit = DeviceUtil.dip2px(mContext,addPPUnit);
        //5dp
        baseSizePix =  DeviceUtil.dip2px(mContext,5);

        initBuffer();
        
        ppTexture = GLESTools.loadTexture(bitmap, GLESTools.NO_TEXTURE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        glProgram = GLESTools.createProgram(vertexShader_filter, fragmentShader_filter);
        GLES20.glUseProgram(glProgram);
        glPPTextureLoc = GLES20.glGetUniformLocation(glProgram, "uPPTexture");
        glPPPostionLoc = GLES20.glGetAttribLocation(glProgram, "aPPPosition");
        glPPTextureCoordLoc = GLES20.glGetAttribLocation(glProgram, "aPPTextureCoord");
        glMatrix = GLES20.glGetUniformLocation(glProgram,"vMatrix");


    }

    private void initBuffer() { //初始化坐标顶点 与 纹理顶点
        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
        textrueBuffer = GLHelper.getScreenTextureVerticesBuffer();
    }



    public void setRunderView(View view) {
        drawView = view;
        drawCont = 0;

        showRectF = FocusViewTools.getFocusRect(drawView,pixVerticesW,pixVerticesH);

        //隐藏的范围
        hiddenRectF.left = showRectF.left - (pixScope * pixVerticesW);
        hiddenRectF.right = showRectF.right + (pixScope * pixVerticesW);
        hiddenRectF.top = showRectF.top + (pixScope * pixVerticesH);
        hiddenRectF.bottom = showRectF.bottom - (pixScope * pixVerticesH);



        setPPData();
    }

    private void setPPData(){
        ppList.clear();

        int xNum = (int) Math.ceil((float) drawView.getMeasuredWidth() /(float) pixPPUnit) + 1;
        int yNum = (int) Math.ceil((float) drawView.getMeasuredHeight() / (float)pixPPUnit) + 1;

        Log.i("adu","xNum:"+xNum+"  yNum:"+yNum);
//        for (int i = 0; i < yNum; i++) {
//            ppList.add(getPPItem(OrientationLeft, i));
//        }
//        for (int i = 0; i < yNum; i++) {
//            ppList.add(getPPItem(OrientationRight, i));
//        }
//        for (int i = 0; i < xNum; i++) {
//            ppList.add(getPPItem(OrientationTop, i));
//        }
//        for (int i = 0; i < xNum; i++) {
//            ppList.add(getPPItem(OrientationBottom, i));
//        }

        ppList.add(getPPItem(OrientationBottom, 0));
    }


    public void onDraw() {
        if (drawCont % 15 == 0){
            updatePPMoveOffset = true;
        }else {
            updatePPMoveOffset = false;
        }

        GLES20.glUseProgram(glProgram);

        GLES20.glEnableVertexAttribArray(glPPPostionLoc);
        GLES20.glEnableVertexAttribArray(glPPTextureCoordLoc);

        for (PPItem ppItem : ppList) {
            ppItem.drawPP();
        }

        GLES20.glFinish();
        GLHelper.disableVertex(glPPPostionLoc, glPPTextureCoordLoc);

        drawCont ++;
    }

    public PPItem getPPItem(int orientation, int index){
        PPItem item =  new PPItem();
        item.orientation = orientation;
        item.scaleSize = random.nextFloat()* (maxScale-minScale) + minScale;
        item.index = index;
        item.setPPLocation();
        return item;
    }


    public class PPItem{

        public int index;
        public float scaleSize = 1.0f;
        public int orientation = OrientationLeft;
        private float moveOffset = 0;
        private float ppVertices[];
        private FloatBuffer shapeBuffer = GLHelper.getShapeVerticesBuffer();

        private VaryTools varyTools;
        private float speed = 1;
        //注意 方便气泡移动缩放 使用的是3D
        //camera 位置 与 投影矩阵  会影响 最终的显示位置

        public void setPPLocation(){

            ppVertices = Arrays.copyOf(GLHelper.SquareVertices,GLHelper.SquareVertices.length);

            speed = random.nextFloat() * (3 - 1) + 1;

            varyTools = new VaryTools();


            varyTools.frustum(-1, 1, -1, 1, 1f, 2);
            //设置相机位置
            varyTools.setCamera( 0, 0, 2.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            ppVertices[0] = - baseSizePix * pixVerticesW;
            ppVertices[1] = baseSizePix * pixVerticesH;
            ppVertices[2] =  - baseSizePix * pixVerticesW;
            ppVertices[3] =  - baseSizePix * pixVerticesH;
            ppVertices[4] =  baseSizePix * pixVerticesW;
            ppVertices[5] = - baseSizePix * pixVerticesH;
            ppVertices[6] = baseSizePix * pixVerticesW;
            ppVertices[7] = baseSizePix * pixVerticesH;


            if (orientation == OrientationLeft){

                varyTools.translate(showRectF.left * multiple ,showRectF.top * multiple,0);
                varyTools.translate(0 ,-index * pixPPUnit * pixVerticesH * multiple,0);

            }else if (orientation == OrientationRight){

                varyTools.translate(showRectF.right * multiple, showRectF.top * multiple,0);
                varyTools.translate(0 ,-index * pixPPUnit * pixVerticesH * multiple,0);

            }else if (orientation == OrientationTop){

                varyTools.translate(showRectF.left * multiple,showRectF.top * multiple ,0);
                varyTools.translate(index * pixPPUnit * pixVerticesW * multiple,0 ,0);

            }else if (orientation == OrientationBottom){

                varyTools.translate(showRectF.left * multiple,showRectF.bottom * multiple ,0);
                varyTools.translate(index * pixPPUnit * pixVerticesW * multiple,0 ,0);

            }
            varyTools.scale(scaleSize,scaleSize,1);

            shapeBuffer.clear();
            shapeBuffer.put(ppVertices);
            shapeBuffer.position(0);

        }

        private final float multiple = 2;




        public void drawPP(){

            if (updatePPMoveOffset) {
                moveOffset = random.nextFloat() * (maxMoveOffset - minMoveOffset) - (maxMoveOffset / 2);
            }

            if (orientation == OrientationLeft){
                varyTools.translate(- pixVerticesW * speed* multiple ,moveOffset ,0);
            }else if (orientation == OrientationRight){
                varyTools.translate(pixVerticesW * speed * multiple ,moveOffset,0);
            }else if (orientation == OrientationTop){
                varyTools.translate(moveOffset , pixVerticesH * speed * multiple ,0);
            }else if (orientation == OrientationBottom){
                Log.i("adu","drawPP x:"+moveOffset+"  y:"+(-pixVerticesH * speed * multiple));
//                varyTools.translate(moveOffset, -pixVerticesH * speed * multiple ,0);
//                varyTools.translate(0.0053100996f, -0.0018876828f ,0);
                varyTools.translate(0.01f,0,0);
            }


            //指定vMatrix的值
            GLES20.glUniformMatrix4fv(glMatrix,1,false,varyTools.getFinalMatrix(),0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ppTexture);
            GLES20.glUniform1i(glPPTextureLoc, 0);

            GLHelper.updateVertex(glPPPostionLoc, glPPTextureCoordLoc, shapeBuffer,textrueBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);


        }
    }





}
