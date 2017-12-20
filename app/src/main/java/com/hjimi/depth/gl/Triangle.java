package com.hjimi.depth.gl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2017/12/18.
 */

public class Triangle {

    private FloatBuffer vertexBuffer;

    //设置每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    //设置三角形顶点数组
    static float triangleCoords[] = {   //默认按逆时针方向绘制
            0.0f,  1.0f, 0.0f, // 顶点
            -1.0f, -0.0f, 0.0f, // 左下角
            1.0f, -0.0f, 0.0f  // 右下角
    };

    // 设置三角形颜色和透明度（r,g,b,a）
    float color[] = {0.0f, 1.0f, 0f, 1.0f};//绿色不透明
int mProgram;
    public Triangle() {
        // 初始化顶点字节缓冲区，用于存放形状的坐标
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        // 创建空的OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        // 将vertex shader添加到program
        GLES20.glAttachShader(mProgram, vertexShader);

        // 将fragment shader添加到program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 创建可执行的 OpenGL ES program
        GLES20.glLinkProgram(mProgram);
        ByteBuffer bb = ByteBuffer.allocateDirect(
                //(每个浮点数占用4个字节
                triangleCoords.length * 4);
        //设置使用设备硬件的原生字节序
        bb.order(ByteOrder.nativeOrder());

        //从ByteBuffer中创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标都添加到FloatBuffer中
        vertexBuffer.put(triangleCoords);
        //设置buffer从第一个坐标开始读
        vertexBuffer.position(0);
    }
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    public  int loadShader(int type, String shaderCode){

        //创建一个vertex shader类型(GLES20.GL_VERTEX_SHADER)
        //或一个fragment shader类型(GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // 将源码添加到shader并编译它
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    int mPositionHandle;
    int mColorHandle;
    public void ondraw(){
        GLES20.glUseProgram(mProgram);

        // 获取指向vertex shader的成员vPosition的handle
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // 获取指向fragment shader的成员vColor的handle
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //  绘制三角形
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // 禁用指向三角形的顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
