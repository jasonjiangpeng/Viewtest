package com.hjimi.depth;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/12/19.
 */

public class ToolsList {
private  int[]  value=new int[6];

    public ToolsList() {
    }
    public void setValue(int x,int y){
        if (x<120){
            if (y<105){
                value[0]++;
            }else  if (y<210){
                value[1]++;
            }else {
                value[2]++;
            }

        }else {
            if (y<105){
                value[3]++;
            }else  if (y<210){
                value[4]++;
            }else {
                value[5]++;
            }
        }
    }
    public void setTpye(int x,int y){
        if (x<120){
            if (y<105){
             type=0;
            }else  if (y<210){
                type=1;
            }else {
                type=2;
            }

        }else {
            if (y<105){
                type=3;
            }else  if (y<210){
                type=4;
            }else {
                type=5;
            }
        }
    }
    private  int  type=0;
    public  int  getValue(){
        int max=value[0];

        for (int i = 1; i <value.length ; i++) {
            if (value[i]>max){
                max=value[i];
                type=i;
            }
        }

        return max;
    }
    public int  getTpye(){
        type++;
        return type;
    }

}
