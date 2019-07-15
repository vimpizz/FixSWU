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

public class AdminBoardAdapter extends BaseAdapter {

    private Context mContext;
    private List<BoardBean> mBoardList;

    public AdminBoardAdapter(Context context, List<BoardBean> boardList) {
        mContext = context;
        mBoardList = boardList;
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
        TextView txtvInfo = view.findViewById(R.id.txtvInfo);
        TextView txtvDate = view.findViewById(R.id.txtvDate);
        LinearLayout applyBox = view.findViewById(R.id.applyBox);

        final BoardBean boardBean = mBoardList.get(i);

        txtvNum.setText(boardBean.ApplyNum);
        txtvCondition.setText(boardBean.Condition);
        txtvContents.setText(boardBean.content);
        txtvInfo.setText(boardBean.house + boardBean.roomNum + boardBean.deskNum);
        txtvDate.setText(boardBean.date);

        applyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, AdminWriteActivity.class);
                i.putExtra(BoardBean.class.getName(), boardBean);
                mContext.startActivity(i);
            }
        });

        return view;
    }
}
