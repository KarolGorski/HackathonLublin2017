package com.mojzesze.hackathonlublin2017;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FindRoute extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List<BikesPlace> bikesPlaces = new ArrayList<>();
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mLast;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }
        mLast = new LatLng(51.2455, 22.5428);

        new XmlParser().execute("https://nextbike.net/maps/nextbike-official.xml");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for(BikesPlace bp: bikesPlaces) {
            LatLng latLng = new LatLng(bp.getLat(), bp.getLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(bp.getName() + ", " + "Liczba rowerów: " + bp.getBikes()));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private class XmlParser extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) ((new URL(urls[0]).openConnection()));
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();

                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser xmlParser = xmlFactoryObject.newPullParser();
                xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlParser.setInput(stream, null);

                parseXml(xmlParser);

                stream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(FindRoute.this);

            sendRequest();
        }

        private void parseXml(XmlPullParser myParser) {
            int event;
            try {
                myParser.next();
                event = myParser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();
                    if ("city".equals(name) && event==XmlPullParser.START_TAG) {
                        if("Lublin".equals(myParser.getAttributeValue(null, "name"))) {
                            do {
                                myParser.next();
                                if("place".equals(myParser.getName()) && myParser.getEventType()==XmlPullParser.START_TAG) {
                                    bikesPlaces.add(new BikesPlace(Double.valueOf(myParser.getAttributeValue(null, "lat")),
                                            Double.valueOf(myParser.getAttributeValue(null, "lng")),
                                            myParser.getAttributeValue(null, "name"),
                                            Integer.valueOf(myParser.getAttributeValue(null, "bikes")),
                                            Integer.valueOf(myParser.getAttributeValue(null, "free_racks"))
                                    ));

                                    Log.d("xd", myParser.getAttributeValue(null, "name"));

                                }

                            } while(!"city".equals(myParser.getName()));
                        }
                    }
                    event = myParser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //metody z mapy activity

    private void sendRequest(){
        //todo polacz send requesta zeby go gdzies wywolac
        //todo zalacz jakis destination, to na dole przykladowe jest
        LatLng sample = new LatLng(51.40,22.10);

        try {
            Log.d("xd", sample.toString());
            LatLng closestStart;
            if (mLastLocation == null){
                closestStart = findClosestPoint(mLast);
                new DirectionFinder(this, closestStart.latitude + "," + closestStart.longitude, MapActivity.destination).execute();
            } else {
                closestStart = findClosestPoint(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                new DirectionFinder(this, closestStart.latitude+ "," + closestStart.longitude, MapActivity.destination).execute();
            }
        } catch (SecurityException e){
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.GREEN).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private LatLng findClosestPoint(LatLng latLng) {
        LatLng closest = new LatLng(bikesPlaces.get(0).getLat(), bikesPlaces.get(0).getLng());
        double minDistance = Math.sqrt(Math.pow(latLng.latitude-bikesPlaces.get(0).getLat(), 2) + Math.pow(latLng.longitude-bikesPlaces.get(0).getLng(), 2));
        double distance;

        for(BikesPlace bp: bikesPlaces) {
            distance = Math.sqrt(Math.pow(latLng.latitude-bp.getLat(), 2) + Math.pow(latLng.longitude-bp.getLng(), 2));
            if(distance < minDistance) {
                minDistance = distance;
                closest = new LatLng(bp.getLat(), bp.getLng());
            }
        }
        return closest;
    }


}