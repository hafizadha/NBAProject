/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.NBAProject.Journey.Graph;

public class Edge {
	private Vertex start;
	private Vertex end;
	private Integer weight;

	//Each edge has a start Vertex, an end vertex, and the weight between them (distance)
	public Edge(Vertex startV, Vertex endV, Integer inputWeight) {
		this.start = startV;
		this.end = endV;
		this.weight = inputWeight;
	}

	//Getter methods
	public Vertex getStart() {
		return this.start;
	}
	
	public Vertex getEnd() {
		return this.end;
	}
	
	public Integer getWeight() {
		return this.weight;
	}
        
}