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

    public GraphTraverse(Context context,ArrayList<NBATeam> nbacities) {
        this.context = context;
        this.nbacities = nbacities;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.graphtraverse, container, false);

        spinner = view.findViewById(R.id.spinner);
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                hideAllViews();
                switch (position) {
                    case 0:
                        cityorder = new ArrayList<>();
                        pathdistance1 = 0;

                        showNearestNeighborTSPResult();
                        generateRecyclerView(cityorder,pathdistance1);
                        break;
                    case 1:
                        cityorder = new ArrayList<>();
                        NBAGraph test = new NBAGraph();
                        pathdistance1 = 0;

                        Vertex startingVertex = test.getStartingVertex();
                        ArrayList<Vertex> visitedVertices1 = new ArrayList<>();
                        visitedVertices1.add(startingVertex);
                        depthFirstTraversal(startingVertex, visitedVertices1);

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
        routeAdapter = new RouteAdapter(sortedCities);
        recyclerView.setAdapter(routeAdapter);

        routeAdapter.notifyDataSetChanged();

        distance = view.findViewById(R.id.totaldistance);
        distance.setText("TOTAL DISTANCE" + pathdistance1);
    }




    private void showNearestNeighborTSPResult() {
        List<Integer> route = nearestNeighborTSP();
        if (route != null) {
            int pathdistance1 = 0;
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
        boolean[] visited = new boolean[NUM_CITIES];
        List<Integer> route = new ArrayList<>();
        int currentCity = findInitialCity();
        if (currentCity == -1) {
            return null;
        }
        route.add(currentCity);
        visited[currentCity] = true;

        int numVisited = 1;

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
                if (i != j && DISTANCES[i][j] != INF) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void hideAllViews() {
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
    }


    //DFS Algorithm
    public void depthFirstTraversal(Vertex start, ArrayList<Vertex> visitedVertices) {
        int totalteam = 10;
        int leafnodeweight = 0;
        cityorder.add(start.getData());

        boolean isLeaf = true;

        for (Edge e : start.getEdges()){
            leafnodeweight = e.getWeight();
            Vertex neighbor = e.getEnd();

            if (!visitedVertices.contains(neighbor)) {
                Vertex prev = e.getStart();

                //Refer back to it previous vertex
                for(Edge e2: prev.getEdges()){
                    //If the vertex
                    if(!visitedVertices.contains(e2)){
                        isLeaf =false;
                    }
                }
                System.out.println("Weight: " + e.getWeight());
                visitedVertices.add(neighbor);
                pathdistance1 += e.getWeight();

                depthFirstTraversal(neighbor, visitedVertices);
            }
        }

        if (isLeaf) {
            System.out.println("Reached a leaf node: " + start.getData());

            if(visitedVertices.size() != totalteam){
                pathdistance1 += leafnodeweight;
            }
        }


    }
}
