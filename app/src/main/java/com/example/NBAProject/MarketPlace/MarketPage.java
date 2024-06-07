package com.example.NBAProject.MarketPlace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    //Parameters passed by Search Page
    private int minHeight;
    private int minWeight;
    private int minSalary;
    private String position;
    private String heightoperand;
    private String weightoperand;
    private String salaryoperand;


    //Components inside the layout
    RecyclerView recyclerView;
    SearchView searchbar;
    TestAdapter myadapter;
    View view;
    ImageView imageView;

    //list of all free agents
    ArrayList<PlayerInfo> list;

    //Used to import data from Firestore database
    DatabaseReference databaseReference;

    //Crucial in handling the team roster
    RosterManager rosterManager;

    public MarketPage() {
    }

    //A Fragment lifecycle that displays the UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Setting to Marketplace layout (marketpage.xml)
        this.view = inflater.inflate(R.layout.marketpage, container, false);

        //----------------------------------------------------------------------------------------
        //RecyclerView to display list of players
        recyclerView = (RecyclerView) view.findViewById(R.id.playerList);

        recyclerView = view.findViewById(R.id.playerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Retrieve the rosterManager for adding mechanism
        rosterManager = RosterManager.getInstance();

        // Define the space between items in RecyclerView <Merely for recyclerview's design>
        int verticalSpace = 50; // 50 pixels of space
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(verticalSpace);

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(itemDecoration);

        list = new ArrayList<>(); //Generate new arraylist of players before loading them

        Bundle bundle = getArguments(); //Get data from the SearchPage
        if(getArguments() != null){
            //Initialising instance variables by referencing unique keys set inside the SearchPage class
            //Retrieve integer values ( the default values are 0 for each )
            minHeight = bundle.getInt("minH");
            minWeight = bundle.getInt("minW");
            minSalary = bundle.getInt("minSalary");

            //Retrieve comparison symbols and strings ( can be null values if the user doesn't input anything )
            position = bundle.getString("pos");
            heightoperand = bundle.getString("heightsym");
            weightoperand = bundle.getString("weightsym");
            salaryoperand = bundle.getString("salarysym");

        }

        //Load data from database based on these parameters
        loadPlayerData(position,heightoperand,weightoperand,salaryoperand);

        //Adapters are for generating the components inside the Recycler view
        myadapter = new TestAdapter(getContext(), list,TestAdapter.MODE_PLAYER_LIST,rosterManager);
        recyclerView.setAdapter(myadapter);

        //-----------------------------------------------------------------------------------------
        //Search bar to search player's name
        searchbar = view.findViewById(R.id.searchView);

        //Setting up color of text in the searchbar
        EditText editText = searchbar.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.DarkerGray));

        //Search bar activity (when user types on the searchbar)
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //These
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Each time the text change, this method is called
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
                SearchPage as = new SearchPage();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, as)
                        .commit();
            }
        });

        return view;
    }


    private void filterList(String text) {
        //Create a local ArrayList to be filled with filtered names
        //So that it doesn't disturb the purity of instance list
        ArrayList<PlayerInfo> filteredList = new ArrayList<>();
        for (PlayerInfo item : list) {
            //If player name matches with the text
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
        } else {
            //Send the list to adapter to display the filtered players
            myadapter.setFilteredList(filteredList);
        }
    }

    //Load Player data based on the conditional parameters
    private void loadPlayerData(String POS,String heightoperand,String weightoperand,String salaryoperand) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("players");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Initialize a new getName instance
                        PlayerInfo playerdetails = new PlayerInfo();

                        //BIAR DULU
                        String profilePhoto = snapshot.child("profilePhoto").getValue(String.class);

                        Integer height = snapshot.child("Height").getValue(Integer.class);
                        Integer weight = snapshot.child("Weight").getValue(Integer.class);
                        String age = snapshot.child("Age").getValue(String.class);
                        String points = snapshot.child("Points").getValue(String.class);
                        String assists = snapshot.child("Assist").getValue(String.class);
                        String steals = snapshot.child("Steal").getValue(String.class);
                        String rebound = snapshot.child("Rebound").getValue(String.class);
                        Integer salary = snapshot.child("Salary").getValue(Integer.class);
                        String block = snapshot.child("Block").getValue(String.class);
                        String position = snapshot.child("POS").getValue(String.class);
                        String name = snapshot.child("Name").getValue(String.class);

                        //Setting the matches to true, assuming the player matches the conditions
                        boolean matches = true;

                        //If the user inputs comparison operators into at least one of the 3 condition
                        if(heightoperand != null || weightoperand != null || salaryoperand != null){

                            //Check which attributes need to be considered
                            //If there are no consideration, it is set to false
                            boolean heightcon = heightoperand != null;
                            boolean weightcon = weightoperand != null;
                            boolean salarycon = salaryoperand != null;

                            if(heightcon && weightcon && salarycon){
                                //Check the condition for each attributes
                                boolean match1 = checkComparision(heightoperand,height,minHeight);
                                boolean match2 = checkComparision(weightoperand,weight,minWeight);
                                boolean match3 = checkComparision(salaryoperand,salary,minSalary);
                                //If  any of the attributes do not match
                                if(!match1 || !match2 || !match3){
                                    matches = false;
                                }
                            }
                            //Height attributes are not considered
                            else if(!heightcon){
                                //Check the condition of the other 2 attributes
                                matches = checkCondition(weightcon,weight,minWeight,weightoperand,salarycon,salary,minSalary,salaryoperand);
                            }
                            //Weight is not considered
                            else if(!weightcon){
                                matches = checkCondition(heightcon,height,minHeight,heightoperand,salarycon,salary,minSalary,salaryoperand);
                            }
                            //Salary is not considered
                            else if(!salarycon){
                                matches = checkCondition(weightcon,weight,minWeight,weightoperand,heightcon,height,minHeight,heightoperand);
                            }

                        }
                        else{ //If user doesn't input any operators at all, then the default conditions are to be considered
                            //Each attributes must be greater than or equal to the minimum value to be filtered
                            if(height<minHeight || weight <minWeight || salary <minSalary ){
                                matches = false;
                            }
                        }
                        //If player's position is to be considered
                        if (POS != null && !POS.equals(position)) {
                            matches = false; // If position doesn't match, it fails
                        }

                        //If all condition matchers, then player is added into the list
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
                    //Notify adapter so that UI can be changed
                    myadapter.notifyDataSetChanged();

                    if(list.isEmpty()){//Display message if all players do not match the condition
                        Toast.makeText(getContext(), "No Player Found", Toast.LENGTH_SHORT).show();
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    //Generic method that can only take of Number Class
    //This method will evaluate comparision
    private <T extends Number> boolean checkComparision(String sym,T value,T condition){
        boolean match2 = false;
        //Converts the generic into double
        double val = value.doubleValue();
        double cond = condition.doubleValue();
        //Check condition between the value and the conditional value given the symbol
        switch(sym){
            case "<":
                match2 = val<cond;break;
            case "<=":
                match2 = val<=cond;break;
            case "==":
                match2 = val==cond;break;
            case ">":
                match2 = val>cond;break;
            case ">=":
                match2 = val>=cond;break;
        }
        //If it doesn't meet the condition, return false
        return match2;
    }

    //This method takes two condition along with its value, conditional value and operators used to compare
    private <T extends Number> boolean checkCondition(boolean condition1,T val1,T condval1 ,String operand1, boolean condition2,T val2, T condval2,String operand2){
        //If the two condition needs to be considered
        if(condition1 && condition2){
            return checkComparision(operand1,val1,condval1) && checkComparision(operand2,val2,condval2) ;
        } else if (condition1) {//If only one of the condition need to be consider, then check the comparison of
            return checkComparision(operand1,val1,condval1);
        }else{
            return checkComparision(operand2,val2,condval2);
        }
    }


}
