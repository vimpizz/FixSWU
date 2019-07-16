package com.swu.cho4.fixswu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class BoardAdapter extends BaseAdapter {

    private Context mContext;
    private List<BoardBean> mBoardList;
    private String house;


    public BoardAdapter(Context context, List<BoardBean> boardList) {
        mContext = context;
        mBoardList = boardList;
        sortForDdate();
    }

    public void setBoardList(List<BoardBean> boardList) {
        mBoardList = boardList;
        sortForDdate();
    }

    //리스트 정렬
    private void sortForDdate() {

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
        if(boardBean.house == 0 ) {
            house = "샬롬하우스 A동";
        } else if(boardBean.house == 1) {
            house = "샬롬하우스 B동";
        } else if(boardBean.house == 2) {
            house ="국제생활관";
        } else if(boardBean.house == 3 ) {
            house = "바롬관 10층";
        }
        txtvHouse.setText("기관: " +house);
        txtvRoom.setText("방: " + boardBean.roomNum);
        txtvDesk.setText("번호 :"+ boardBean.deskNum);
        txtvDate.setText("일자 :" +boardBean.date);

        applyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.equals(boardBean.condition, mContext.getResources().getString(R.string.condition1))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("알림창");
                    builder.setMessage("기사님께서 게시글을 확인하신 후에는 수정이 불가능합니다.");
                    builder.setNegativeButton("뒤로가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    builder.setPositiveButton("", null);
                    builder.create().show();

                }else{
                Intent i = new Intent(mContext, DetailBoardActivity.class);
                i.putExtra(BoardBean.class.getName(), boardBean);
                mContext.startActivity(i);}
            }
        });
        return view;
    }
}
