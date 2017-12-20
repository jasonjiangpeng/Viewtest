package com.hjimi.depth;

import android.app.Activity;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hjimi.depth.sound.Soundplay;

/**
 * Created by Administrator on 2017/12/20.
 */

public class TestActivity extends Activity {
    private Soundplay soundplay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testact);
        soundplay=new Soundplay();
    }
    int  position=0;
    public void testabc(View view){
        if (position>5){
            position=0;
        }
       soundplay.play(position);
        position++;
    }
    public void stopabc(View view){
        soundplay.stop();
    }

    @Override
    protected void onDestroy() {
        soundplay.destory();
        super.onDestroy();

    }
}
