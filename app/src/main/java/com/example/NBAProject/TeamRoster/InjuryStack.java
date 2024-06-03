package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Stack;

public class InjuryStack extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private InjuryAdapter injuryAdapter;
    private ArrayList<PlayerInfo> injuryList;
    private RosterManager rosterManager;
    private Context context;

    Button remove;

    private static final String KEY_INJURY_LIST = "injury_list";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.injurystack,container,false);


        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);

        // Get the singleton instance of RosterManager
        rosterManager = RosterManager.getInstance();
        context = getContext();



        /*if (savedInstanceState != null) {
            injuryList = savedInstanceState.getParcelableArrayList(KEY_INJURY_LIST);
        } else {
            injuryList = getInjuredPlayersFromRosterManager();
        }*/
        // Fetch data from Firebase
        fetchDataFromFirebase();

        injuryList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.injuryStack);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns
        injuryAdapter = new InjuryAdapter(getContext(), injuryList);
        recyclerView.setAdapter(injuryAdapter);



        remove = view.findViewById(R.id.removeInjuredPlayerButton);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!injuryList.isEmpty()) {
                    // Remove the top player from the local injury list
                    PlayerInfo data = injuryList.remove(0);
                    String playerId = data.getName();
                    removePlayerFromFirebase(playerId);

                    // Remove the player from the injury reserve stack and add them back to the roster
                    rosterManager.removeFromInjuryReserve(data);
                    injuryAdapter.notifyDataSetChanged();



                }
            }
        });




        return view;
    }

    private void removePlayerFromFirebase(String playerId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("injuryReserve");

        if (playerId != null) {
            myRef.child(playerId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Player info removed successfully.");
                    Toast.makeText(context, "Player removed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Firebase", "Failed to remove player info.");
                    Toast.makeText(context, "Failed to remove player.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("injuryReserve");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                injuryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        injuryList.add(data);
                    }
                }
                injuryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read data from Firebase", error.toException());
            }
        });
    }


    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_INJURY_LIST, injuryList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        injuryList = savedInstanceState.getParcelableArrayList(KEY_INJURY_LIST);
    }*/



    private ArrayList<PlayerInfo> getInjuredPlayersFromRosterManager() {
        ArrayList<PlayerInfo> injuredPlayers = new ArrayList<>();
        Stack<PlayerInfo> injuryReserveStack = rosterManager.getInjuryReserve();

        for (PlayerInfo player : injuryReserveStack) {  // Iterating over a Stack<PlayerInfo>
            injuredPlayers.add(player);
        }
        return injuredPlayers;
    }
}


