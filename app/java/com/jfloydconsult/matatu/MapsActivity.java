package com.jfloydconsult.matatu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jfloydconsult.edas.Utility.SurveyLab;
import com.jfloydconsult.edas.model.GeoPoints;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("GeoPoints");
    private static final String TAG = "MapsActivity";
    private GeoPointMarkerInfoWindowView mGeoPointMarkerInfoWindowView;
    private List<GeoPoints> mGeoPointsList;
    private Button bSTA, bRTK, tryButton;
    public SurveyLab mSurveyLab;

    SupportMapFragment mapFragment;

    LinearLayout secondaryLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        bSTA=findViewById(R.id.bSTA);
        bRTK=findViewById(R.id.bRTK);
        tryButton = findViewById(R.id.activity_maps_try_button);
        secondaryLayout = findViewById(R.id.activity_maps_secondary_layout);

        mSurveyLab = SurveyLab.get(getApplicationContext());

        mGeoPointsList=new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment!=null)
            mapFragment.getMapAsync(this);
        mGeoPointMarkerInfoWindowView = new GeoPointMarkerInfoWindowView(this);
        if (mSurveyLab.checkConnectivity())
            onFetchGeoPoints();
        else{
            secondaryLayout.setVisibility(View.VISIBLE);
            if(mapFragment.getView()!=null)
                mapFragment.getView().setVisibility(View.INVISIBLE);
            bSTA.setVisibility(View.INVISIBLE);
            bRTK.setVisibility(View.INVISIBLE);
        }
        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTryAgain();
            }
        });
    }
    private void onTryAgain() {
        if (mSurveyLab.checkConnectivity()) {
            secondaryLayout.setVisibility(View.INVISIBLE);
            if(mapFragment.getView()!=null)
                mapFragment.getView().setVisibility(View.VISIBLE);
            bSTA.setVisibility(View.VISIBLE);
            bRTK.setVisibility(View.VISIBLE);
            onFetchGeoPoints();
        } else
            Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.setInfoWindowAdapter(mGeoPointMarkerInfoWindowView);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
				Uri geoUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                startActivity(mapIntent);
            }
        });
    }

    public void onAddMarkers(double latitude, double longitude, String title, String snippet){
        // Add a marker in Sydney and move the camera
        if(!title.equals("KMPL")){
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
        }else{
            LatLng kampala = new LatLng(0.335483513, 32.661739244);
            mMap.addMarker(new MarkerOptions()
                    .position(kampala)
                    .title("Kampala")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kampala, 7));
        }
    }
    public void onFetchGeoPoints(){
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                GeoPoints geoPoints = dataSnapshot.getValue(GeoPoints.class);
				mGeoPointsList.add(geoPoints);
                if(geoPoints!=null)
                   onAddMarkers(geoPoints.getGeoLatitude(), geoPoints.getGeoLongitude(), geoPoints.getGeoBase(), geoPoints.getGeoLocation());

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                GeoPoints geoPoints = dataSnapshot.getValue(GeoPoints.class);
              //  if(geoPoints!=null)
                    //Log.d(TAG, "Base:" + geoPoints.getBase());

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String geoPointKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                GeoPoints geoPoints = dataSnapshot.getValue(GeoPoints.class);
               // if(geoPoints!=null)
               //     Log.d(TAG, "Base:" + geoPoints.getBase());

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };
        myRef.addChildEventListener(childEventListener);
    }

    private void onCreateRTKRadius(){
        if (mMap != null)
        {
            for (GeoPoints point :  mGeoPointsList)
            {
               // Fill color of the circle
                    // 0x represents, this is an hexadecimal code
                    // 55 represents percentage of transparency. For 100% transparency, specify 00.
                    // For 0% transparency ( ie, opaque ) , specify ff
                    // The remaining 6 characters(00ff00) specify the fill color
                    CircleOptions circleOptions = new CircleOptions()
                            .center(new LatLng(point.getGeoLatitude(), point.getGeoLongitude()))
                            .radius(50000)
                            .fillColor(0X66B71C1C)
                            .strokeColor(0X66B71C1C)
                            .strokeWidth(1);
                    mMap.addCircle(circleOptions);
            }

        }
    }
    private void onCreateSTARadius(){
        if (mMap != null)
        {
            for (GeoPoints point :  mGeoPointsList)
            {
                // Fill color of the circle
                // 0x represents, this is an hexadecimal code
                // 55 represents percentage of transparency. For 100% transparency, specify 00.
                // For 0% transparency ( ie, opaque ) , specify ff
                // The remaining 6 characters(00ff00) specify the fill color
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(point.getGeoLatitude(), point.getGeoLongitude()))
                        .radius(100000)
                        .fillColor(0x3343A047)
                        .strokeColor(0x3343A047)
                        .strokeWidth(1);
                mMap.addCircle(circleOptions);
            }

        }
    }
}
