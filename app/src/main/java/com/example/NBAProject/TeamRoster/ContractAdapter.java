package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;

import java.util.ArrayList;

//Explanation of the Overriding methods in Adapters are in TestAdapter.java
public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder>  {
    Context context; //Get context to access resources
    ArrayList<PlayerInfo> list; //ArrayList of players in the queue

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
        holder.points.setText(data.getPoints());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerName,points;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.contractName);
            points = itemView.findViewById(R.id.PointsTV);
        }
    }
}
