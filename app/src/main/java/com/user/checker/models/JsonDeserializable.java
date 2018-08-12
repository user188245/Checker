package com.user.checker.models;

import org.json.JSONException;
import org.json.JSONObject;


public interface JsonDeserializable {
    JSONObject jsonDeserialize(String json) throws JSONException;
}
