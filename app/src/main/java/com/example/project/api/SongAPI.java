package com.example.project.api;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.project.cache.CacheObject;
import com.example.project.cache.UserCache;
import com.example.project.event.CallbackAPI;
import com.example.project.event.OnClickListener;
import com.example.project.cache.CacheMemory;
import com.example.project.model.Playlist;
import com.example.project.model.Section;
import com.example.project.model.SectionPlaylist;
import com.example.project.model.SectionSong;

import com.example.project.model.Subject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SongAPI{
    private static OnClickListener o;
    private static ArrayList<Subject> rs = new ArrayList<>();
    private static final String API_URL = "http://192.168.1.179:3008/api";


    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();


    public static void setO(OnClickListener o) {
        SongAPI.o = o;
    }
    public static ArrayList<Subject> getSongsByIdPlaylist(String id, CallbackAPI callbackAPI) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(API_URL+"/get_songs_by_id_playlist").newBuilder();
        httpBuilder.addQueryParameter("id",id);
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Lỗi khi gọi API: " + e.getMessage());
                System.out.println("Lỗi call api: " + e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                JSONArray songs = null;
                ArrayList<Subject> subjects = new ArrayList<>();
                try {
                    songs = new JSONArray(result);
                    for (int i = 0; i < songs.length(); i++)
                    {
                        String id = songs.getJSONObject(i).getString("id");
                        String title = songs.getJSONObject(i).getString("title");
                        String artistsNames = songs.getJSONObject(i).getString("artistsNames");
                        String img = songs.getJSONObject(i).getString("thumbnailM");
                        String url = songs.getJSONObject(i).getString("linkStream");
                        Subject subject = new Subject(id, title, artistsNames, img, url);
                        subjects.add(subject);
                    }
                    callbackAPI.callback(subjects);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return rs;
    }
    public static ArrayList<Subject> searchSongByName(String name) throws IOException, JSONException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(API_URL+"/search").newBuilder();
        httpBuilder.addQueryParameter("name",name);
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                System.out.println("createOrder: " + result);
                JSONArray songs = null;
                ArrayList<Subject> subjects = new ArrayList<>();
                try {
                    songs = new JSONArray(result);
                    for (int i = 0; i < songs.length(); i++)
                    {
                        String id = songs.getJSONObject(i).getString("id");
                        String title = songs.getJSONObject(i).getString("title");
                        String artistsNames = songs.getJSONObject(i).getString("artistsNames");
                        String img = songs.getJSONObject(i).getString("thumbnailM");
                        String url = songs.getJSONObject(i).getString("linkStream");
                        Subject subject = new Subject(id, title, artistsNames, img, url);
                        subjects.add(subject);
                    }

                o.setData(subjects);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return rs;
    }
    public static ArrayList<Subject> getSongByUsername(String username ,CallbackAPI callbackAPI) throws IOException, JSONException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(API_URL+"/get_song_by_username").newBuilder();
        httpBuilder.addQueryParameter("username",username);
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                System.out.println("createOrder: " + result);
                JSONArray songs = null;
                ArrayList<Subject> subjects = new ArrayList<>();
                try {
                    songs = new JSONArray(result);
                    for (int i = 0; i < songs.length(); i++)
                    {
                        String id = songs.getJSONObject(i).getString("id");
                        String title = songs.getJSONObject(i).getString("title");
                        String artistsNames = songs.getJSONObject(i).getString("artistsNames");
                        String img = songs.getJSONObject(i).getString("thumbnailM");
                        String url = songs.getJSONObject(i).getString("linkStream");
                        Subject subject = new Subject(id, title, artistsNames, img, url);
                        subjects.add(subject);
                    }

                    callbackAPI.callback(subjects);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return rs;
    }
    public static void getHome(CallbackAPI callback){
        CacheMemory cacheMemory = CacheMemory.getMemoryCache();
        String homeCache = cacheMemory.getDataFromCache("home");

        Type type = new TypeToken<ArrayList<CacheObject>>(){}.getType();
        ArrayList<CacheObject> cacheObjects = new ArrayList<>();
        ArrayList<Section> sections = new ArrayList<>();
        if(homeCache != null){
            cacheObjects = new Gson().fromJson(homeCache,type);
            for (CacheObject tmp: cacheObjects) {
                sections.add(tmp.getObject());
            }
            callback.callback(sections);
            return;

        }
        System.out.println("Call API HOME");
        HttpUrl.Builder httpBuilder = HttpUrl.parse(API_URL+"/get_playlist_home").newBuilder();
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ArrayList<Section> sections = new ArrayList<>();

                try {
                    JSONArray items = new JSONArray(result);
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject tmp = items.getJSONObject(i);
                        String sectionType = tmp.getString("sectionType");
                        String title= tmp.getString("title");
                        if(sectionType.equals("new-release")){
                            Section section = new SectionSong();
                            section.setTitle(title);
                            section.setSectionType(sectionType);
                            JSONArray arrayItems = tmp.getJSONArray("items");
                            ArrayList<Subject> arrayListSubject = new ArrayList<>();
                            for (int j = 0; j < arrayItems.length(); j++) {
                                Subject subject = new Subject();
                                subject.setId(arrayItems.getJSONObject(j).getString("id"));
                                subject.setUrl(arrayItems.getJSONObject(j).getString("linkStream"));
                                subject.setSrc(arrayItems.getJSONObject(j).getString("thumbnailM"));
                                subject.setName(arrayItems.getJSONObject(j).getString("title"));
                                subject.setArtist(arrayItems.getJSONObject(j).getString("artistsNames"));
                                arrayListSubject.add(subject);
                            }
                            ((SectionSong) section).setSubjects(arrayListSubject);
                            sections.add(section);
                            continue;
                        }

                        Section section = new SectionPlaylist();
                        section.setTitle(title);
                        section.setSectionType(sectionType);

                        JSONArray arrayItems = tmp.getJSONArray("items");
                        ArrayList<Playlist> arrayList = new ArrayList<>();

                        for (int j = 0; j < arrayItems.length(); j++) {
                            Playlist playlist = new Playlist();
                            playlist.setId(arrayItems.getJSONObject(j).getString("encodeId"));
                            playlist.setImg(arrayItems.getJSONObject(j).getString("thumbnail"));
                            playlist.setName(arrayItems.getJSONObject(j).getString("title"));
                            playlist.setDescription(arrayItems.getJSONObject(j).getString("sortDescription"));
                            arrayList.add(playlist);

                        }
                        ((SectionPlaylist) section).setPlaylists(arrayList);

                        sections.add(section);
                    }
                    ArrayList<CacheObject> cacheObjects = new ArrayList<>();
                    for (Section tmp: sections) {
                        cacheObjects.add(new CacheObject(tmp));
                    }
                    cacheMemory.putDataToCache("home", new Gson().toJson(cacheObjects));

                    callback.callback(sections);

                    System.out.println(items.length());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });
    }
    public static void getInfoSong(JSONArray subjects, CallbackAPI callbackAPI){
        RequestBody requestBody = new FormBody.Builder()
                .add("subjects", new Gson().toJson(subjects))
                .build();
        Request request = new Request.Builder()
                .url(API_URL + "/get_all_info_history")
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
                ArrayList<Subject> listMusic = new ArrayList<>();
                Log.e("result", result);
                JSONArray songs= null;
                try {
                    songs = new JSONArray(result);
                    for (int i = 0; i < songs.length(); i++)
                    {
                        String id = songs.getJSONObject(i).getString("id");
                        String title = songs.getJSONObject(i).getString("title");
                        String artistsNames = songs.getJSONObject(i).getString("artistsNames");
                        String img = songs.getJSONObject(i).getString("thumbnailM");
                        String url = songs.getJSONObject(i).getString("linkStream");
                        Subject subject = new Subject(id, title, artistsNames, img, url);
                        listMusic.add(subject);
                    }
                    callbackAPI.callback(listMusic);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });

    }
    public static Subject getInfoSong(String id) {
        final CountDownLatch latch = new CountDownLatch(1); // Khởi tạo CountDownLatch với giá trị ban đầu là 1

        Subject[] subjectWrapper = new Subject[1]; // Sử dụng một mảng để bọc đối tượng Subject

        HttpUrl.Builder httpBuilder = HttpUrl.parse(API_URL + "/get_all_info_song?id=" + id).newBuilder();
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject res = new JSONObject(result);
                    subjectWrapper[0] = new Subject(res.getString("id"), res.getString("title"), res.getString("artistsNames"), res.getString("thumbnailM"), res.getString("linkStream"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return subjectWrapper[0];
    }

    public static void main(String[] args) throws IOException {
    }

}
