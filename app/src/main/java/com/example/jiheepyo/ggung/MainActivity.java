package com.example.jiheepyo.ggung;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btn1;
    Button btn2;
    FloatingActionButton fab;

    Button writebutton;
    Button contentsviewbutton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //스플래시를 받아주는 구문
        startActivity(new Intent(this, SplashActivity.class));

        init();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "현재 위치가 고궁일 때만 서찰을 남길 수 있어요!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


    }
});
    }

    public void init(){

        btn1 = (ImageButton)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        writebutton = (Button)findViewById(R.id.writebutton);
        contentsviewbutton = (Button)findViewById(R.id.contentsviewbutton);
    }


    public void onclick(View v){
        switch (v.getId()) {
            case R.id.btn1:
                Intent intent1 = new Intent(this, ContentsActivity.class);
                startActivity(intent1);
                break;

            case R.id.btn2:
                Intent intent2 = new Intent(this, ContentsActivity.class);
                startActivity(intent2);
                break;


            case R.id.writebutton:
                Intent intent3 = new Intent(this, WriteActivity.class);
                startActivity(intent3);
                break;

            case R.id.contentsviewbutton:
                Intent intent4 = new Intent(this, TextViewActivity.class);
                startActivity(intent4);
                break;


        }

    }
}