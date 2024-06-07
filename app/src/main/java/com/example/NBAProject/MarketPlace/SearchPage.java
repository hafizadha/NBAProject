package com.example.NBAProject.MarketPlace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.NBAProject.R;

import java.util.ArrayList;

public class SearchPage extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searchpage, container, false);

        // Get references to the EditText fields in layout file
        //This is where user inputs values, string and numericals
        EditText editTextHeight = view.findViewById(R.id.editTextHeight);
        EditText editTextWeight = view.findViewById(R.id.editTextWeight);
        EditText editTextPosition = view.findViewById(R.id.editTextPosition);
        EditText editTextSalary = view.findViewById(R.id.editTextSalary);

        // This is where the user inputs comparision operands (==,<,>,etc.)
        EditText heightcon = view.findViewById(R.id.heightcond);
        EditText weightcon = view.findViewById(R.id.weightcond);
        EditText salarycon = view.findViewById(R.id.salarycond);
        ImageView buttonFilter = view.findViewById(R.id.buttonFilter);

        //When button is clicked.
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the prompt by user in the form of String ( can be empty )
                String heightInput = editTextHeight.getText().toString();
                String weightInput = editTextWeight.getText().toString();
                String salaryInput = editTextSalary.getText().toString();
                String positionInput = editTextPosition.getText().toString();

                // Get the comparison operators prompted by the user ( can be empty )
                String heightoperand = heightcon.getText().toString();
                String weightoperand = weightcon.getText().toString();
                String salaryoperand = salarycon.getText().toString();

                //List of valid comparison operators ( any characters/string other than these will not be passed )
                ArrayList<String> validOperators = new ArrayList<>();
                validOperators.add("==");
                validOperators.add("<");
                validOperators.add("<=");
                validOperators.add(">");
                validOperators.add(">=");


                // Parse numbers only if input is not empty, otherwise use a default
                int minHeight = heightInput.isEmpty() ? 0 : Integer.parseInt(heightInput);
                int minWeight = weightInput.isEmpty() ? 0 : Integer.parseInt(weightInput);
                int minSalary = salaryInput.isEmpty() ? 0 : Integer.parseInt(salaryInput);


                //Bundle is used to store data and pass to another fragment by settings arguments for it
                Bundle bundle = new Bundle();
                MarketPage mp = new MarketPage(); //Loading back to the Market Page fragment

                //Storing desired data into the bundle with key and value for each.
                bundle.putInt("minH",minHeight);
                //"minH" is the key as reference for the receiver fragment to extract the specific value
                // ( it is case-sensitive)
                bundle.putInt("minW",minWeight);
                bundle.putInt("minSalary",minSalary);


                // Pass position only if provided
                if (!positionInput.isEmpty()) {
                    bundle.putString("pos",positionInput);
                }
                // Pass comparison symbol for height, weight and salary only if provided and it's a valid operator
                if (!heightoperand.isEmpty() && validOperators.contains(heightoperand)) {
                    bundle.putString("heightsym",heightoperand.trim());
                }
                if (!weightoperand.isEmpty() && validOperators.contains(weightoperand)) {
                    bundle.putString("weightsym",weightoperand.trim());
                }
                if (!salaryoperand.isEmpty() && validOperators.contains(salaryoperand)) {
                    bundle.putString("salarysym",salaryoperand.trim());
                }

                //Use this method to pass the data to the MarketPage fragment
                mp.setArguments(bundle);

                // Replace this SearchPage fragment back to the MarketPage fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, mp)
                        .commit();
            }
        });
        return view;

    }


}

