package com.user.checker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.user.checker.models.Room;
import java.util.ArrayList;

public class RoomAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<Room> list;

    public RoomAdaptor(Context context, ArrayList<Room> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_room,null);
        TextView room_num = (TextView) convertView.findViewById(R.id.textView_custom_roomId);
        TextView s_id     = (TextView) convertView.findViewById(R.id.textView_custom_sid);
        room_num.setText(String.valueOf(list.get(position).room_id));
        s_id.setText(list.get(position).s_id);
        return convertView;
    }

    public void setList(ArrayList<Room> list) {
        this.list = list;
    }
}
