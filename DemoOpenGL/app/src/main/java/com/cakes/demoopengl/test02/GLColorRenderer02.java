package com.cakes.demoopengl.test02;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.cakes.demoopengl.utils.Constant;
import com.cakes.demoopengl.utils.LogUtil;
import com.cakes.demoopengl.utils.OpenGLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLColorRenderer02 implements GLSurfaceView.Renderer {

    private final String TAG = "GLColorRenderer02";

    private static final String VERTEX_SHADER_FILE = "test02/triangle_color_vertex_shader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "test02/triangle_color_fragment_shader.glsl";
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

    //顶点的坐标系
    private static float TRIANGLE_COLOR_COORDS[] = {
            //Order of coordinates: X, Y, Z, R,G,B,
            0.5f, 0.5f, 0.0f, 1.f, 0f, 0f, // top
            -0.5f, -0.5f, 0.0f, 0.f, 1f, 0f,  // bottom left
            0.5f, -0.5f, 0.0f, 0.f, 0f, 1f // bottom right
    };
    //设置颜色，依次为红绿蓝和透明通道。
    //因为颜色是常量，所以用单独的数据表示？
//    private static float TRIANGLE_COLOR[] = {1.0f, 1.0f, 1.0f, 1.0f};
    private static final int VERTEX_COUNT = TRIANGLE_COLOR_COORDS.length / TOTAL_COMPONENT_COUNT;
    private final Context context;

    //pragram的指针
    private int mProgramObjectId;
    //顶点数据的内存映射
    private final FloatBuffer mVertexFloatBuffer;

    public GLColorRenderer02(Context c) {
        this.context = c;
        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(TRIANGLE_COLOR_COORDS.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TRIANGLE_COLOR_COORDS);
        mVertexFloatBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        doOnSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //0.glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);
    }


    private void doOnSurfaceCreated(GL10 gl, EGLConfig config) {
        //0.先去得到着色器的代码
        String vertexShaderCode = OpenGLUtil.readAssetShaderCode(context, VERTEX_SHADER_FILE);
        String fragmentShaderCode = OpenGLUtil.readAssetShaderCode(context, FRAGMENT_SHADER_FILE);
        //1.得到之后，进行编译。得到id
        int vertexShaderObjectId = OpenGLUtil.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderObjectId = OpenGLUtil.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //3.继续套路。取得到program
        mProgramObjectId = GLES20.glCreateProgram();
        LogUtil.d(TAG, "doOnSurfaceCreated() -- mProgramObjectId = " + mProgramObjectId);
        //将shaderId绑定到program当中
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        //4.最后，启动GL link program
        GLES20.glLinkProgram(mProgramObjectId);

        GLES20.glUseProgram(mProgramObjectId);

        int aPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION);
        LogUtil.d(TAG, "doOnSurfaceCreated() -- mProgramObjectId = " + mProgramObjectId);
        mVertexFloatBuffer.position(0);
        GLES20.glVertexAttribPointer(
                aPosition,  //上面得到的id
                COORDS_PER_VERTEX, //告诉他用几个偏移量来描述一个顶点
                GLES20.GL_FLOAT, false,
                STRIDE, //一个顶点需要多少个字节的偏移量
                mVertexFloatBuffer);
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(aPosition);

        //4.去取得颜色的信息
        int aColor = GLES20.glGetAttribLocation(mProgramObjectId, A_COLOR);
        //这里需要position到第一个颜色变量的位置
        mVertexFloatBuffer.position(COORDS_PER_VERTEX);
        GLES20.glVertexAttribPointer(
                aColor,  //上面得到的id
                COORDS_PER_COLOR, //告诉他用几个偏移量来描述一个顶点
                GLES20.GL_FLOAT, false,
                STRIDE, //一个顶点需要多少个字节的偏移量
                mVertexFloatBuffer);
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(aColor);
    }
}
