package com.swu.cho4.fixswu.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swu.cho4.fixswu.R;
import com.swu.cho4.fixswu.bean.BoardBean;
import com.swu.cho4.fixswu.bean.Utils;
import com.swu.cho4.fixswu.user.DetailBoardActivity;

import java.util.List;

public class BoardAdapter extends BaseAdapter {

    private Context mContext;
    private List<BoardBean> mBoardList;
    private String condition,house;

    public BoardAdapter(Context context, List<BoardBean> boardList) {
        mContext = context;
        mBoardList = boardList;
        //mBoardList =  Utils.getSortForDate(boardList);
    }

    public void setBoardList(List<BoardBean> boardList) {
        mBoardList =  Utils.getSortForDate(boardList);
    }

    @Override
    public int getCount() {
        return mBoardList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBoardList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_board_item, null);

        TextView txtvCondition = view.findViewById(R.id.txtvCondition);
        TextView txtvContents = view.findViewById(R.id.txtvContents);
        TextView txtvHouse = view.findViewById(R.id.txtvHouse);
        TextView txtvDate = view.findViewById(R.id.txtvDate);
        LinearLayout applyBox = view.findViewById(R.id.applyBox);

        final BoardBean boardBean = mBoardList.get(i);

        txtvCondition.setText(boardBean.intToCondition());
        txtvCondition.setTextColor(Color.parseColor("#FFFFFF"));
        if (boardBean.intToCondition() == "확인전") {
            txtvCondition.setBackgroundColor(Color.parseColor("#8a8d8f"));
        } else if ( boardBean.intToCondition() == "읽음") {
            txtvCondition.setBackgroundColor(Color.parseColor("#89734c"));
        } else if(boardBean.intToCondition() == "수리완료") {
            txtvCondition.setBackgroundColor(Color.parseColor("#143E8D"));
        }

        txtvContents.setText(boardBean.content);
        txtvHouse.setText(boardBean.house+" "+boardBean.roomNum+"호  "+boardBean.deskNum);
        txtvDate.setText(boardBean.date);

        applyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetailBoardActivity.class);
                i.putExtra(BoardBean.class.getName(), boardBean);
                mContext.startActivity(i);}
        });
        return view;
    }
}
