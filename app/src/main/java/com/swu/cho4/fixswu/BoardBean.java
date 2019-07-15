package com.swu.cho4.fixswu;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BoardBean implements Serializable {
    public String ApplyNum;
    public String Condition;
    public String stuNum;
    public String name;
    public String house;
    public String roomNum;
    public String deskNum;
    public String imgUrl;
    public String content;
    public String date;
    public String comment;
    public boolean like;
    public transient Bitmap bmpTitle;
}
