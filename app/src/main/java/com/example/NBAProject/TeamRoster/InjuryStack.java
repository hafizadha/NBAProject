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

import java.util.Stack;

public class InjuryStack extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private InjuryAdapter injuryAdapter;
    private Stack<PlayerInfo> injuryList;
    private RosterManager rosterManager;
    private Context context;
    Button remove;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.injurystack,container,false);

        // Get the singleton instance of RosterManager
        rosterManager = RosterManager.getInstance();

        context = getContext();

        injuryList = rosterManager.getInjuryReserve();



        recyclerView = view.findViewById(R.id.injuryStack);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 3 columns
        injuryAdapter = new InjuryAdapter(getContext(), injuryList);
        recyclerView.setAdapter(injuryAdapter);



        remove = view.findViewById(R.id.removeInjuredPlayerButton);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!injuryList.isEmpty()) {
                    // Remove the top player from the local injury list
                    PlayerInfo data = injuryList.pop();
                    // Remove the player from the injury reserve stack and add them back to the roster
                    rosterManager.saveInjuryReserve(data,false);
                    injuryAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }


}


