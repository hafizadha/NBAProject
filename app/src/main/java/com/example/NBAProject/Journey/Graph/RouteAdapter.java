package com.example.NBAProject.Journey.Graph;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> {
    private List<String> list;

    public RouteAdapter(List<String> dataList) {
        this.list = dataList;
    }

    @NonNull
    @Override
    public RouteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city, parent, false);
        return new RouteAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RouteAdapter.MyViewHolder holder, int position) {
        String cityname = list.get(position);

        // Setting Age as text
        holder.city.setText(String.format(cityname));

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView city;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.cityname);

        }


    }

}
