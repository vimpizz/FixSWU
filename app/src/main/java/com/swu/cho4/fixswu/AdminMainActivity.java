package com.swu.cho4.fixswu;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private AdminBoardAdapter mBoardAdapter;

    private TextView hearNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        mListView = findViewById(R.id.lstBoard);

        hearNum = findViewById(R.id.heartNum);
        //TODO 하트 수 표시

        // 최초 데이터 셋팅
        mBoardAdapter = new AdminBoardAdapter(this, mBoardList);
        mListView.setAdapter(mBoardAdapter);
    } // onCreate() 끝

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


}
