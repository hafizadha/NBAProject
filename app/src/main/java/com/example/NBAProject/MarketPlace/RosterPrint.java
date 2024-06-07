package com.example.NBAProject.MarketPlace;

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
import com.example.NBAProject.TeamRoster.PlayerInfo;
import com.example.NBAProject.TeamRoster.RosterManager;
import com.example.NBAProject.VerticalSpaceItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RosterPrint extends Fragment {

    View view;
    RecyclerView recyclerView;
    PrintAdapter adapter;
    static ArrayList<PlayerInfo> dataList;
    RosterManager rosterManager;
    private static final String ARG_ARRAY_LIST = "array_list";



    public static RosterPrint newInstance(ArrayList<? extends PlayerInfo> arrayList) {
        RosterPrint fragment = new RosterPrint();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ARRAY_LIST, dataList); // Pass the ArrayList
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataList = (ArrayList<PlayerInfo>) getArguments().getSerializable(ARG_ARRAY_LIST); // Retrieve the ArrayList of custom objects
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rosterprint, container, false);

        rosterManager = RosterManager.getInstance();


        // Fetch data from Firebase
        fetchDataFromFirebase();

        dataList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.teamList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PrintAdapter(dataList);
        recyclerView.setAdapter(adapter);

        int verticalSpace = 50; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);




        return view;

    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("roster");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read data from Firebase", error.toException());
            }
        });
    }
}
