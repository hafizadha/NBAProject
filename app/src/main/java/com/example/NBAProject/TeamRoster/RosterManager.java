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
    private final int MAX_PLAYERS = 15;
    private ArrayList<PlayerInfo> roster;
    private Stack<PlayerInfo> injuryReserve;
    private PriorityQueue<PlayerInfo> contractExtensionQueue;
    private int currentNumberOfPlayers; // Variable to store the number of players fetched from Firebase

    private static RosterManager instance;

    FirebaseDatabase database;

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

    public boolean addPlayer(PlayerInfo player) {
        if (!contains(player) && salaryPass(player.getSalary()) && !isFull()) {
            roster.add(player);
            balance -= player.getSalary();
            currentNumberOfPlayers ++;

            saveRoster(); // Save updated roster to Firebase
            saveCurrentSalary();
            saveCurrentPlayers();
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(PlayerInfo player) {
        return roster.contains(player);
    }

    public boolean inInjury(PlayerInfo player) {
        return injuryReserve.contains(player);
    }

    public boolean inContract(PlayerInfo player) {
        return contractExtensionQueue.contains(player);
    }

    public boolean isFull() {
        return currentNumberOfPlayers == MAX_PLAYERS;
    }

    public boolean salaryPass(long salary) {
        return salary <= balance;
    }







    public void removePlayerFromRoster(PlayerInfo player){
        if(!roster.isEmpty()){
            roster.remove(player);
            currentNumberOfPlayers--;
            balance += player.getSalary();
            saveRoster(); // Save updated roster to Firebase
            saveCurrentSalary();
            saveCurrentPlayers();
        }
    }



    public boolean checkExistInjury(PlayerInfo player){
        return injuryReserve.contains(player);
    }
    public void addToInjuryReserve(PlayerInfo player, String injury) {
        if(!injuryReserve.contains(player)) {
            player.setInjuryDescription(injury);
            injuryReserve.push(player);
            saveInjuryReserve(injuryReserve.peek(), true); // Save updated injury reserve to Firebase
        }
    }

    public void removeFromInjuryReserve(PlayerInfo player) {
            saveInjuryReserve(player,false); // Save updated injury reserve to Firebase

    }


    public void addToContractExtensionQueue(PlayerInfo player) {
        contractExtensionQueue.offer(player);
        saveContractExtensionQueue(player,true);
    }

    public void removeFromContractExtensionQueue() {
        if (!contractExtensionQueue.isEmpty()) {
            PlayerInfo player = contractExtensionQueue.remove();
            saveContractExtensionQueue(player,false);
        }
    }

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

    public void importContract(){
        DatabaseReference contractQueue = FirebaseDatabase.getInstance().getReference("contractQueue");
        contractQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contractExtensionQueue.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("HAFIZ","APA NI" + dataSnapshot);
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        contractExtensionQueue.add(data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    public void saveInjuryReserve(PlayerInfo player, boolean add) {
        DatabaseReference injury = FirebaseDatabase.getInstance().getReference("injuryReserve");
        String playername = sanitizePlayerName(player.getName());
        player.setTimestamp(System.currentTimeMillis());
        if(add){
            injury.child(playername).setValue(player);
        }
        else {
            injury.child(playername).removeValue();
        }
    }

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

    public void getBalanceFromDatabase() {
        DatabaseReference ref = database.getReference("currentSalary");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    balance = dataSnapshot.getValue(Long.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error getting data: " + databaseError.getMessage());
            }
        });
    }

    private void saveCurrentSalary() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("currentSalary");
        rosterRef.setValue(balance);
    }

    public int getCurrentPlayersfrom(){
        return currentNumberOfPlayers;
    }

    private void saveCurrentPlayers() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("NoOfPlayers");
        rosterRef.setValue(roster.size());
    }

    //Firebase can't receive some special characters when trying to read/write, thus names need to be replaced
    public String sanitizePlayerName(String playerName) {
        return playerName.replaceAll("[.$\\[\\]#\\/]", "_");
    }
}
