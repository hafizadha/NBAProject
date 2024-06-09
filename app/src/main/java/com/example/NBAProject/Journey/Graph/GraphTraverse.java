package com.example.NBAProject.Journey.Graph;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.Journey.NBATeam;
import com.example.NBAProject.R;

import java.util.ArrayList;
import java.util.List;

public class GraphTraverse extends Fragment {

    Context context;
    private Spinner spinner;
    private TextView textView1, textView2;
    View view;
    RecyclerView recyclerView;
    TextView distance;
    private List<String> cityorder;
    private ArrayList<NBATeam> nbacities;

    RouteAdapter routeAdapter;
    private static final int NUM_CITIES = 10;
    private static final int INF = Integer.MAX_VALUE;
    private static int pathdistance1 = 0;

    //For Nearest Neighbour Algorithm
    //2D Distance matrix, where the order are arranged accordingly (from the city_names matrix, starting with Spurs)
    //INF represents INFINITE distance, showing that there's no connecting edge between the cities
    private final int[][] DISTANCES = {
            {0, INF, INF, INF, INF, 500, 1137, INF, 678, 983},
            {INF, 0, INF, INF, 554, INF, INF, 1507, 2214, INF},
            {INF, INF, 0, 3045, INF, INF, INF, 2845, INF, 2584},
            {INF, INF, 3045, 0, INF, INF, 268, INF, INF, INF},
            {INF, 554, INF, INF, 0, 577, INF, INF, 1901, INF},
            {500, INF, INF, INF, 577, 0, INF, INF, INF, INF},
            {1137, INF, INF, 268, INF, INF, 0, INF, INF, 458},
            {INF, 1507, 2845, INF, INF, INF, INF, 0, 942, INF},
            {678, 2214, INF, INF, 1901, INF, INF, 942, 0, 778},
            {983, INF, 2584, INF, INF, INF, 458, INF, 778, 0},
    };
    private final String[] CITY_NAMES = {
            "SAS", "GSW", "BC", "MH", "LAL",
            "PS", "OM", "DN", "OCT", "HR"
    };

    //Constructor with context and ArrayList of NBATeam as parameters
    //Context (passed by parent fragment) are used to get resources and use app services for the front end
    public GraphTraverse(Context context,ArrayList<NBATeam> nbacities) {
        this.context = context;
        this.nbacities = nbacities;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout
        view = inflater.inflate(R.layout.graphtraverse, container, false);

        //References for components
        spinner = view.findViewById(R.id.spinner);
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);

        //Setting the dropdown consisting of choices of graph traversal algorithm
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //When the dropdown is clicked:
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                hideAllViews(); //Hide dropdown options after choices are clocked

