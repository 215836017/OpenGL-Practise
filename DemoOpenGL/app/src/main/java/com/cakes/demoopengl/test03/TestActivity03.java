package com.cakes.demoopengl.test03;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.cakes.demoopengl.BaseActivity;
import com.cakes.demoopengl.R;
import com.cakes.demoopengl.utils.LogUtil;
import com.cakes.demoopengl.utils.OpenGLUtil;

public class TestActivity03 extends BaseActivity {

    private final String TAG = "TestActivity03";

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenGLUtil.isSupportEs2(this)) {
            showToast(TAG, "不支持OpenGL 2.0");
            finish();
        }

        setContentView(R.layout.activity_test03);
        glSurfaceView = findViewById(R.id.test_03_gl_surface_view);
        glSurfaceView.setEGLContextClientVersion(2);

        LogUtil.d(TAG, "onCreate() - - 11111111");
        /*
         ................
         在有些手机上，修改代码后直接Apply changes and Restart就可以。
         而如果使用 Run "app" 则需要删除build文件夹才可以看到更新
         */
        glSurfaceView.setRenderer(new Texture2DShapeRender(this));  // 圆锥体
        showToast(TAG, "2D 2D 2D");
//        glSurfaceView.setRenderer(new TextureFilterShapeRender(this));  // 圆锥体
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != glSurfaceView) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != glSurfaceView) {
            glSurfaceView.onResume();
        }
    }
}