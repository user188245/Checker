package com.user.checker.models;

import org.json.JSONException;

public interface JsonSerializable {
    String jsonSerialize() throws JSONException;
}
