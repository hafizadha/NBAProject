package com.example.NBAProject.Journey;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.NBAProject.Journey.Graph.GraphTraverse;
import com.example.NBAProject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class MapFragment extends Fragment {
    ArrayList<NBATeam> teamArrayList;
    Geocoder geocoder;
    View view;
    DatabaseReference databaseReference;
    Button toRoute;
    boolean newupload;

    //Boundaries of the map
    private LatLngBounds USA = new LatLngBounds(
            new LatLng(32.666126, -118.057793), new LatLng(43.885896, -69.149253));


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Doing the try and catch method to avoid loading new data everytime the fragment runs
    try {//Throw exception if the teamArrayList is null
        if (teamArrayList.isEmpty()) {
            loadTeam();
        }
        newupload = false; //First time uplod false
    }catch (NullPointerException e){
        teamArrayList = new ArrayList<>();
        loadTeam();
        newupload = true;
    }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.mapbox, container, false);
        geocoder = new Geocoder(getActivity());

        //Generates the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setLatLngBoundsForCameraTarget(USA); //Bound map to only limit to the United States
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //Setting the built in style for the map

                //Call method to set Coordinates into each team details
                if (newupload) {
                    getCords();
                }

                for (NBATeam team : teamArrayList) {
                    String codename = team.getCodename().toLowerCase();
                    //calling this method to get resource by using codenames of each team
                    //From the resource id, we are able to create icons for markers
                    int resourceid = getResourceId(codename, "mipmap", getContext());


                    //Automatically zoom to SPURS when map loads
                    if (team.getCodename().equals("SAS")) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(team.getCord()));
                    }

                    //If the resource isn't empty
                    if (resourceid != 0) {
                        team.setResourceid(resourceid); //Set resource id into object for later use
                        MarkerOptions mo = new MarkerOptions()
                                .position(team.getCord()) //Positions markers to Map based on coordinates
                                .title(team.getTeamName()) //Set title for the markers
                                .icon(bitmapDescriptor(getContext(), resourceid)); //Generate icons for the markers
                        googleMap.addMarker(mo); //Add markers
                    } else {
                        //If resource ID isn't available, the built in marker icon is generated instead
                        MarkerOptions mo = new MarkerOptions()
                                .position(team.getCord())//Positions markers to Map based on coordinates
                                .title(team.getTeamName());//Set title for the markers
                        googleMap.addMarker(mo);
                    }

                    //When the marker is clicked, a popup of team's information will appear
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            NBATeam display = findTeam(marker);


                            Bundle bundle = new Bundle();
                            bundle.putString("name", display.getTeamName());
                            bundle.putString("location", display.getLocation());
                            bundle.putString("arena", display.getArena());

                            //This method creates the popup dialog
                            CreateTeamInfo(display);
                            return false;
                        }
                    });


                }

            }
        });

        //Button to Route Page
        toRoute = view.findViewById(R.id.journey);
        toRoute.setOnClickListener(view -> {
            GraphTraverse fragment = new GraphTraverse(getContext(),teamArrayList);
            //Replace current fragment with a new one
            getParentFragmentManager().beginTransaction().replace(R.id.main,fragment).addToBackStack(null).commit();
        });
        return view;
    }

    //Find specific NBATeam object from the clicked Marker
    private NBATeam findTeam(Marker marker) {
        for (NBATeam team : teamArrayList) {
            if (marker.getTitle().equals(team.getTeamName())) {
                return team;
            }
        }
        return null;
    }


    //Forward geocoding: Convert addresses into Coordinates to place Markers inside the map
    private void getCords() {
        CountDownLatch latch = new CountDownLatch(teamArrayList.size());
        for (NBATeam teamdetails : teamArrayList) {

            //Get ONLY one result (most accurate address) from the given the teams' location
            geocoder.getFromLocationName(teamdetails.getLocation(), 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(@NonNull List<Address> addresses) {
                    //If an address exists
                    if (!addresses.isEmpty()) {
                        //Get the first addreess from the List
                        Address address = addresses.get(0); //index 0 since there's only one result

                        //Get the Latitude and Longitude and set it into the NBATeam object
                        LatLng cords = new LatLng(address.getLatitude(), address.getLongitude());

                        teamdetails.setCord(cords);

                    } else {
                        Log.d("ERROR", "No location found: " + teamdetails.getLocation());
                    }

                    latch.countDown();
                }

            });
        }
        try {
            latch.await(); // Wait for all geocoding operations to complete
            // Continue with operations after all coordinates have been set
        } catch (InterruptedException e) { //If there's failure in the process
            e.printStackTrace();
        }
    }

    //Using strings as reference to resources ID
    public static int getResourceId(String resourceName, String resourceType, Context context) {
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }

    //Method to create a BitmapDescriptor from a drawable resource for the Google Marker
    private static BitmapDescriptor bitmapDescriptor(Context context, int resid) {
        Drawable drawable = ContextCompat.getDrawable(context, resid); //get Drawable from resource ID
        drawable.setBounds(0, 0, 175, 175); //set size for the drawable
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        //ARGB_8888 means that the pixels are stored in 4 bytes, allowing transparency

        //Draws drawable into canvas and , rendering it into bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //Create a popup view for team details
    public void CreateTeamInfo(NBATeam team) {
        //Setting the popup view layout and attribute
        Dialog mDialog = new Dialog(getContext());
        mDialog.setContentView(R.layout.teampopupview);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //References to Layout Components
        ImageView pic = mDialog.findViewById(R.id.picture);
        TextView name = mDialog.findViewById(R.id.teamname);
        TextView loctext = mDialog.findViewById(R.id.location);
        TextView arenatext = mDialog.findViewById(R.id.arena);


        //Set team logo picture from Resource id into the Imageview of this layour
        if (team.getResourceid() != 0) {
            pic.setImageResource(team.getResourceid());
        }

        //Displaying teams's info by setting text to display
        name.setText("Team: " + team.getTeamName());
        loctext.setText("Location: " + team.getLocation());
        arenatext.setText("Arena: " + team.getArena());

        //Show popup
        mDialog.show();
    }


    //Get teams' details from the database
    private void loadTeam(){
        //Node reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("team");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                teamArrayList.clear(); //Clear to avoid duplication
                //Get the children of the team node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Get the values of children
                    String arena = snapshot.child("Arena").getValue(String.class);
                    String codename = snapshot.child("Codename").getValue(String.class);
                    String location = snapshot.child("Location").getValue(String.class);
                    String teamname = snapshot.child("TeamName").getValue(String.class);

                    //Instantiate new NBATeam and set values into it
                    NBATeam nbaTeam = new NBATeam();
                    nbaTeam.setArena(arena);
                    nbaTeam.setLocation(location);
                    nbaTeam.setTeamName(teamname);
                    nbaTeam.setCodename(codename);

                    teamArrayList.add(nbaTeam);
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}

