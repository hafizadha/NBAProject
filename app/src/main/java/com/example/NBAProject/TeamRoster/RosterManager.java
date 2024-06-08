package com.example.NBAProject.TeamRoster;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

//This Java Class is responsible for most of the team management's
//mechanism includes importing data, updating database, removing and adding players.

public class RosterManager {
    private long balance = 20000;
    private final int MAX_PLAYERS = 15; //Maximum player in a roster
    private ArrayList<PlayerInfo> roster; //Roster
    private Stack<PlayerInfo> injuryReserve; //Stack
    private PriorityQueue<PlayerInfo> contractExtensionQueue; //Priority Queue
    private int currentNumberOfPlayers; // Variable to store the number of players fetched from Firebase
    private static RosterManager instance;
    FirebaseDatabase database; //Reference to database

    public RosterManager(int size) {
        this.roster = new ArrayList<>();
        this.injuryReserve = new Stack<>();
        this.contractExtensionQueue = new PriorityQueue<>();

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance();

        //Import players assigned to Injury reserve and Contract Queue into its respective collection
        importInjury();
        importContract();
        //Get the balance from the Database
        getBalanceFromDatabase();

        currentNumberOfPlayers = size;
        Log.d("APA NI","X FHM + " + currentNumberOfPlayers);
    }

    public static RosterManager getInstance(int size) {
        //For first time call (opening the app), the instance is null, thus a new RosterManager is instantiated
        if (instance == null) {
            instance = new RosterManager(size);
        }
        //For non-first time call, return the instance that have been created once.
        return instance;
    }

    // This method is an implementation of the Singleton pattern (only a single instance of a class exists throughout an application)
    // Any classes can have access to same instance (all Java class can retrieve the same instance), thus preventing any confusion in passing data.
    public static RosterManager getInstance() {
        return instance;
    }

    //Add player to roster
    public boolean addPlayer(PlayerInfo player) {
        //Fulfillment must be met before adding to roster (not full, sufficient balance and non-existing player)
        if (!contains(player) && salaryPass(player.getSalary()) && !isFull()) {
            roster.add(player);
            balance -= player.getSalary(); //Subtracting the balance with players salary
            currentNumberOfPlayers ++;

            saveRoster(); // Save updated roster to Firebase
            saveCurrentSalary(); //Save salary to database
            saveCurrentPlayers(); //Save player to database
            return true;
        } else {
            return false;
        }
        //Return true if successfully added,otherwise false
    }

    //Check if player exists in roster
    public boolean contains(PlayerInfo player) {
        return roster.contains(player);
    }

    //Check if players exists in injury
    public boolean inInjury(PlayerInfo player) {
        return injuryReserve.contains(player);
    }

    //Check if player exists in contract
    public boolean inContract(PlayerInfo player) {
        return contractExtensionQueue.contains(player);
    }

    //For the above methods, return true if player does exist

    //Check if the roster is full to prevent any addition of players
    public boolean isFull() {
        return currentNumberOfPlayers == MAX_PLAYERS;
    }

    //Check if the salary of selected player passes the current balance
    //Return true, if it is enough to buy the player
    public boolean salaryPass(long salary) {
        return salary <= balance;
    }


    //Remove player from current roster
    public void removePlayerFromRoster(PlayerInfo player){
        if(!roster.isEmpty()){
            roster.remove(player); //Remove the player
            currentNumberOfPlayers--;
            balance += player.getSalary(); //Increases balance when selling player
            saveRoster(); // Save updated roster to Firebase
            saveCurrentSalary(); //Save salary to database
            saveCurrentPlayers(); //Save total number of players into database
        }
    }



    public boolean checkExistInjury(PlayerInfo player){
        return injuryReserve.contains(player);
    }

    public boolean checkExistContract(PlayerInfo player){
        return contractExtensionQueue.contains(player);
    }

    //Add player to the injury reserve (
    public void addToInjuryReserve(PlayerInfo player, String injury) {
        //If injury doesn't contain the player
        if(!injuryReserve.contains(player)) {
            player.setInjuryDescription(injury); //Set injury description into PlayerInfo Object
            injuryReserve.push(player); //Push object to the top of the stack
            saveInjuryReserve(injuryReserve.peek(), true); // Save updated injury reserve to Firebase
        }
    }

    public void removeFromInjuryReserve(PlayerInfo player) {
            saveInjuryReserve(player,false); // Save updated injury reserve to Firebase
    }

    //Add player to Contract Queuee
    public void addToContractExtensionQueue(PlayerInfo player) {
        contractExtensionQueue.offer(player); //Inserts specifies element into the queue
        saveContractExtensionQueue(player,true);
    }

