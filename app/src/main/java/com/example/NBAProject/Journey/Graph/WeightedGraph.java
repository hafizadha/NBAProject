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

    public Vertex addVertex(String data) {
        Vertex newVertex = new Vertex(data);
        this.vertices.add(newVertex);
        return newVertex;
    }

    public void addEdge(Vertex vertex1, Vertex vertex2, Integer weight) {
        vertex1.addEdge(vertex2, weight);
        vertex2.addEdge(vertex1, weight);
    }

    public void removeEdge(Vertex vertex1, Vertex vertex2) {
        vertex1.removeEdge(vertex2);
        
        vertex2.removeEdge(vertex1);
        
    }

    public void removeVertex(Vertex vertex) {
        this.vertices.remove(vertex);
    }

    public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}



	public Vertex getVertexByValue(String value) {
		for(Vertex v: this.vertices) { 
			if (v.getData() == value) {
				return v;
			}
		}

		return null;
	}
	
	public void print() {
		for(Vertex v: this.vertices) {
                    System.out.println(v.getEdges());
		}
	}

        

}