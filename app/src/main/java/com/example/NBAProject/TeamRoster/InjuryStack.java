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

public class InjuryStack extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private InjuryAdapter injuryAdapter;
    private MyStack<PlayerInfo> injuryList;
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

        //Get the injury reserve stack from the instance
        injuryList = rosterManager.getInjuryReserve();


        //Setting recycler view with adapter to display the list of Players
        recyclerView = view.findViewById(R.id.injuredplayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 3 columns
        injuryAdapter = new InjuryAdapter(getContext(), injuryList);
        recyclerView.setAdapter(injuryAdapter);

        int verticalSpace = 60; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);

        //When remove button is clicked:
        remove = view.findViewById(R.id.treatplayers);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!injuryList.isEmpty()) {
                    // Remove the top player from the local injury list if its not empty
                    PlayerInfo data = injuryList.pop(); //Stack implementation
                    // Remove the player from the injury reserve stack and add them back to the roster
                    rosterManager.saveInjuryReserve(data,false); //updates database (false indicating that it should be removed)
                    injuryAdapter.notifyDataSetChanged(); //Notify the adapter so it can update the views
                }
            }
        });
        return view;
    }


}


