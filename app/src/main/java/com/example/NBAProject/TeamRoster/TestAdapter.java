package com.example.NBAProject.TeamRoster;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.NBAProject.MarketPlace.RosterPrint;
import com.example.NBAProject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {
    //Adapter is an implementation to dynamically display a Collection of Objects into the Recycler view

    //This adapter (each item inside the Recycler view) have two modes,
    // where it will show different view of popup window when clicked.
    public static final int MODE_PLAYER_LIST = 1;
    public static final int MODE_ROSTER = 2;

    //Roster manager for adding and removing mechanism for the adapter
    RosterManager rosterManager;

    //Context are for accessing resources, inflating new layouts and using system services
    //Some services like LayoutInflator, AlertDialog, and Toast require context to function
    Context context;
    ArrayList<PlayerInfo> list;
    int mode; //To determine the mode of the Adapter


    //Constructor method
    public TestAdapter(Context context, ArrayList<PlayerInfo> list, int mode,RosterManager rosterManager) {
        this.context = context;
        this.list = list;
        this.mode = mode;
        this.rosterManager = rosterManager;
    }

    //Filtered retrieved from the input in the search bar ( in MarketPage )
    public void setFilteredList(ArrayList<PlayerInfo> filteredList){
        this.list = filteredList;//filteredList consists of Players with names that matches the input from the Searchbar
        notifyDataSetChanged(); //This method needs to be called when there's change in data in the Collection so that it update the UI
    }


    //Inflates the item layout (player cards) for view holder
    @NonNull
    @Override
    public TestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.player2,parent,false);
        return new MyViewHolder(v);
    }

    //Components inside the item layout can be adjusted here (settings text, images, etc.)
    @Override
    public void onBindViewHolder(@NonNull TestAdapter.MyViewHolder holder, int position) {
        PlayerInfo playerInfo = list.get(position);

        // Setting Age as text
        holder.age.setText(String.format("Age: %s", playerInfo.getAge()));
        holder.points.setText(String.format("Points: %s", playerInfo.getPoints()));
        holder.assist.setText(String.format("Assist: %s", playerInfo.getAssist()));
        holder.steal.setText("Steal:" + String.valueOf(playerInfo.getSteal()));
        holder.weight.setText(String.format("Weight: %s", playerInfo.getWeight()));
        holder.height.setText(String.format("Height: %s", playerInfo.getHeight()));
        holder.block.setText(String.format("Block: %s", playerInfo.getBlock()));
        holder.reb.setText(String.format("Rebound: %s", playerInfo.getRebound()));
        holder.salary.setText(String.format("Salary: %s", playerInfo.getSalary()));

        holder.name.setText(playerInfo.getName());
        holder.pos.setText("Position:" + playerInfo.getPOS());


        boolean inInjury = rosterManager.inInjury(playerInfo);
        boolean inContract = rosterManager.inContract(playerInfo);


        if(mode == MODE_ROSTER) {
            if (inInjury && inContract) {
                holder.icon.setImageResource(R.drawable.medicaliconsm);
                holder.icon2.setImageResource(R.drawable.contracticonsmall);
            } else if (inInjury) {
                holder.icon.setImageResource(R.drawable.medicaliconsm);
            } else if (rosterManager.getContractPlayers().contains(playerInfo)) {
                holder.icon.setImageResource(R.drawable.contracticonsmall);
            }
        }


        String imageURL = playerInfo.getPhoto();
        Log.d("Image URL", "URL: " + imageURL);

        Glide.with(holder.itemView.getContext())
                .load(imageURL)
                .placeholder(R.drawable.player_dunking) // Optional placeholder
                .into(holder.profileImg);


        //Show popup dialog window when the itemView is clicked based on the mode
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

    //This popup shows when clicking the player cards in MarketPage
    private void showPlayerListDialog(MyViewHolder holder, int position) {
        //Inflates the popout layout and then set AlertDialog with the layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.popupview, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundBlurRadius(2);
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setLayout(850, 900);
        });
        dialog.show();

        //Get the current balance from rosterManager, and use it to set the text View ( shows balance to front end)
        Long balance = rosterManager.getBalance();
        TextView showbalance = dialogView.findViewById(R.id.showbalance);
        showbalance.setText("Balance: " + balance);

        TextView displayplayer = dialogView.findViewById(R.id.getwho);

        displayplayer.setText("Get " + list.get(position).getName() + "?");



        Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        //When confirm button is clicked:
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerInfo data = list.get(position);
                boolean isFull = rosterManager.isFull();
                boolean sufficient = rosterManager.salaryPass(data.getSalary());//Compares selected player's salary with the current balance
                boolean added;
                if(sufficient && ! isFull){ //If the player salary is less than or equal to // the balance
                    added = RosterManager.getInstance().addPlayer(data);//Return true if added into the roster

                    if(added){
                        //Remove the selected player in
                        list.remove(position);
                        notifyDataSetChanged(); //Updates to RecyclerView

                        FragmentActivity activity = (FragmentActivity) view.getContext();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        //Show message if added to roster
                        Toast.makeText(context,data.getName() + " added to roster",Toast.LENGTH_SHORT).show();
                        // Create a new instance of the new fragment with the updated list
                        RosterPrint fragment = RosterPrint.newInstance(list);

                        // Perform the fragment transaction
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(
                                R.anim.slideup,  // enter animation
                                R.anim.slidedown,
                                R.anim.slidedown,  // enter animation
                                R.anim.slideup
                        );
                        fragmentTransaction.replace(R.id.container, fragment);

                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    else{ //Not added means the player is already in roster
                        Toast.makeText(context,"Player is already in your team",Toast.LENGTH_SHORT).show();}
                }
                else{ //If insufficient balance, display error messages
                    if(!sufficient){
                        Toast.makeText(context,"Insufficient balance",Toast.LENGTH_SHORT).show();
                    }else{//Else, display that the team is full
                        Toast.makeText(context,"Team is full",Toast.LENGTH_SHORT).show();
                    }

                }//Quit the popup view
                dialog.dismiss();
            }
        });


        dialog.show();//Show popup window
    }


    //This popup shows when clicking the player cards in Roster
    private void showRosterDialog(MyViewHolder holder, int position) {
        //Create an alert dialog popup with the layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.rosterpopupview, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        //Setting custom popup layout size and background color to transparaent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setLayout(850, 900);
        });
        dialog.show();

        //Button references from layout
        Button remove = dialogView.findViewById(R.id.removePlayer);
        Button addInjury = dialogView.findViewById(R.id.addInjury);
        Button addContract = dialogView.findViewById(R.id.addQueue);

        // Get the data for the selected player
        PlayerInfo data = list.get(position);

        //Display selected player's name in this popup
        TextView PlayerName = dialogView.findViewById(R.id.selectedPlayer);
        PlayerName.setText(data.getName());

        //When 'Remove player' button is clicked:
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean inInjury = rosterManager.getInjuryReserve().contains(data);
                boolean inContract = rosterManager.getContractPlayers().contains(data);
                if(inInjury){
                    Toast.makeText(context,"You must treat this player first",Toast.LENGTH_SHORT).show();
                }else if(inContract){
                    Toast.makeText(context,"Solve this player's contract first",Toast.LENGTH_SHORT).show();
                }
                else {
                    //Get the specific PlayerInfo object that was clicked
                    PlayerInfo data = list.get(position);
                    String playerId = data.getName(); // Get the player ID
                    removePlayerFromFirebase1(playerId); // Pass the player ID to the method

                    //Remove from current roster and roster node in database
                    RosterManager.getInstance().removePlayerFromRoster(data);

                    //Remove from current Stack Reserve and injuryReserve node in database
                    RosterManager.getInstance().removeFromInjuryReserve(data);

                    //Remove from current Queue and contractQueue node in database
                    RosterManager.getInstance().removeFromContractExtensionQueue();

                    //Remove from the adapter and update the UI
                    list.remove(position); // Remove the item from the list
                    notifyDataSetChanged(); // Notify adapter about the item removal
                    dialog.dismiss();
                }

            }
        });

        //When add to Injury button is clicked:
        addInjury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlayerInfo data = list.get(position);
                boolean checkExist = RosterManager.getInstance().inInjury(data);
                Log.d("EXIST","EXIST" + checkExist);
                if(!checkExist) {
                    //Randomized scenarios of injury
                    String[] injuries = {"Sprained Ankle", "Torn ACL", "Concussion", "Fractured Finger", "Strained Hamstring"};
                    Random random = new Random();

                    // Generate a random index within the size of array
                    int randomIndex = random.nextInt(injuries.length);
                    String injury = injuries[randomIndex];
                    //Pass the parameters of player and his randomized injury to the method
                    RosterManager.getInstance().addToInjuryReserve(data, injury);
                    notifyDataSetChanged();
                    // Save the player's injury status
                }
                dialog.dismiss();
            }
        });

        //When add to contract button is clicked:
        addContract.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PlayerInfo data = list.get(position);

                //If player isn't in queue yet, add them
                boolean checkExist = rosterManager.inContract(data);
                if(!checkExist) {
                    RosterManager.getInstance().addToContractExtensionQueue(data);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });


        //Show the popup view
        dialog.show();
    }

    private void removePlayerFromFirebase1(String playerId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("roster");

        if (playerId != null) {
            String sanitizedPlayerId = sanitizePlayerName(playerId);
            myRef.child(sanitizedPlayerId).removeValue().addOnCompleteListener(task -> {
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

    // Sanitize player name to remove invalid characters for Firebase Database paths
    private String sanitizePlayerName(String playerName) {
        return playerName.replaceAll("[.$\\[\\]#\\/]", "_");
    }


    //this overriding method informs the RecyclerView how many items it needs to manage and display, influencing layout, scrolling behavior, and data binding
    @Override
    public int getItemCount() {
        return list.size();
    } //Usually, it gets the size of the data

    //This class holds references to the views that will display the data,
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //Components inside the player card's layout (player.xml)
        //These components must exist in the layout
        ImageView profileImg,icon,icon2;
        TextView name,age,assist,height,pos,points,reb,salary,steal,weight,block;


        //onCreateViewHolder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //The variables are initialised by reference IDS inside the layout

            //Imageview references from layout
            icon = itemView.findViewById(R.id.statusicon);
            icon2 = itemView.findViewById(R.id.statusicon2);

            //Textview references from layout
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
