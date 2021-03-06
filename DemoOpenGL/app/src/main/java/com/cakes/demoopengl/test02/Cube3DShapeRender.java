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
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 立方体
 */
public class Cube3DShapeRender implements GLSurfaceView.Renderer {
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

    //在数组中，描述一个顶点，总共的顶点需要的偏移量。这里因为只有位置顶点，所以和上面的值一样
    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX + COORDS_PER_COLOR;
    //一个点需要的byte偏移量。
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT;

    private final Context context;

    //pragram的指针
    private int mProgramObjectId;
    //顶点数据的内存映射
    private final FloatBuffer mVertexFloatBuffer;

    /*
    添加矩阵

     */
    private static final String U_MATRIX = "u_Matrix";
    //模型矩阵
    private float[] mModelMatrix = new float[16];

    private Matrix mViewMatrix;

    //投影矩阵
    private float[] mProjectionMatrix = new float[16];
    private int uMatrix;


    private float[] CUBE_COORD = {
            //order  x,y,z r,g,b
            -0.2f, 0.2f, 0.2f, 0f, 1f, 0f,   //正面左上0
            -0.2f, -0.2f, 0.2f, 0f, 1f, 0f,  //正面左下1
            0.2f, -0.2f, 0.2f, 0f, 1f, 0f,    //正面右下2
            0.2f, 0.2f, 0.2f, 0f, 1f, 0f,     //正面右上3
            -0.2f, 0.2f, -0.2f, 1f, 0f, 0f,   //反面左上4
            -0.2f, -0.2f, -0.2f, 1f, 0f, 0f,  //反面左下5
            0.2f, -0.2f, -0.2f, 1f, 0f, 0f,   //反面右下6
            0.2f, 0.2f, -0.2f, 1f, 0f, 0f,   //反面右上7
    };

    private final short CUBE_INDEX[] = {
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
    };

    private int VERTEX_COUNT = CUBE_COORD.length / TOTAL_COMPONENT_COUNT;
    private final ShortBuffer mIndexShortBuffer;

    public Cube3DShapeRender(Context context) {
        this.context = context;

        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(CUBE_COORD.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(CUBE_COORD);
        mVertexFloatBuffer.position(0);

        mIndexShortBuffer = ByteBuffer
                .allocateDirect(CUBE_INDEX.length * Constant.BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(CUBE_INDEX);
        mIndexShortBuffer.position(0);
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

        uMatrix = GLES20.glGetUniformLocation(mProgramObjectId, U_MATRIX);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //在窗口改变的时候调用
        GLES20.glViewport(0, 0, width, height);
//        //主要还是长宽进行比例缩放
        float aspectRatio =
                (float) width / (float) height;

        /**
         * fovy是表示视角 45du 的视角来创建一个投影
         */
        Matrix.perspectiveM(mProjectionMatrix, 0, 45, aspectRatio, 1f, 10f);

        //设置模型矩阵.沿着z轴平移-2
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0f, 0f, -2f);

        //添加旋转
        Matrix.rotateM(mModelMatrix, 0, 30f, 1f, -1f, -1f);


        //缓存相乘的结果
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, mProjectionMatrix, 0, mModelMatrix, 0);
//        最后复制到投影矩阵中
        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.length);
    }

    //在OnDrawFrame中进行绘制
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //传递给着色器
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, mProjectionMatrix, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                CUBE_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT,//数据的类型
                mIndexShortBuffer
        );

    }
}
