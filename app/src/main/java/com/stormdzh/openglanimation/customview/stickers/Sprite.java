package com.stormdzh.openglanimation.customview.stickers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class Sprite {
    final String DEFAULT_VERTEXTGLSL = "#version 300 es \n" +
            "layout (location = 0) in vec4 aposition;" +
            "layout (location = 1) in vec4 aTextureCoord;" +
            "uniform mat4 mvpMatrix;" +
            "out vec2 vTexCoord;" +


            "void main(){ " +
            "gl_Position=aposition*mvpMatrix;" +
            " vTexCoord = aTextureCoord.xy;\n" +

            "}";
    final String DEFAULT_FRAGMENESHADER = "#version 300 es \n" +
            "precision mediump float; " +
            "out vec4 vFragColor ;" +
            "uniform sampler2D  yuvTexSampler;\n" +
            "in vec2 vTexCoord;\n " +
            "void main() { " +
            "vFragColor  = texture(yuvTexSampler,vTexCoord);" +
            "}";
    static final float[] TEX_VERTEX2 = {
            0f, 1f,   //纹理坐标V1
            1f, 1f,   //纹理坐标V2
            0f, 0f,     //纹理坐标V3
            1f, 0f   //纹理坐标V4


    };
    public int curX, curY;
    public static final String TYPE_BIRD = "type_bird";
    public static final String TYPE_SMILE = "type_smile";
    public static final String TYPE_STUDENT = "type_student";
    public int spriteWidth = 200, spriteHeight = 200;
    FloatBuffer texturebuffer2;
    int mvpMartricLocatin;
    String MVPMATRIX="mvpMatrix";
    float mvpMartix[]=new float[16];
    public int width,height;

    public Sprite() {
        texturebuffer2 = ByteBuffer.allocateDirect(TEX_VERTEX2.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturebuffer2.put(TEX_VERTEX2);
        texturebuffer2.position(0);
    }
    public  void setDisplayParam(int width,int height){
        this.width=width;
        this.height=height;
    }
    public abstract void rotate(int degree);
    public abstract void setUp();

    public abstract int initTexture(int res);

    public abstract void move(int x, int y);

    public abstract void draw();

    public abstract void draw(int x, int y);

    public abstract void release();
}
