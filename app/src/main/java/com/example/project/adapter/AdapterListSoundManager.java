package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.event.OnClickListener;
import com.example.project.model.Subject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class AdapterListSoundManager extends RecyclerView.Adapter<AdapterListSoundManager.Sound> {
    private List<Subject> mData;
    private OnClickListener onClick;
    @Override
    public Sound onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sound_maneger, parent, false);
        Sound viewSound = new Sound(view);
        System.out.println(view.toString() + "sdsdsdsdsdsdsd");
        return viewSound;
    }
    public void setData(List<Subject> data, OnClickListener o) {
        this.onClick=o;
        this.mData = data;
    }
    @Override
    public void onBindViewHolder( Sound holder, int position) {
        Subject tmp=mData.get(position);
        holder.nameMusic.setText(tmp.getName());
        System.out.println(tmp.getName());
        holder.nameAstist.setText(tmp.getArtist());
        System.out.println(1212);
        Picasso.get().load(tmp.getSrc()).into(holder.avatar);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onClick.playSong(tmp, mData,holder.getAdapterPosition());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class Sound extends RecyclerView.ViewHolder {
        public TextView nameMusic;
        public TextView nameAstist;
        public ImageView avatar;
        public LinearLayout linearLayout;

        public Sound(View itemView) {
            super(itemView);
            nameMusic = itemView.findViewById(R.id.name_item_sound);
            nameAstist = itemView.findViewById(R.id.astist_item_sound);
            avatar = itemView.findViewById(R.id.img_item_sound);
            linearLayout = itemView.findViewById(R.id.container_item_sound);

        }
    }
}
