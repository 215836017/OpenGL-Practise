package com.cakes.demoopengl.test02;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.cakes.demoopengl.BaseActivity;
import com.cakes.demoopengl.R;
import com.cakes.demoopengl.utils.OpenGLUtil;

public class TestActivity02 extends BaseActivity {

    private final String TAG = "TestActivity02";

    private GLSurfaceView glSurfaceView;
    private GLRenderer02 glRenderer;
    private GLColorRenderer02 glColorRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenGLUtil.isSupportEs2(this)) {
            showToast(TAG, "不支持OpenGL 2.0");
            finish();
        }

        setContentView(R.layout.activity_test02);
        glSurfaceView = findViewById(R.id.test_02_gl_surface_view);
        glSurfaceView.setEGLContextClientVersion(2);
        glRenderer = new GLRenderer02(this);  // 绘制一个纯白色的的三角形
        glColorRenderer = new GLColorRenderer02(this); // 绘制一个有颜色的三角形

//        glSurfaceView.setRenderer(glRenderer);
        glSurfaceView.setRenderer(glColorRenderer);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (null != glSurfaceView) {
//            glSurfaceView.onResume();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (null != glSurfaceView) {
//            glSurfaceView.onPause();
//        }
//    }

}