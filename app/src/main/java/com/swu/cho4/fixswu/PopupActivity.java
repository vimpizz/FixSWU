package com.swu.cho4.fixswu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;

public class PopupActivity extends Activity implements View.OnClickListener{

    ImageView mImageView;

    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        // UI 객체 생성
        mImageView = findViewById(R.id.imgPop);

        Intent intent = getIntent();
        String strImgUri = intent.getStringExtra("imgUri");

        try {
            new DownloadImgTask(this, mImageView, null, 0).execute(
                    new URL(strImgUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

    } // end onCreate()

    //바깥 레이어 클릭시 닫힘 안 이루어져서 주석처리함
/*    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 레이어 클릭시 닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finish();
            return true;
        }
        return false;
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {

    }
}
