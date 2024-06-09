package com.example.NBAProject.TeamRoster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;
import com.example.NBAProject.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class PerformanceRanking extends Fragment {
    View view;
    RecyclerView recyclerView;
    RankAdapter adapter;
    private ArrayList<PlayerInfo> players = new ArrayList<>();
    private double[] weights;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rankpage,container,false);


        //Receive an ArrayList of players passed from the Roster class
        Bundle bundle = getArguments();
        if (bundle != null) {
            players = bundle.getParcelableArrayList("playerlist");
        }

        //Method to rank the players based on their composite score ( sorting )
        rankPlayers();

        //From the previous method call, the arraylist of players are sorted and then passed to the Adapter to display them
        recyclerView = view.findViewById(R.id.rankAdapter);// 3 columns
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RankAdapter(getContext(),players);
        recyclerView.setAdapter(adapter);

        int verticalSpace = 60; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);

        return view;
    }


    //Method to sort players based on their composite score via merge sort
    public void rankPlayers() {
        mergeSort(players, 0, players.size() - 1); //send arraylist ands its lowest index as left and highest index as right
        // Print the ranked players in Logcat
        for (int i = 0; i < players.size(); i++) {
            PlayerInfo player = players.get(i);
            double compositeScore = Math.round(calculateCompositeScore(player));

            player.setCompositeScore(compositeScore);

        }
    }

    //This method split the list into halves and sort them
    private void mergeSort(List<PlayerInfo> players, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;//Get the middle index of the list

            //Recursive call
            mergeSort(players, left, middle); // sort the first half
            mergeSort(players, middle + 1, right); // sort the second half
            merge(players, left, middle, right);
        }
    }

    //This method is where the players are sorted based on their composite score
    private void merge(List<PlayerInfo> players, int left, int middle, int right) {
        //Initializing the sizes of first and second half
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        //Create new list for both first and second half to flood with players inside the sublist
        List<PlayerInfo> leftList = new ArrayList<>();
        List<PlayerInfo> rightList = new ArrayList<>();

        //Adding player from first half into list
        for (int i = 0; i < leftSize; i++) {
            leftList.add(players.get(left + i));
        }
        //Adding player from second half into the list
        for (int i = 0; i < rightSize; i++) {
            rightList.add(players.get(middle + 1 + i)); //First player of second half starts right after index of middle
        }

        //Initialise indexes for loop
        int leftIndex = 0;
        int rightIndex = 0;
        int currentIndex = left;

        //The iteration of left and right list has not completed
        while (leftIndex < leftSize && rightIndex < rightSize) {
            //Calculate score for each
            double leftCompositeScore = calculateCompositeScore(leftList.get(leftIndex));
            double rightCompositeScore = calculateCompositeScore(rightList.get(rightIndex));
            //If left has greater score
            if (leftCompositeScore >= rightCompositeScore) {
                players.set(currentIndex, leftList.get(leftIndex)); //Set current index of the playerlist with the object taken by the index of the left list
                leftIndex++;//Move to the next index of left list
            } else {
                players.set(currentIndex, rightList.get(rightIndex));//Set current index of the playerlist with the object from right list with the index
                rightIndex++; //Move to the next index of rightlist
            }
            currentIndex++; //Move to the next index of playerList
        }
        //While iteration of left sublist has not completed yet
        while (leftIndex < leftSize) {
            players.set(currentIndex, leftList.get(leftIndex));
            leftIndex++;
            currentIndex++;
        }
        //While iteration of right sublist has not completed yet
        while (rightIndex < rightSize) {
            players.set(currentIndex, rightList.get(rightIndex));
            rightIndex++;
            currentIndex++;
        }
    }

    //Calculation of composite Score is done by taking account of some attributes and weight (depending on the role he plays)
    private double calculateCompositeScore(PlayerInfo player) {
        String positions = player.getPOS().toString();

        //These attributes are stored as String in the database, thus needed to parse into Double for calculations
        double points = Double.parseDouble(player.getPoints());
        double rebounds = Double.parseDouble(player.getRebound());
        double assists = Double.parseDouble(player.getAssist());
        double steals = Double.parseDouble(player.getSteal());
        double blocks = Double.parseDouble(player.getBlock());

        // Refer to the player's position, and assign weightage to evaluate performance
        // Refer to report, for more details on how each position is considered
        // Weightage
        if(positions.contains("G")){ //guards- heavier weightage on assists and steals
            weights = new double[]{0.1, 0.1, 0.35, 0.35, 0.1};
        }else if(positions.contains("F")){ //forward- balanced weightage
            weights = new double[]{0.2, 0.2, 0.2, 0.2, 0.2};
        }else if(positions.contains("C")){ //centre- heavier weightage on rebounds and blocks
            weights = new double[]{0.1, 0.35, 0.1, 0.1, 0.35};
        }else{
            System.out.println("Invalid position.");
            return 0.0;
        }

        //Score is calculated by totalling the product of each attributes and its respective weightage
        double compositeScore = (((points/(double) 31) * weights[0]) + ((rebounds/(double)12) * weights[1]) +
                ((assists/(double)7) * weights[2]) + ((steals/(double)2) * weights[3]) + ((blocks/(double)3)* weights[4])) * 100;

        return compositeScore;
    }
}
