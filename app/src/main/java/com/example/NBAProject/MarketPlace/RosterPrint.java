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
import com.example.NBAProject.VerticalSpaceItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//Basic requirement 3.2 can be found here (printing roster after addition)
public class RosterPrint extends Fragment {
    View view;
    RecyclerView recyclerView;
    PrintAdapter adapter;
    static ArrayList<PlayerInfo> dataList;

    private static final String ARG_ARRAY_LIST = "array_list";




    public static RosterPrint newInstance(ArrayList<? extends PlayerInfo> arrayList) {
        RosterPrint fragment = new RosterPrint();
        Bundle args = new Bundle();// Bundle to hold fragment arguments
        args.putSerializable(ARG_ARRAY_LIST, dataList); // Pass the ArrayList as a serializable
        fragment.setArguments(args);// Set the arguments for the fragment
        return fragment;
    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rosterprint, container, false);

        // Fetch roster data from Firebase
        fetchDataFromFirebase();

        dataList = new ArrayList<>();

        //Get thee recycler view's reference from the inflated layout and generate items using PrintAdapter
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

    //Import the current roster from the database ( Data is stored right after addition of player)
    private void fetchDataFromFirebase() {
        //Node reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("roster");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); //Clear to prevent any duplicates
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) { //Iterate through the children of the node
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            //If failed to import
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read data from Firebase", error.toException());
            }
        });
    }
}