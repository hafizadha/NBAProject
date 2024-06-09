/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.NBAProject.Journey.Graph;

import java.util.ArrayList;

public class Vertex {

	//Vertex consist NBA team name and edge(s) to neighbour(s)
    private String data;
    private ArrayList<Edge> edges;

    public Vertex(String inputData) {
        this.data = inputData;
        this.edges = new ArrayList<Edge>();
    }

	//Add edge with destination city and distance between them as parameter
    public void addEdge(Vertex endVertex, Integer weight) {
        this.edges.add(new Edge(this, endVertex, weight));
    }

	//Remove the edge by refering to the destination city
    public void removeEdge(Vertex endVertex) {
		//Remove from edges if the edge's destination is equal to the end vertex parameter
        this.edges.removeIf(edge -> edge.getEnd().equals(endVertex));
    }

	//Get data (name) of vertex
    public String getData() {
		return this.data;
	}

	//Return all edges connected to the Vertex
	public ArrayList<Edge> getEdges(){
		return this.edges;
	}

	//Print out all edges connected to the Vertex
	public void print(boolean showWeight) {
		String message = "";

		//no edge
		if (this.edges.size() == 0) {
			System.out.println(this.data + " -->");
			return;
		}

		for(int i = 0; i < this.edges.size(); i++) {
			if (i == 0) {
				message += this.edges.get(i).getStart().data + " -->  ";
			}

			message += this.edges.get(i).getEnd().data;

			if (showWeight) {
				message += " (" + this.edges.get(i).getWeight() + ")";
			}

			if (i != this.edges.size() - 1) {
				message += ", ";
			}
		}
		System.out.println(message);
	}
        
}