                //Check choice
                switch (position) {
                    case 0: //Nearest Neighbour algorithm
                        cityorder = new ArrayList<>();//Create new arraylist to be displayed
                        pathdistance1 = 0;//Set path distance back to 0 for new calculation


                        showNearestNeighborTSPResult();
                        generateRecyclerView(cityorder,pathdistance1); //Displays results by the Ui
                        break;
                    case 1: //DFS
                        cityorder = new ArrayList<>();//Create new arraylist to be displayed
                        NBAGraph test = new NBAGraph(); //Create an instance NBAGraph constructed from Weighted Graph
                        pathdistance1 = 0;//Set path distance back to 0 for new calculation

                        Vertex startingVertex = test.getStartingVertex(); //Get the starting vertex (SPURS) from the custom graph
                        ArrayList<Vertex> visitedVertices1 = new ArrayList<>();//Create instance of visitedVertices
                        visitedVertices1.add(startingVertex); //Add the first city ( indicating that this is the initial city)
                        depthFirstTraversal(startingVertex, visitedVertices1); //Call DFS method with starting city and visited cities as parameters

                        //Generate result in front end
                        generateRecyclerView(cityorder,pathdistance1);

                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                hideAllViews();
            }
        });

        return view;
    }

    //Generate the UI view showing the ordered cities and the total travel distance
    private void generateRecyclerView(List<String> cityorder,int totalpath) {
        ArrayList<NBATeam> sortedCities = new ArrayList<>();

        for(String codename : cityorder){
            for(NBATeam team: nbacities){
                if(codename.equals(team.getCodename())){
                    sortedCities.add(team);
                    break;
                }
            }
        }
        recyclerView = view.findViewById(R.id.cities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        routeAdapter = new RouteAdapter(context,sortedCities);
        recyclerView.setAdapter(routeAdapter);


        routeAdapter.notifyDataSetChanged();

        distance = view.findViewById(R.id.totaldistance);
        distance.setText(String.valueOf(pathdistance1));
    }




    private void showNearestNeighborTSPResult() {
        List<Integer> route = nearestNeighborTSP();
        if (route != null) {
            pathdistance1 = 0;  // Reset the path distance
            for (int i = 0; i < route.size(); i++) {
                int cityIndex = route.get(i);
                cityorder.add(CITY_NAMES[cityIndex]);
                if (i < route.size() - 1) {
                    pathdistance1 += DISTANCES[route.get(i)][route.get(i + 1)];
                }
            }
        } else {
            textView1.setText("No valid route found.");
            textView1.setVisibility(View.VISIBLE);
        }
    }

    private List<Integer> nearestNeighborTSP() {

        //create a boolean array to keeep track of visited cities
        boolean[] visited = new boolean[NUM_CITIES];
        //Instantiate a list of route taking order of visited cities
        List<Integer> route = new ArrayList<>();

        //Finding first city
        int currentCity = findInitialCity();
        if (currentCity == -1) {
            return null;
        }
        route.add(currentCity); // Add current city into the order
        visited[currentCity] = true; //true indicates it has been visited

        int numVisited = 1; //One city has been visited

        //
        while (numVisited < NUM_CITIES) {
            int nextCity = -1;
            int minDistance = INF;

            for (int j = 0; j < NUM_CITIES; j++) {
                if (!visited[j] && DISTANCES[currentCity][j] < minDistance) {
                    nextCity = j;
                    minDistance = DISTANCES[currentCity][j];
                }
            }

            if (nextCity == -1) {
                return null;
            }
            route.add(nextCity);
            visited[nextCity] = true;
            currentCity = nextCity;
            numVisited++;
        }

        return route;
    }

    private int findInitialCity() {
        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                //If it's not the same city and there exists a connection between them
                if (i != j && DISTANCES[i][j] != INF) {
                    return i;
                }
            }
        }

        //Return -1 if can't find
        return -1;
    }


    //Closes the Dropdown view
    private void hideAllViews() {
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
    }



    //DFS Algorithm
    public void depthFirstTraversal(Vertex start, ArrayList<Vertex> visitedVertices) {
        int totalteam = 10;
        int leafnodeweight = 0;
        cityorder.add(start.getData()); //Store the first city into the city order, store the next order of city if recursion is called

        //Assuming all vertex is a leaf node
        boolean isLeaf = true;

        //Iterate through all edges of the vertex
        for (Edge e : start.getEdges()){
            leafnodeweight = e.getWeight(); //Get distance between them
            Vertex neighbor = e.getEnd(); //Get the destination city (next vertex)

            //If the destination city has not been visited
            if (!visitedVertices.contains(neighbor)) {
                //Get its source city ( similar to Vertex start)
                Vertex prev = e.getStart();

                //Refer back to it previous vertex
                for(Edge e2: prev.getEdges()){
                    //If the vertex's edges has neighbouring cities that has not been visited
                    //It indicates that the vertex is not a leaf node
                    if(!visitedVertices.contains(e2.getEnd())){
                        isLeaf =false;
                    }
                }
                System.out.println("Weight: " + e.getWeight());
                visitedVertices.add(neighbor);
                pathdistance1 += e.getWeight();

                //Recursion call
                depthFirstTraversal(neighbor, visitedVertices);
            }
        }

        //If it is a list node
        if (isLeaf) {
            System.out.println("Reached a leaf node: " + start.getData());

            //If the size is equal to the total team (10), indicates that the vertex is the last leaf node
            //Thus, do not need to calculate the distance to its origin city again
            if(visitedVertices.size() != totalteam){
                pathdistance1 += leafnodeweight;
            }
        }


    }
}
