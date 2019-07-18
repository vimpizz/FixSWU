package com.swu.cho4.fixswu.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.swu.cho4.fixswu.DownloadImgTask;
import com.swu.cho4.fixswu.PopupActivity;
import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.bean.BoardBean;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class DetailBoardActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com";
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private BoardBean mBoardBean;

    private ImageView mImgProfile, mImgLlike;
    private TextView mTxtStuNum, mTxtName,mTxtHouse,mTxtRoomNum,mTxtDeskNum,mTxtDate,mTxtCondition,mTxtContent,mTxtComment;

    private String strImgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board);

        mImgLlike = findViewById(R.id.imgLike);
        mImgProfile = findViewById(R.id.imgWriteDetail);
        mTxtStuNum = findViewById(R.id.txtStuNumDetail);
        mTxtName = findViewById(R.id.txtNameDetail);
        mTxtHouse= findViewById(R.id.txtHouseDetail);
        mTxtContent = findViewById(R.id.txtContentDetail);
        mTxtDate = findViewById(R.id.txtDateDetail);
        mTxtCondition = findViewById(R.id.txtConditionDetail);
        mTxtComment = findViewById(R.id.txtCommentDetail);


        //이미지 클릭시 팝업창으로 이미지 띄움
        mImgProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BoardBean boardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());
                strImgUri = boardBean.imgUri;

                Intent i = new Intent(DetailBoardActivity.this, PopupActivity.class);
                i.putExtra("imgUri", strImgUri);
                startActivity(i);
            }
        });


        findViewById(R.id.btnCancelDetail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBoardBean.intCondition == 0) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailBoardActivity.this);
                    builder.setTitle("알림");
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("아니오", null);
                    builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String uuid = DetailBoardActivity.getUseridFromUUID(email);

                            //DB에서 삭제처리
                            FirebaseDatabase.getInstance().getReference().child("board").child(uuid).child(mBoardBean.id).removeValue();

                            if (mBoardBean.imgName != null) {
                                //storage에서 삭제처리
                                if (mBoardBean.imgName != null) {
                                    try {
                                        FirebaseStorage.getInstance().getReference().child("Image").child(mBoardBean.imgName).delete();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Toast.makeText(DetailBoardActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    builder.create().show();

                } else {

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


                }
            }
        });

        findViewById(R.id.btnModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBoardBean.intCondition == 0) {
                    Intent i = new Intent(getApplicationContext(), ModifyWriteActivity.class);
                    i.putExtra(BoardBean.class.getName(), mBoardBean);
                    startActivity(i);
                } else {
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


                }
            }
        });

        findViewById(R.id.btnLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBoardBean.comment == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailBoardActivity.this);
                    builder.setTitle("알림창");
                    builder.setMessage("기사님께서 코멘트를 남기시지 않으셨습니다.");
                    builder.setNegativeButton("확인",null);
                    builder.create().show();
                }

                if(mBoardBean.comment != null && mBoardBean.like==false)
                    mBoardBean.like = true;
                    setmImgLlike();
                    uploadLike();
            }
        });
    }

    private void setmImgLlike() {
        if(mBoardBean.like==true)
            mImgLlike.setImageResource(R.drawable.heart_on);
        else if (mBoardBean.like==false)
            mImgLlike.setImageResource(R.drawable.heart);
    }

    private void uploadLike() {
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        String uuid = getUseridFromUUID(mBoardBean.userId);
        dbRef.child("board").child(uuid).child(mBoardBean.id).child("like").setValue(mBoardBean.like);
        //dbRef.child("board").child(uuid).child(mBoardBean.id).setValue(mBoardBean);
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

                        if (mBoardBean != null) {
                            try {
                                new DownloadImgTask(DetailBoardActivity.this, mImgProfile, null, 0).execute(new URL(mBoardBean.imgUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            setmImgLlike();
                            mTxtStuNum.setText(mBoardBean.stuNum);
                            mTxtName.setText(mBoardBean.name);
                            mTxtCondition.setText(mBoardBean.condition);
                            mTxtHouse.setText(mBoardBean.house+" "+mBoardBean.roomNum+"호  "+mBoardBean.deskNum);
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
