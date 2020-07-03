package com.stormdzh.openglanimation.customview.stickers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import com.stormdzh.openglanimation.util.LogUtil;

public class TextureUtils {
    static final String TAG = "TextureUtils";

    public static int loadOESTexture() {
        int texTureis[] = new int[1];
        GLES30.glGenTextures(1, texTureis, 0);
        if (texTureis[0] == 0) {
            LogUtil.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texTureis[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES30.glBindBuffer(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        return texTureis[0];

    }

    public static int loadTexture(Context ctx, int id) {
        int texTureis[] = new int[1];
        GLES30.glGenTextures(1, texTureis, 0);
        if (texTureis[0] == 0) {
            LogUtil.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texTureis[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), id);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        GLES30.glBindBuffer(GLES30.GL_TEXTURE_2D, 0);

        return texTureis[0];

    }
}
