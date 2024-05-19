package com.example.NBAProject;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdvancedSearching extends AppCompatActivity {

    ImageView buttonFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        // Get references to the EditText fields
        EditText editTextHeight = findViewById(R.id.editTextHeight);
        EditText editTextWeight = findViewById(R.id.editTextWeight);
        EditText editTextPosition = findViewById(R.id.editTextPosition);
        EditText editTextSalary = findViewById(R.id.editTextSalary);
        ImageView buttonFilter = findViewById(R.id.buttonFilter);

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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mp)
                        .commit();
            }
        });


    }
}

