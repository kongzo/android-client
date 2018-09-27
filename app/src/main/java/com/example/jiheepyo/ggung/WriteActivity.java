package com.example.jiheepyo.ggung;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.concurrent.ExecutionException;

public class WriteActivity extends AppCompatActivity{
    private final static String apiURL = "13.125.78.77:8081/api/message";
    private PostAsync postAsync;
    private int curLayout = 1;
    View writeView;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        init();
    }

    public void init(){
        setClickListener();
        writeView = findViewById(R.id.writeView);
        loadingDialog = new LoadingDialog(this);
    }

    public void setClickListener(){
        ImageButton layoutBtn0 = findViewById(R.id.postLayout0);
        ImageButton layoutBtn1 = findViewById(R.id.postLayout1);
        ImageButton layoutBtn2 = findViewById(R.id.postLayout2);
        ImageButton layoutBtn3 = findViewById(R.id.postLayout3);
        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.postLayout0:
                        chageLayout(0);
                        break;
                    case R.id.postLayout1:
                        chageLayout(1);
                        break;
                    case R.id.postLayout2:
                        chageLayout(2);
                        break;
                    case R.id.postLayout3:
                        chageLayout(3);
                        break;
                    default:
                        break;
                }
            }
        };

        layoutBtn0.setOnClickListener(onClickListener);
        layoutBtn1.setOnClickListener(onClickListener);
        layoutBtn2.setOnClickListener(onClickListener);
        layoutBtn3.setOnClickListener(onClickListener);
    }

    public void onCancel(View view) {
        finish();
    }

    public void onPost(View view) throws ExecutionException, InterruptedException {
        Intent intent = getIntent();
        writePost(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0), getContent(), curLayout);
    }

    public String getContent(){
        EditText editContent = findViewById(R.id.postContent);
        return editContent.getText().toString();
    }

    public void chageLayout(int num){
        EditText editContent = findViewById(R.id.postContent);
        int drawableID = getResources().getIdentifier("m_paper" + num, "drawable", "com.example.jiheepyo.ggung");
        editContent.setBackgroundResource(drawableID);
        curLayout = num;
    }
    public void writePost(double lat, double lng, String content, int layout) throws ExecutionException, InterruptedException {
        postAsync = new PostAsync(lng, lat, content, layout, this);
        if(postAsync.execute().get()){
            Snackbar.make(writeView, "서찰 작성 완료!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            Snackbar.make(writeView, "서찰 작성 실패!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
