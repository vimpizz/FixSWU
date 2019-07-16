package com.swu.cho4.fixswu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DetailBoardActivity extends AppCompatActivity {

    private TextView mTxtStuNum, mTxtName,mHouse,mTxtRoomNum,mTxtDeskNum,mTxtContent,mTxtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board);



    }
}
