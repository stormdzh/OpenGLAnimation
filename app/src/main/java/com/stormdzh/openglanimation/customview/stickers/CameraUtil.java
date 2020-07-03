package com.stormdzh.openglanimation.customview.stickers;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.stormdzh.openglanimation.util.LogUtil;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2020-07-03 10:35
 */
public class CameraUtil {
    Camera camera;
    SurfaceHolder surfaceHolder;
    MediaRecorder mediaRecorder;
    int width = 1280;
    int height = 720;
    Camera.Size size;
    public static final String TAG = "CameraUtil";
    ICameraSize iCameraSize;
    int degrees;
    int AudioSource = MediaRecorder.AudioSource.MIC;
    int VideoSource = MediaRecorder.VideoSource.CAMERA;
    int OutputFormat = MediaRecorder.OutputFormat.MPEG_4;
    int AudioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
    int VideoEncoder = MediaRecorder.VideoEncoder.H264;
    Camera.PreviewCallback previewCallbackWithBuffer;
    int cameraId;
    Context ctx;

    public CameraUtil setOrientationHint(int degrees) {
        this.degrees = degrees;
        return this;

    }

    public CameraUtil setAudioSource(int AudioSource) {
        this.AudioSource = AudioSource;
        return this;

    }

    public CameraUtil setVideoSource(int VideoSource) {
        this.VideoSource = VideoSource;
        return this;

    }

    public CameraUtil setOutputFormat(int OutputFormat) {
        this.OutputFormat = OutputFormat;
        return this;

    }

    public CameraUtil setAudioEncoder(int AudioEncoder) {
        this.AudioEncoder = AudioEncoder;
        return this;

    }

    public CameraUtil setVideoEncoder(int VideoEncoder) {
        this.VideoEncoder = VideoEncoder;
        return this;

    }

    public CameraUtil setVideoWidth(int width) {
        this.width = width;
        return this;

    }

    public CameraUtil setVideoHeight(int height) {
        this.height = height;
        return this;

    }

    public int getVideoWidth() {
        return width;

    }

    public int getVideoHeight() {
        return height;

    }

    private CameraUtil() {

    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    ;
    static CameraUtil takeVideoUtil;

    public static synchronized CameraUtil getInstance() {
        if (takeVideoUtil == null) {
            takeVideoUtil = new CameraUtil();
        }
        return takeVideoUtil;
    }


    public void checkCamera() {
        if (camera != null) {
            try {
                Camera.Parameters p = camera.getParameters();
                camera.setParameters(p);
            } catch (RuntimeException e) {
                Log.d("jia=====", "检验camera，打开摄像头失败，重启中");
                releaseCamera();
//                initCamera(surfaceHolder);
                // TakeVideoActivity.this.setResult(-1);
            }

        }
    }

    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    private int FindBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }


    public Camera getCamera() {
        int id = FindBackCamera();
        LogUtil.d(TAG, "FindFrontCamera,cameraid=" + id);
        if (id == -1) {
            id = FindFrontCamera();
            LogUtil.d(TAG, "FindBackCamera,cameraid=" + id);

        }
        cameraId = id;
        camera = Camera.open(id);
        return camera;
    }

    public void initCamera(SurfaceHolder arg0, SurfaceTexture s) {
        this.surfaceHolder = arg0;

        try {
            Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();


            Camera.Parameters p = camera.getParameters();
            p.setPreviewFormat(ImageFormat.NV21);
            LogUtil.d(TAG, "display.getRotation()=" + display.getRotation());
            if (display.getRotation() == Surface.ROTATION_0) {
                camera.setDisplayOrientation(0);
            } else if (display.getRotation() == Surface.ROTATION_270) {
                camera.setDisplayOrientation(180);
            }

            camera.setPreviewTexture(s);
            camera.addCallbackBuffer(new byte[(int) ((width * height) * 1.5)]);
            camera.setPreviewCallbackWithBuffer(previewCallbackWithBuffer);

            List<Camera.Size> list = p.getSupportedVideoSizes();

            if (size != null) {
                width = size.width;
                height = size.height;
            } else {
//
//	 params set KEY_PREVIEW_SIZE:800x600
//	 set KEY_SUPPORTED_PREVIEW_SIZES:176x144,320x240,352x288,640x480,720x480,800x600,1280x720,1920x1080,960x540
//	 initDefaultParameters(1376): Support Preview sizes: 640x480,800x600,1280x720,1280x960    1280x720(default)    800x600(force)
//                    width = 1280;
//                    height = 720;

            }
            p.setPreviewSize(width, height);
//                p.setRotation(270);
            if (iCameraSize != null) {
                iCameraSize.onPreviewSize(width, height);
            }
            camera.setParameters(p);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "开摄像头失败,请重新打开");
            e.printStackTrace();
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "开摄像头失败,请重新打开");

            e.printStackTrace();
        }
        Log.e(TAG, "initCamera");
    }


    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            // camera.lock();
            camera.release();
            camera = null;
        }
    }

    public void setICameraSizeCallBack(ICameraSize iCameraSize) {
        this.iCameraSize = iCameraSize;
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallbackWithBuffer) {
        this.previewCallbackWithBuffer = previewCallbackWithBuffer;
    }

    public interface ICameraSize {
        public void onPreviewSize(int width, int height);
    }

    public int getRotation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
//        LogUtils.d(TAG,"Camera orientation"+info.orientation);
        return info.orientation;
    }

}