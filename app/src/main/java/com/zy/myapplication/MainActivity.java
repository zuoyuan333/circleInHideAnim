package com.zy.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{

    private CircleView mCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCv = (CircleView) findViewById(R.id.cv_main_anim);
        Button     btn_start= (Button) findViewById(R.id.bt_main_btn1);
        Button     btn_end= (Button) findViewById(R.id.bt_main_btn2);
        btn_start.setOnClickListener(this);
        btn_end.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main_btn1:
                //开始
                mCv.setCircleColor(0x3F00a73c);
                mCv.setCircleColor(0x99ffffff);
                mCv.setCircleSize(10);
                mCv.setHideRegionSize(30);
                mCv.readyViewDraw();
                mCv.startAnim();
                 break;
            case R.id.bt_main_btn2:
                //停止
                mCv.stopAnim();
                break;
            default:
                 break;
        }
    }
}
