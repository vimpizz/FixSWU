package com.swu.cho4.fixswu.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.bean.BoardBean;
import com.swu.cho4.fixswu.foregin.ForeignWrite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class WriteActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URI = "gs://fixswu.appspot.com";

    private BoardBean mBoardBean;

    private Uri mCaptureUri;
    private Uri imgUri;
    public String mPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 200;
    private final int GALLERY_CODE=1112;
    private boolean gallery;

    private ImageView mImgProfile;
    private EditText mEdtStuNum, mEdtName,mEdtRoomNum,mEdtDeskNum,mEdtContent;
    private Spinner mSpinner;
    private int mintHouse=0; //기관

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        mImgProfile = findViewById(R.id.imgWrite);
        mEdtStuNum = findViewById(R.id.edtStuNum);
        mEdtName = findViewById(R.id.edtName);
        mSpinner=findViewById(R.id.spinnerHouse);
        mEdtRoomNum = findViewById(R.id.edtRoomNum);
        mEdtDeskNum = findViewById(R.id.edtDeskNum);
        mEdtContent = findViewById(R.id.edtContent);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mintHouse=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        findViewById(R.id.engVersion).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i = new Intent(WriteActivity.this, ForeignWrite.class);
                 startActivityForResult(i, 3333);
             }
         });

        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });


        findViewById(R.id.btnStuSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mEdtStuNum.getText().toString())){
                    Toast.makeText(getApplicationContext(),"학번을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(mEdtName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"이름을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(mEdtRoomNum.getText().toString())){
                    Toast.makeText(getApplicationContext(),"호수를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
               /* else if(TextUtils.isEmpty(mEdtDeskNum.getText().toString())){
                    Toast.makeText(getApplicationContext(),"번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                else if(TextUtils.isEmpty(mEdtContent.getText().toString())){
                    Toast.makeText(getApplicationContext(),"수리내용을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                builder.setTitle("경고");
                builder.setMessage("기사님께서 게시글을 확인하신 후에는 수정이나 삭제가 불가합니다.");
                builder.setNegativeButton("뒤로가기",null);
                builder.setPositiveButton("게시물 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mBoardBean==null){
                            Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_SHORT).show();
                            upload();
                        }
                    }
                });
                builder.create().show();
            }
        });

        findViewById(R.id.btnStuCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                builder.setTitle("알림창");
                builder.setMessage("게시글 작성을 취소하고 뒤로 가시겠습니까?");
                builder.setNegativeButton("아니오",null);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.create().show();
            }
        });
    }


    private void upload(){

        //사진을 안 찍었으면 사진 제외하고 업로드한다
        if(mPhotoPath == null){
            //Firebase 데이터베이스에 메모를 등록한다.
            DatabaseReference dbRef = mFirebaseDatabase.getReference();
            String id = dbRef.push().getKey();

            //데이터베이스에 저장한다.
            BoardBean boardBean = new BoardBean();

            boardBean.id = id;
            boardBean.userId=mFirebaseAuth.getCurrentUser().getEmail();
            boardBean.intCondition = 0;
            boardBean.intToCondition();

            boardBean.stuNum = mEdtStuNum.getText().toString();
            boardBean.name = mEdtName.getText().toString();

            boardBean.intHouse = mintHouse;
            boardBean.intToHouse();

            boardBean.roomNum = mEdtRoomNum.getText().toString();
            boardBean.deskNum = mEdtDeskNum.getText().toString();
            boardBean.content = mEdtContent.getText().toString();

            boardBean.millisecond = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
            boardBean.date=sdf.format(new Date());

            //고유번호를 생성한다

            String guid = getUseridFromUUID(boardBean.userId);
            dbRef.child("board").child(guid).child(boardBean.id).setValue(boardBean);
            Toast.makeText(this,"게시물이 등록되었습니다",Toast.LENGTH_SHORT).show();
            finish();

        }else {

            StorageReference storageRef = mFirebaseStorage.getReference();
            if (gallery == false) {
                //사진부터 storage에 업로드한다
                final StorageReference imagesRef = storageRef.child("image/" + mCaptureUri.getLastPathSegment());

                UploadTask uploadTask = imagesRef.putFile(mCaptureUri);
                //파일 업로드 실패에 따른 콜백 처리를 한다
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //데이터 베이스 업로드를 호출한다
                        uploadDB(task.getResult().toString(), mCaptureUri.getLastPathSegment());
                    }
                });
            } else {
                Uri file = imgUri;
                StorageReference imagesRef = storageRef.child("image/" + file.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(file);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //데이터 베이스 업로드를 호출한다
                        uploadDB(task.getResult().toString(), file.getLastPathSegment());
                    }
                });
            }
        }
    }



    private void uploadDB(String imgUri,String imgName){
        //Firebase 데이터베이스에 메모를 등록한다.
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        String id = dbRef.push().getKey();

        //데이터베이스에 저장한다.
        BoardBean boardBean = new BoardBean();

        boardBean.id = id;
        boardBean.userId=mFirebaseAuth.getCurrentUser().getEmail();
        boardBean.intCondition = 0;
        boardBean.intToCondition();

        boardBean.stuNum = mEdtStuNum.getText().toString();
        boardBean.name = mEdtName.getText().toString();

        boardBean.intHouse = mintHouse;
        boardBean.intToHouse();

        boardBean.roomNum = mEdtRoomNum.getText().toString();
        boardBean.deskNum = mEdtDeskNum.getText().toString();
        boardBean.content = mEdtContent.getText().toString();

        boardBean.imgUri=imgUri;
        boardBean.imgName=imgName;

        boardBean.millisecond = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
        boardBean.date=sdf.format(new Date());

        //고유번호를 생성한다
        String guid = getUseridFromUUID(boardBean.userId);
        dbRef.child("board").child(guid).child(boardBean.id).setValue(boardBean);
        Toast.makeText(this,"게시물이 등록되었습니다",Toast.LENGTH_SHORT).show();
        finish();
    }



    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    private void takePicture() {

        gallery = false;
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile( getOutPutMediaFile() );
        } else {
            mCaptureUri = FileProvider.getUriForFile(this,
                    "com.swu.cho4.fixswu", getOutPutMediaFile());
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);

    }

    private File getOutPutMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "cameraDemo");
        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        mPhotoPath = file.getAbsolutePath();

        return file;
    }

    private void sendPicture() {
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
        Bitmap resizedBmp = getResizedBitmap(bitmap, 2, 300, 600);

        bitmap.recycle();

        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mPhotoPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if(exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientToDegree(exifOrientation);
        } else {
            exifDegree = 0;
        }
        Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
        mImgProfile.setImageBitmap( rotatedBmp );

        saveBitmapToFileCache(resizedBmp, mPhotoPath);
    }

    private void getPictureFromGallery(Uri imgUri){

        gallery = true;
        this.imgUri = imgUri;
        mPhotoPath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientToDegree(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);//경로를 통해 비트맵으로 전환
        mImgProfile.setImageBitmap(roate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        } return cursor.getString(column_index);
    }

    private void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int exifOrientToDegree(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
    }

    //비트맵의 사이즈를 줄여준다.
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
        return resized;
    }

    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if(imgUri!=null)imgUri = data.getData();

        //카메라로부터 오는 데이터를 취득한다.
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    sendPicture();
                    break;
                case GALLERY_CODE:
                    getPictureFromGallery(data.getData());
                    break;
                case 3333:
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
