package com.swu.cho4.fixswu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class DetailBoardActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com";

    private BoardBean mBoardBean;

    public String mPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 200;


    private ImageView mImgProfile;
    private TextView mTxtStuNum, mTxtName,mHouse,mTxtRoomNum,mTxtDeskNum,mTxtContent,mTxtComment;


    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board);

        mImgProfile = findViewById(R.id.imgWriteAdmin);
        mTxtStuNum = findViewById(R.id.txtStuNumDetail);
        mTxtName = findViewById(R.id.txtNameDetail);
        mHouse= findViewById(R.id.txtHouseDetail);
        mTxtRoomNum = findViewById(R.id.txtRoomNumDetail);
        mTxtDeskNum = findViewById(R.id.txtDeskNumDetail);
        mTxtContent = findViewById(R.id.txtContentDetail);
        mTxtComment = findViewById(R.id.txtCommentDetail);


        mBoardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());

        if(mBoardBean!=null) {
            mBoardBean.bmpTitle = getIntent().getParcelableExtra("titleBitmap");
            if (mBoardBean.bmpTitle != null) {
                mImgProfile.setImageBitmap(mBoardBean.bmpTitle);
            }
            mTxtStuNum.setText(mBoardBean.stuNum);
            mTxtName.setText(mBoardBean.name);
            mTxtRoomNum.setText(mBoardBean.roomNum);
            mTxtDeskNum.setText(mBoardBean.deskNum);
            mTxtContent.setText(mBoardBean.content);
        }



        findViewById(R.id.btnCancelDetail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.equals(mBoardBean.condition, getString(R.string.condition1))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailBoardActivity.this);
                    builder.setTitle("알림창");
                    builder.setMessage("게시글 확인 후에는 수정이 불가능합니다.");
                    builder.setNegativeButton("뒤로가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    builder.setPositiveButton("", null);
                    builder.create().show();

                }else{
                    Intent i = new Intent(getApplicationContext(), ModifyWriteActivity.class);
                    i.putExtra(BoardBean.class.getName(), mBoardBean);
                    startActivity(i);
                }
            }
        });

    }
}
