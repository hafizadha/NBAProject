package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.PriorityQueue;

public class ContractQueue extends Fragment {
    View view;
    RosterManager rosterManager;
    RecyclerView recyclerView;
    Button renew;
    ContractAdapter contractAdapter;
    PriorityQueue<PlayerInfo> contractList;
    ArrayList<PlayerInfo> contractArray;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contractqueue,container,false);

        rosterManager = RosterManager.getInstance();

        context=getContext();


        contractList = new PriorityQueue<>();

        contractList = rosterManager.getContractPlayers();
        Log.d("TEST","NULL X" + contractList.isEmpty());

        contractArray = new ArrayList<>(contractList);

        for(PlayerInfo p: contractArray){
            Log.d("DEDE","NAME: " + p.getPoints());
        }

        recyclerView = view.findViewById(R.id.contractQueue);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        contractAdapter = new ContractAdapter(context,contractArray);
        recyclerView.setAdapter(contractAdapter);

        renew = view.findViewById(R.id.renewButton);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!contractList.isEmpty()) {
                    // Remove the top player from the local injury list
                    PlayerInfo data = contractList.remove();
                    contractArray.remove(data);
                    // Remove the player from the injury reserve stack and add them back to the roster
                    rosterManager.saveContractExtensionQueue(data,false);
                    contractAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }
}
