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
import java.util.Queue;

public class ContractQueue extends Fragment {
    View view;
    RosterManager rosterManager;
    RecyclerView recyclerView;
    Button renew;
    ContractAdapter contractAdapter;

    private ArrayList<PlayerInfo> contractList;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contractqueue,container,false);

        LinearLayout linearLayout = view.findViewById(R.id.main);

        rosterManager = RosterManager.getInstance();
        context=getContext();

        // Fetch data from Firebase
        fetchDataFromFirebase();

        //contractList = getContractPlayersFromRosterManager();
        contractList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.contractQueue);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns
        contractAdapter = new ContractAdapter(getContext(),contractList);
        recyclerView.setAdapter(contractAdapter);

        renew = view.findViewById(R.id.renewButton);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!contractList.isEmpty()) {
                    // Remove the top player from the local injury list
                    PlayerInfo data = contractList.remove(0);
                    String playerId = data.getName();
                    removePlayerFromFirebase(playerId);

                    // Remove the player from the injury reserve stack and add them back to the roster
                    rosterManager.removeFromContractExtensionQueue(data);
                    contractAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;


    }


    private void removePlayerFromFirebase(String playerId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("contractQueue");

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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("contractQueue");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contractList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        contractList.add(data);
                    }
                }
                contractAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read data from Firebase", error.toException());
            }
        });
    }

    private ArrayList<PlayerInfo> getContractPlayersFromRosterManager() {
        ArrayList<PlayerInfo> contractPlayers = new ArrayList<>();
        Queue<PlayerInfo> contractQueue = rosterManager.getContractPlayers();

        for (PlayerInfo player : contractQueue) {  // Iterating over a Stack<PlayerInfo>
            contractPlayers.add(player);
        }
        return contractPlayers;
    }
}
