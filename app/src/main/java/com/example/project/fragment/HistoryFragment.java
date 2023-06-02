package com.example.project.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project.api.SongAPI;
import com.example.project.cache.SongCache;
import com.example.project.event.CallbackAPI;
import com.example.project.event.OnClickListener;
import com.example.project.R;
import com.example.project.activity.PlaylistActivity;
import com.example.project.adapter.AdapterHistory;
import com.example.project.adapter.AdapterRecently;
import com.example.project.model.Subject;
import com.example.project.service.ProcessBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements OnClickListener {

    RecyclerView homeRecycler;
    RecyclerView homeFavourites;

    List<Subject> listMusic;
    View view;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        Bundle bundle = getArguments();
        listMusic  = (ArrayList<Subject>) bundle.getSerializable("listMusic");
        try {
            this.init();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return view;
    }
    public void init() throws JSONException {
        homeRecycler = view.findViewById(R.id.homeRecycler);
        homeFavourites = view.findViewById(R.id.homeFavourites);
        System.out.println(R.id.linearLayout);
        setAdapterRecently();
        setAdapterHistory();
    }

    public void setAdapterRecently() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        homeRecycler.setLayoutManager(layoutManager);
        AdapterRecently adapterRecently = new AdapterRecently();
        ArrayList<Subject> subjects =(ArrayList<Subject>) listMusic;
        int count = subjects.size();
        if(count > 10 )

        adapterRecently.setData(listMusic,this);
        homeRecycler.setAdapter(adapterRecently);
    }
    public void setAdapterHistory() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        homeFavourites.setLayoutManager(gridLayoutManager);
        AdapterHistory adapterFavourites = new AdapterHistory();
        adapterFavourites.setData(listMusic,this);
        homeFavourites.setAdapter(adapterFavourites);
    }


    @Override
    public void clickItem() {

    }

    @Override
    public void setData(ArrayList<Subject> arr) {

    }

    @Override
    public void playSong(Subject tmp, List<Subject> listSubject,int position) throws JSONException {
        Intent intent = new Intent(getActivity(), PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("subjectList", (ArrayList<? extends Parcelable>) listSubject);
        bundle.putString("nameAstist", tmp.getArtist());
        bundle.putString("title", tmp.getName());
        bundle.putString("img", tmp.getSrc());
        bundle.putString("url", tmp.getUrl());
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivity(intent);
        ProcessBar.setURL(getContext(), tmp.getUrl(),tmp.getId());
        Toast.makeText(this.getContext(), "clickITem", Toast.LENGTH_SHORT).show();
    }
}