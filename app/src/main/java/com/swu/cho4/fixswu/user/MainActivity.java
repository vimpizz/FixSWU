package com.swu.cho4.fixswu.user;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swu.cho4.fixswu.LoginActivity;
import com.swu.cho4.fixswu.UserInfoActivity;
import com.swu.cho4.fixswu.bean.BoardBean;
import com.swu.cho4.fixswu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private BoardAdapter mBoardAdapter;
    private long backPressedAt;

    // 데이터 취득
    String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
    String uuid = WriteActivity.getUseridFromUUID(userEmail);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        // 리스트뷰
        mListView = findViewById(R.id.lstBoard);
        // 회원정보 상세보기 버튼, 추가 버튼
        findViewById(R.id.btnUserInfo).setOnClickListener(mBtnClick);
        findViewById(R.id.btnWrite).setOnClickListener(mBtnClick);

        // 최초 데이터 세팅
        mBoardAdapter = new BoardAdapter(this, mBoardList);
        mListView.setAdapter(mBoardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseDB.getReference().child("board").child(uuid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 데이터를 받아와서 List에 저장
                        mBoardList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            BoardBean bean = snapshot.getValue(BoardBean.class);
                            mBoardList.add(0,bean);
                        }
                        // 바뀐 데이터로 새로고침
                        if(mBoardAdapter != null) {
                            mBoardAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                }
        );
        // 어댑터 생성
    }

    private View.OnClickListener mBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btnUserInfo:
                    Intent i = new Intent(getApplication(), UserInfoActivity.class);
                    i.putExtra("userEmail", mFirebaseAuth.getCurrentUser().getEmail());
                    startActivityForResult(i, 2000);
                    break;
                case R.id.btnWrite:
                    Intent i2= new Intent(getApplication(), WriteActivity.class);
                    startActivity(i2);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (backPressedAt + TimeUnit.SECONDS.toMillis(2) > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        }
        else {
            if(this instanceof MainActivity) {
                Toast.makeText(this, "한번 더 뒤로가기 클릭시 앱을 종료 합니다.", Toast.LENGTH_LONG).show();
                backPressedAt = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2000 && resultCode == RESULT_OK) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish(); //로그아웃
        }
    }
}
