package com.stormdzh.openglanimation.customview.gt;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.stormdzh.openglanimation.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GLESTools
{

   public static int loadShader
   (
		 int shaderType,
		 String source
   ) 
   {

        int shader = GLES20.glCreateShader(shaderType);

        if (shader != 0) 
        {

            GLES20.glShaderSource(shader, source);

            GLES20.glCompileShader(shader);

            int[] compiled = new int[1];

            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0)
            {
                LogUtil.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                LogUtil.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;      
            }  
        }
        return shader;
    }
    
   public static int createProgram(String vertexSource, String fragmentSource)
   {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) 
        {
            return 0;
        }
        
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) 
        {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0)
        {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE)
            {
                LogUtil.e("ES20_ERROR", "Could not link program: ");
                LogUtil.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    
   public static void checkGlError(String op)
   {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
        {
            LogUtil.e("ES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
   }
   
   public static String loadFromAssetsFile(String fname, Resources r)
   {
   	String result=null;
   	try
   	{
   		InputStream in=r.getAssets().open(fname);
			int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    byte[] buff=baos.toByteArray();
		    baos.close();
		    in.close();
   		result=new String(buff,"UTF-8");
   		result=result.replaceAll("\\r\\n","\n");
   	}
   	catch(Exception e)
   	{
   		e.printStackTrace();
   	}    	
   	return result;
   }


    public static final int NO_TEXTURE = -1;

    public static int loadTexture(final Bitmap image, final int reUseTexture) {
        int[] texture = new int[1];
        if (reUseTexture == NO_TEXTURE) {
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, reUseTexture);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, image);
            texture[0] = reUseTexture;
        }
        return texture[0];
    }

}
