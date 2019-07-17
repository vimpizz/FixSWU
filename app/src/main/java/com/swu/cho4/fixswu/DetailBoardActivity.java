package com.swu.cho4.fixswu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.UUID;

public class DetailBoardActivity extends AppCompatActivity {

    private BoardBean mBoardBean;

    private ImageView mImgProfile, mImgLlike;
    private TextView mTxtStuNum, mTxtName,mTxtHouse,mTxtRoomNum,mTxtDeskNum,mTxtDate,mTxtContent,mTxtComment;
    private String house;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board);

        mImgLlike = findViewById(R.id.imgLike);
        mImgProfile = findViewById(R.id.imgWriteDetail);
        mTxtStuNum = findViewById(R.id.txtStuNumDetail);
        mTxtName = findViewById(R.id.txtNameDetail);
        mTxtHouse= findViewById(R.id.txtHouseDetail);
        mTxtRoomNum = findViewById(R.id.txtRoomNumDetail);
        mTxtDeskNum = findViewById(R.id.txtDeskNumDetail);
        mTxtContent = findViewById(R.id.txtContentDetail);
        mTxtDate = findViewById(R.id.txtDateDetail);
        mTxtComment = findViewById(R.id.txtCommentDetail);


        findViewById(R.id.btnCancelDetail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.equals(mBoardBean.condition, getString(R.string.condition1))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailBoardActivity.this);
                    builder.setTitle("알림창");
                    builder.setMessage("게시글 확인 후에는 삭제가 불가능합니다.");
                    builder.setNegativeButton("뒤로가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    builder.setPositiveButton("", null);
                    builder.create().show();

                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailBoardActivity.this);
                    builder.setTitle("알림");
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setNegativeButton("아니오", null);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String uuid = DetailBoardActivity.getUseridFromUUID(email);

                            //DB에서 삭제처리
                            FirebaseDatabase.getInstance().getReference().child("board").child(uuid).child(mBoardBean.id).removeValue();
                            //storage에서 삭제처리
                            if (mBoardBean.imgName != null) {
                                try {
                                    FirebaseStorage.getInstance().getReference().child("Image").child(mBoardBean.imgName).delete();
                                    Toast.makeText(DetailBoardActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    builder.create().show();
                }
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

        findViewById(R.id.btnLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImgLlike.setImageResource(R.drawable.heart_on);
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();

        BoardBean boardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());
        String uuid = getUseridFromUUID(boardBean.userId);

        FirebaseDatabase.getInstance().getReference().child("board").child(uuid).child(boardBean.id).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mBoardBean = dataSnapshot.getValue(BoardBean.class);

                        if(mBoardBean!=null) {
                            try {
                                new DownloadImgTask(DetailBoardActivity.this, mImgProfile, null, 0).execute(new URL(mBoardBean.imgUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mTxtStuNum.setText(mBoardBean.stuNum);
                            mTxtName.setText(mBoardBean.name);
                            if(mBoardBean.house == 0 ) {
                                house = "샬롬하우스 A동";
                            } else if(mBoardBean.house == 1) {
                                house = "샬롬하우스 B동";
                            } else if(mBoardBean.house == 2) {
                                house ="국제생활관";
                            } else if(mBoardBean.house == 3 ) {
                                house = "바롬관 10층";
                            }
                            mTxtHouse.setText(house);
                            mTxtRoomNum.setText(mBoardBean.roomNum);
                            mTxtDeskNum.setText(mBoardBean.deskNum);
                            mTxtDate.setText(mBoardBean.date);
                            mTxtContent.setText(mBoardBean.content);
                            mTxtComment.setText(mBoardBean.comment);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                }
        );

    }


    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }


}
