package com.example.jiheepyo.ggung;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class TextViewActivity extends AppCompatActivity {
    TextView contentView;
    TextView commentCountView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        init();
    }

    public void init(){
        contentView = findViewById(R.id.postText);
        commentCountView = findViewById(R.id.commentCountText);
        Intent intent = getIntent();
        Post data = (Post)intent.getSerializableExtra("post");

        switch (data.getLayout()){
            case 0:
                contentView.setBackgroundResource(R.drawable.paper0);
                break;
            case 1:
                contentView.setBackgroundResource(R.drawable.paper1);
                break;
            case 2:
                contentView.setBackgroundResource(R.drawable.paper2);
                break;
            case 3:
                contentView.setBackgroundResource(R.drawable.paper3);
                break;
        }
        contentView.setText(data.getContents());
        commentCountView.setText("댓글 " + data.getComments().size() + "개");
    }

}
