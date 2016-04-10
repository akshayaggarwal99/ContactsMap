package com.example.aka.contactsmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aka.contactsmap.data.api.ContactsMapApi;
import com.example.aka.contactsmap.data.api.model.Contact;
import com.example.aka.contactsmap.data.api.model.ContactsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsFragment extends Fragment implements
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    private static View view;
    private int mPage;
    // Google Map
    public static GoogleMap mMap;
    private MapView mMapView;
    private static Double latitude, longitude;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;



    private Marker mPerth;

    private Marker mSydney;

    private Marker mBrisbane;

    private Marker mAdelaide;

    private Marker mMelbourne;
    private final List<Marker> mMarkerContacts = new ArrayList<>();

    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);

    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);

    static final float COORDINATE_OFFSET = 0.00002f;
    HashMap<String, String> markerLocation;



    public static MapsFragment newInstance() {
        Bundle args = new Bundle();
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        if (container == null) {
//            return null;
//        }
//        view = (FrameLayout) inflater.inflate(R.layout.fragment_maps, container, false);
//        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
//        latitude = 26.78;
//        longitude = 72.56;
//
////        setUpMapIfNeeded(); // For setting up the MapFragment
//        return view;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_maps, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.map);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id
                .coordinatorLayout);


        mMapView.onCreate(savedInstanceState);
        sendrequest();
        mMapView.onResume();// needed to get the map to display immediately
        // Set a listener for info window events.
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

      mMap = mMapView.getMap();
//        // latitude and longitude
//        double latitude = 17.385044;
//        double longitude = 78.486671;
//
//        // create marker
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(latitude, longitude)).title("Hello Maps");

//        // Changing marker icon
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//
//        // adding marker
//        mMap.addMarker(marker);
//        addMarkersToMap();
        mMap.setOnInfoWindowClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(2).build();
       mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }

    /****
     * The mapfragment's id must be removed from the FragmentManager
     * *** or else if the same it is passed on the next time then
     * *** app will crash
     ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            MainActivity.fragmentManager.beginTransaction()
                    .remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
            mMap = null;
        }
    }



    private void addMarkersToMap() {

        // Uses a colored icon.
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane")
                .snippet("Population: 2,074,200")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Uses a custom icon with the info window popping out of the center of the icon.
        mSydney = mMap.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney")
                .snippet("Population: 4,627,300")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .infoWindowAnchor(0.5f, 0.5f));

        // Creates a draggable marker. Long press to drag.
        mMelbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .draggable(true));

        // A few more markers for good measure.
        mPerth = mMap.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth")
                .snippet("Population: 1,738,800"));
        mAdelaide = mMap.addMarker(new MarkerOptions()
                .position(ADELAIDE)
                .title("Adelaide")
                .snippet("Population: 1,213,000"));


        Log.i("contacts_debug",contacts.size()+"");

    }

    public void sendrequest() {
        ContactsMapApi.Factory.getInstance().loadcontacts().enqueue(new Callback<List<ContactsResponse>>() {
            @Override
            public void onResponse(Call<List<ContactsResponse>> call, Response<List<ContactsResponse>> response) {

                List<ContactsResponse> contactsResponseArrayList = response.body();
                ContactsResponse contactsResponse = contactsResponseArrayList.get(0);
                contacts = new ArrayList<>(contactsResponse.getContacts());
//                for(int i=0;i<contacts.size();i++){
//                    String[] location=coordinateForMarker(contacts.get(i).getLatitude(),contacts.get(i).getLongitude());
//                    markerLocation.put(location[0],location[1]);
//                }

                for (int i = 0; i < contacts.size(); i++) {
                    mMarkerContacts.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(contacts.get(i).getLatitude(), contacts.get(i).getLongitude()))
                            .title(contacts.get(i).getName())
                            .snippet(contacts.get(i).getPhone())));


                }

            }

            @Override
            public void onFailure(Call<List<ContactsResponse>> call, Throwable t) {
                Log.e("error", t.toString());
            }
        });


    }




    // Check if any marker is displayed on given coordinate. If yes then decide
// another appropriate coordinate to display this marker. It returns an
// array with latitude(at index 0) and longitude(at index 1).
//    private String[] coordinateForMarker(Double latitude, Double longitude) {
//
//        String[] location = new String[2];
//
//        for (int i = 0; i <= 13; i++) {
//
//            if (mapAlreadyHasMarkerForLocation((latitude + i
//                    * COORDINATE_OFFSET)
//                    + "," + (longitude + i * COORDINATE_OFFSET))) {
//
//                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
//                if (i == 0)
//                    continue;
//
//                if (mapAlreadyHasMarkerForLocation((latitude - i
//                        * COORDINATE_OFFSET)
//                        + "," + (longitude - i * COORDINATE_OFFSET))) {
//
//                    continue;
//
//                } else {
//                    location[0] = latitude - (i * COORDINATE_OFFSET) + "";
//                    location[1] = longitude - (i * COORDINATE_OFFSET) + "";
//                    break;
//                }
//
//            } else {
//                location[0] = latitude + (i * COORDINATE_OFFSET) + "";
//                location[1] = longitude + (i * COORDINATE_OFFSET) + "";
//                break;
//            }
//        }
//
//        return location;
//    }
//
//    // Return whether marker with same location is already on map
//    private boolean mapAlreadyHasMarkerForLocation(String location) {
//        return (markerLocation.containsValue(location));
//    }
    @Override
    public void onInfoWindowClick(final Marker marker) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, marker.getSnippet(), Snackbar.LENGTH_LONG)
                .setAction("CALL", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse(marker.getSnippet()));

                        }catch (Exception ex){
                            Toast.makeText(getActivity().getApplicationContext(),"Your activity is not found",Toast.LENGTH_LONG).show();
                        }

                    }
                });

        snackbar.show();


    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}