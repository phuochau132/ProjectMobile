package com.example.project.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.project.R;
import com.example.project.cache.UserCache;
import com.example.project.event.CallbackAPI;
import com.example.project.fragment.Login;
import com.example.project.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAPI {
    private static final String API_URL = "http://192.168.1.179:3008/auth";

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

    public static void uploadFile(File fileUriAudio, File fileUriImage, String title, String artist,Context context, CallbackAPI callbackAPI) {
        System.out.println(fileUriAudio + "------"+fileUriImage);



        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("artists", artist)
                .addFormDataPart("audio", "audio", RequestBody.create(fileUriAudio, MediaType.parse("audio/*")))
                .addFormDataPart("image", "image", RequestBody.create(fileUriImage, MediaType.parse("image/*")))
                .build();

//
        HttpUrl.Builder httpBuilder = HttpUrl.parse(API_URL+"/upload_audio").newBuilder();

        Request request = new Request.Builder()
                .addHeader("x_authorization", UserCache.getToken(context))
                .url(httpBuilder.build())
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                System.out.println(e.getMessage() + "huy");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                System.out.println(result + "huy");
                callbackAPI.callback(result);

            }
        });
    }
    public static void forgotPassword(String username, Context context) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .build();
        Request request = new Request.Builder()
                .url(API_URL + "/forgot_password")
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
                UserCache.saveNotifyForgot(context, result);

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





