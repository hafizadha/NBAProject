package com.example.NBAProject.Journey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.NBAProject.R;

public class RoutePage extends Fragment {
    View view;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.routepage, container, false);

        Bundle bundle = getArguments();
        String codename = bundle.getString("Codename");
        Log.d("SR","CODE " + codename);

        Button mapbutton = view.findViewById(R.id.back_to_map);
        TextView text = view.findViewById(R.id.route_code);
        text.setText(codename);
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming you want to move to SomeFragment
                MapFragment fragment = new MapFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.main,fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }
}