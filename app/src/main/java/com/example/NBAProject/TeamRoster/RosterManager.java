package com.example.NBAProject.TeamRoster;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class RosterManager {
    private long currentSalary = 0;
    private final int MAX_SALARY = 20000;
    private final int MAX_PLAYERS = 15;
    private ArrayList<PlayerInfo> roster;
    private Stack<PlayerInfo> injuryReserve;
    private Queue<PlayerInfo> contractExtensionQueue;
    private String teamName;
    private String teamCity;
    private int salaryCap;
    private int currentNumberOfPlayers; // Variable to store the number of players fetched from Firebase

    private static RosterManager instance;

    DatabaseReference database;

    public RosterManager(String teamName, String teamCity, int salaryCap) {
        this.teamName = teamName;
        this.teamCity = teamCity;
        this.salaryCap = salaryCap;
        this.roster = new ArrayList<>();
        this.injuryReserve = new Stack<>();
        this.contractExtensionQueue = new LinkedList<>();

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference();

        // Fetch current number of players and current salary from Firebase
        //fetchCurrentNumberOfPlayersFromFirebase();
        //fetchCurrentSalaryFromFirebase();
    }

    public void setCurrentSalary(Long currentSalary){
        this.currentSalary = currentSalary;
    }

    public static RosterManager getInstance() {
        if (instance == null) {
            instance = new RosterManager("Default Team", "Default City", 20000);
        }
        return instance;
    }

    public static void initializeInstance(String teamName, String teamCity, int salaryCap) {
        if (instance == null) {
            instance = new RosterManager(teamName, teamCity, salaryCap);
        }
    }

    public boolean addPlayer(PlayerInfo player) {
        if (!contains(player) && salaryPass(player.getSalary()) && !isFull()) {
            roster.add(player);
            currentSalary += player.getSalary();
            saveRoster(); // Save updated roster to Firebase
            saveCurrentSalary();
            saveCurrentPlayers();
            return true;
        } else {
            return false;
        }
    }

    public boolean addPlayers(List<PlayerInfo> players) {
        for (PlayerInfo player : players) {
            if (!addPlayer(player)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(PlayerInfo player) {
        return roster.contains(player);
    }

    public boolean isFull() {
        return currentNumberOfPlayers == MAX_PLAYERS;
    }

    public boolean salaryPass(long salary) {
        return (currentSalary + salary) <= MAX_SALARY;
    }

    public void checkPositionalRequirement() {
        int guards = 0, forwards = 0, centers = 0;
        for (PlayerInfo player : this.roster) {
            String position = player.getPOS();
            switch (position) {
                case "Guard":
                    guards++;
                    break;
                case "Forward":
                    forwards++;
                    break;
                case "Center":
                    centers++;
                    break;
                default:
                    break;
            }
        }

        if (guards >= 2)
            System.out.println("GUARD: " + guards);
        else
            System.out.println("MUST ADD " + (2 - guards) + " MORE GUARD!");

        if (forwards >= 2)
            System.out.println("FORWARD: " + forwards);
        else
            System.out.println("MUST ADD " + (2 - forwards) + " MORE FORWARD!");

        if (centers >= 2)
            System.out.println("CENTER: " + centers);
        else
            System.out.println("MUST ADD " + (2 - centers) + " MORE CENTER!");
    }

    public boolean removePlayer(String playerName) {
        PlayerInfo toRemove = null;
        for (PlayerInfo player : this.roster) {
            if (player.getName().equals(playerName)) {
                toRemove = player;
                break;
            }
        }

        if (toRemove == null) {
            System.out.println(playerName + " not found in the team roster.");
            return false;
        }

        // Log the roster before removal
        System.out.println("Roster before removal:");
        displayRoster();

        // Update the currentSalary before removing the player
        currentSalary -= toRemove.getSalary();

        this.roster.remove(toRemove);
        System.out.println("Successfully removed " + playerName + " from team roster.");

        // Log the roster after removal
        System.out.println("Roster after removal:");
        displayRoster();

        System.out.println("Current salary:" + currentSalary);

        return true;
    }

    public void removePlayerFromRoster(PlayerInfo player){
        if(!roster.isEmpty()){
            roster.remove(player);
            currentSalary -= player.getSalary();
            saveRoster(); // Save updated roster to Firebase
            saveCurrentSalary();
            saveCurrentPlayers();
        }
    }

    public boolean removeLastPlayer() {
        if (!roster.isEmpty()) {
            PlayerInfo toRemove = roster.get(roster.size() - 1);
            roster.remove(roster.size() - 1);
            currentSalary -= toRemove.getSalary();
            System.out.println("Successfully removed " + toRemove.getName() + " from team roster.");
            return true;
        } else {
            System.out.println("The roster is empty. No player to remove.");
            return false;
        }
    }

    private boolean isTeamValid() {
        int guards = 0, forwards = 0, centers = 0;
        for (PlayerInfo player : this.roster) {
            String position = player.getPOS();
            switch (position) {
                case "Guard":
                    guards++;
                    break;
                case "Forward":
                    forwards++;
                    break;
                case "Center":
                    centers++;
                    break;
                default:
                    break;
            }
        }
        return guards >= 2 && forwards >= 2 && centers >= 2;
    }

    public void addToInjuryReserve(PlayerInfo player, String injury) {
        player.setInjuryDescription(injury);
        injuryReserve.push(player);
        System.out.println("Player: " + player.getName() + " added to Injury Reserve with Injury: " + injury);
        saveInjuryReserve(); // Save updated injury reserve to Firebase
    }

    public void removeFromInjuryReserve(PlayerInfo player) {
        if (!injuryReserve.isEmpty()) {
            injuryReserve.pop();
            System.out.println("Player: " + player.getName() + " removed from Injury Reserve.");
            saveInjuryReserve(); // Save updated injury reserve to Firebase
        } else {
            System.out.println(player.getName() + " is not ready to be removed from Injury Reserve.");
        }
    }

    public void displayInjuredPlayers() {
        if (injuryReserve.isEmpty()) {
            System.out.println("Injury Reserve is empty.");
        } else {
            System.out.println("Injured Players:");
            for (PlayerInfo player : injuryReserve) {
                System.out.println(player.getName() + " - Injury: " + player.getInjuryDescription());
            }
        }
    }

    public void displayRoster() {
        if (roster.isEmpty()) {
            System.out.println("Team Roster is empty.");
        } else {
            System.out.println("THE OFFICIAL ROSTER:");
            for (PlayerInfo player : roster) {
                System.out.println(player.getName() + " - " + player.getPOS() + " - $" + player.getSalary());
            }
        }
    }

    public void addToContractExtensionQueue(PlayerInfo player) {
        contractExtensionQueue.offer(player);
        System.out.println("Player: " + player.getName() + " added to Contract Extension Queue.");
        saveContractExtensionQueue();
    }

    public void removeFromContractExtensionQueue(PlayerInfo player) {
        if (!contractExtensionQueue.isEmpty()) {
            contractExtensionQueue.poll();
            System.out.println("Player: " + player.getName() + " removed from Contract Extension Queue.");
            saveContractExtensionQueue();
        } else {
            System.out.println("Contract extension queue is empty or " + player.getName() + " is not at the top of the queue.");
        }
    }

    public ArrayList<PlayerInfo> getRoster() {
        return this.roster;
    }

    public Stack<PlayerInfo> getInjuryReserve() {
        return this.injuryReserve;
    }

    public Queue<PlayerInfo> getContractPlayers() {
        return this.contractExtensionQueue;
    }

    public Long getCurrentSalary() {
        return this.currentSalary;
    }

    private void saveRoster() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("roster");

        // Loop through each player in the roster
        for (PlayerInfo player : roster) {
            String playerName = player.getName(); // Get the playerName
            rosterRef.child(playerName).setValue(player); // Use playerName as the key
        }
    }

    private void saveInjuryReserve() {
        DatabaseReference injury = FirebaseDatabase.getInstance().getReference("injuryReserve");

        // Loop through each player in the injury reserve
        for (PlayerInfo player : injuryReserve) {
            String playerName = player.getName(); // Get the playerName
            injury.child(playerName).setValue(player); // Use playerName as the key
        }
    }

    private void saveContractExtensionQueue() {
        DatabaseReference contract = FirebaseDatabase.getInstance().getReference("contractQueue");

        // Loop through each player in the contract extension queue
        for (PlayerInfo player : contractExtensionQueue) {
            String playerName = player.getName(); // Get the playerName
            contract.child(playerName).setValue(player); // Use playerName as the key
        }
    }

    private void saveCurrentSalary() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("currentSalary");
        rosterRef.setValue(currentSalary);
    }

    private void saveCurrentPlayers() {
        DatabaseReference rosterRef = FirebaseDatabase.getInstance().getReference("NoOfPlayers");
        rosterRef.setValue(roster.size());
    }

    public List<PlayerInfo> compilePlayers(){
        List<PlayerInfo> allplayers = new ArrayList<>();
        allplayers.addAll(roster);

        allplayers.addAll(injuryReserve);
        allplayers.addAll(contractExtensionQueue);


        return allplayers;
    }



}
