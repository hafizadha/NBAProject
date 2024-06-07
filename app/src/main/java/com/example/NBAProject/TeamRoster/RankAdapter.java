package com.example.NBAProject.TeamRoster;

import android.content.Context;
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

//Shows the ranking of Players based on their composite score
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

        //Setting values for components in the layout of each player card
        holder.score.setText(String.format("%s", playerInfo.getCompositeScore()));
        holder.name.setText(playerInfo.getName());

        String imageURL = playerInfo.getPhoto();

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

        //Components inside the layout
        //Only relevant information are to be displayed.
        ImageView profileImg;
        TextView name,score;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //References from the layout
            profileImg = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.playerNameTV);
            score = itemView.findViewById(R.id.scoreTV);
        }


    }

}