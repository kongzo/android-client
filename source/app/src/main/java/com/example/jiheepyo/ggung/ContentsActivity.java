package com.example.jiheepyo.ggung;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentsActivity extends AppCompatActivity {
    private ImageView palaceImage;
    private TextView palaceTitle;
    private TextView palaceDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        init();
    }

    public void init(){
        palaceImage = findViewById(R.id.palaceImage);
        palaceTitle = findViewById(R.id.palaceTitle);
        palaceDescription = findViewById(R.id.palaceDescription);

        Intent intent = getIntent();
        int i = intent.getIntExtra("palaceNumber", 0);
        setContents(i);
    }

    public void setContents(int num) {
        int drawableID = getResources().getIdentifier("gung" + num, "drawable", "com.example.jiheepyo.ggung");
        palaceImage.setImageResource(drawableID);
        drawableID = getResources().getIdentifier("palace_title_" + num, "string", "com.example.jiheepyo.ggung");
        palaceTitle.setText(drawableID);
        drawableID = getResources().getIdentifier("palace_description_" + num, "string", "com.example.jiheepyo.ggung");
        palaceDescription.setText(drawableID);
    }

    public void onWeb(View view) {
    }
}
