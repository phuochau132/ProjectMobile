package com.example.project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project.R;
import com.example.project.api.SongAPI;
import com.example.project.cache.SongCache;
import com.example.project.cache.UserCache;
import com.example.project.event.CallbackAPI;
import com.example.project.fragment.HomeFragment;
import com.example.project.fragment.HistoryFragment;
import com.example.project.fragment.Login;
import com.example.project.fragment.ProfileFragment;
import com.example.project.fragment.UploadFragment;
import com.example.project.model.Subject;
import com.example.project.service.ProcessBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton btnPlay;
    private Intent serviceIntent;
    LinearLayout miniControl;
    TextView textArtist;
    TextView textName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
        this.addEventTab();
        this.addEventPlaySong();
        this.showFragHome();
        this.checkShowMiniControl();
        this.addEventKillThis();
        this.setTextFMiniControl();
        if(PlaylistActivity.serviceIntent!=null){
            if (ProcessBar.check) {
                btnPlay.setImageResource(R.drawable.baseline_pause_24);
            } else {
                btnPlay.setImageResource(R.drawable.baseline_play_arrow_24);
            }
        }
    }

    public void showFragHome() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).addToBackStack(null).commit();
    }

    public void addEventTab() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).addToBackStack(null).commit();
                        System.out.println("Home");
                        return true;

                    case R.id.lib:
                        try {
                            initListMusic();
                        } catch (JSONException e) {

                        }
                        return true;
                    case R.id.profile:
                        String token = UserCache.getToken(getApplicationContext());
                        if(token.equals("")){
                            Login loginFragment = new Login();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).addToBackStack(null).commit();

                        }else{
                            ProfileFragment profileFragment = new ProfileFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).addToBackStack(null).commit();

//                            UploadFragment uploadFragment = new UploadFragment();
//                            getSupportFragmentManager().beginTransaction().replace(R.id.container, uploadFragment).addToBackStack(null).commit();

                        }
                        return true;
                }
                return false;
            }
        });
    }
    public void getHistory(){

    }
    public void initListMusic() throws JSONException {
        JSONArray dataInput= SongCache.getAllMusic(this);
//        for (int i = 0; i <data.length(); i++) {
//            try {
//                String tmp = data.getString(i);
//                Subject song= SongAPI.getInfoSong(tmp);
//                listMusic.add(song);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        SongAPI.getInfoSong(dataInput, new CallbackAPI() {
            @Override
            public <T> void callback(T data)  {
                HistoryFragment historyFragment = new HistoryFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("listMusic", (ArrayList<Subject>) data);
                System.out.println("sdsdsd" +  ((ArrayList<?>) data).size());
                historyFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, historyFragment).addToBackStack(null).commit();
            }
        });
    }
    public void checkShowMiniControl() {
        if (!ProcessBar.url.equals("")) {
            miniControl.setVisibility(View.VISIBLE);
        }


    }
    public void setTextFMiniControl(){
        Intent intent = getIntent();
        if(intent!=null){
            String artist = intent.getStringExtra("artist");
            String name = intent.getStringExtra("name");
            textArtist.setText(artist);
            textName.setText(name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.checkShowMiniControl();
    }

    public void addEventKillThis() {
        miniControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void addEventPlaySong() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceIntent = PlaylistActivity.serviceIntent;
                if (!ProcessBar.check) {
                    ProcessBar.check=true;
                    btnPlay.setImageResource(R.drawable.baseline_pause_24);
                } else {
                    ProcessBar.check=false;
                    btnPlay.setImageResource(R.drawable.baseline_play_arrow_24);
                }
                startService(serviceIntent);

            }
        });

    }

    public void init() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnPlay = findViewById(R.id.play);;
        miniControl = findViewById(R.id.mini_control);
        textArtist=findViewById(R.id.artist);
        textName=findViewById(R.id.name);

    }

}