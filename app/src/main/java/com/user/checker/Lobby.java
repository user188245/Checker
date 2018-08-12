package com.user.checker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.user.checker.models.BaseModel;
import com.user.checker.models.Room;
import com.user.checker.models.RoomModel;

import java.io.IOException;
import java.util.ArrayList;

public class Lobby extends AppCompatActivity implements View.OnClickListener,MenuItem.OnMenuItemClickListener,ListView.OnItemClickListener{
    ListView listView;
    RoomAdaptor roomAdaptor;
    ArrayList<Room> roomList;
    Room selectedRoom = null;
    static String s_id;

    EditText editText_roomId,editText_roomName;
    Button button_lobby_join,button_lobby_exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        init();
        setPreSID();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRoom();
    }

    private void init() {
        editText_roomId = (EditText) findViewById(R.id.editText_roomId);
        editText_roomName = (EditText) findViewById(R.id.editText_roomName);
        button_lobby_join = (Button) findViewById(R.id.button_lobby_join);
        button_lobby_exit = (Button) findViewById(R.id.button_lobby_exit);
        listView = (ListView) findViewById(R.id.listView);
        roomList = new ArrayList<>();
        roomAdaptor = new RoomAdaptor(getApplicationContext(),roomList);
        listView.setAdapter(roomAdaptor);
        listView.setOnItemClickListener(this);
    }

    public void setPreSID(){
        DBManager db = new DBManager(this);
        try {
            Lobby.s_id = db.getSID();
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), "err : REQUEST TIMEOUT: Network Failed!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem refresh_room = menu.add(0,0,0,"Refresh Room List");
        MenuItem create_room  = menu.add(0,1,0,"Create Room");
        refresh_room.setOnMenuItemClickListener(this);
        create_room.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.button_lobby_join:
                if(selectedRoom != null) {
                    intent = new Intent(Lobby.this, MultiGameActivity.class);
                    intent.putExtra("s_id", s_id);
                    intent.putExtra("s_id2", selectedRoom.s_id);
                    intent.putExtra("isHost", false);
                    startActivity(intent);
                }
                break;
            case R.id.button_lobby_exit:
                finish();
                break;
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case 0:
                refreshRoom();
                break;
            case 1:
                try{
                    BaseModel baseModel = new BaseModel(HttpManager.post(getResources().getString(R.string.web) + "create.php","s_id",s_id));
                    if(baseModel.errorCode !=0)
                        Toast.makeText(getApplicationContext(),"err : " + baseModel.cause,Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(Lobby.this, MultiGameActivity.class);
                        intent.putExtra("s_id",s_id);
                        intent.putExtra("isHost",true);
                        startActivity(intent);
                    }
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "err : REQUEST TIMEOUT: Network Failed!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedRoom = (Room) roomAdaptor.getItem(position);
        editText_roomId.setText(String.valueOf(selectedRoom.room_id));
        editText_roomName.setText("");
        editText_roomName.append("Checker #");
        editText_roomName.append(String.valueOf(selectedRoom.room_id));
    }

    public void refreshRoom(){
        try{
            RoomModel roomModel = new RoomModel(HttpManager.post(getResources().getString(R.string.web) + "list.php","s_id",s_id));
            if(roomModel.errorCode != 0)
                Toast.makeText(getApplicationContext(),"err : " + roomModel.cause,Toast.LENGTH_SHORT).show();
            else{
                roomAdaptor.setList(roomModel.rooms);
                roomAdaptor.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"refresh completed, The number of room : " + roomAdaptor.getCount(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "err : REQUEST TIMEOUT: Network Failed!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }



}
