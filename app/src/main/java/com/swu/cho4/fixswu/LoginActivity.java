package com.swu.cho4.fixswu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class LoginActivity extends AppCompatActivity {

    //구글 로그인 클라이언트 제어자
    private GoogleSignInClient mGoogleSignInClient;
    //FireBase 인증객체
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mFirebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(mClicks);

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

        if (mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getEmail() != null) {
            //이미 로그인 되어 있다. 따라서 메인화면으로 바로 이동한다.
            //Toast.makeText(this, "로그인 성공 - 메인화면 이동", Toast.LENGTH_LONG).show();
            if(mFirebaseAuth.getCurrentUser().getEmail().equals("flzl03077@gmail.com")) {
                Toast.makeText(getBaseContext(), "AdminMain"
                        ,Toast.LENGTH_SHORT).show();
                goAdminMainActivity();
            } else {
                Toast.makeText(getBaseContext(), "Loading..."
                        , Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
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
                    googleSignIn();
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
                            //Toast.makeText(getBaseContext(), "Firebase 로그인 성공", Toast.LENGTH_LONG).show();
                            //메인화면으로 이동한다.
                            if(mFirebaseAuth.getCurrentUser().getEmail().equals("flzl03077@gmail.com")) {
                                Toast.makeText(getBaseContext(), "AdminMain"
                                        ,Toast.LENGTH_SHORT).show();
                                goAdminMainActivity();
                            } else {
                                Toast.makeText(getBaseContext(), "Loading...", Toast.LENGTH_SHORT).show();
                                goMainActivity();
                            }
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
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }//end
}
