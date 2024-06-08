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

        //References to layout components
        button = view.findViewById(R.id.toRank);
        button2 = view.findViewById(R.id.toInjury);
        button3 = view.findViewById(R.id.toContract);
        button4 = view.findViewById(R.id.GoToStatus);
        textView1 = view.findViewById(R.id.currentSalary);
        swipeRefreshLayout = view.findViewById(R.id.container);

        //Setting the icons for buttons
        button.setImageDrawable(context.getDrawable(R.drawable.trophybutton));
        button2.setImageDrawable(context.getDrawable(R.drawable.medicalbutton));
        button3.setImageDrawable(context.getDrawable(R.drawable.contractbutton));


        //Generate recyclerView and its items
        recyclerView = view.findViewById(R.id.teamList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Passing List of players in current roster to the TestAdapter to be displayed
        adapter = new TestAdapter(getContext(), dataList, TestAdapter.MODE_ROSTER, rosterManager);
        recyclerView.setAdapter(adapter);

        int verticalSpace = 50; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);
        swipeRefreshLayout.setOnRefreshListener(this::fetchDataFromFirebase);


        //When Ranking button is clicked
        button.setOnClickListener(view -> {
            //Replacing the current layout with the new fragment
            //Passing the List of Players to the PerformanceRanking fragment via Bundle
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("playerlist", dataList); //Storing in the form of ArrayList
            PerformanceRanking fragment = new PerformanceRanking();
            fragment.setArguments(bundle);
            //Replacing current layout with the new fragment
            getParentFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
        });

        //When Injury Stack button is clicked
        button2.setOnClickListener(view -> {
            //Replacing the current layout with the new fragment
            InjuryStack fragment = new InjuryStack();
            getParentFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
        });

        //When contract extension button is clicked
        button3.setOnClickListener(view -> {
            //Replacing the current layout with the new fragment
            ContractQueue fragment = new ContractQueue();
            getParentFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
        });

        button4.setOnClickListener(view -> {
            //Showing the status of team (whether it comply to the NBA's rule)
            showTeamStatus();
        });

        return view;

    }

    //Showing the status of team (whether it comply to the NBA's rule)
    private void showTeamStatus(){
        //Creating an AlertDialog (popup window)
        View dialogView = LayoutInflater.from(context).inflate(R.layout.teamstatus, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        //Get the total of players, including active and inactive
        int totalplayers = dataList.size(); //Current List of Players size
        int inactivePlayers = RosterManager.getInstance().getInactivePlayers(); //Get it from the RosterManager instance
        int activePlayers = totalplayers - inactivePlayers;


        //References to the components in the layout
        TextView totaldisplay = dialogView.findViewById(R.id.totaldisplay);
        TextView activedisplay = dialogView.findViewById(R.id.activePlayers);
        TextView inactivedisplay = dialogView.findViewById(R.id.inactives);
        TextView centerdis = dialogView.findViewById(R.id.centerplayers);
        TextView guarddis = dialogView.findViewById(R.id.Guards);
        TextView forwarddis = dialogView.findViewById(R.id.Forward);


        //Displaying the values into the components
        totaldisplay.setText(String.valueOf(totalplayers));
        activedisplay.setText(String.valueOf(activePlayers));
        inactivedisplay.setText(String.valueOf(inactivePlayers));

        //Setting the color to red if it doesn't not comply to the rule
        //Otherwise, black (normal)

        //Total player must be not be less than 10
        //That goes for active players too
        //No regulation for the amount of inactive players
        totaldisplay.setTextColor(context.getResources().getColor(totalplayers <10 ? R.color.NBARed : R.color.green));
        activedisplay.setTextColor(context.getResources().getColor(activePlayers <10 ? R.color.NBARed : R.color.green));
        inactivedisplay.setTextColor(context.getResources().getColor(inactivePlayers <10 ? R.color.NBARed : R.color.green));

        //Counting the players playing a role for each position
        //There must be at least 2 players for each position

        //iterate to the list to calculate the sum of each position
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

        //Setting the values as text to be displayed
        guarddis.setText(String.valueOf(Guards));
        forwarddis.setText(String.valueOf(Forwards));
        centerdis.setText(String.valueOf(Centers));

        // Set text color for guards (Red if less than two)
        guarddis.setTextColor(context.getResources().getColor(Guards >= 2 ? R.color.black : R.color.NBARed));

        // Set text color for forwards(Red if less than two)
        forwarddis.setTextColor(context.getResources().getColor(Forwards >= 2 ? R.color.black : R.color.NBARed));

        // Set text color for centers(Red if less than two)
        centerdis.setTextColor(context.getResources().getColor(Centers >= 2 ? R.color.black : R.color.NBARed));

        //Display the view
        dialog.show();
    }


    //Fetching the players in the roster stored in the Firestore database
    public void fetchDataFromFirebase() {
        //Node reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("roster");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); //Clear list to prevent duplication
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlayerInfo data = dataSnapshot.getValue(PlayerInfo.class);
                    if (data != null) {
                        //Add PlayerInfo object into the List
                        dataList.add(data);
                    }
                }
                adapter.notifyDataSetChanged(); //Updates the UI of recycler View
                updateSalaryAndStatus(); //Update the salary and status to be displayed and for conditions
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Error message if importing failed
                Log.e("Roster", "Failed to read data from Firebase", error.toException());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //Update the salary
    public void updateSalaryAndStatus() {

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

    //Save salary to Database
    private void saveSalaryToFirebase() {
        //Node reference
        DatabaseReference salaryRef = FirebaseDatabase.getInstance().getReference("currentSalary");


        //int salary = rosterManager.getCurrentSalary();
        int salary = calculateTotalSalary();
        salaryRef.setValue(salary).addOnCompleteListener(task -> {
            //Show message in Log (for debugging)
            if (task.isSuccessful()) {
                Log.d("Firebase", "Salary saved successfully.");
            } else {
                Log.d("Firebase", "Failed to save salary.");
            }
        });
    }

    //Retrive current Salary from the Database
    public void fetchCurrentSalaryFromFirebase() {
        //Node reference (currentSalary)
        DatabaseReference salaryRef = FirebaseDatabase.getInstance().getReference("currentSalary");
        salaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentSalary = snapshot.getValue(Long.class);
                if (currentSalary != null) {
                    //Fetching salary and display into the UI
                    RosterManager.getInstance().getBalance(); // Update the current salary in RosterManager
                    textView1.setText(String.valueOf(currentSalary)); // Update the UI
                }
            }

            @Override //Error message if failed to import
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Roster", "Failed to read current salary from Firebase", error.toException());
            }
        });
    }

}
