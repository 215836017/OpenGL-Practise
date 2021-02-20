package com.cakes.demoopengl.test02;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.cakes.demoopengl.BaseActivity;
import com.cakes.demoopengl.R;
import com.cakes.demoopengl.utils.LogUtil;
import com.cakes.demoopengl.utils.OpenGLUtil;

public class TestActivity02 extends BaseActivity {

    private final String TAG = "TestActivity02";

    private GLSurfaceView glSurfaceView;
    private GLRenderer02 glRenderer;
    private GLColorRenderer02 glColorRenderer;
    private TriangleColorMatrixShapeRender triangleColorMatrixShapeRender;
    private Triangle3DShapeRender triangle3DShapeRender;
    private SquareShapeRender squareShapeRender;
    private CircleShapeRender circleShapeRender;
    private Cube3DShapeRender cube3DShapeRender;
    private Cylinder3DShapeRender cylinder3DShapeRender;
    private Ball3DShapeRender ball3DShapeRender;
    private Cone3DShapeRender cone3DShapeRender;

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
        triangleColorMatrixShapeRender = new TriangleColorMatrixShapeRender(this);
        triangle3DShapeRender = new Triangle3DShapeRender(this);
        squareShapeRender = new SquareShapeRender(this); // 正方形
        circleShapeRender = new CircleShapeRender(this);  // 圆形
        cube3DShapeRender = new Cube3DShapeRender(this);  // 立方体
        cylinder3DShapeRender = new Cylinder3DShapeRender(this);  // 圆柱体
        ball3DShapeRender = new Ball3DShapeRender(this);  // 球体
        cone3DShapeRender = new Cone3DShapeRender(this); // 圆锥体

        LogUtil.d(TAG, "onCreate() - - 11111111");
        /*
         ................
         在有些手机上，修改代码后直接Apply changes and Restart就可以。
         而如果使用 Run "app" 则需要删除build文件夹才可以看到更新
         */
//        glSurfaceView.setRenderer(glRenderer);
//        glSurfaceView.setRenderer(glColorRenderer);
//        glSurfaceView.setRenderer(triangleColorMatrixShapeRender);
//        glSurfaceView.setRenderer(triangle3DShapeRender);
//        glSurfaceView.setRenderer(squareShapeRender);
//        glSurfaceView.setRenderer(circleShapeRender);
//        glSurfaceView.setRenderer(cube3DShapeRender);
//        glSurfaceView.setRenderer(cylinder3DShapeRender);
//        glSurfaceView.setRenderer(ball3DShapeRender);
        glSurfaceView.setRenderer(cone3DShapeRender);
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