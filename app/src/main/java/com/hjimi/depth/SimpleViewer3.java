package com.hjimi.depth;

import android.os.Handler;

import com.hjimi.api.iminect.ImiDevice;
import com.hjimi.api.iminect.ImiFrameMode;
import com.hjimi.api.iminect.ImiFrameType;
import com.hjimi.api.iminect.ImiImageFrame;
import com.hjimi.api.iminect.ImiStream;
import com.hjimi.api.iminect.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class SimpleViewer3 extends Thread {

    private boolean mShouldRun = false;

    private ImiFrameType mFrameType;
    private ImiStream mStream;
    private GLPanel mGLPanel;
    private ImiDevice mDevice;
    private ImiFrameMode mCurrentMode;
    private Handler mhandler;


    public SimpleViewer3(ImiDevice device, ImiFrameType frameType) {
        mDevice = device;
        mFrameType = frameType;
    }
    public SimpleViewer3(ImiDevice device, ImiFrameType frameType, Handler handler) {
        mDevice = device;
        mFrameType = frameType;
        mhandler = handler;
    }

    public void setGLPanel(GLPanel GLPanel) {
        this.mGLPanel = GLPanel;
    }

    @Override
    public void run() {
        super.run();

        //open stream.
        mStream = ImiStream.create(mDevice,mFrameType);

        //get current framemode.
        mCurrentMode = mDevice.getCurrentFrameMode(mFrameType);

        //start read frame.
        while (mShouldRun) {
            ImiImageFrame nextFrame = mStream.readNextFrame(25);
            //frame maybe null, if null, continue.
            if(nextFrame == null){
                continue;
            }
            long  start=System.currentTimeMillis();
            ByteBuffer data = nextFrame.getData().order(ByteOrder.nativeOrder());
            short[]  comdata=new short[nextFrame.getWidth()*nextFrame.getHeight()];
            data.asShortBuffer().get(comdata);
            original=trans2short(comdata);
            System.out.println(original[220][300]);
            System.out.println(original[220][310]);
            System.out.println(original[220][318]);
            System.out.println(original[220][280]);
            ToolsList comparedata = comparedata(original);
            int  c=comparedata.getValue();
            int  d=comparedata.getTpye();
            mhandler.obtainMessage(0,c,d).sendToTarget();


            //   long  end=System.currentTimeMillis()-start;
               drawDepth(nextFrame);
        }
    }
    private   short[][] original=null;
    private  int value =320*240;
    private void drawDepth(ImiImageFrame nextFrame) {
        ByteBuffer frameData = nextFrame.getData();
        int width = nextFrame.getWidth();
        int height = nextFrame.getHeight();
        //get rgb data
        frameData = Utils.depth2RGB888(nextFrame, false, false);
        //draw depth image.
        mGLPanel.paint(null, frameData, width, height);
    }

    public void onPause(){
        if(mGLPanel != null){
            mGLPanel.onPause();
        }
    }

    public void onResume(){
        if(mGLPanel != null){
            mGLPanel.onResume();
        }
    }

    public void onStart(){
        if(!mShouldRun){
            mShouldRun = true;

            //start read thread
            this.start();
        }
    }

    public void onDestroy(){
        mShouldRun = false;

        //destroy stream.
        if(mStream != null){
            mStream.destroy();
        }
    }
    public short[][]  trans2short(short[] data){
        short[][]  sd=new short[240][320];
        int position=0;
        for (int i = 0; i <240 ; i++) {
            for (int j = 0; j <320 ; j++) {
                sd[i][j]=data[position];
                position++;
            }
        }
        return sd;
    }
    public  ToolsList  compareShort(short[][] data1,short[][] data2){
        ToolsList  tools=new ToolsList();

        for (int i = 0; i <240 ; i++) {
            for (int j = 0; j <320 ; j++) {
                if (data1[i][j]!=data2[i][j]){
                      tools.setValue(i,j);
                }
            }
        }
        return tools;
    }
    public  ToolsList  comparedata(short[][] data2){
        ToolsList  tools=new ToolsList();
        for (int i = 0; i <240 ; i++) {
            for (int j = 0; j <320 ; j++) {
                if (0!=data2[i][j]&&data2[i][j]<1400){
                    tools.setValue(i,j);
                }
            }
        }
        return tools;
    }
}