    //Remove player from Contract Queue
    public void removeFromContractExtensionQueue() {
        if (!contractExtensionQueue.isEmpty()) {
            PlayerInfo player = contractExtensionQueue.remove();
            saveContractExtensionQueue(player,false); //Updates database (remove player)
        }
    }

    //Getter methods
    public ArrayList<PlayerInfo> getRoster() {
        return this.roster;
    }
    public Stack<PlayerInfo> getInjuryReserve() {
        return this.injuryReserve;
    }
    public PriorityQueue<PlayerInfo> getContractPlayers() {
        return this.contractExtensionQueue;
    }
    public Long getBalance() {
        return this.balance;
    }

    private void saveRoster() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("roster");

        // Loop through each player in the roster
        for (PlayerInfo player : roster) {
            //Clean player's name before saving
            String playerName = sanitizePlayerName(player.getName());
            rosterRef.child(playerName).setValue(player); // Use playerName as the key
        }
    }


    public void importInjury(){
        //Refers to the injuryReserve node from Database
        DatabaseReference injury = FirebaseDatabase.getInstance().getReference("injuryReserve");

        //Special case for Stack:
        //By default, Firebase sorts its nodes (player's name) alphabetically which means that when importing from the
        //injuryReserve node, the new order may defers from the order it was inserted before
        //However, in a stack, the order of the element must not be neglected.
        //As a solution, PlayerInfo object are assigned with unique timestamp for the sole purpose of chronological sorting
        //PlayerInfo objects are inserted based on its timestamp order.
        injury.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                injuryReserve.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("HAFIZ","APA NI" + dataSnapshot);
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        injuryReserve.add(data);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Import contract queue players from the database
    public void importContract(){
        //Reference to contractQueue node
        DatabaseReference contractQueue = FirebaseDatabase.getInstance().getReference("contractQueue");
        contractQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contractExtensionQueue.clear();//Clear to prevent duplication
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        contractExtensionQueue.add(data); //Inserts player into the queue
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Updates the ContractQueue node ( boolean determines whether to add or remove)
    public void saveInjuryReserve(PlayerInfo player, boolean add) {
        DatabaseReference injury = FirebaseDatabase.getInstance().getReference("injuryReserve");
        String playername = sanitizePlayerName(player.getName());
        //Setting timestamp (further details in local importInjury method)
        player.setTimestamp(System.currentTimeMillis());
        //Add if true, otherwise remove
        if(add){
            injury.child(playername).setValue(player);
        }
        else {
            injury.child(playername).removeValue();
        }
    }

    //Updates the ContractQueue node ( boolean determines whether to add or remove)
    public void saveContractExtensionQueue(PlayerInfo player, boolean add) {
        DatabaseReference contract = FirebaseDatabase.getInstance().getReference("contractQueue");
        String playername = sanitizePlayerName(player.getName());
        if(add){
            contract.child(playername).setValue(player);
        }
        else{
            contract.child(playername).removeValue();
        }

    }

    //Retrieving the balance form the currentSalary node
    public void getBalanceFromDatabase() {
        //Reference to the node
        DatabaseReference ref = database.getReference("currentSalary");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Setting the value into the instance variable balance
                if (dataSnapshot.exists()) {
                    balance = dataSnapshot.getValue(Long.class);
                }
            }
            @Override //Display error if failed in importing
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error getting data: " + databaseError.getMessage());
            }
        });
    }

    //Save current salary into data base by getting the reference
    private void saveCurrentSalary() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("currentSalary");//Getting the reference node
        rosterRef.setValue(balance);//Setting value to it
    }

    public int getNumberOfPlayers(){
        return currentNumberOfPlayers;
    }

    //Get total inactive players (in stack or queue or both )
    public int getInactivePlayers(){
        int dupe = 0; //Duplicate player ( player in both injury and contract)
        for(PlayerInfo playerInfo: injuryReserve){
            for(PlayerInfo playerInfo1:contractExtensionQueue){
                if(playerInfo1.getName().equals(playerInfo.getName())){//If there exists the same player in both injury and stack
                    dupe++;
                }
            }
        }
        //Total size of injury reserve and contract queue minus by duplicated player
        return injuryReserve.size() + contractExtensionQueue.size() -dupe;
    }


    //Save the current total players into data
    private void saveCurrentPlayers() {
        //Referencing the NoOfPlayers node in databse
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("NoOfPlayers");
        rosterRef.setValue(roster.size());//Set vale into it
    }

    //Firebase can't receive some special characters when trying to read/write, thus names need to be replaced
    public String sanitizePlayerName(String playerName) {
        //Replace the specified characters into a valid one before saving
        return playerName.replaceAll("[.$\\[\\]#\\/]", "_");
    }
}
