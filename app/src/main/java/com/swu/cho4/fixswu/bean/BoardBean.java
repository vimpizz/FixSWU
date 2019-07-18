package com.swu.cho4.fixswu.bean;

import android.graphics.Bitmap;

import java.io.Serializable;


public class BoardBean implements Serializable {

    public String ApplyNum;

    public int intCondition;
    public String condition;

    public String id; // 보드 구분 아이디
    public String userId;

    public String stuNum;
    public String name;

    public int intHouse;
    public String house;

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


    public String intToCondition(){
        if(this.intCondition == 0 ) {
            this.condition = "확인전";
        } else if(this.intCondition == 1) {
            this.condition = "읽음";
        } else if(this.intCondition== 2) {
            this.condition = "수리완료";
        }
        return this.condition;
    }

    public void intToHouse(){
        if(this.intHouse == 0 ) {
            this.house = "샬롬하우스 A동";
        } else if(this.intHouse == 1) {
            this.house = "샬롬하우스 B동";
        } else if(this.intHouse == 2) {
            this.house ="국제생활관";
        } else if(this.intHouse == 3 ) {
            this.house = "바롬관 10층";
        }
    }
}
