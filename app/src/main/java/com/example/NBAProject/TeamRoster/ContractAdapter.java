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

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder>  {
    Context context;
    ArrayList<PlayerInfo> list;

    public ContractAdapter(Context context, ArrayList<PlayerInfo> queue) {
        this.list = queue;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contract_players, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerInfo data = list.get(position);
        holder.playerName.setText(data.getName());
        holder.score.setText(data.getPoints());
        String imageURL = data.getPhoto();
        Glide.with(holder.itemView.getContext())
                .load(imageURL)
                .placeholder(R.drawable.player_dunking)
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView playerName,score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            playerName = itemView.findViewById(R.id.playerNameTV);
            score = itemView.findViewById(R.id.pointsTV);
        }
    }
}