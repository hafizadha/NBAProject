package com.example.NBAProject.TeamRoster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.NBAProject.R;

public class TeamManagement extends Fragment {

    RosterManager rosterManager;
    View view;
    TextView allP,incP,acP,freeAgent,onContract;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.teammanagement, container, false);

        LinearLayout linearLayout = view.findViewById(R.id.main);

        rosterManager = RosterManager.getInstance();

        allP = view.findViewById(R.id.allPlayers);
        acP = view.findViewById(R.id.activePlayers);
        incP = view.findViewById(R.id.inactivePlayers);
        freeAgent = view.findViewById(R.id.freeAgent);
        onContract = view.findViewById(R.id.onContractPlayers);


        int freeAgents = rosterManager.getContractPlayers().size();
        int sumIncPlayers = rosterManager.getInjuryReserve().size() + rosterManager.getContractPlayers().size();
        int sumAcPlayers = rosterManager.getRoster().size() - (rosterManager.getInjuryReserve().size() + rosterManager.getContractPlayers().size());
        int sumContractedPlayers = rosterManager.getRoster().size() - freeAgents;

        int noPlayers = rosterManager.getRoster().size();

        allP.setText(String.valueOf(noPlayers));
        acP.setText(String.valueOf(sumAcPlayers));
        incP.setText(String.valueOf(sumIncPlayers));
        freeAgent.setText(String.valueOf(freeAgents));
        onContract.setText(String.valueOf(sumContractedPlayers));

        return view;
    }


}
