package com.example.NBAProject.MarketPlace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;
import com.example.NBAProject.TeamRoster.PlayerInfo;
import com.example.NBAProject.TeamRoster.RosterManager;
import com.example.NBAProject.TeamRoster.TestAdapter;
import com.example.NBAProject.VerticalSpaceItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MarketPage extends Fragment {

    //Parameters from Advanced Searching
    private static final int ARG_PARAM1 = 0;
    private static final int ARG_PARAM2 = 0;
    private static final int ARG_PARAM3 = 0;
    private static final String ARG_PARAM4 = "param4";
    private int minHeight;
    private int minWeight;
    private int minSalary;
    private String position;


    //Components in the layout
    RecyclerView recyclerView;
    SearchView searchbar;
    TestAdapter myadapter;
    View view;
    ImageView imageView;
    //list of all free agents
    ArrayList<PlayerInfo> list;
    DatabaseReference databaseReference;

    RosterManager rosterManager;


    public MarketPage() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            minHeight = getArguments().getInt(String.valueOf("minH"));
            minWeight = getArguments().getInt("minW");
            minSalary = getArguments().getInt("minSalary");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Setting to Marketplace layout
        this.view = inflater.inflate(R.layout.marketpage, container, false);

        rosterManager = new RosterManager("Team Name","Team City",20000);
        //----------------------------------------------------------------------------------------
        //RecyclerView to display list of players
        recyclerView = (RecyclerView) view.findViewById(R.id.playerList);

        recyclerView = view.findViewById(R.id.playerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Define the space between items
        int verticalSpace = 50; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);

        list = new ArrayList<>();

        Bundle bundle = getArguments();
        boolean test = getArguments() == null;
        Log.d("BETD","EDED" + test);
        if(getArguments() != null){
            minHeight = bundle.getInt("minH");
            minWeight = bundle.getInt("minW");
            minSalary = bundle.getInt("minSalary");
            position = bundle.getString("pos");
        }

        loadPlayerData(minHeight,minWeight,minSalary,position);
        myadapter = new TestAdapter(getContext(), list,TestAdapter.MODE_PLAYER_LIST,rosterManager);
        recyclerView.setAdapter(myadapter);

        //-----------------------------------------------------------------------------------------
        //Search bar to search player's name
        searchbar = view.findViewById(R.id.searchView);

        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TEST", "DED" + newText);
                filterList(newText);
                return true;
            }
        });

        //-----------------------------------------------------------------------------------------
        //Image Button to transition to Advanced Searching
        Log.d("Text", "Hafiz" + minHeight);
        imageView = view.findViewById(R.id.advSearch);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvancedSearch as = new AdvancedSearch();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, as)
                        .commit();
            }
        });

        return view;
    }


    private void filterList(String text) {
        ArrayList<PlayerInfo> filteredList = new ArrayList<>();
        for (PlayerInfo item : list) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                Log.d("TEST", "DED" + item.getName());
            }
        }

        if (filteredList.isEmpty()) {
        } else {
            myadapter.setFilteredList(filteredList);
        }
    }

    private void loadPlayerData(int minHeight, int minWeight, int minSalary, String POS) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("players");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                    Log.d("FirebaseData", "DataSnapshot content: " + dataSnapshot.toString());

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("FirebaseData", "Snapshot: " + snapshot.toString());

                        // Initialize a new getName instance
                        PlayerInfo playerdetails = new PlayerInfo();

                        //BIAR DULU
                        String profilePhoto = snapshot.child("profilePhoto").getValue(String.class);

                        Integer height = snapshot.child("Height").getValue(Integer.class);
                        Float weight = snapshot.child("Weight").getValue(Float.class);
                        String age = snapshot.child("Age").getValue(String.class);
                        String points = snapshot.child("Points").getValue(String.class);
                        String assists = snapshot.child("Assist").getValue(String.class);
                        String steals = snapshot.child("Steal").getValue(String.class);
                        String rebound = snapshot.child("Rebound").getValue(String.class);
                        Integer salary = snapshot.child("Salary").getValue(Integer.class);
                        String block = snapshot.child("Block").getValue(String.class);
                        String position = snapshot.child("POS").getValue(String.class);
                        String name = snapshot.child("Name").getValue(String.class);


                        boolean matches = true;

                        if (height < minHeight || weight < minWeight || salary < minSalary) {
                            matches = false; // If any of these are below the filter, it's not a match
                        }

                        if (POS != null && !POS.equals(position)) {
                            matches = false; // If position doesn't match, it fails
                        }

                        if (matches) {

                            playerdetails.setPhoto(profilePhoto);
                            playerdetails.setPOS(position);
                            playerdetails.setName(name);

                            // Safely set numerical data
                            playerdetails.setHeight(height);
                            playerdetails.setWeight(weight);
                            playerdetails.setAge(age);
                            playerdetails.setPoints(points);
                            playerdetails.setAssist(assists);
                            playerdetails.setSteal(steals);
                            playerdetails.setRebound(rebound);
                            playerdetails.setSalary(salary);
                            playerdetails.setBlock(block);

                            list.add(playerdetails);
                        }
                    }
                    myadapter.notifyDataSetChanged();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
