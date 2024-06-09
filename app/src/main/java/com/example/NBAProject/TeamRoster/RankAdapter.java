package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;

import java.util.ArrayList;

//Shows the ranking of Players based on their composite score
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {
    Context context;
    boolean firstplace = true;
    boolean secondplace = true;
    boolean thirdplace = true;
    int rank = 1;
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
        holder.ranknumber.setText(String.valueOf(position + 1));



        //If first place, set gold medal
        if(position == 0 && firstplace){
            holder.rankicon.setImageDrawable(context.getDrawable(R.drawable.goldmedal));
            firstplace = false;
        }else if(position == 1&& secondplace){ //if second place, set silver medal
            holder.rankicon.setImageDrawable(context.getDrawable(R.drawable.silvermedal));
            secondplace = false;
        }else if(position == 2&& thirdplace){ //If third place, set bronze medal
            holder.rankicon.setImageDrawable(context.getDrawable(R.drawable.bronzemedal));
            thirdplace = false;
        }else { //otherwise, set nothing
            holder.rankicon.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //Components inside the layout
        //Only relevant information are to be displayed.
        ImageView rankicon;
        TextView name,score,ranknumber;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //References from the layout
            ranknumber = itemView.findViewById(R.id.rankNumber);
            name = itemView.findViewById(R.id.playerNameTV);
            score = itemView.findViewById(R.id.scoreTV);
            rankicon = itemView.findViewById(R.id.rankicon);
        }


    }

}