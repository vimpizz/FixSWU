package com.swu.cho4.fixswu;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;

public class AdminWriteActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com/";

    private BoardBean mBoardBean;

    private ImageView mImgProfile;
    private TextView mTxtApplyNum,mTxtStuNum, mTxtName,mTxtRoomNum,mTxtDeskNum,mTxtContent,mTxtDate;
    private EditText mEdtComment;
    private Spinner mSpinner;
    private int mIntCondition; //보드 상태

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_write);

        mImgProfile = findViewById(R.id.imgWriteAdmin);
        mTxtApplyNum = findViewById(R.id.txtApplyNum);
       // mTxtStuNum = findViewById(R.id.edtStuNum);
        mTxtName = findViewById(R.id.txtName);
        mTxtRoomNum = findViewById(R.id.txtRoomNum);
        mTxtDeskNum = findViewById(R.id.txtDeskNum);
        mTxtContent = findViewById(R.id.txtContent);
        mSpinner=findViewById(R.id.spinnerCondition);
        mTxtDate = findViewById(R.id.txtDate);
        mEdtComment=findViewById(R.id.edtComment);



        mBoardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());

        mSpinner.setSelection(mBoardBean.intCondition);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mIntCondition=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


        if(mBoardBean != null){
            mBoardBean.bmpTitle = getIntent().getParcelableExtra("titleBitmap");
            if(mBoardBean.bmpTitle != null){
                mImgProfile.setImageBitmap(mBoardBean.bmpTitle);
            }
            mTxtApplyNum.setText(mBoardBean.ApplyNum);
            mTxtName.setText(mBoardBean.name);
            mTxtRoomNum.setText(mBoardBean.roomNum);
            mTxtDeskNum.setText(mBoardBean.deskNum);
            mTxtDate.setText(mBoardBean.date);
            mTxtContent.setText(mBoardBean.content);
            mEdtComment.setText(mBoardBean.comment);
        }

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

    private void update(){
        mBoardBean.intCondition=mIntCondition;
        mBoardBean.intToCondition();
        mBoardBean.comment=mEdtComment.getText().toString();

            //DB 업로드
            DatabaseReference dbRef = mFirebaseDatabase.getReference();
            String uuid = getUseridFromUUID(mBoardBean.userId);
            dbRef.child("board").child(uuid).child(mBoardBean.id).setValue(mBoardBean);
            Toast.makeText(this,"저장되었습니다",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


}