package com.swu.cho4.fixswu.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.swu.cho4.fixswu.DownloadImgTask;
import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.bean.BoardBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminWriteActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com/";

    private BoardBean mBoardBean;

    private ImageView mImgProfile;
    private TextView mTxtApplyNum,mTxtStuNum, mTxtName,mTxtHouse,mTxtContent,mTxtDate;
    private EditText mEdtComment;
    private Spinner mSpinner;
    private int mIntCondition; //보드 상태

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private List<BoardBean> mBoardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_write);

        mImgProfile = findViewById(R.id.imgWriteAdmin);
       // mTxtStuNum = findViewById(R.id.edtStuNum);
        mTxtName = findViewById(R.id.txtName);
        mTxtHouse = findViewById(R.id.txtHouse);
        mTxtContent = findViewById(R.id.txtContent);
        mSpinner=findViewById(R.id.spinnerCondition);
        mTxtDate = findViewById(R.id.txtDate);
        mEdtComment=findViewById(R.id.edtComment);

        mBoardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());
        String uuid = getUseridFromUUID(mBoardBean.userId);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mIntCondition=i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        mSpinner.setSelection(mBoardBean.intCondition);

        FirebaseDatabase.getInstance().getReference().child("board").child(uuid).child(mBoardBean.id).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mBoardBean = dataSnapshot.getValue(BoardBean.class);

                        if(mBoardBean!=null) {
                            try {
                                new DownloadImgTask(AdminWriteActivity.this, mImgProfile, null, 0).execute(new URL(mBoardBean.imgUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mTxtName.setText(mBoardBean.name);
                            mTxtHouse.setText(mBoardBean.house+" "+mBoardBean.roomNum+"호  "+mBoardBean.deskNum);
                            mTxtDate.setText(mBoardBean.date);
                            mTxtContent.setText(mBoardBean.content);
                            mEdtComment.setText(mBoardBean.comment);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                }
        );



        findViewById(R.id.btnCancelAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnSaveAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

    }


    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    // 게시물 수정
    private void update(){
        if(
                (mBoardBean.intCondition==1 && mIntCondition==2) |
                        (mBoardBean.intCondition==2 && mIntCondition==3) |
                        (mBoardBean.intCondition==1 && mIntCondition==3)
        ) {
            mBoardBean.intCondition=mIntCondition;
            mBoardBean.condition = mBoardBean.intToCondition();

        }else{
            Toast.makeText(this,"상태를 되돌리는 것은 불가능합니다",Toast.LENGTH_SHORT).show();
            return;
        }
        mBoardBean.comment=mEdtComment.getText().toString();

        //DB 업로드
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        String uuid = getUseridFromUUID(mBoardBean.userId);
        dbRef.child("board").child(uuid).child(mBoardBean.id).setValue(mBoardBean);

/*        //DB 업로드
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        String uuid = getUseridFromUUID(mBoardBean.userId);

        // 동일 ID로 데이터 수정
        dbRef.child("board").child(uuid).child(mBoardBean.id).child("comment").setValue(mBoardBean.comment);
        dbRef.child("board").child(uuid).child(mBoardBean.id).child("intCondition").setValue(mBoardBean.intCondition);*/

        Toast.makeText(this,"저장되었습니다",Toast.LENGTH_SHORT).show();
        finish();
        return;
    }
}