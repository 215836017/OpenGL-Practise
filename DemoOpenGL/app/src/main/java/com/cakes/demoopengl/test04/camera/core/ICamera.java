package com.cakes.demoopengl.test04.camera.core;

import android.graphics.SurfaceTexture;

public interface ICamera {
    boolean open(int cameraId);
    void setAspectRatio(AspectRatio ratio);
    boolean preview();
    boolean switchTo(int cameraId);
    boolean close();

    void setPreviewTexture(SurfaceTexture texture);

    ISize getPreviewSize();
    ISize getPictureSize();

    //拍照的回调
    void takePhoto(TakePhotoCallback callback);
    //预览图的回调
    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    interface TakePhotoCallback{
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    interface PreviewFrameCallback{
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

}
