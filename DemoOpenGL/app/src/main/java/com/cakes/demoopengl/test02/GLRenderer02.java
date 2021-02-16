package com.cakes.demoopengl.test02;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.cakes.demoopengl.utils.Constant;
import com.cakes.demoopengl.utils.OpenGLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer02 implements GLSurfaceView.Renderer {

    private final String TAG = "GLRenderer02";

    private Context context;
    private FloatBuffer mVertexFloatBuffer;

    private static final String VERTEX_SHADER_FILE = "test02/triangle_vertex_shader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "test02/triangle_fragment_shader.glsl";
    private static final String A_POSITION = "aPosition";
    private static final String U_COLOR = "uColor";

    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_COLOR = 0;

    //在数组中，描述一个顶点，总共的顶点需要的偏移量。这里因为只有位置顶点，所以和上面的值一样
    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX + COORDS_PER_COLOR;
    //一个点需要的byte偏移量。
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT;

    //顶点的坐标系
    private static float TRIANGLE_COORDS[] = {
            //Order of coordinates: X, Y, Z
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f   // bottom right
    };
    //设置颜色，依次为红绿蓝和透明通道。
    private static float TRIANGLE_COLOR[] = {1.0f, 1.0f, 1.0f, 1.0f};
    private static final int VERTEX_COUNT = TRIANGLE_COORDS.length / TOTAL_COMPONENT_COUNT;
    //pragram的指针
    private int mProgramObjectId;

    public GLRenderer02(Context context) {
        this.context = context;
        /*
        0. 调用GLES20的包的方法时，其实就是调用JNI的方法。
        所以分配本地的内存块，将java数据复制到本地内存中，而本地内存可以不受垃圾回收的控制
        1. 使用nio中的ByteBuffer来创建内存区域。
        2. ByteOrder.nativeOrder()来保证，同一个平台使用相同的顺序
        3. 然后可以通过put方法，将内存复制过去。

        因为这里是Float，所以就使用floatBuffer
         */
        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(TRIANGLE_COORDS.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TRIANGLE_COORDS);
        mVertexFloatBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //0.简单的给窗口填充一种颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //0.先去得到着色器的代码
        String vertexShaderCode = OpenGLUtil.readAssetShaderCode(context, VERTEX_SHADER_FILE);
        String fragmentShaderCode = OpenGLUtil.readAssetShaderCode(context, FRAGMENT_SHADER_FILE);
        //1.得到之后，进行编译。得到id
        int vertexShaderObjectId = OpenGLUtil.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderObjectId = OpenGLUtil.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //3.继续套路。取得到program
        mProgramObjectId = GLES20.glCreateProgram();
        //将shaderId绑定到program当中
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        //4.最后，启动GL link program
        GLES20.glLinkProgram(mProgramObjectId);

        test();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //在窗口改变的时候调用
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //0.glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        test();
    }

    private void test() {
        GLES20.glUseProgram(mProgramObjectId);

        //1.根据我们定义的取出定义的位置
        int vPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION);
        //2.使用我们的position
        GLES20.glEnableVertexAttribArray(vPosition);
        //3.将坐标数据放入
        GLES20.glVertexAttribPointer(
                vPosition,  //上面得到的id
                COORDS_PER_VERTEX, //告诉他用几个偏移量来描述一个顶点
                GLES20.GL_FLOAT, false,
                STRIDE, //一个顶点需要多少个字节的偏移量
                mVertexFloatBuffer);

        //取出颜色
        int uColor = GLES20.glGetUniformLocation(mProgramObjectId, U_COLOR);

        //设置绘制三角形的颜色
        GLES20.glUniform4fv(
                uColor,
                1,
                TRIANGLE_COLOR,
                0
        );

        //绘制三角形.

        // 第一种方式：
        GLES20.glEnableVertexAttribArray(vPosition);  // 当test()在onSurfaceCreated()中调用时要加这一句

        // 第二种方式：
        /*
        draw arrays的几种方式：
        GL_TRIANGLES三角形
        GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形)
        GL_TRIANGLE_FAN扇形(可以描述圆形)
         */
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT); // 当test()在onDrawFrame()中调用时要加这一句
        //禁止顶点数组的句柄
//        GLES20.glDisableVertexAttribArray(vPosition);
    }

}
