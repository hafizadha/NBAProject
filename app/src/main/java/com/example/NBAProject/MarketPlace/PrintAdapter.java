package com.example.NBAProject.MarketPlace;

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
import com.example.NBAProject.TeamRoster.PlayerInfo;

import java.util.List;

public class PrintAdapter extends RecyclerView.Adapter<PrintAdapter.MyViewHolder> {
    private List<? extends PlayerInfo> list;

    public PrintAdapter(List<? extends PlayerInfo> dataList) {
        this.list = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PrintAdapter.MyViewHolder holder, int position) {
        PlayerInfo playerInfo = list.get(position);

        // Setting Age as text
        holder.age.setText(String.format("Age:%s", playerInfo.getAge()));
        holder.points.setText(String.format("Points:%s", playerInfo.getPoints()));
        holder.assist.setText(String.format("Assist:%s", playerInfo.getAssist()));
        holder.steal.setText("Steal:" + String.valueOf(playerInfo.getSteal()));
        holder.weight.setText(String.format("Weight:%s", playerInfo.getWeight()));
        holder.height.setText(String.format("Height:%s", playerInfo.getHeight()));
        holder.block.setText(String.format("Block:%s", playerInfo.getBlock()));
        holder.reb.setText(String.format("Rebound:%s", playerInfo.getRebound()));
        holder.salary.setText(String.format("Salary:%s", playerInfo.getSalary()));

        holder.name.setText(playerInfo.getName());
        holder.pos.setText("Position:" + playerInfo.getPOS());
        String imageURL = playerInfo.getPhoto();
        Log.d("Image URL", "URL: " + imageURL);

        Glide.with(holder.itemView.getContext())
                .load(imageURL)
                .placeholder(R.drawable.player_dunking) // Optional placeholder
                .into(holder.profileImg);



        if (imageURL != null && !imageURL.isEmpty()) {

        } else {
            holder.profileImg.setImageResource(R.drawable.player_dunking); // Default placeholder
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImg;
        TextView name,age,assist,height,pos,points,reb,salary,steal,weight,block;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.playerNameTV);
            age= itemView.findViewById(R.id.AgeTV);
            assist = itemView.findViewById(R.id.AssistsTV);
            height = itemView.findViewById(R.id.HeightTV);
            reb = itemView.findViewById(R.id.ReboundsTV);
            pos = itemView.findViewById(R.id.PositionTV);
            points = itemView.findViewById(R.id.PointsTV);
            salary = itemView.findViewById(R.id.SalaryTV);
            steal = itemView.findViewById(R.id.StealsTV);
            weight = itemView.findViewById(R.id.WeightTV);
            block = itemView.findViewById(R.id.BlocksTV);

        }


    }

}
