package com.swu.cho4.fixswu.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.swu.cho4.fixswu.bean.BoardBean;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

public class DownloadImgTask extends AsyncTask<URL, Void, Bitmap> {

    private Context mContext;
    private WeakReference<ImageView> mImageView = null;
    private List<BoardBean> mBoardList;
    private int mPosition;


    //생성자
    public DownloadImgTask(Context context, ImageView imageView, List<BoardBean> boardList, int position){
        mContext = context;
        mImageView = new WeakReference<>(imageView);
        mBoardList= boardList;
        mPosition = position;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        URL imageURL = params[0];
        Bitmap downloadBitmap = null;

        try{
            InputStream inputStream = imageURL.openStream();
            downloadBitmap = BitmapFactory.decodeStream(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }

        return downloadBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null){
            //이미지 다운로드 성공
            mImageView.get().setImageBitmap(bitmap);
            //리스트 갱신
            if(mBoardList != null) {
                mBoardList.get(mPosition).bmpTitle = bitmap;
            }
        }

    }
}
