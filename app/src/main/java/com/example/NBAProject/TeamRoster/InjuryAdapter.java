package com.example.NBAProject.TeamRoster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.R;

//Explanation of the Overriding methods in Adapters are in TestAdapter.java
public class InjuryAdapter extends RecyclerView.Adapter<InjuryAdapter.ViewHolder> {

    Context context;
    MyStack<PlayerInfo> list;

    public InjuryAdapter(Context context, MyStack<PlayerInfo> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.injuredplayers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerInfo data = list.get(position);
        holder.playerName.setText(data.getName());
        holder.orderno.setText(String.valueOf(position+1));
        holder.injurydesc.setText(data.getInjuryDescription());

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerName,injurydesc,orderno;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderno = itemView.findViewById(R.id.orderNumber);
            playerName = itemView.findViewById(R.id.playerNameTV);
            injurydesc = itemView.findViewById(R.id.injurydesc);
        }
    }
}
