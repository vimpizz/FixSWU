package com.swu.cho4.fixswu.foregin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.swu.cho4.fixswu.user.WriteActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ForeignWrite extends AppCompatActivity {

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
    private String contents = "";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URI);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    private ArrayAdapter houseAdapter;
    private Spinner houseSpinner;
    private int housespinner;

    private ArrayAdapter classAdapter;
    private Spinner classSpinner;
    private int classpinner;


    private ArrayAdapter exampleAdapter;
    private Spinner exampleSpinner;
    private int examplespinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_write);

        houseSpinner = findViewById(R.id.spinnerHouse);
        //어댑터 설정
        ArrayAdapter houseaAdapter = ArrayAdapter.createFromResource(this, R.array.houseForeigner, R.layout.support_simple_spinner_dropdown_item);
        houseSpinner.setAdapter(houseaAdapter);

        classSpinner = findViewById(R.id.spinnerClass);
        //어댑터 설정
        ArrayAdapter classAdapter = ArrayAdapter.createFromResource(this, R.array.exampleClass, R.layout.support_simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);

        exampleSpinner = findViewById(R.id.spinnerExample);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if (classSpinner.getSelectedItem().equals("Electric")) {
                   classpinner = 0;
                   setExamplespinner();
               } else if (classSpinner.getSelectedItem().equals("Furnicture")) {
                   classpinner = 1;
                   setExamplespinner();
               } else if (classSpinner.getSelectedItem().equals("Other")) {
                   classpinner = 2;
                   setExamplespinner();
               }
           }
           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {
           }
       });

        mImgProfile = findViewById(R.id.imgWrite);
        mEdtStuNum = findViewById(R.id.edtStuNum);
        mEdtName = findViewById(R.id.edtName);
        mEdtRoomNum = findViewById(R.id.edtRoomNum);
        mEdtDeskNum = findViewById(R.id.edtDeskNum);
        mEdtContent = findViewById(R.id.edtContent);

        houseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mintHouse=i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        exampleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 if (exampleSpinner.getSelectedItem().equals("1. Replacement of lights(Room)")) {
                     contents = "방 전등 교체";
                 } else if (exampleSpinner.getSelectedItem().equals("2. Replacement of lights(Desk)")) {
                     contents = "책상 전등 교체";
                 } else if (exampleSpinner.getSelectedItem().equals("3. Refrigerator malfunction") ) {
                     contents = "냉장고 오작동";
                 } else if (exampleSpinner.getSelectedItem().equals("4. Power plug/socket problem") ) {
                     contents = "전원 플러그/소켓 문제";
                 } else if ( exampleSpinner.getSelectedItem().equals("5. Problems with drawers in the closet")) {
                     contents = "벽장 서랍 문제";
                 } else if (exampleSpinner.getSelectedItem().equals("6. Problems with bed drawers" )) {
                     contents = "침대 서랍 문제";
                 } else if ( exampleSpinner.getSelectedItem().equals("7. Problems with chairs")) {
                     contents = "의자의 문제";
                 } else if ( exampleSpinner.getSelectedItem().equals("8. A fallen mirror") ){
                     contents = "떨어진 거울";
                 } else if ( exampleSpinner.getSelectedItem().equals("9. Problems with insect screen")) {
                     contents = "방충망 문제";
                 } else if ( exampleSpinner.getSelectedItem().equals("10. Problems with WIFI")) {
                     contents = "WIFI 문제";
                 } else if ( exampleSpinner.getSelectedItem().equals("11. Problems with an insect")) {
                     contents = "곤충 문제";
                 }else if ( exampleSpinner.getSelectedItem().equals("12. Water dropping from the Air Conditioner")) {
                     contents = "에어컨에서 떨어지는 물";
                 } else if ( exampleSpinner.getSelectedItem().equals("13. Cell phone dropped between the beds")) {
                     contents = "침대 사이에 휴대전화가 떨어졌어요.";
                 } else if ( exampleSpinner.getSelectedItem().equals("14. A fallen acrylic plate")) {
                     contents = "떨어진 아크릴판";
                 } else if ( exampleSpinner.getSelectedItem().equals("15. Other")) {
                     contents = "그 외 문제";
                 }
             }
             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

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
                    Toast.makeText(getApplicationContext(),"Please enter your Student Number",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(mEdtName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter your Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(mEdtRoomNum.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter your Room Number",Toast.LENGTH_SHORT).show();
                    return;
                }
               /* else if(TextUtils.isEmpty(mEdtDeskNum.getText().toString())){
                    Toast.makeText(getApplicationContext(),"번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                else if(TextUtils.isEmpty(contents)){
                    Toast.makeText(getApplicationContext(),"Please choose the Description of repair",Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ForeignWrite.this);
                builder.setTitle("Warning");
                builder.setMessage("After the driver has checked the post, it cannot be modified or deleted.");
                builder.setNegativeButton("Backward",null);
                builder.setPositiveButton("Register post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mBoardBean==null){
                            setResult(RESULT_OK);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ForeignWrite.this);
                builder.setTitle("Notification");
                builder.setMessage("Are you sure you want to cancel the posting and go back");
                builder.setNegativeButton("No",null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setExamplespinner() {
        if (classpinner == 0 ) {
            ArrayAdapter exampleAdapter1 = ArrayAdapter.createFromResource(this, R.array.electricExmaple, R.layout.support_simple_spinner_dropdown_item);
            exampleSpinner.setAdapter(exampleAdapter1);
        } else if (classpinner == 1) {
            ArrayAdapter exampleAdapter2 = ArrayAdapter.createFromResource(this, R.array.furnictureExmaple, R.layout.support_simple_spinner_dropdown_item);
            exampleSpinner.setAdapter(exampleAdapter2);
        } else if (classpinner == 2) {
            ArrayAdapter exampleAdapter3 = ArrayAdapter.createFromResource(this, R.array.otherExmaple, R.layout.support_simple_spinner_dropdown_item);
            exampleSpinner.setAdapter(exampleAdapter3);
        }
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
            boardBean.content = contents;

            boardBean.millisecond = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
            boardBean.date=sdf.format(new Date());

            //고유번호를 생성한다

            String guid = getUseridFromUUID(boardBean.userId);
            dbRef.child("board").child(guid).child(boardBean.id).setValue(boardBean);
            Toast.makeText(this,"The post has been registered.",Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this,"The post has been registered.",Toast.LENGTH_SHORT).show();
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

        //내가 원하는 액티비티로 이동하고, 그 액티비티가 종료되면 (finish되면)
        //다시금 나의 액티비티의 onActivityResult() 메서드가 호출되는 구조이다.
        //내가 어떤 데이터를 받고 싶을때 상대 액티비티를 호출해주고 그 액티비티에서
        //호출한 나의 액티비티로 데이터를 넘겨주는 구조이다. 이때 호출되는 메서드가
        //onActivityResult() 메서드 이다.
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
        Bitmap resizedBmp = getResizedBitmap(bitmap, 3, 100, 100);

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

        //Toast.makeText(this,"사진경로 : "+ mPhotoPath, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,"mCaptureUri : "+ mCaptureUri, Toast.LENGTH_LONG).show();
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
                default:
                    break;
            }
        }
    }



}
