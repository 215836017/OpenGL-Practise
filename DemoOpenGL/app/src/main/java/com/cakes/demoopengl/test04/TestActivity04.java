package com.cakes.demoopengl.test04;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cakes.demoopengl.R;
import com.cakes.demoopengl.test04.camera.CameraView;
import com.cakes.demoopengl.test04.camera.core.ICamera;

import java.nio.ByteBuffer;

public class TestActivity04 extends AppCompatActivity {

    private final String TAG = "TestActivity04";

    public CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();// 隐藏ActionBar
        setContentView(R.layout.activity_test04);

        findViewById(R.id.test_04_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        startCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (null != cameraView) {
//            cameraView.onResume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.onPause();
        }
    }

    private void startCamera() {
        FrameLayout container = findViewById(R.id.test_04_layout_container);
        if (null == cameraView) {
            cameraView = new CameraView(this);
        }
        container.addView(cameraView,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void takePhoto() {
        if (null != cameraView) {
            cameraView.takePhoto(new ICamera.TakePhotoCallback() {
                @Override
                public void onTakePhoto(final byte[] bytes, final int width, final int height) {

                    //这里这个是从GL中读取现存
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ByteBuffer wrap = ByteBuffer.wrap(bytes);
                            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(wrap);

                            ImageView imageView = new ImageView(TestActivity04.this);
                            imageView.setImageBitmap(bitmap);
                            //因为读到的图上下翻转了。所以scale
                            imageView.setScaleY(-1);
                            new AlertDialog.Builder(TestActivity04.this)
                                    .setView(imageView)
                                    .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    });
                }
            });
        }
    }
}