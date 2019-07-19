package com.swu.cho4.fixswu;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.swu.cho4.fixswu.R;

public class Pop extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop);

        // 초기세팅
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 너비와 높이
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // 크기 설정
        getWindow().setLayout((int) (width*0.9), (int)(height*0.85));
    }
}
