/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.NBAProject.Journey.Graph;

import java.util.ArrayList;

public class WeightedGraph {
    
    private ArrayList<Vertex> vertices;

    public WeightedGraph(){
        this.vertices = new ArrayList<Vertex>();
    }

    //Add vertex into the instance ArrayList of Vertices variable
    public Vertex addVertex(String data) {
        Vertex newVertex = new Vertex(data);
        this.vertices.add(newVertex);
        return newVertex;
    }

    //Add edge by passing the vertex1 (origin city) , vertex2 ( destination city ) and weight (distance)
    public void addEdge(Vertex vertex1, Vertex vertex2, Integer weight) {
        vertex1.addEdge(vertex2, weight);
        vertex2.addEdge(vertex1, weight);
    }

    public void removeEdge(Vertex vertex1, Vertex vertex2) {
        vertex1.removeEdge(vertex2);
        
        vertex2.removeEdge(vertex1);
        
    }

    //remove specific vertex
    public void removeVertex(Vertex vertex) {
        this.vertices.remove(vertex);
    }

    //Get all vertices in the graph
    public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}


}