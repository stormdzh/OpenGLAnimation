package com.stormdzh.openglanimation.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 图片-FBO
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class EarthRenderer implements GLSurfaceView.Renderer {
    private String TAG = "EarthRenderer";

    private Context mContext;


    public EarthRenderer(Context context) {
        this.mContext = context;

    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }


//    private void generateSphere(float radius, int rings, int sectors) {
//        float PI = (float) Math.PI;
//        float PI_2 = (float) (Math.PI / 2);
//
//        float R = 1f / rings;
//        float S = 1f / sectors;
//        short r;
//        short s;
//        short x;
//        float y;
//        float z;
//
//        int numPoint = (rings + 1) * (sectors + 1);
//        float vertexs[] =  new float[numPoint * 3];
//        float texcoords[] = new float[numPoint * 2];
//        short indices [] = new short[numPoint * 6];
//
//        int t = 0;
//        int v = 0;
//        r = 0;
//        while (r < rings + 1) {
//            s = 0;
//            while (s < sectors + 1) {
//                x =
//                        (Math.cos((2f * PI * (double)(float) s * S)) * Math.sin((PI * (float) r * R).toDouble())).toFloat();
//                y = -Math.sin((-PI_2 + PI * r.toFloat() * R).toDouble()).toFloat();
//                z =
//                        (Math.sin((2f * PI * s.toFloat() * S).toDouble()) * Math.sin((PI * r.toFloat() * R).toDouble())).toFloat();
//
//                texcoords[t++] = s * S;
//                texcoords[t++] = r * R;
//
//                vertexs[v++] = x * radius;
//                vertexs[v++] = y * radius;
//                vertexs[v++] = z * radius;
//                s++;
//            }
//            r++;
//        }
//
//        int counter = 0;
//        int sectorsPlusOne = sectors + 1;
//        r = 0;
//        while (r < rings) {
//            s = 0;
//            while (s < sectors) {
//                indices[counter++] = (short) (r * sectorsPlusOne + s);       //(a)
//                indices[counter++] = (short)((r + 1) * sectorsPlusOne + s)  ;  //(b)
//                indices[counter++] = (short)(r * sectorsPlusOne + (s + 1)) ; // (c)
//                indices[counter++] = (short)(r * sectorsPlusOne + (s + 1)) ; // (c)
//                indices[counter++] = (short)((r + 1) * sectorsPlusOne + s) ;   //(b)
//                indices[counter++] = (short)((r + 1) * sectorsPlusOne + (s + 1)) ; // (d)
//                s++;
//            }
//            r++;
//        }
//
//        vertexBuffer = GLTools.array2Buffer(vertexs);
//        texBuffer = GLTools.array2Buffer(texcoords);
//        mIndicesBuffer = GLTools.array2Buffer(indices);
//        indicesNum = indices.size;
//    }
}
