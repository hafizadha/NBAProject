package com.example.NBAProject.TeamRoster;

import android.os.Bundle;
import android.util.Log;
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


    public void rankPlayers() {
        mergeSort(players, 0, players.size() - 1);
        // Print the ranked players in Logcat
        for (int i = 0; i < players.size(); i++) {
            PlayerInfo player = players.get(i);
            double compositeScore = Math.round(calculateCompositeScore(player));

            Log.d("TESR","RENAK" + compositeScore);
            player.setCompositeScore(compositeScore);
            Log.d("TEDT","Rank: "+ i );
            Log.d("RERE","Player: "+player.getName());
            Log.d("TESDT","Composite Score: "+player.getCompositeScore());
        }
    }

    private void mergeSort(List<PlayerInfo> players, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;
            mergeSort(players, left, middle);

            mergeSort(players, middle + 1, right);
            merge(players, left, middle, right);
        }
    }

    private void merge(List<PlayerInfo> players, int left, int middle, int right) {
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        List<PlayerInfo> leftList = new ArrayList<>();
        List<PlayerInfo> rightList = new ArrayList<>();

        for (int i = 0; i < leftSize; i++) {
            leftList.add(players.get(left + i));
        }
        for (int i = 0; i < rightSize; i++) {
            rightList.add(players.get(middle + 1 + i));
        }

        int leftIndex = 0;
        int rightIndex = 0;
        int currentIndex = left;

        while (leftIndex < leftSize && rightIndex < rightSize) {
            double leftCompositeScore = calculateCompositeScore(leftList.get(leftIndex));
            double rightCompositeScore = calculateCompositeScore(rightList.get(rightIndex));

            if (leftCompositeScore >= rightCompositeScore) { // here(?)
                players.set(currentIndex, leftList.get(leftIndex));
                leftIndex++;
            } else {
                players.set(currentIndex, rightList.get(rightIndex));
                rightIndex++;
            }
            currentIndex++;
        }

        while (leftIndex < leftSize) {
            players.set(currentIndex, leftList.get(leftIndex));
            leftIndex++;
            currentIndex++;
        }

        while (rightIndex < rightSize) {
            players.set(currentIndex, rightList.get(rightIndex));
            rightIndex++;
            currentIndex++;
        }
    }

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
        double compositeScore = (points * weights[0]) + (rebounds * weights[1]) +
                (assists * weights[2]) + (steals * weights[3]) + (blocks * weights[4]);

        return compositeScore;
    }
}
