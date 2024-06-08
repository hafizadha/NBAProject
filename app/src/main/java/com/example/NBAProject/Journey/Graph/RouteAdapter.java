package com.example.NBAProject.Journey.Graph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.Journey.NBATeam;
import com.example.NBAProject.R;

import java.util.ArrayList;

//Generate the UI of order of cities visited sorted by the two algorithms
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> {
    Context context;
    private ArrayList<NBATeam> nbaTeams;
    public RouteAdapter(Context context,ArrayList<NBATeam> dataList) {
        this.nbaTeams = dataList;
        this.context = context;
    }


    @NonNull
    @Override
    public RouteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city, parent, false);
        return new RouteAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteAdapter.MyViewHolder holder, int position) {
        NBATeam team = nbaTeams.get(position);
        String cityname = team.getLocation();

        int resid = team.getResourceid();


        // Setting Age as text
        holder.city.setText(String.format(cityname));
        holder.TeamLogo.setImageResource(resid);

    }

    @Override
    public int getItemCount() {
        return nbaTeams.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView city;
        ImageView TeamLogo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.cityname);
            TeamLogo = itemView.findViewById(R.id.teamlogo);

        }


    }

    public static int getResourceId(String resourceName, String resourceType, Context context) {
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }


}
