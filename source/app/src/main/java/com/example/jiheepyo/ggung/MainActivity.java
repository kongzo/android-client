package com.example.jiheepyo.ggung;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    View mainView;

    ImageButton btn1;
    Button btn2;
    FloatingActionButton fab;

    final int gpsRequest = 100;

    Location curLocation;
    private MyLocation location;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //스플래시를 받아주는 구문
        startActivity(new Intent(this, SplashActivity.class));

        init();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkAppPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})){
                    requestLocation();
                } else {
                    if(checkAppPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION})){
                        askPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, gpsRequest);
                    }else{
                        askPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, gpsRequest);
                    }
                }
            }
        });
    }

    public void init(){
        mainView = findViewById(R.id.mainView);

        btn1 = (ImageButton)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        loadingDialog = new LoadingDialog(this);
    }



    boolean checkAppPermission(String[] requestPermission){
        boolean[] requestResult=new boolean [requestPermission.length];
        for(int i=0;i<requestResult.length;i++){
            requestResult[i]=(ContextCompat.checkSelfPermission(this, requestPermission[i])
                    == PackageManager.PERMISSION_GRANTED);
            if(!requestResult[i]){return false;}
        }
        return true;
    }

    void askPermission(String[] requestPermission, int REQ_PERMISSION){
        ActivityCompat.requestPermissions(this, requestPermission, REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case gpsRequest:
                if(checkAppPermission(permissions)) {
                    //퍼미션 동의했을 때 할 일
                    requestLocation();
                }else{
                    cancelMapActivity();
                }
                break;
            default:
                break;
        }
    }

    public boolean isInPalace(){
        curLocation = location.getCurLocation();
        //궁인지 확인
        return true;
    }

    public void requestLocation(){
        location = new MyLocation(getApplicationContext());
        location.setMainActivity(MainActivity.this);
        if(!location.findLocation(false)){
            Snackbar.make(mainView, "현재 위치를 찾을 수 없습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        //로딩중 다이얼로그 시작
        loadingDialog.show();
    }
    public void toMapActivity(){
        //로딩중 다이얼로그 종료
        loadingDialog.dismiss();
        if(isInPalace()) {
            Intent in = new Intent(getApplicationContext(), MapsActivity.class);
            in.putExtra("Latitude", curLocation.getLatitude());
            in.putExtra("Longitude", curLocation.getLongitude());
            startActivity(in);
        } else {
            Snackbar.make(mainView, "현재 위치가 고궁일 때만 서찰을 남길 수 있어요!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void cancelMapActivity(){
        Snackbar.make(mainView, "위치 권한을 승인하셔야 서찰을 남길 수 있어요!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void gotoDetail(int num){
        Intent intent = new Intent(this, ContentsActivity.class);
        intent.putExtra("palaceNumber", num);
        startActivity(intent);
    }

    public void onGyeongbokgung(View view) {
        gotoDetail(1);
    }

    public void onDeoksugung(View view) {
        gotoDetail(2);
    }

    public void onChanggyeonggung(View view) {
        gotoDetail(3);
    }

    public void onChangdeokgung(View view) {
        gotoDetail(4);
    }
}