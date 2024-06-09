package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;
import com.example.NBAProject.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class ContractQueue extends Fragment {
    //COmponents of the layout
    View view;
    RosterManager rosterManager;
    RecyclerView recyclerView;
    Button renew;
    ContractAdapter contractAdapter;

    //Collection of PlayerInfo object (PriorityQueue to showcase mechanism, ArrayList to display into the UI)
    PriorityQueue<PlayerInfo> contractList;
    ArrayList<PlayerInfo> contractArray;

    Context context; //to get app resources

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contractqueue,container,false); //Inflates layout

        //Get the rosterManager instance
        rosterManager = RosterManager.getInstance();

        context=getContext();

        contractList = new PriorityQueue<>();
        contractList = rosterManager.getContractPlayers(); //Get the priorityQueue of player's contract from the instance

        contractArray = new ArrayList<>(contractList);//instantiate new ArrayList into contractArray by copying the contents of the existing Queue


        //Setting recycler view with adapter to display the list of Players
        recyclerView = view.findViewById(R.id.contractPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        contractAdapter = new ContractAdapter(context,contractArray);
        recyclerView.setAdapter(contractAdapter);

        int verticalSpace = 60; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);


        //When renew button is Clicked
        renew = view.findViewById(R.id.renewButton);
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!contractList.isEmpty()) {
                    // Remove player from the queue based on the priority (higher points)
                    PlayerInfo data = contractList.remove();
                    contractArray.remove(data); //
                    // Remove the player from the injury reserve stack and add them back to the roster
                    rosterManager.saveContractExtensionQueue(data,false);
                    contractAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }
}
