package com.sinodom.elevator.quickalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;

public class MainActivity extends AppCompatActivity {

    private HomeKeyListener homeKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeKeyListener = new HomeKeyListener(this);
        homeKeyStart(); //处理方法

        homeKeyListener.startHomeListener(); //开启监听
    }

    /**
     * Home键开始监听
     */
    private void homeKeyStart(){

        homeKeyListener.setOnHomePressedListener(new HomeKeyListener.OnHomePressedListener() {

            @Override
            public void onHomePressed() {

                // 这里获取到home键按下事件
                LogUtils.i("lock", "onHomePressed ========================================");

            }

            @Override
            public void onHomeLongPressed() {

                LogUtils.i("lock", "onHomeLongPressed ========================================");

            }



            @Override
            public void onScreenPressed() {
                // TODO Auto-generated method stub

            }

            @Override
            public void offScreenPressed() {
                // TODO Auto-generated method stub
            }
        });
    }
}
