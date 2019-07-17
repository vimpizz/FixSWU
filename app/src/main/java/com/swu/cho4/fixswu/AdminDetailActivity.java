package com.swu.cho4.fixswu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class AdminDetailActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com/";

    private BoardBean mBoardBean;

    private ImageView mImgProfile;
    private TextView mTxtApplyNum,mTxtHouse,mTxtStuNum, mTxtName,mTxtRoomNum,mTxtDeskNum,mTxtContent,mTxtDate, mTxtComment, mTxtStatus;
    private EditText mEdtComment;
    private Spinner mSpinner;
    private int intCondition=0; //보드 상태

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail);

        mImgProfile = findViewById(R.id.imgWriteAdmin);
        mTxtApplyNum = findViewById(R.id.txtApplyNum);
        mTxtHouse = findViewById(R.id.txtHouse);
        // mTxtStuNum = findViewById(R.id.edtStuNum);
        mTxtName = findViewById(R.id.txtName);
        mTxtRoomNum = findViewById(R.id.txtRoomNum);
        mTxtDeskNum = findViewById(R.id.txtDeskNum);
        mTxtContent = findViewById(R.id.txtContent);
        mSpinner=findViewById(R.id.spinnerCondition);
        mTxtDate = findViewById(R.id.txtDate);
        mTxtComment = findViewById(R.id.txtvComment);
        mTxtStatus = findViewById(R.id.txtvStatus);


        mBoardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());
        if(mBoardBean != null){
            mBoardBean.bmpTitle = getIntent().getParcelableExtra("titleBitmap");
            if(mBoardBean.bmpTitle != null){
                mImgProfile.setImageBitmap(mBoardBean.bmpTitle);
            }
            mTxtApplyNum.setText(mBoardBean.ApplyNum);
            mTxtName.setText(mBoardBean.name);
            mTxtHouse.setText(mBoardBean.house);

            mTxtRoomNum.setText(mBoardBean.roomNum);
            mTxtDeskNum.setText(mBoardBean.deskNum);
            mTxtDate.setText(mBoardBean.date);
            mTxtContent.setText(mBoardBean.content);
            mTxtStatus.setText(mBoardBean.condition);
            mTxtComment.setText(mBoardBean.comment);

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
                Intent i = new Intent(getApplicationContext(), AdminWriteActivity.class);
                startActivity(i);
            }
        });
    }
}
