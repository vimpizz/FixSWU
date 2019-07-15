package com.swu.cho4.fixswu;

<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
=======
        >>>>>>> origin/master
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private BoardAdapter mBoardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 리스트뷰
        mListView = findViewById(R.id.lstBoard);
        // 회원정보 상세보기 버튼, 추가 버튼
        findViewById(R.id.btnUserInfo).setOnClickListener(mBtnClick);
        findViewById(R.id.btnWrite).setOnClickListener(mBtnClick);

        // 최초 데이터 세팅
        //mBoardAdapter = new BoardAdapter(this, mBoardList);
        //mListView.setAdapter(mBoardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 데이터 취득
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        String uuid = WriteActivity.getUseridFromUUID(userEmail);
    }

    private View.OnClickListener mBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btnUserInfo:
                    Intent i = new Intent(getApplication(), UserInfoActivity.class);
                    i.putExtra("userEmail", mFirebaseAuth.getCurrentUser().getEmail());
                    startActivity(i);
                    break;
                case R.id.btnWrite:
                    Intent i2= new Intent(getApplication(), WriteActivity.class);
                    startActivity(i2);
                    break;
            }
        }
    };
}
