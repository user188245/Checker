package com.user.checker.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class RoomModel extends BaseModel implements JsonDeserializable{
    public ArrayList<Room> rooms;

    public RoomModel(String json) throws JSONException {
        super(json);
    }

    public RoomModel(String... url__name_val) throws ExecutionException, InterruptedException, JSONException {
        super(url__name_val);
    }

    @Override
    public JSONObject jsonDeserialize(String json) throws JSONException {
        JSONObject jsonObject = super.jsonDeserialize(json);
        JSONArray jsonArray = new JSONArray(jsonObject.get("rooms").toString());
        this.rooms = new ArrayList<Room>();
        for(int i=0; i<jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            rooms.add(new Room(object.getInt("room_id"),object.getString("s_id")));
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "[" +
                "rooms=" + rooms +
                ']';
    }
}
