package com.swu.cho4.fixswu;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BoardBean implements Serializable {
    public String ApplyNum;
    public String Condition;

    public String id;
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
    public String comment;
    public boolean like;
    public transient Bitmap bmpTitle;
}
