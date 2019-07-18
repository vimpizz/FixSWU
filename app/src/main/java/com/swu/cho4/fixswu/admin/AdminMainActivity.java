package com.swu.cho4.fixswu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swu.cho4.fixswu.bean.BoardBean;
import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.user.LoginActivity;
import com.swu.cho4.fixswu.user.MainActivity;
import com.swu.cho4.fixswu.user.UserInfoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdminMainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private AdminBoardAdapter mBoardAdapter;
    private long backPressedAt;

    private TextView hearNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        mListView = findViewById(R.id.lstBoard);

        findViewById(R.id.btnUserInfoAdmin).setOnClickListener(mBtnClick);

        hearNum = findViewById(R.id.heartNum);
        //TODO 하트 수 표시

        // 최초 데이터 셋팅
        mBoardAdapter = new AdminBoardAdapter(this, mBoardList);
        mListView.setAdapter(mBoardAdapter);
    } // onCreate() 끝

    private View.OnClickListener mBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btnUserInfoAdmin:
                    Intent i = new Intent(getApplication(), UserInfoActivity.class);
                    i.putExtra("userEmail", mFirebaseAuth.getCurrentUser().getEmail());
                    startActivityForResult(i, 2000);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        List<BoardBean> newBoardList = new ArrayList<>();

        // 전체 회원의 데이터 취득
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference usersRef = rootRef.child("board");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countHeartNum=0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot ds2 : ds.getChildren()) {
                        BoardBean bean = ds2.getValue(BoardBean.class);
                        if(bean.like == true) {
                            countHeartNum ++;
                        }
                        newBoardList.add(bean);
                    }
                }

                hearNum.setText(String.valueOf(countHeartNum));
                //리스트 갱신

                mBoardAdapter.setBoardList(newBoardList);
                mBoardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedAt + TimeUnit.SECONDS.toMillis(2) > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        }
        else {
            if(this instanceof AdminMainActivity) {
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
            Intent i = new Intent(AdminMainActivity.this, LoginActivity.class);
            startActivity(i);
            finish(); //로그아웃
        }
    }


}
