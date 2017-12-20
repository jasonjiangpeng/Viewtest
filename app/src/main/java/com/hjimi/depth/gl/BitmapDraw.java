package com.hjimi.depth.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


import com.hjimi.depth.R;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/12/18.
 */

public class BitmapDraw extends ObjectDraw{
    private Bitmap bitmap;

    public BitmapDraw(Context context) {
      //  this.bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.floatround);
    }

    @Override
    void ondraw(GL10 gl10) {
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        int TextureID = textureHandle[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureID);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    }

}
