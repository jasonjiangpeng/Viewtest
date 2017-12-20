package com.hjimi.depth.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/12/18.
 */

public class MyGlDraw extends GLSurfaceView {
    public MyGlDraw(Context context) {
        this(context,null);
    }

  private Triangle triangle;
  private ObjectDraw objectDraw;
    public MyGlDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
       setEGLContextClientVersion(2);
        setRenderer(new Render());
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        objectDraw=new BitmapDraw(context);
    }
    class   Render implements Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置背景色（r,g,b,a）
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//白色不透明
            //triangle=new Triangle();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //重绘背景色
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            //triangle.ondraw();
            objectDraw.ondraw(gl);
        }
    }

}
