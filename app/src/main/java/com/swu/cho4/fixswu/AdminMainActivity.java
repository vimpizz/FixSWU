package com.swu.cho4.fixswu;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMainActivity extends AppCompatActivity {

    private TextView hearNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        hearNum = findViewById(R.id.heartNum);
        //TODO 하트 수 표시
        //hearNum.setText();
    }
}
