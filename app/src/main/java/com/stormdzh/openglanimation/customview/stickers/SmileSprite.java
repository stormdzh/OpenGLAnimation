package com.stormdzh.openglanimation.customview.stickers;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.stormdzh.openglanimation.R;

public class SmileSprite extends Sprite {
    Context ctx;
    int program;
    int textureId;
    public SmileSprite(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void rotate(int degree) {
//        Matrix.rotateM(mvpMartix,0,degree,0,0,1);
    }


    @Override
    public void setUp() {
        int vertextid2 = ShaderUtils.createShader(GLES30.GL_VERTEX_SHADER, DEFAULT_VERTEXTGLSL);
        int fragmentid2 = ShaderUtils.createShader(GLES30.GL_FRAGMENT_SHADER, DEFAULT_FRAGMENESHADER);
        program = ShaderUtils.createProgram(vertextid2, fragmentid2);
        textureId = initTexture(R.drawable.smile);
        mvpMartricLocatin=GLES30.glGetUniformLocation(program,MVPMATRIX);
        Matrix.setIdentityM(mvpMartix,0);

    }

    public int initTexture(int res) {
        return TextureUtils.loadTexture(ctx, res);
    }
    @Override
    public void move(int x, int y) {
        curX = x;
        curY = y;
    }

    public void draw() {
        draw(curX, curY);
    }

    @Override
    public void draw(int x, int y) {
        GLES30.glEnable(GLES30.GL_BLEND);
        //2d贴图 开启后贴图会透明混合
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        GLES30.glUseProgram(program);
        GLES30.glUniformMatrix4fv(mvpMartricLocatin, 1, false, mvpMartix, 0);
        GLES30.glViewport(x, height-y-spriteHeight, spriteWidth, spriteHeight);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, texturebuffer2);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisable(GLES30.GL_BLEND);
    }

    @Override
    public void release() {
        GLES30.glDeleteProgram(program);
    }
}
