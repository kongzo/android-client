package com.example.jiheepyo.ggung;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    MapFragment mapFragment;
    GoogleMap googleMap;
    MyLocation myLocation = null;
    ArrayList<Post> posts;
    private Marker currentMarker = null;
    private Circle currentCircle = null;
    private LatLng curLocation;
    private ArrayList<Marker> messages = null;
    private boolean isMapReady = false;
    private boolean isSetFind = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                intent.putExtra("lat", curLocation.latitude);
                intent.putExtra("lng", curLocation.longitude);
                startActivity(intent);
            }
        });

        init();
    }

    public void init(){
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myLocation = new MyLocation(getApplicationContext());
        myLocation.setMapsActivity(MapsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSetFind) {
            myLocation.removeLocationUpdates();
            isSetFind = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isSetFind) {
            myLocation.removeLocationUpdates();
            isSetFind = false;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMapReady && !isSetFind){
            myLocation.findLocation(true);
            isSetFind = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(this);

        Intent intent = getIntent();
        curLocation = new LatLng(intent.getDoubleExtra("Latitude", 0), intent.getDoubleExtra("Longitude", 0));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 14));

        showCurrentMarker(curLocation);
        try {
            findMessages();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!isSetFind) {
            myLocation.findLocation(true);
            isSetFind = true;
        }

    }

    public void findMessages() throws ExecutionException, InterruptedException {
        GetMessagesAsync messagesAsync = new GetMessagesAsync(curLocation, this);
        if(messagesAsync.execute().get()){
            posts = messagesAsync.getPosts();
            showPostMarker();
        }
    }

    public void showPostMarker(){
        if(messages != null){
            for(int i=0;i<messages.size();i++)
                messages.get(i).remove();
        }
        messages = new ArrayList<>();
        for(int i=0;i<posts.size();i++){
            View markerView = LayoutInflater.from(this).inflate(R.layout.post_marker, null);
            ImageView imageView = markerView.findViewById(R.id.postImage);
            LatLng pos = new LatLng(posts.get(i).getLongitude(), posts.get(i).getLatitude());
            int drawableID = getResources().getIdentifier("post0" + posts.get(i).getLayout(), "drawable", "com.example.jiheepyo.ggung");
            imageView.setImageBitmap(resizeImage(drawableID, 180,223));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(posts.get(i).getNickname());
            markerOptions.position(pos);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, markerView)));
            markerOptions.snippet(posts.get(i).getIdx()+"");
            messages.add(googleMap.addMarker(markerOptions));
        }
    }

    public void showCurrentMarker(LatLng location){
        if(currentMarker != null)
            currentMarker.remove();
        if(currentCircle != null)
            currentCircle.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeImage(R.drawable.cur_position, 130, 130)));
        currentMarker = googleMap.addMarker(markerOptions);

        CircleOptions circleOptions = new CircleOptions().center(location).radius(1000).strokeWidth(0f).fillColor(Color.parseColor("#330000ff"));
        currentCircle = googleMap.addCircle(circleOptions);
    }

    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getSnippet() != null) {
            int idx = Integer.parseInt(marker.getSnippet());
            for (int i = 0; i < posts.size(); i++) {
                if (posts.get(i).getIdx() == idx) {
                    Intent intent = new Intent(this, TextViewActivity.class);
                    intent.putExtra("post", posts.get(i));
                    startActivity(intent);
                    return true;
                }
            }
        }
        return false;
    }

    public void renewPosition() throws ExecutionException, InterruptedException {
        Location location = myLocation.getCurLocation();
        curLocation = new LatLng(location.getLatitude(), location.getLongitude());
        showCurrentMarker(curLocation);
        findMessages();
    }

    public Bitmap resizeImage(int rID, int width, int height){
        Bitmap bitmap =  BitmapFactory.decodeResource(getResources(), rID);
        bitmap = Bitmap.createScaledBitmap(bitmap,width,height, true);
        return bitmap;
    }
}
