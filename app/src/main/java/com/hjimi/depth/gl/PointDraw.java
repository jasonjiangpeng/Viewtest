package com.hjimi.depth.gl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/12/18.
 */

public class PointDraw extends  ObjectDraw{
    private float[] mArray = { 0f, 0f, 0f };
    private FloatBuffer mBuffer;
    public PointDraw() {
        mBuffer=Glutils.getFloatBuffer(mArray);
    }
    @Override
    public void ondraw(GL10 gl){
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // 允许设置顶点 // GL10.GL_VERTEX_ARRAY顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // 设置顶点
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBuffer);

        //设置点的颜色为绿色
        gl.glColor4f(0f, 1f, 0f, 0f);

        //设置点的大小
        gl.glPointSize(80f);

        // 绘制点
        gl.glDrawArrays(GL10.GL_POINTS, 0, 1);

        // 禁止顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
