package com.example.project.event;

import org.json.JSONException;

public interface CallbackAPI {
    public  <T> void callback(T data);
}
