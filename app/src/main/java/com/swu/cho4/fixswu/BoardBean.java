package com.swu.cho4.fixswu;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BoardBean implements Serializable {
    public String ApplyNum;
    public String condition; //

    public String id; // 보드 구분 아이디
    public String userId;

    public String stuNum;
    public String name;
    public int house;
    public String roomNum;
    public String deskNum;

    public String imgUri;
    public String imgName;

    public String content;
    public String date;
    public long millisecond;
    public String comment;
    public boolean like;
    public transient Bitmap bmpTitle;

    public long getMillisecond() {
        return millisecond;
    }
}
