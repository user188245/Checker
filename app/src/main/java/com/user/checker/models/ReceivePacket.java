package com.user.checker.models;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReceivePacket extends BaseModel implements JsonDeserializable{
	
	
	public String state;
	public Queue<CommModel> receive;
	public PlayerInfo enemy;
	
	public ReceivePacket(String json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JSONObject jsonDeserialize(String json) throws JSONException {
		JSONObject jsonObject = super.jsonDeserialize(json);
		this.state = jsonObject.getString("state");
		JSONArray jsonArray = new JSONArray(jsonObject.get("receive").toString());
		receive = new LinkedList<CommModel>();
		for(int i = 0; i<jsonArray.length(); i++){
			receive.add(new CommModel(jsonArray.getJSONObject(i).toString()));
		}
		this.enemy = new PlayerInfo(jsonObject.getJSONObject("enemy").toString());
		return jsonObject;
	}

	@Override
	public String toString() {
		return "[state=" + state + ", receive=" + receive + ", errorCode=" + errorCode + ", cause="
				+ cause + "]";
	}

	
	
	
	
	
	
	

}
