package com.example.NBAProject.TeamRoster;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
    TextView textView1;
    ImageButton button, button2, button3;
    Button button4;
    TestAdapter adapter;
    ArrayList<PlayerInfo> dataList;
    RosterManager rosterManager;
    SwipeRefreshLayout swipeRefreshLayout;


    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.teamrosterpage, container, false);

        //getActivity since this fragment is not the child of any other fragment
        context = getActivity();

        // Fetch data from Firebase
        fetchDataFromFirebase();

        // Fetch saved salary from Firebase
        fetchCurrentSalaryFromFirebase();
        dataList = new ArrayList<>();
        rosterManager = RosterManager.getInstance(dataList.size());




        button = view.findViewById(R.id.toRank);
        button2 = view.findViewById(R.id.toInjury);
        button3 = view.findViewById(R.id.toContract);

        button.setImageDrawable(context.getDrawable(R.drawable.trophybutton));
        button2.setImageDrawable(context.getDrawable(R.drawable.medicalbutton));
        button3.setImageDrawable(context.getDrawable(R.drawable.contractbutton));



        textView1 = view.findViewById(R.id.currentSalary);
        button4 = view.findViewById(R.id.GoToStatus);
        swipeRefreshLayout = view.findViewById(R.id.container);

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
        int totalplayers = dataList.size();
        int inactivePlayers = RosterManager.getInstance().getInactivePlayers();
        int activePlayers = totalplayers - inactivePlayers;


        TextView totaldisplay = dialogView.findViewById(R.id.totaldisplay);
        TextView activedisplay = dialogView.findViewById(R.id.activePlayers);
        TextView inactivedisplay = dialogView.findViewById(R.id.inactives);
        TextView centerdis = dialogView.findViewById(R.id.centerplayers);
        TextView guarddis = dialogView.findViewById(R.id.Guards);
        TextView forwarddis = dialogView.findViewById(R.id.Forward);



        totaldisplay.setText(String.valueOf(totalplayers));
        activedisplay.setText(String.valueOf(activePlayers));
        inactivedisplay.setText(String.valueOf(inactivePlayers));


        totaldisplay.setTextColor(context.getResources().getColor(totalplayers <10 ? R.color.NBARed : R.color.green));
        activedisplay.setTextColor(context.getResources().getColor(activePlayers <10 ? R.color.NBARed : R.color.green));
        inactivedisplay.setTextColor(context.getResources().getColor(inactivePlayers <10 ? R.color.NBARed : R.color.green));

        int Guards = 0,Centers = 0,Forwards = 0;

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
        guarddis.setText(String.valueOf(Guards));
        forwarddis.setText(String.valueOf(Forwards));
        centerdis.setText(String.valueOf(Centers));

        // Set text color for guards
        guarddis.setTextColor(context.getResources().getColor(Guards >= 2 ? R.color.black : R.color.NBARed));

        // Set text color for forwards
        forwarddis.setTextColor(context.getResources().getColor(Forwards >= 2 ? R.color.black : R.color.NBARed));

        // Set text color for centers
        centerdis.setTextColor(context.getResources().getColor(Centers >= 2 ? R.color.black : R.color.NBARed));


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

}
