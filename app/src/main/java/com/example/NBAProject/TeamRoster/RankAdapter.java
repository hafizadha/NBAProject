package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.NBAProject.R;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {
    Context context;
    ArrayList<PlayerInfo> list;

    public RankAdapter(Context context, ArrayList<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.MyViewHolder holder, int position) {
        PlayerInfo playerInfo = list.get(position);

        holder.score.setText(String.format("Score:%s", playerInfo.getCompositeScore()));
        holder.name.setText(playerInfo.getName());



        String imageURL = playerInfo.getPhoto();
        Log.d("Image URL", "URL: " + imageURL);

        Glide.with(holder.itemView.getContext())
                .load(imageURL)
                .placeholder(R.drawable.player_dunking) // Optional placeholder
                .into(holder.profileImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImg;
        TextView name, age,score;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.playerNameTV);
            score = itemView.findViewById(R.id.scoreTV);
        }


    }

}