package com.example.project.api;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.project.R;
import com.example.project.cache.UserCache;
import com.example.project.fragment.Login;
import com.example.project.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAPI {
    private static final String API_URL = "https://backend-clone-zing-mp3.vercel.app/auth";
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    public static void checkLogin(String username, String password,Context context) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(API_URL + "/login")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.fillInStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Log.e("result", result);
                if (isJSONValid(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String token = jsonObject.getString("accessToken");
                        Log.e("userTrue", token);
                        UserCache.saveToken(context, token, username);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    UserCache.saveToken(context,"", "");
                    Log.e("userFalse", "Not token");
                }

            }
        });
    }
    public static void register(String username, String password, String email, Context context) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("email", email)
                .build();
        Request request = new Request.Builder()
                .url(API_URL + "/register")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.fillInStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Log.e("result", result);
                if (isJSONValid(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String username = jsonObject.getString("username");
                        Log.e("userTrue", username);
                        UserCache.saveRegister(context, username);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    UserCache.saveRegister(context, "");
                    Log.e("userFalse", "Fail");
                }

            }
        });
    }
    public static boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
