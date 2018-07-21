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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Lobby extends AppCompatActivity implements View.OnClickListener,MenuItem.OnMenuItemClickListener,ListView.OnItemClickListener{
    ListView listView;
    RoomAdaptor roomAdaptor;
    ArrayList<Room> roomList;
    Room selectedRoom = null;
    String s_id;

    EditText editText_roomId,editText_roomName;
    Button button_lobby_join,button_lobby_exit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        init();
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
        DBManager db = new DBManager(getApplicationContext());
        s_id = db.getSID();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem refresh_room = menu.add(0,0,0,"방 목록 초기화");
        MenuItem create_room  = menu.add(0,1,0,"방 만들기");
        refresh_room.setOnMenuItemClickListener(this);
        create_room.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.button_lobby_join:
                intent = new Intent(Lobby.this, MultiGameActivity.class);
                intent.putExtra("s_id",s_id);
                intent.putExtra("s_id2",selectedRoom.s_id);
                intent.putExtra("isHost",false);
                startActivity(intent);
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
                    BaseModel baseModel = new BaseModel(HttpManager.post(HttpManager.webServer + "create.php","s_id",s_id));
                    if(baseModel.errorCode !=0)
                        Toast.makeText(getApplicationContext(),"err : " + baseModel.cause,Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(Lobby.this, MultiGameActivity.class);
                        intent.putExtra("s_id",s_id);
                        intent.putExtra("isHost",true);
                        startActivity(intent);
                    }
                }catch(Exception e){

                }
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedRoom = (Room) roomAdaptor.getItem(position);
        editText_roomId.setText(String.valueOf(selectedRoom.room_id));
        editText_roomName.setText("Checker #" + selectedRoom.room_id);
    }

    public void refreshRoom(){
        try{
            RoomModel roomModel = new RoomModel(HttpManager.post(HttpManager.webServer + "list.php","s_id",s_id));
            if(roomModel.errorCode != 0)
                Toast.makeText(getApplicationContext(),"err : " + roomModel.cause,Toast.LENGTH_SHORT).show();
            else{
                roomAdaptor.setList(roomModel.rooms);
                roomAdaptor.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"refresh completed, The number of room : " + roomAdaptor.getCount(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
