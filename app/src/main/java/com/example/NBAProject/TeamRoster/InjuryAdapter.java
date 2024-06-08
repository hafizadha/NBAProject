package com.example.NBAProject.TeamRoster;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;

import java.util.Random;
import java.util.Stack;

public class InjuryAdapter extends RecyclerView.Adapter<InjuryAdapter.ViewHolder> {

    Context context;
    Stack<PlayerInfo> list;

    public InjuryAdapter(Context context, Stack<PlayerInfo> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
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
        holder.orderno.setText(String.valueOf(position+1));
        holder.injurydesc.setText(data.getInjuryDescription());

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
        TextView playerName,injurydesc,orderno;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderno = itemView.findViewById(R.id.orderNumber);
            playerName = itemView.findViewById(R.id.playerNameTV);
            injurydesc = itemView.findViewById(R.id.injurydesc);
        }
    }
}
