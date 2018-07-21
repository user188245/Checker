package com.user.checker.models;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerInfo implements JsonDeserializable{
	public int flag;
	public String s_id;
	
	public PlayerInfo(int flag, String s_id) {
		this.flag = flag;
		this.s_id = s_id;
	}
	
	public PlayerInfo(String json) throws JSONException {
		jsonDeserialize(json);
	}

	@Override
	public JSONObject jsonDeserialize(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json.replace("\\", ""));
        this.flag = jsonObject.getInt("flag");
        this.s_id = jsonObject.getString("s_id");
        return jsonObject;
	}
	
	
	
	

}
