package com.example.NBAProject;


import android.app.AlertDialog;
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

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {

    //OnItemListener onItemListener;
    Context context;
    ArrayList<PlayerInfo> list;
    public void setFilteredList(ArrayList<PlayerInfo> filteredList){
        this.list = filteredList;
    }

    public TestAdapter(Context context, ArrayList<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.player,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapter.MyViewHolder holder, int position) {
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

        holder.itemView.setOnClickListener(view -> {
            // Create and show the pop-up dialog
            View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_pop_up_view, null);
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create();

            dialog.show(); // Show the dialog
        });
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

