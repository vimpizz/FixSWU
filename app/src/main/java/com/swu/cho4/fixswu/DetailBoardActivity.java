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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;

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

        findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailBoardActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제하시겠습니까?");
                builder.setNegativeButton("아니오",null);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String uuid = DetailBoardActivity.getUseridFromUUID(email);


                        //DB에서 삭제처리
                        FirebaseDatabase.getInstance().getReference().child("board").child(uuid).child(mBoardBean.id).removeValue();
                        //storage에서 삭제처리
                        if(mBoardBean.imgName!=null){
                            try {
                                FirebaseStorage.getInstance().getReference().child("Image").child(mBoardBean.imgName).delete();
                                Toast.makeText(DetailBoardActivity.this,"삭제되었습니다",Toast.LENGTH_SHORT).show();
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                builder.create().show();
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

    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

}
