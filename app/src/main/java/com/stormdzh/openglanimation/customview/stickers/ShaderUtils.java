package com.stormdzh.openglanimation.customview.stickers;

import android.opengl.GLES30;

import com.stormdzh.openglanimation.util.LogUtil;

public class ShaderUtils {
    static final String TAG = "ShaderUtils";

    public static int createShader(int type, String code) {

        int shaderid = GLES30.glCreateShader(type);
        if (shaderid != 0) {
            GLES30.glShaderSource(shaderid, code);
            GLES30.glCompileShader(shaderid);
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shaderid, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                String logInfo = GLES30.glGetShaderInfoLog(shaderid);
                LogUtil.e(TAG, "createShader error：" + logInfo);
                //创建失败
                GLES30.glDeleteShader(shaderid);
                return -1;
            }
            return shaderid;
        }


        return -1;
    }

    public static int createProgram(int vertextid, int framentid) {
        int programid = GLES30.glCreateProgram();
        if (programid != 0) {
            GLES30.glAttachShader(programid, vertextid);
            GLES30.glAttachShader(programid, framentid);
            GLES30.glLinkProgram(programid);

            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programid, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String logInfo = GLES30.glGetProgramInfoLog(programid);
                LogUtil.e(TAG, "createProgram error；" + logInfo);
                GLES30.glDeleteProgram(programid);
                GLES30.glDeleteShader(vertextid);
                GLES30.glDeleteShader(framentid);
                return 0;
            }
            GLES30.glDeleteShader(vertextid);
            GLES30.glDeleteShader(framentid);
            return programid;
        }

        return -1;
    }
}
