package com.swu.cho4.fixswu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swu.cho4.fixswu.admin.AdminMainActivity;
import com.swu.cho4.fixswu.bean.AdminBean;
import com.swu.cho4.fixswu.bean.BoardBean;
import com.swu.cho4.fixswu.user.DetailBoardActivity;
import com.swu.cho4.fixswu.user.MainActivity;
import com.swu.cho4.fixswu.user.ModifyWriteActivity;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    //구글 로그인 클라이언트 제어자
    private GoogleSignInClient mGoogleSignInClient;
    //FireBase 인증객체
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private long backPressedAt;
    private int btnNum = -1;
    private long btnPressTime = 0;
    private boolean mAdmin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(mClicks);
        findViewById(R.id.btnGoogleSignInAdmin).setOnClickListener(mClicks);
        findViewById(R.id.appTitle).setOnClickListener(mClicks);

        //구글 로그인 객체선언
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //누른 횟수 초기화
        btnPressTime=0;

        Intent intent =getIntent();
        int i = intent.getIntExtra("logout",0);
        if(i==1){
            //mFirebaseAuth.getCurrentUser() = null;
            mGoogleSignInClient.signOut();
            return;
        }

        if (mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getEmail() != null) {
            //이미 로그인 되어 있다. 따라서 메인화면으로 바로 이동한다.
            long val = UUID.nameUUIDFromBytes(mFirebaseAuth.getCurrentUser().getEmail().getBytes()).getMostSignificantBits();
            String uuid = String.valueOf(val);

            FirebaseDatabase.getInstance().getReference().child("admin").child(uuid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            AdminBean adminBean = dataSnapshot.getValue(AdminBean.class);
                            mAdmin=adminBean.admin;
                            if(mAdmin) {
                                goAdminMainActivity();
                            }else
                            {
                                goMainActivity();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    }
            );




        }
    }

    //게시판 메인 화면으로 이동한다.
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

     private void goAdminMainActivity(){
        Intent i = new Intent(this, AdminMainActivity.class);
        startActivity(i);
        finish();
    }

    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnGoogleSignIn:
                    btnNum=1;
                    googleSignIn();
                    break;
                case R.id.btnGoogleSignInAdmin:
                    btnNum=2;
                    googleSignIn();
                    break;
                case R.id.appTitle:
                    btnPressTime++;
                    if(btnPressTime>=5){
                        findViewById(R.id.btnGoogleSignInAdmin).setVisibility(view.getVisibility());
                        findViewById(R.id.btnGoogleSignIn).setVisibility(view.GONE);
                    }
                    break;
            }
        }
    };

    //구글 로그인 처리
    private void googleSignIn(){
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, 1004);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //FireBase 인증
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //FireBase 로그인 성공

                            //메인화면으로 이동한다.
                                Toast.makeText(getBaseContext(), "Loading...", Toast.LENGTH_SHORT).show();
                                goMainActivity();

                        } else {
                            //로그인 실패
                            Toast.makeText(getBaseContext(), "Firebase 로그인 실패", Toast.LENGTH_LONG).show();
                            Log.w("TEST", "인증실패: " + task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //구글 로그인 버튼 응답
        if (requestCode == 1004) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //구글 로그인 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Toast.makeText(getBaseContext(), "구글 로그인에 성공 하였습니다.", Toast.LENGTH_LONG).show();

                //FireBase 인증하러 가기
                if(btnNum==1)
                    firebaseAuthWithGoogle(account);
                else if(btnNum==2)
                    firebaseAuthWithGoogleAdmin(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }/*else if(requestCode==3000&& requestCode==RESULT_OK){
            mGoogleSignInClient.signOut();
        }*/

    }//end



    private void firebaseAuthWithGoogleAdmin(GoogleSignInAccount account) {
        //FireBase 인증
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //FireBase 로그인 성공

                            //Firebase 데이터베이스에 관리자를 등록한다
                            DatabaseReference dbRef = mFirebaseDatabase.getReference();
                            //데이터베이스에 저장한다.
                            AdminBean adminBean = new AdminBean();
                            adminBean.userId=mFirebaseAuth.getCurrentUser().getEmail();
                            adminBean.admin = true;
                            //고유번호를 생성한다
                            String guid = getUseridFromUUID(adminBean.userId);
                            dbRef.child("admin").child(guid).setValue(adminBean);


                            //메인화면으로 이동한다.
                            Toast.makeText(getBaseContext(), "관리자 목록으로 이동합니다",Toast.LENGTH_SHORT).show();
                            goAdminMainActivity();
                        } else {
                            //로그인 실패
                            Toast.makeText(getBaseContext(), "Firebase 로그인 실패", Toast.LENGTH_LONG).show();
                            Log.w("TEST", "인증실패: " + task.getException());
                        }
                    }
                });
    }

    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    @Override
    public void onBackPressed() {
        if (backPressedAt + TimeUnit.SECONDS.toMillis(2) > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        }
        else {
            if(this instanceof LoginActivity) {
                Toast.makeText(this, "한번 더 뒤로가기 클릭시 앱을 종료 합니다.", Toast.LENGTH_LONG).show();
                backPressedAt = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }
}
