/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.NBAProject.Journey.Graph;

/**
 *
 * @author Hafiz
 */
public class NBAGraph {


    //Build an weighted NBA map from the question using the WeightedGraph class
    private WeightedGraph nbamap;
    public NBAGraph(){
        this.nbamap = new WeightedGraph();

        //Adding vertex by the codename for team
        Vertex Spurs = nbamap.addVertex("SAS");
        Vertex Warriors = nbamap.addVertex("GSW");
        Vertex Celtics = nbamap.addVertex("BC");
        Vertex Heat = nbamap.addVertex("MH");
        Vertex Lakers = nbamap.addVertex("LAL");
        Vertex Suns = nbamap.addVertex("PS");
        Vertex Magic = nbamap.addVertex("OM");
        Vertex Nuggets = nbamap.addVertex("DN");
        Vertex Thunder = nbamap.addVertex("OCT");
        Vertex Rockets = nbamap.addVertex("HR");
        

        //Adding edges between sources and destinations cities, including the distance between them (weight)
        nbamap.addEdge(Spurs, Magic, 1137);
        nbamap.addEdge(Spurs, Rockets, 983);
        nbamap.addEdge(Spurs, Thunder, 678);
        nbamap.addEdge(Spurs, Suns, 500);
        nbamap.addEdge(Suns, Lakers, 577);
        nbamap.addEdge(Lakers, Warriors, 554);
        nbamap.addEdge(Warriors, Nuggets, 1507);
        nbamap.addEdge(Nuggets, Celtics, 2845);
        nbamap.addEdge(Thunder, Lakers, 1901);
        nbamap.addEdge(Thunder, Warriors, 2214);
        nbamap.addEdge(Thunder, Nuggets, 942);
        nbamap.addEdge(Thunder, Rockets, 778);
        nbamap.addEdge(Rockets, Celtics, 2584);
        nbamap.addEdge(Rockets, Magic, 458);
        nbamap.addEdge(Magic, Heat, 268);
        nbamap.addEdge(Heat, Celtics, 3045);
    }

    //Get the start location of the map (SPURS)
    public Vertex getStartingVertex() {
		return this.nbamap.getVertices().get(0);
	}

    //Get specific vertex by referencing the target String (team name)
    public Vertex getSpecificVertex(String target) {
            for(Vertex location: nbamap.getVertices()){
                if(target.equals(location.getData())){
                    return location;
                }
            }
            return null;
	}
    
    
}
