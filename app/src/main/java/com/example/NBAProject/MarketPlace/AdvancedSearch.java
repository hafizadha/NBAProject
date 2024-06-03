package com.example.NBAProject.MarketPlace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.NBAProject.R;

public class AdvancedSearch extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searchpage, container, false);

        // Get references to the EditText fields
        EditText editTextHeight = view.findViewById(R.id.editTextHeight);
        EditText editTextWeight = view.findViewById(R.id.editTextWeight);
        EditText editTextPosition = view.findViewById(R.id.editTextPosition);
        EditText editTextSalary = view.findViewById(R.id.editTextSalary);
        ImageView buttonFilter = view.findViewById(R.id.buttonFilter);

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input from EditText fields
                String heightInput = editTextHeight.getText().toString();
                String weightInput = editTextWeight.getText().toString();
                String salaryInput = editTextSalary.getText().toString();
                String positionInput = editTextPosition.getText().toString();

                // Parse numbers only if input is not empty, otherwise use a default
                int minHeight = heightInput.isEmpty() ? 0 : Integer.parseInt(heightInput);
                int minWeight = weightInput.isEmpty() ? 0 : Integer.parseInt(weightInput);
                int minSalary = salaryInput.isEmpty() ? 0 : Integer.parseInt(salaryInput);


                Log.d("wedwed","TEst" + minHeight);
                Bundle bundle = new Bundle();
                MarketPage mp = new MarketPage();

                bundle.putInt("minH",minHeight);
                bundle.putInt("minW",minWeight);
                bundle.putInt("minSalary",minSalary);

                // Pass position only if provided
                if (!positionInput.isEmpty()) {
                    bundle.putString("pos",positionInput);
                }
                mp.setArguments(bundle);

                // Add the fragment to the activity
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, mp)
                        .commit();
            }
        });

        return view;

    }


}

