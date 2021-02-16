package com.cakes.demoopengl.test02;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.cakes.demoopengl.utils.Constant;
import com.cakes.demoopengl.utils.OpenGLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 添加变化矩阵
 * 归一化设备坐标系。将设备的坐标都归一
 * 虚拟坐标空间。
 * 通过正交投影的方式。将虚拟坐标变换成归一化设备坐标时。实际上定义了三维世界的部分区域
 * <p>
 * 正交投影。
 * 通过正交投影的矩阵变化，让图形显示的像是正常的。不需要考虑设备屏幕适配的相关因素。
 * 透视出发。
 * 其实就是得到一个方向的视图。可以想象成是正视图。
 */
public class TriangleColorMatrixShapeRender implements GLSurfaceView.Renderer {

    private final String TAG = "TriangleColorMatrixShapeRender";
    private final Context context;
    /**
     * 更新shader的位置
     */
    private static final String VERTEX_SHADER_FILE = "test02/triangle_matrix_color_vertex_shader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "test02/triangle_matrix_color_fragment_shader.glsl";
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";

    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private static final int COORDS_PER_VERTEX = 3;
    //颜色信息的偏移量
    private static final int COORDS_PER_COLOR = 3;

    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX + COORDS_PER_COLOR;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT;

    //顶点的坐标系
    private static float TRIANGLE_COLOR_COORDS[] = {
            //Order of coordinates: X, Y, Z, R,G,B,
            0.5f, 0.5f, 0.0f, 1.f, 0f, 0f, // top
            -0.5f, -0.5f, 0.0f, 0.f, 1f, 0f,  // bottom left
            0.5f, -0.5f, 0.0f, 0.f, 0f, 1f // bottom right
    };

    private static final int VERTEX_COUNT = TRIANGLE_COLOR_COORDS.length / TOTAL_COMPONENT_COUNT;
    //pragram的指针
    private int mProgramObjectId;
    //顶点数据的内存映射
    private final FloatBuffer mVertexFloatBuffer;

    /*
        添加矩阵

         */
    private static final String U_MATRIX = "u_Matrix";
    private Matrix mModelMatrix;
    private Matrix mViewMatrix;

    //投影矩阵
    private float[] mProjectionMatrix = new float[16];
    private int uMatrix;

    public TriangleColorMatrixShapeRender(Context context) {
        this.context = context;
        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(TRIANGLE_COLOR_COORDS.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TRIANGLE_COLOR_COORDS);
        mVertexFloatBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //0.简单的给窗口填充一种颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderCode = OpenGLUtil.readAssetShaderCode(context, VERTEX_SHADER_FILE);
        String fragmentShaderCode = OpenGLUtil.readAssetShaderCode(context, FRAGMENT_SHADER_FILE);
        int vertexShaderObjectId = OpenGLUtil.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderObjectId = OpenGLUtil.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgramObjectId = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        GLES20.glLinkProgram(mProgramObjectId);

        GLES20.glUseProgram(mProgramObjectId);

        int aPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION);
        mVertexFloatBuffer.position(0);
        GLES20.glVertexAttribPointer(
                aPosition,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                STRIDE,
                mVertexFloatBuffer);

        GLES20.glEnableVertexAttribArray(aPosition);

        int aColor = GLES20.glGetAttribLocation(mProgramObjectId, A_COLOR);

        mVertexFloatBuffer.position(COORDS_PER_VERTEX);
        GLES20.glVertexAttribPointer(
                aColor,
                COORDS_PER_COLOR,
                GLES20.GL_FLOAT, false,
                STRIDE,
                mVertexFloatBuffer);
        GLES20.glEnableVertexAttribArray(aColor);


        /*************** 新增代码 ******************/
        uMatrix = GLES20.glGetUniformLocation(mProgramObjectId, U_MATRIX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //在窗口改变的时候调用
        GLES20.glViewport(0, 0, width, height);
        //主要还是长宽进行比例缩放
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            //横屏。需要设置的就是左右。
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1f, -1.f, 1f);
        } else {
            //竖屏。需要设置的就是上下
            Matrix.orthoM(mProjectionMatrix, 0, -1, 1f, -aspectRatio, aspectRatio, -1.f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //0.glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //传递给着色器
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, mProjectionMatrix, 0);

        //绘制三角形.
        //draw arrays的几种方式 GL_TRIANGLES三角形 GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形) GL_TRIANGLE_FAN扇形(可以描述圆形)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);

    }
}
