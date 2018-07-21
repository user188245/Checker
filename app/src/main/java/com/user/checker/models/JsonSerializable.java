package com.user.checker.models;

import org.json.JSONException;

public interface JsonSerializable {
    public String jsonSerialize() throws JSONException;
}
