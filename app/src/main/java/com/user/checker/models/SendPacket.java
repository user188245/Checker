package com.user.checker.models;

import org.json.JSONException;
import org.json.JSONObject;


public class SendPacket implements JsonSerializable{
    public String s_id;
    public String purpose;
    public CommModel data;

    public SendPacket(String s_id, String purpose, CommModel data) {
        this.s_id = s_id;
        this.purpose = purpose;
        this.data = data;
    }

    @Override
    public String jsonSerialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("s_id",s_id);
        jsonObject.put("purpose",purpose);
        if(data != null)
            jsonObject.put("data",data.jsonSerialize());
        else
            jsonObject.put("data","");
        return jsonObject.toString();
    }
}
