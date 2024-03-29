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
import com.swu.cho4.fixswu.DownloadImgTask;
import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.bean.BoardBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ModifyWriteActivity extends AppCompatActivity {

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
    private int mintHouse = 0 ; //기관

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_write);


        mImgProfile = findViewById(R.id.imgWriteModify);
        mEdtStuNum = findViewById(R.id.edtStuNumModify);
        mEdtName = findViewById(R.id.edtNameModify);
        mSpinner=findViewById(R.id.spinnerHouseModify);
        mEdtRoomNum = findViewById(R.id.edtRoomNumModify);
        mEdtDeskNum = findViewById(R.id.edtDeskNumModify);
        mEdtContent = findViewById(R.id.edtContentModify);

        mBoardBean = (BoardBean) getIntent().getSerializableExtra(BoardBean.class.getName());

        mSpinner.setSelection(mBoardBean.intHouse);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mintHouse=i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        if(mBoardBean!=null) {

            try {
                new DownloadImgTask(this, mImgProfile, null, 0).execute(new URL(mBoardBean.imgUri));
            } catch (Exception e) {
                e.printStackTrace();
            }

            mEdtStuNum.setText(mBoardBean.stuNum);
            mEdtName.setText(mBoardBean.name);
            mEdtRoomNum.setText(mBoardBean.roomNum);
            mEdtDeskNum.setText(mBoardBean.deskNum);
            mEdtContent.setText(mBoardBean.content);


        }

        findViewById(R.id.btnCameraModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        findViewById(R.id.btnGalleryModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        findViewById(R.id.btnStuSaveModify).setOnClickListener(new View.OnClickListener() {
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
/*                else if(TextUtils.isEmpty(mEdtDeskNum.getText().toString())){
                    Toast.makeText(getApplicationContext(),"번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                else if(TextUtils.isEmpty(mEdtContent.getText().toString())){
                    Toast.makeText(getApplicationContext(),"수리내용을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyWriteActivity.this);
                builder.setTitle("경고");
                builder.setMessage("허위기재 시 불이익을 받으실 수 있습니다.");
                builder.setNegativeButton("뒤로가기",null);
                builder.setPositiveButton("수정하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mBoardBean!=null){
                            Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_SHORT).show();
                            update();
                        }
                    }
                });
                builder.create().show();
            }
        });

        findViewById(R.id.btnStuCancelModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyWriteActivity.this);
                builder.setTitle("알림창");
                builder.setMessage("게시글 수정을 취소하고 뒤로 가시겠습니까?");
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

    //게시물 수정
    private void update(){

        // 안찍었을 경우 DB만 업데이트 시켜준다
        if(mPhotoPath==null){
            mBoardBean.stuNum=mEdtStuNum.getText().toString();
            mBoardBean.name=mEdtName.getText().toString();
            mBoardBean.intHouse=mintHouse;
            mBoardBean.intToHouse();
            mBoardBean.roomNum=mEdtRoomNum.getText().toString();
            mBoardBean.deskNum=mEdtDeskNum.getText().toString();
            mBoardBean.content=mEdtContent.getText().toString();
            mBoardBean.millisecond = System.currentTimeMillis();
            //수정된 날짜로
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
            mBoardBean.date = sdf.format(new Date());

            //DB 업로드
            DatabaseReference dbRef = mFirebaseDatabase.getReference();
            String uuid = getUseridFromUUID(mBoardBean.userId);
            dbRef.child("board").child(uuid).child(mBoardBean.id).setValue(mBoardBean);
            Toast.makeText(this,"수정 되었습니다",Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        //사진을 찍었을경우, 사진부터 업로드하고 DB만 업데이트한다
        StorageReference storageRef = mFirebaseStorage.getReference();
        if(gallery == false){
            final StorageReference imagesRef = storageRef.child("image/"+mCaptureUri.getLastPathSegment());
            UploadTask uploadTask = imagesRef.putFile(mCaptureUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        throw  task.getException();
                    return imagesRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //파일 업로드 완료 후 호출된다
                    //기존이미지 파일 삭제한다.
                    if(mBoardBean.imgName!=null) {
                        try {
                            mFirebaseStorage.getReference().child("image").child(mBoardBean.imgName).delete();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    mBoardBean.imgUri = task.getResult().toString();
                    mBoardBean.imgName = mCaptureUri.getLastPathSegment();
                    mBoardBean.intHouse=mintHouse;
                    mBoardBean.intToHouse();
                    mBoardBean.stuNum=mEdtStuNum.getText().toString();
                    mBoardBean.name=mEdtName.getText().toString();
                    mBoardBean.roomNum=mEdtRoomNum.getText().toString();
                    mBoardBean.deskNum=mEdtDeskNum.getText().toString();
                    mBoardBean.content=mEdtContent.getText().toString();
                    mBoardBean.millisecond = System.currentTimeMillis();

                    //수정된 날짜로
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
                    mBoardBean.date = sdf.format(new Date());

                    String uuid = getUseridFromUUID(mBoardBean.userId);
                    mFirebaseDatabase.getReference().child("board").child(uuid).child(mBoardBean.id).setValue(mBoardBean);
                    Toast.makeText(getBaseContext(),"수정되었습니다",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else{
            Uri file = imgUri;
            StorageReference imagesRef = storageRef.child("image/"+file.getLastPathSegment());
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
                    //파일 업로드 완료 후 호출된다
                    //기존이미지 파일 삭제한다.
                    if(mBoardBean.imgName!=null) {
                        try {
                            mFirebaseStorage.getReference().child("image").child(mBoardBean.imgName).delete();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    mBoardBean.imgUri = task.getResult().toString();
                    mBoardBean.imgName = imgUri.getLastPathSegment();
                    mBoardBean.intHouse=mintHouse;
                    mBoardBean.intToHouse();
                    mBoardBean.stuNum=mEdtStuNum.getText().toString();
                    mBoardBean.name=mEdtName.getText().toString();
                    mBoardBean.roomNum=mEdtRoomNum.getText().toString();
                    mBoardBean.deskNum=mEdtDeskNum.getText().toString();
                    mBoardBean.content=mEdtContent.getText().toString();
                    mBoardBean.millisecond = System.currentTimeMillis();

                    //수정된 날짜로
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
                    mBoardBean.date = sdf.format(new Date());

                    String uuid = getUseridFromUUID(mBoardBean.userId);
                    mFirebaseDatabase.getReference().child("board").child(uuid).child(mBoardBean.id).setValue(mBoardBean);

                    Toast.makeText(getBaseContext(),"수정되었습니다",Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        }
    }

    public static String getUseridFromUUID(String userEmail){
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    private void takePicture() {

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
        Bitmap resizedBmp = getResizedBitmap(bitmap, 3, 300, 600);

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

        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    sendPicture();
                    break;

                case GALLERY_CODE:
                    getPictureFromGallery(data.getData());
                    break;
                default:
                    break;
            }
        }
    }
}
