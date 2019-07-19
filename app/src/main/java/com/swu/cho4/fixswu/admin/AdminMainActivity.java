package com.swu.cho4.fixswu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.swu.cho4.fixswu.DownloadImgTask;
import com.swu.cho4.fixswu.LoginActivity;
import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.UserInfoActivity;
import com.swu.cho4.fixswu.bean.AdminBean;
import com.swu.cho4.fixswu.bean.BoardBean;
import com.swu.cho4.fixswu.user.DetailBoardActivity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdminMainActivity extends AppCompatActivity {
    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com";
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);


    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private AdminBoardAdapter mBoardAdapter;
    private long backPressedAt;
    private String adminEmail;

    private TextView hearNum;

    private ArrayAdapter conditionAdapter;
    private Spinner conditionSpinner;

    private String selectedCondition = "";
    private int conditionspinner;

    private ArrayAdapter houseAdapter;
    private Spinner houseSpinner;

    private int housespinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        mListView = findViewById(R.id.lstBoard);

        //매칭
        conditionSpinner = findViewById(R.id.conditionSpinner);

        //어댑터 설정
        ArrayAdapter conditionAdapter = ArrayAdapter.createFromResource(this, R.array.condition, R.layout.support_simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(conditionSpinner.getSelectedItem().equals("전체")) {
                    conditionspinner = 0;
                    onResume();
                } else if(conditionSpinner.getSelectedItem().equals("확인전")) {
                    conditionspinner = 1;
                    onResume();
                }else if(conditionSpinner.getSelectedItem().equals("읽음")) {
                    conditionspinner = 2;
                    onResume();
                }else if(conditionSpinner.getSelectedItem().equals("수리완료")) {
                    conditionspinner = 3;
                    onResume();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        houseSpinner = findViewById(R.id.houseSpinner);
        //어댑터 설정
        ArrayAdapter houseaAdapter = ArrayAdapter.createFromResource(this, R.array.house, R.layout.support_simple_spinner_dropdown_item);
        houseSpinner.setAdapter(houseaAdapter);

        houseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(houseSpinner.getSelectedItem().equals("전체")) {
                    housespinner = 0;
                    onResume();
                } else if(houseSpinner.getSelectedItem().equals("샬롬하우스 A동")) {
                    housespinner = 1;
                    onResume();
                }else if(houseSpinner.getSelectedItem().equals("샬롬하우스 B동")) {
                    housespinner = 2;
                    onResume();
                }else if(houseSpinner.getSelectedItem().equals("국제생활관")) {
                    housespinner = 3;
                    onResume();
                }else if(houseSpinner.getSelectedItem().equals("바롬관 10층")) {
                    housespinner = 4;
                    onResume();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
            });

                findViewById(R.id.btnUserInfoAdmin).setOnClickListener(mBtnClick);
                hearNum = findViewById(R.id.heartNum);
        //TODO 하트 수 표시

        // 최초 데이터 셋팅
        mBoardAdapter = new AdminBoardAdapter(this, mBoardList);
        mListView.setAdapter(mBoardAdapter);

    } // onCreate() 끝

    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }


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
                        //newBoardList.add(bean);

                        if (conditionspinner == 0 && housespinner == 0) {newBoardList.add(bean);}
                        else if (conditionspinner == 0 && housespinner == 1 && bean.intHouse == 0) { newBoardList.add(bean);}
                        else if (conditionspinner == 0 && housespinner == 2 && bean.intHouse == 1) { newBoardList.add(bean);}
                        else if (conditionspinner == 0 && housespinner == 3 && bean.intHouse == 2) { newBoardList.add(bean);}
                        else if (conditionspinner == 0 && housespinner == 4 && bean.intHouse == 3) { newBoardList.add(bean);}
                        else if (conditionspinner == 1 && bean.intCondition == 0 && housespinner == 0) { newBoardList.add(bean);}
                        else if (conditionspinner == 1 && bean.intCondition == 0 && housespinner == 1 && bean.intHouse == 0 ) { newBoardList.add(bean);}
                        else if (conditionspinner == 1 && bean.intCondition == 0 && housespinner == 2 && bean.intHouse == 1) { newBoardList.add(bean);}
                        else if (conditionspinner == 1 && bean.intCondition == 0 && housespinner == 3 && bean.intHouse == 2) { newBoardList.add(bean);}
                        else if (conditionspinner == 1 && bean.intCondition == 0 && housespinner == 4 && bean.intHouse == 3) { newBoardList.add(bean);}
                        else if (conditionspinner == 2 && bean.intCondition == 1 && housespinner == 0) { newBoardList.add(bean);}
                        else if (conditionspinner == 2 && bean.intCondition == 1 && housespinner == 1 && bean.intHouse == 0) { newBoardList.add(bean);}
                        else if (conditionspinner == 2 && bean.intCondition == 1 && housespinner == 2 && bean.intHouse == 1) { newBoardList.add(bean);}
                        else if (conditionspinner == 2 && bean.intCondition == 1 && housespinner == 3 && bean.intHouse == 2) { newBoardList.add(bean);}
                        else if (conditionspinner == 2 && bean.intCondition == 1 && housespinner == 4 && bean.intHouse == 3) { newBoardList.add(bean);}
                        else if (conditionspinner == 3 && bean.intCondition == 2 && housespinner == 0) { newBoardList.add(bean);}
                        else if (conditionspinner == 3 && bean.intCondition == 2 && housespinner == 1 && bean.intHouse == 0) { newBoardList.add(bean);}
                        else if (conditionspinner == 3 && bean.intCondition == 2 && housespinner == 2 && bean.intHouse == 1) { newBoardList.add(bean);}
                        else if (conditionspinner == 3 && bean.intCondition == 2 && housespinner == 3 && bean.intHouse == 2) { newBoardList.add(bean);}
                        else if (conditionspinner == 3 && bean.intCondition == 2 && housespinner == 4 && bean.intHouse == 3) { newBoardList.add(bean);}
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