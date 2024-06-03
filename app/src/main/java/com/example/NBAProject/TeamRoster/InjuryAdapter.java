package com.example.NBAProject.TeamRoster;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.NBAProject.R;

import java.util.ArrayList;
import java.util.Random;

public class InjuryAdapter extends RecyclerView.Adapter<InjuryAdapter.ViewHolder> {

    Context context;
    ArrayList<PlayerInfo> list;

    public InjuryAdapter(Context context, ArrayList<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.injuredplayers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerInfo data = list.get(position);
        holder.playerName.setText(data.getName());
        String imageURL = data.getPhoto();
        Glide.with(holder.itemView.getContext())
                .load(imageURL)
                .placeholder(R.drawable.player_dunking)
                .into(holder.profileImage);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                showInjuryDialog(holder, clickedPosition);
                return true; // Return true to indicate that the long click event is consumed
            }
        });

    }


    private void showInjuryDialog(ViewHolder holder,int position){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.injurypopupview, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        TextView typeOfInjury = dialogView.findViewById(R.id.typeInjury);

        String[] injuries = {"Sprained Ankle", "Torn ACL", "Concussion", "Fractured Finger", "Strained Hamstring"};

        Random rand = new Random();
        int randomIndex = rand.nextInt(injuries.length);
        String randomInjury = injuries[randomIndex];

        typeOfInjury.setText("The player has suffered from " + randomInjury + " and will not be able to play until fully healed");


        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView playerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            playerName = itemView.findViewById(R.id.playerNameTV);
        }
    }
}
