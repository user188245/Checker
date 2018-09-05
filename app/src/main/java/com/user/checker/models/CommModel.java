package com.user.checker.models;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;


public class CommModel implements JsonSerializable,JsonDeserializable{
    public int move;
    public String target;
    public String chat;

    public CommModel(int move, String target, String chat) {
        this.move = move;
        this.target = target;
        this.chat = chat;
    }

    public CommModel(String json) throws JSONException {
        jsonDeserialize(json);
    }

    @Override
    public JSONObject jsonDeserialize(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json.replace("\\n","").replace("\\r","").replace("\\", ""));
        if(json != null && !json.isEmpty()){
            this.move = jsonObject.getInt("move");
            this.target = jsonObject.getString("target");
            try {

                this.chat = new String(Base64.decode(jsonObject.getString("chat"),Base64.DEFAULT));
            } catch (Exception e) {
                this.chat = "";
            }
        }
        return jsonObject;
    }



    @Override
    public String toString() {
        return "[move=" + move + ", target=" + target + ", chat=" + chat + "]";
    }

    @Override
    public String jsonSerialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("move",move);
        jsonObject.put("target",target);
        jsonObject.put("chat",Base64.encodeToString(chat.getBytes(),Base64.DEFAULT));
        return jsonObject.toString();
    }

}

