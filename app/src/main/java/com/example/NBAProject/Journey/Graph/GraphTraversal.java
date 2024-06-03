/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.NBAProject.Journey.Graph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.NBAProject.R;

import java.util.ArrayList;

public class GraphTraversal extends Fragment {
    private static int pathdistance1 = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.routepage,container,false);
        return view;
    }

    public static void depthFirstTraversal(Vertex start, ArrayList<Vertex> visitedVertices) {
        int totalteam = 10;
        int leafnodeweight = 0;

        boolean isLeaf = true;
        
        for (Edge e : start.getEdges()){ 
            leafnodeweight = e.getWeight();
            Vertex neighbor = e.getEnd();

            if (!visitedVertices.contains(neighbor)) {
                Vertex prev = e.getStart();
                
                for(Edge e2: prev.getEdges()){
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
        

        public static void main(String[] args) {
		NBAGraph test = new NBAGraph();
		Vertex startingVertex = test.getStartingVertex();
		ArrayList<Vertex> visitedVertices1 = new ArrayList<>();
		ArrayList<Vertex> visitedVertices2 = new ArrayList<>();
		visitedVertices1.add(startingVertex);
		visitedVertices2.add(startingVertex);
		System.out.println("DFS:");
		GraphTraversal.depthFirstTraversal(startingVertex, visitedVertices1);
        System.out.println("Total distance: " + pathdistance1);
	}
        
        
}