package com.hjimi.depth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.hjimi.api.iminect.ImiDevice;
import com.hjimi.api.iminect.ImiDeviceAttribute;
import com.hjimi.api.iminect.ImiDeviceState;
import com.hjimi.api.iminect.ImiFrameMode;
import com.hjimi.api.iminect.ImiFrameType;
import com.hjimi.api.iminect.ImiNect;
import com.hjimi.api.iminect.ImiPixelFormat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private GLPanel mGLPanel;

    private ImiNect mImiNect;
    private ImiDevice mDevice;
    private SimpleViewer mViewer;
    private ImiFrameMode mCurrDepthMode;

    private static final int DEVICE_OPEN_SUCCESS = 0;
    private static final int DEVICE_OPEN_FALIED = 1;
    private static final int DEVICE_DISCONNECT = 2;

    private ImiDeviceState deviceState = ImiDeviceState.IMI_DEVICE_STATE_CONNECT;

    private Handler MainHandler = new Handler(){
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

    private void runViewer() {
        //create viewer
        mViewer = new SimpleViewer(mDevice, ImiFrameType.DEPTH,handler);

        mViewer.setGLPanel(mGLPanel);

        //start viewer
        mViewer.onStart();
    }

    private class MainListener implements
            ImiDevice.OpenDeviceListener,ImiNect.DeviceStateChangedListener{

        @Override
        public void onSuccess(ImiDevice imiDevice, ImiDeviceAttribute imiDeviceAttribute) {
            //open device success.
            mDevice = imiDevice;

            //get current depth frame mode.
            mCurrDepthMode = mDevice.getCurrentFrameMode(ImiFrameType.DEPTH);

            try {
                setDepthExpectMode(320, 240);
            }catch (Exception e){
                Log.e(TAG, "setDepthExpectMode: falied, invalid frame mode");
            }

            MainHandler.sendEmptyMessage(DEVICE_OPEN_SUCCESS);
        }

        @Override
        public void onFalied(String s) {
            //open device falied.
            MainHandler.sendEmptyMessage(DEVICE_OPEN_FALIED);
        }

        @Override
        public void onDeviceStateChanged(String s, int i) {
            if (i == ImiDeviceState.IMI_DEVICE_STATE_DISCONNECT.toNative()) {
                //device disconnect.
                deviceState = ImiDeviceState.IMI_DEVICE_STATE_DISCONNECT;
                MainHandler.sendEmptyMessage(DEVICE_DISCONNECT);
            }else if(i == ImiDeviceState.IMI_DEVICE_STATE_CONNECT.toNative()){
                deviceState = ImiDeviceState.IMI_DEVICE_STATE_CONNECT;
            }
        }
    }

    private void setDepthExpectMode(int width, int height) {
        ImiFrameMode frameMode = new ImiFrameMode(ImiPixelFormat.IMI_PIXEL_FORMAT_DEP_16BIT, width, height);

        mDevice.setFrameMode(ImiFrameType.DEPTH, frameMode);
    }

    private class OpenDeviceRunnable implements Runnable{

        @Override
        public void run() {
            //get iminect instance.
            mImiNect = ImiNect.getInstance(MainActivity.this);

            MainListener listener = new MainListener();

            //add device state change listener.
            mImiNect.addDeviceStateChangedListener(listener);

            //open device.
            ImiDevice.open(listener);
        }
    }
private TextView textView;
    private Handler  handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int value=msg.arg1;
            int tpte=msg.arg2;
            textView.setText("变化区域"+tpte+"------变化值"+value);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depth_view);
        mGLPanel = (GLPanel) findViewById(R.id.sv_depth_view);
        textView = (TextView) findViewById(R.id.textvalue);
        new Thread(new OpenDeviceRunnable()).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mViewer != null){
            mViewer.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mViewer != null){
            mViewer.onPause();
        }

        if(mViewer != null){
            mViewer.onDestroy();
        }
        
        if(mDevice != null){
            mDevice.close();
        }

        ImiNect.destroy();

        finish();

        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
