package com.cakes.demoopengl.test01;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.cakes.demoopengl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TestActivity01 extends AppCompatActivity {

    private final String TAG = "TestActivity01";

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_01);

        glSurfaceView = findViewById(R.id.gl_surface_view);

        initData();

        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                doOnGlSurfaceCreated(gl, config);
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                doOnGlSurfaceChanged(gl, width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                doOnGlSurfaceDrawFrame(gl);
            }
        });
    }


    private void doOnGlSurfaceCreated(GL10 gl, EGLConfig config) {
        // 关闭抗抖动
        gl.glDisable(GL10.GL_DITHER);
        // 设置系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        // 用红，绿，蓝，透明度四个值指定的颜色清屏
        gl.glClearColor(0, 0, 0, 0);
        // 设置阴影平滑模式
        gl.glShadeModel(GL10.GL_SMOOTH);
        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 设置深度测试的类型
        gl.glDepthFunc(GL10.GL_LEQUAL);

    }

    private void doOnGlSurfaceChanged(GL10 gl, int width, int height) {
        // 设置3D视窗的大小及位置
        gl.glViewport(0, 0, width, height);
        // 将当前矩阵模式设置为投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 初始化单位矩阵
        gl.glLoadIdentity();
        // 计算透视视窗的宽高比
        float ratio = (float) width / height;
        // 设置透视视窗的空间大小
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

    }

    /*** 控制旋转的角度 */
    private float rotate;

    private void doOnGlSurfaceDrawFrame(GL10 gl) {
        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 启用定点坐标数据
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 启用定点颜色数据
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        // 设置当前矩阵堆栈为模型堆栈
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // ---------- 绘制第一个图形 -------------
        // 重置当前的模型视图矩阵
        gl.glLoadIdentity();
        gl.glTranslatef(-0.32f, 0.35f, -1.2f);
        // 设置定点的位置数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleDataBuffer);
        // 设置定点的颜色数据
        gl.glColorPointer(4, GL10.GL_FIXED, 0, triangleColorBuffer);
        // 根据定点数据绘制平面图形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

        // ---------- 绘制第二个图形 -------------
        // 重置当前的模型视图矩阵
        gl.glLoadIdentity();
        gl.glTranslatef(0.6f, 0.8f, -1.5f);
        // 设置旋转
        gl.glRotatef(rotate, 0f, 0f, 0.1f);
        // 设置定点的位置数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rectDataBuffer);
        // 设置定点的颜色数据
        gl.glColorPointer(4, GL10.GL_FIXED, 0, rectColorBuffer);
        // 根据定点数据绘制平面图形
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        // ---------- 绘制第三个图形 -------------
        // 重置当前的模型视图矩阵
        gl.glLoadIdentity();
        gl.glTranslatef(-0.4f, -0.5f, -1.5f);
        // 设置旋转
        gl.glRotatef(rotate, 0f, 0.2f, 0f);
        // 设置定点的位置数据 继续使用上次的颜色
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rectDataBuffer2);
        // 根据定点数据绘制平面图形
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        // ---------- 绘制第四个图形 -------------
        // 重置当前的模型视图矩阵
        gl.glLoadIdentity();
        gl.glTranslatef(0.4f, -0.5f, -1.5f);
        // 设置使用纯色填充
        gl.glColor4f(1.0f, 0.2f, 0.2f, 0.0f);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        // 设置定点的位置数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, pentacleBuffer);
        // 根据定点数据绘制平面图形
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 5);

        // 绘制结束
        gl.glFinish();
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        rotate += 1;
    }


    private float[] triangleData = new float[]{
            0.1f, 0.6f, 0.0f,   // 上定点
            -0.3f, 0.0f, 0.0f,   // 左定点
            0.3f, 0.1f, 0.0f   // 右定点
    };
    private int[] triangleColor = new int[]{
            65535, 0, 0, 0,   // 上定点红色
            0, 65535, 0, 0,   // 左定点绿色
            0, 0, 65535, 0   // 右定点蓝色
    };

    private float[] rectData = new float[]{
            0.4f, 0.4f, 0.0f,   // 右上定点
            0.4f, -0.4f, 0.0f,   // 右下定点
            -0.4f, 0.4f, 0.0f,   // 左上定点
            -0.4f, -0.4f, 0.0f   // 左下定点
    };
    private int[] rectColor = new int[]{
            0, 65535, 0, 0,   // 右上定点绿色
            0, 0, 65535, 0,     // 右下定点蓝色
            65535, 0, 0, 0,    // 左上定点红色
            65535, 65535, 0, 0   // 左下定点黄色
    };

    private float[] rectData2 = new float[]{
            -0.4f, 0.4f, 0.0f,   // 左上定点
            0.4f, 0.4f, 0.0f,   // 右上定点
            0.4f, -0.4f, 0.0f,   // 右下定点
            -0.4f, -0.4f, 0.0f   // 左下定点
    };

    private float[] pentacle = new float[]{
            0.4f, 0.4f, 0.0f,
            -0.2f, 0.3f, 0.0f,
            0.5f, 0.0f, 0f,
            -0.4f, 0f, 0f,
            -0.1f, -0.3f, 0f
    };

    private FloatBuffer triangleDataBuffer;
    private IntBuffer triangleColorBuffer;
    private FloatBuffer rectDataBuffer;
    private IntBuffer rectColorBuffer;
    private FloatBuffer rectDataBuffer2;
    private FloatBuffer pentacleBuffer;

    private void initData() {
        triangleDataBuffer = floatBufferUtil(triangleData);
        rectDataBuffer = floatBufferUtil(rectData);
        rectDataBuffer2 = floatBufferUtil(rectData2);
        pentacleBuffer = floatBufferUtil(pentacle);

        triangleColorBuffer = intBufferUtil(triangleColor);
        rectColorBuffer = intBufferUtil(rectColor);
    }

    /**
     * 定义一个工具方法，将int数组转换为OpenGL ES所需的IntBuffer
     */
    private IntBuffer intBufferUtil(int[] arr) {
        IntBuffer intBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(arr);
        intBuffer.position(0);

        return intBuffer;
    }

    /**
     * 定义一个工具方法，将float数组转换为OpenGL ES所需的FloatBuffer
     */
    private FloatBuffer floatBufferUtil(float[] arr) {
        FloatBuffer floatBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(arr);
        floatBuffer.position(0);

        return floatBuffer;
    }
}
