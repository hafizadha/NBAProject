package com.example.NBAProject.TeamRoster;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.NBAProject.R;
import com.example.NBAProject.MarketPlace.RosterPrint;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {

    public static final int MODE_PLAYER_LIST = 1;
    public static final int MODE_ROSTER = 2;


    RosterManager rosterManager;

    //OnItemListener onItemListener;
    Context context;
    ArrayList<PlayerInfo> list;
    int mode;


    public TestAdapter(Context context, ArrayList<PlayerInfo> list, int mode,RosterManager rosterManager) {
        this.context = context;
        this.list = list;
        this.mode = mode;
        this.rosterManager = rosterManager;
    }

    public void setFilteredList(ArrayList<PlayerInfo> filteredList){
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public TestAdapter(Context context, ArrayList<PlayerInfo> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
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
            if (mode == MODE_PLAYER_LIST) {
                showPlayerListDialog(holder, position);
            } else if (mode == MODE_ROSTER) {
                showRosterDialog(holder, position);
            }

        });

        if (imageURL != null && !imageURL.isEmpty()) {

        } else {
            holder.profileImg.setImageResource(R.drawable.player_dunking); // Default placeholder
        }
    }

    private void showPlayerListDialog(MyViewHolder holder, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.popupview, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        Long balance = rosterManager.getCurrentSalary();
        TextView showbalance = dialogView.findViewById(R.id.showbalance);
        showbalance.setText("Balance: " + balance);

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerInfo data = list.get(position);
                boolean added = RosterManager.getInstance().addPlayer(data);
                if(added){

                    list.remove(position);
                    notifyDataSetChanged();


                    FragmentActivity activity = (FragmentActivity) view.getContext();
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();

                    // Create a new instance of the new fragment with the updated list
                    RosterPrint fragment = RosterPrint.newInstance(list);

                    // Perform the fragment transaction
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    private void savePlayerToFirebase(PlayerInfo data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("roster"); // You can change "roster" to any branch name you want

        String playerId = myRef.push().getKey(); // Generate a unique key for each player
        data.setName(playerId);
        myRef.child(playerId).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Player info saved successfully.");
            } else {
                Log.d("Firebase", "Failed to save player info.");
            }
        });
    }


    private void saveCurrentSalaryToFirebase(Long newSalary) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference salaryRef = database.getReference("currentSalary");

        salaryRef.setValue(newSalary).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Current salary updated successfully.");
            } else {
                Log.d("Firebase", "Failed to update current salary.");
            }
        });
    }


    private void showRosterDialog(MyViewHolder holder, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.rosterpopupview, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        Button remove = dialogView.findViewById(R.id.removePlayerButton);
        Button addInjury = dialogView.findViewById(R.id.injuryButton);
        Button addContract = dialogView.findViewById(R.id.addQueue);

        // Get the data for the selected player
        PlayerInfo data = list.get(position);

        TextView PlayerName = dialogView.findViewById(R.id.playerNameTV);


        PlayerName.setText(data.getName());

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerInfo data = list.get(position);
                RosterManager.getInstance().removePlayerFromRoster(data);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        addInjury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerInfo data = list.get(position);
                RosterManager.getInstance().addToInjuryReserve(data, "Injury Description");

                // Save the player's injury status
                savePlayerInjuryStatus(data.getName(), true);

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        addContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerInfo data = list.get(position);
                RosterManager.getInstance().addToContractExtensionQueue(data);

                savePlayerContractStatus(data.getName(),true);

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });



        dialog.show();
    }


    private void removePlayerFromFirebase(String playerId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("roster");

        if (playerId != null) {
            myRef.child(playerId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Player info removed successfully.");
                    Toast.makeText(context, "Player removed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Firebase", "Failed to remove player info.");
                    Toast.makeText(context, "Failed to remove player.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void savePlayerInjuryStatus(String playerName, boolean isInjured) {
        // Use SharedPreferences or any other persistent storage mechanism to save the player's injury status
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerInjuries", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(playerName, isInjured);
        editor.apply();
    }

    private void savePlayerContractStatus(String playerName, boolean isContractEnd) {
        // Use SharedPreferences or any other persistent storage mechanism to save the player's injury status
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerContracts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(playerName, isContractEnd);
        editor.apply();
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

