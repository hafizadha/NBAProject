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
    private LatLngBounds USA = new LatLngBounds(
            new LatLng(32.666126, -118.057793), new LatLng(43.885896, -69.149253));


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    try {
        if (teamArrayList.isEmpty()) {
            loadTeam();
        }
        newupload = false;
        Log.d("TEDD","DH PERNAH UPLOAD");
    }catch (NullPointerException e){
        teamArrayList = new ArrayList<>();
        loadTeam();
        newupload = true;
        Log.d("TEDD","BARU UPLOAD");
    }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.mapbox, container, false);
        geocoder = new Geocoder(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setLatLngBoundsForCameraTarget(USA);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                //Call method to set Coordinates into each team details
                if (newupload) {
                    getCords();
                }

                for (NBATeam team : teamArrayList) {
                    String codename = team.getCodename().toLowerCase();
                    int resourceid = getResourceId(codename, "mipmap", getContext());

                    if (team.getCodename().equals("SAS")) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(team.getCord()));
                    }


                    if (resourceid != 0) {
                        team.setResourceid(resourceid);
                        Log.d("TEST", "RES: " + resourceid);
                        MarkerOptions mo = new MarkerOptions()
                                .position(team.getCord())
                                .title(team.getTeamName())
                                .icon(bitmapDescriptor(getContext(), resourceid));
                        googleMap.addMarker(mo);
                    } else {
                        MarkerOptions mo = new MarkerOptions()
                                .position(team.getCord())
                                .title(team.getTeamName());
                        googleMap.addMarker(mo);
                    }

                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            NBATeam display = findTeam(marker);
                            Log.d("Name", "R" + display.getTeamName());


                            Bundle bundle = new Bundle();
                            bundle.putString("name", display.getTeamName());
                            bundle.putString("location", display.getLocation());
                            bundle.putString("arena", display.getArena());


                            CreateTeamInfo(display);


                            return false;
                        }
                    });


                }

            }
        });


        toRoute = view.findViewById(R.id.journey);
        toRoute.setOnClickListener(view -> {
            GraphTraverse fragment = new GraphTraverse(getContext(),teamArrayList);
            getParentFragmentManager().beginTransaction().replace(R.id.main,fragment).addToBackStack(null).commit();
        });
        return view;
    }


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
            geocoder.getFromLocationName(teamdetails.getLocation(), 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(@NonNull List<Address> addresses) {
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        Log.d("TEst", "HYD" + address);
                        Log.d("TEST", "LT" + address.getLatitude());

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
            Log.d("GeocodeCompletion", "All geocoding tasks completed.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Using strings as reference to resources ID
    public static int getResourceId(String resourceName, String resourceType, Context context) {
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }

    //Method to convert resource ID into BitMap Image
    private static BitmapDescriptor bitmapDescriptor(Context context, int resid) {
        Drawable drawable = ContextCompat.getDrawable(context, resid);
        drawable.setBounds(0, 0, 175, 175);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //Create a popup view for team details that can bring to the Route Page
    public void CreateTeamInfo(NBATeam team) {
        Dialog mDialog = new Dialog(getContext());
        mDialog.setContentView(R.layout.teampopupview);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView pic = mDialog.findViewById(R.id.picture);
        TextView name = mDialog.findViewById(R.id.teamname);
        TextView loctext = mDialog.findViewById(R.id.location);
        TextView arenatext = mDialog.findViewById(R.id.arena);


        if (team.getResourceid() != 0) {
            pic.setImageResource(team.getResourceid());
        }

        name.setText("Team: " + team.getTeamName());
        loctext.setText("Location: " + team.getLocation());
        arenatext.setText("Arena: " + team.getArena());

        mDialog.show();
    }

    private void loadTeam(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("team");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                teamArrayList.clear();
                Log.d("FirebaseData", "DataSnapshot content: " + dataSnapshot.toString());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("FirebaseData", "Snapshot: " + snapshot.toString());

                    String arena = snapshot.child("Arena").getValue(String.class);
                    String codename = snapshot.child("Codename").getValue(String.class);
                    String location = snapshot.child("Location").getValue(String.class);
                    String teamname = snapshot.child("TeamName").getValue(String.class);

                    NBATeam nbaTeam = new NBATeam();
                    nbaTeam.setArena(arena);
                    nbaTeam.setLocation(location);
                    nbaTeam.setTeamName(teamname);
                    nbaTeam.setCodename(codename);

                    teamArrayList.add(nbaTeam);
                }
                Log.d("FirebaseData", "Number of teams added: " + teamArrayList.size());
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}

