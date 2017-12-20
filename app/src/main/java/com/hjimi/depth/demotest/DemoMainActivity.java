package com.hjimi.depth.demotest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjimi.api.iminect.ImiDevice;
import com.hjimi.api.iminect.ImiDeviceAttribute;
import com.hjimi.api.iminect.ImiDeviceState;
import com.hjimi.api.iminect.ImiFrameMode;
import com.hjimi.api.iminect.ImiFrameType;
import com.hjimi.api.iminect.ImiNect;
import com.hjimi.api.iminect.ImiPixelFormat;
import com.hjimi.api.iminect.ImiStream;
import com.hjimi.depth.R;
import com.hjimi.depth.sound.Soundplay;

/**
 * Created by Administrator on 2017/12/20.
 */

public class DemoMainActivity extends Activity {
 private    ImiNect imiNect;
 private    ImiDevice mDevice;
    private RunTheard  runthead;
    private static final int DEVICE_OPEN_SUCCESS = 0;
    private static final int DEVICE_OPEN_FALIED = 1;
    private static final int DEVICE_DISCONNECT = 2;
    private Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case DEVICE_OPEN_FALIED:
                case DEVICE_DISCONNECT:
                    showMessageDialog();
                    break;
                case DEVICE_OPEN_SUCCESS:
                   runViewer();
                    break;
            }
        }

    };
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           int value=msg.what;
            if (bitmap!=null){
                bitmap.recycle();
            }
            if (value==0){
                soundplay.stop();
            }else {
                soundplay.play(value-1);
            }
            img.setBackgroundResource(image[value]);
        }
    };
    Bitmap bitmap;
    ImiStream imiStream;
    private void runViewer() {
        imiStream=ImiStream.create(mDevice,ImiFrameType.DEPTH);
        runthead=new RunTheard(imiStream,handler);
        runthead.restart();
    }
    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("The device is not connected!!!");
        builder.setPositiveButton("quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        runthead.ondestory();
        soundplay.destory();
        ImiNect.destroy();
        super.onDestroy();
    }
private ImageView img;
    private ImiDeviceState deviceState;
    private int[]  image={R.drawable.fruit,R.drawable.apple,R.drawable.banana,R.drawable.fig,R.drawable.monkey,R.drawable.orange,R.drawable.peach};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demomain);
        img= (ImageView) findViewById(R.id.img);
        init();
        new Thread(){
            @Override
            public void run() {
                imiNect=ImiNect.getInstance(DemoMainActivity.this);
                imiNect.addDeviceStateChangedListener(new ImiNect.DeviceStateChangedListener() {
                    @Override
                    public void onDeviceStateChanged(String s, int i) {
                        System.out.println("onDeviceStateChanged"+i);
                        if (i == ImiDeviceState.IMI_DEVICE_STATE_DISCONNECT.toNative()) {
                            //device disconnect.
                              deviceState = ImiDeviceState.IMI_DEVICE_STATE_DISCONNECT;
                            //     MainHandler.sendEmptyMessage(DEVICE_DISCONNECT);
                        }else if(i == ImiDeviceState.IMI_DEVICE_STATE_CONNECT.toNative()){
                             deviceState = ImiDeviceState.IMI_DEVICE_STATE_CONNECT;
                        }
                    }
                });
                ImiDevice.open(new ImiDevice.OpenDeviceListener() {
                    @Override
                    public void onSuccess(ImiDevice imiDevice, ImiDeviceAttribute imiDeviceAttribute) {
                        System.out.println("onSuccess");
                        mDevice=imiDevice;
                        ImiFrameMode frameMode = new ImiFrameMode(ImiPixelFormat.IMI_PIXEL_FORMAT_DEP_16BIT, 320, 240);
                        mDevice.setFrameMode(ImiFrameType.DEPTH, frameMode);
                        mainHandler.sendEmptyMessage(DEVICE_OPEN_SUCCESS);
                    }

                    @Override
                    public void onFalied(String s) {
                        System.out.println("onFalied");
                        Toast.makeText(DemoMainActivity.this, "打开失败，请检查设备是否连接正常?", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();

    }
private Soundplay soundplay;
    private void init() {
        soundplay=new Soundplay();
    }

}
