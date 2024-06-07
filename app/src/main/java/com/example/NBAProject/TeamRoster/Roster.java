package com.example.NBAProject.TeamRoster;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.NBAProject.R;
import com.example.NBAProject.VerticalSpaceItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Roster extends Fragment {
    View view;

    RecyclerView recyclerView;
    TextView textView1, textView2, guards, forwards, centers;
    Button button, button2, button3, button4;
    TestAdapter adapter;
    ArrayList<PlayerInfo> dataList;
    RosterManager rosterManager;
    SwipeRefreshLayout swipeRefreshLayout;
    int Guards,Centers,Forwards;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.teamrosterpage, container, false);

        context = getActivity();

        fetchDataFromFirebase();

        fetchCurrentSalaryFromFirebase();

        dataList = new ArrayList<>();

        rosterManager = RosterManager.getInstance(dataList.size());
        Log.d("APA NI","WHAY TJE FUCK " + dataList.size());

        textView1 = view.findViewById(R.id.currentSalary);
        textView2 = view.findViewById(R.id.playerStatus);
        guards = view.findViewById(R.id.guardsTV);
        forwards = view.findViewById(R.id.forwardsTV);
        centers = view.findViewById(R.id.centersTV);
        button = view.findViewById(R.id.removeButton);
        button2 = view.findViewById(R.id.GoToStack);
        button3 = view.findViewById(R.id.GoToQueue);
        button4 = view.findViewById(R.id.GoToStatus);
        swipeRefreshLayout = view.findViewById(R.id.container);

        // Fetch saved salary from Firebase

        // Fetch data from Firebase

        recyclerView = view.findViewById(R.id.teamList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TestAdapter(getContext(), dataList, TestAdapter.MODE_ROSTER, rosterManager);
        recyclerView.setAdapter(adapter);

        int verticalSpace = 50; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);


        swipeRefreshLayout.setOnRefreshListener(this::fetchDataFromFirebase);

        button.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("playerlist", dataList);
            PerformanceRanking fragment = new PerformanceRanking();
            fragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
        });

        button2.setOnClickListener(view -> {
            InjuryStack fragment = new InjuryStack();
            getParentFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
        });

        button3.setOnClickListener(view -> {
            ContractQueue fragment = new ContractQueue();
            getParentFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
        });

        button4.setOnClickListener(view -> {
            showTeamStatus();
        });

        return view;

    }

    private void showTeamStatus(){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.teamstatus, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        dialog.show();
    }


    public void fetchDataFromFirebase() {
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
                updateSalaryAndStatus();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read data from Firebase", error.toException());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    public void updateSalaryAndStatus() {
        // Existing code...

        // Calculate total salary of all players
        int totalSalary = calculateTotalSalary();

        // Update current salary TextView
        textView1.setText(String.valueOf(totalSalary));
        textView2.setText(isFull() ? "FULL" : "NOT FULL");
        updatePositions();

        // Save the updated salary to Firebase
        saveSalaryToFirebase();
        saveNumberOfPlayersToFirebase();
    }

    private boolean isFull(){
        return dataList.size() >= 15;
    }

    private int calculateTotalSalary() {
        int totalSalary = 20000;
        for (PlayerInfo player : dataList) {
            totalSalary -= player.getSalary();
        }
        return totalSalary;
    }

    private void saveNumberOfPlayersToFirebase() {
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference("NoOfPlayers");

        int numberOfPlayers = dataList.size();
        playersRef.setValue(numberOfPlayers).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Number of players saved successfully.");
            } else {
                Log.d("Firebase", "Failed to save number of players.");
            }
        });
    }

    private void saveSalaryToFirebase() {
        DatabaseReference salaryRef = FirebaseDatabase.getInstance().getReference("currentSalary");


        //int salary = rosterManager.getCurrentSalary();
        int salary = calculateTotalSalary();
        salaryRef.setValue(salary).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Salary saved successfully.");
            } else {
                Log.d("Firebase", "Failed to save salary.");
            }
        });
    }

    public void fetchCurrentSalaryFromFirebase() {
        DatabaseReference salaryRef = FirebaseDatabase.getInstance().getReference("currentSalary");
        salaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentSalary = snapshot.getValue(Long.class);
                if (currentSalary != null) {
                    RosterManager.getInstance().getBalance(); // Update the current salary in RosterManager
                    textView1.setText(String.valueOf(currentSalary)); // Update the UI
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read current salary from Firebase", error.toException());
            }
        });
    }


    public void updatePositions() {
        Guards = 0;Centers = 0;Forwards = 0;

        for (PlayerInfo player : dataList) {
            switch (player.getPOS()) {
                case "SG":
                case "PG":
                    Guards++;
                    break;
                case "SF":
                case "PF":
                    Forwards++;
                    break;
                default:
                    Centers++;
                    break;
            }
        }
        guards.setText("Guards: " + Guards);
        forwards.setText("Forwards: " + Forwards);
        centers.setText("Centers: " + Centers);

        // Set text color for guards
        guards.setTextColor(context.getResources().getColor(Guards >= 2 ? R.color.green : R.color.black));

        // Set text color for forwards
        forwards.setTextColor(context.getResources().getColor(Forwards >= 2 ? R.color.green : R.color.black));

        // Set text color for centers
        centers.setTextColor(context.getResources().getColor(Centers >= 2 ? R.color.green : R.color.black));
    }
}
