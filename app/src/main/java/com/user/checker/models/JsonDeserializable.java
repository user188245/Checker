package com.user.checker.models;

import org.json.JSONException;
import org.json.JSONObject;


public interface JsonDeserializable {
    public JSONObject jsonDeserialize(String json) throws JSONException;
}
