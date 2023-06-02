package com.example.project.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.project.model.Subject;

import org.json.JSONArray;
import org.json.JSONException;

public class SongCache {
    public static JSONArray getAllMusic(Context context) throws JSONException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Songssss", context.MODE_PRIVATE);
        String jsonArrayString = sharedPreferences.getString("songs", null);
        if(jsonArrayString!=null){
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            return jsonArray;
        }
        JSONArray jsonArray=new JSONArray();
        return jsonArray;
    }
    public static void addMusic(Context context, String id) throws JSONException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Songssss", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = getAllMusic(context);
        System.out.println(jsonArray);
        jsonArray.put(id);
        editor.putString("songs", jsonArray.toString());
        editor.apply();
    }
}
