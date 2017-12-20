package com.hjimi.depth.demotest;

import android.os.Handler;
import android.os.SystemClock;

import com.hjimi.api.iminect.ImiImageFrame;
import com.hjimi.api.iminect.ImiStream;
import com.hjimi.depth.ToolsList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by Administrator on 2017/12/20.
 */

public class RunTheard extends Thread {

  private   boolean isRun=true;
  private   boolean isStop=false;
    private   short[][] original=null;
    private Handler handler;
    private ImiStream mStream;
    public RunTheard(ImiStream  ImiStream, Handler handler) {
        this.handler=handler;
        this.mStream=ImiStream;
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
    @Override
    public void run() {
        int  w=320;
        int  h=240;
           while (isRun){
               ImiImageFrame nextFrame = mStream.readNextFrame(25);
               if(nextFrame == null){
                   continue;
               }
             long start=System.currentTimeMillis();
               if (isStop){
                   ByteBuffer data = nextFrame.getData().order(ByteOrder.nativeOrder());
                   short[]  comdata=new short[w*h];
                   data.asShortBuffer().get(comdata);
                   original=trans2short(comdata);
                   int type = comparedata(original);
                   handler.obtainMessage(type).sendToTarget();
               }
               long end=System.currentTimeMillis()-start;
               if (end<triggleTime){
                   long  time=triggleTime-end;
                   SystemClock.sleep(time);
               }
               System.out.println("计算时间"+end);
           }
    }
    private int  triggleTime=100;
    public int comparedata(short[][] data2){
        TypeHelp  tools=new TypeHelp();
        short  mindata=0;
        int a=0;
        int b=0;
        for (int i = 0; i <240 ; i++) {
            for (int j = 0; j <320 ; j++) {
                if (0!=data2[i][j]&&data2[i][j]<1350){
                    if (data2[i][j]>1150) {
                        if (data2[i][j]>mindata){
                            mindata=data2[i][j];
                            a=i;
                            b=j;
                        }
                    }

                 //   tools.setValue(i,j);
                }
            }
        }
        if (a==0&&b==0){
            return tools.getType();
        }
        tools.setTpye2(a,b);
        return tools.getType();
    }
   public void ondestory(){
       isRun=false;
   }
   public void restart(){
       System.out.println("restart");
         isRun =true;
         isStop=true;
           start();
   }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
