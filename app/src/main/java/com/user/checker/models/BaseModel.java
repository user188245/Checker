package com.user.checker.models;

import com.user.checker.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class BaseModel implements JsonDeserializable{
    public int errorCode;
    public String cause;

    public BaseModel(String json) throws JSONException {
        jsonDeserialize(json);
    }

    public BaseModel(String... url__name_val) throws ExecutionException, InterruptedException, JSONException {
        jsonDeserialize(new HttpManager().execute(url__name_val).get());
    }

    @Override
    public JSONObject jsonDeserialize(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        this.errorCode = jsonObject.getInt("errorCode");
        this.cause = jsonObject.getString("cause");
        return jsonObject;
    }
}
