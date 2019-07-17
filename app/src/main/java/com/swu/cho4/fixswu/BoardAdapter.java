package com.swu.cho4.fixswu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

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


        TextView txtvNum = view.findViewById(R.id.txtvNum);
        TextView txtvCondition = view.findViewById(R.id.txtvCondition);
        TextView txtvContents = view.findViewById(R.id.txtvContents);
        TextView txtvHouse = view.findViewById(R.id.txtvHouse);
        TextView txtvRoom = view.findViewById(R.id.txtvRoom);
        TextView txtvDesk = view.findViewById(R.id.txtvDesk);
        TextView txtvDate = view.findViewById(R.id.txtvDate);
        LinearLayout applyBox = view.findViewById(R.id.applyBox);

        final BoardBean boardBean = mBoardList.get(i);

        txtvCondition.setText("상태: " + boardBean.condition);
        txtvContents.setText("수리 내용 : " + boardBean.content);
        txtvHouse.setText(boardBean.house);
        txtvRoom.setText(boardBean.roomNum);
        txtvDesk.setText("번호 :"+ boardBean.deskNum);
        txtvDate.setText(boardBean.date);

        applyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetailBoardActivity.class);
                i.putExtra(BoardBean.class.getName(), boardBean);
                //i.putExtra("titleBitmap",boardBean.bmpTitle);
                mContext.startActivity(i);}
        });
        return view;
    }
}
