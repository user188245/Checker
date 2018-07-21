package com.user.checker.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by user on 2017-06-08.
 */

public class SidModel extends BaseModel implements JsonDeserializable{
    public String s_id;

    public SidModel(String json) throws JSONException {
        super(json);
    }

    public SidModel(String... url__name_val) throws ExecutionException, InterruptedException, JSONException {
        super(url__name_val);
    }

    @Override
    public JSONObject jsonDeserialize(String json) throws JSONException {
        JSONObject jsonObject = super.jsonDeserialize(json);
        s_id = jsonObject.getString("s_id");
        return jsonObject;
    }
}
