package com.example.jiheepyo.ggung;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class TextViewActivity extends AppCompatActivity {
    TextView contentView;
    TextView commentCountView;
    TextView likeView;
    RecyclerView commentView;
    Post data;
    CommentAdapter adapter;

    final String adjective[] = {"능력있는", "매력있는", "친절한", "게으른", "섹시한", "퉁명스러운", "만사가 귀찮은", "불친절한", "아름다운", "잘생긴", "혁신적인", "코딩하는", "그림그리는", "사색하는"};
    final String noun[] = {"토끼", "도롱뇽", "돼지", "유니콘", "양", "개미", "족제비", "고양이", "코끼리", "금붕어", "잉어", "아나콘다"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        init();
    }

    public void init(){
        contentView = findViewById(R.id.postText);
        commentCountView = findViewById(R.id.commentCountText);
        likeView = findViewById(R.id.likes);
        commentView = findViewById(R.id.commentRecyclerView);

        Intent intent = getIntent();
        data = (Post)intent.getSerializableExtra("post");

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
            case 4:
                Picasso.with(this).load(data.getImageURL()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        contentView.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        contentView.setBackgroundResource(R.drawable.paper0);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                break;
        }
        contentView.setText(data.getContents());
        commentCountView.setText("댓글 " + data.getComments().size() + "개");
        likeView.setText("" + data.getLikes());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(data.getComments());
        commentView.setAdapter(adapter);
    }

    public void onLike(View view) throws ExecutionException, InterruptedException {
        LikeAsync likeAsync = new LikeAsync(this);
        int likes = Integer.parseInt(likeView.getText().toString());
        if(likeAsync.execute(data.getIdx()).get()) {
            likeView.setText(++likes + "");
        }
    }

    public void onComment(View view) throws ExecutionException, InterruptedException {
        EditText editText = findViewById(R.id.commentEditText);
        CommentAsnyc commentAsnyc = new CommentAsnyc(this);
        if(commentAsnyc.execute(data.getIdx() + "", editText.getText().toString()).get()){
            data.getComments().add(new Comment(new Date(), "", commentAsnyc.getNickname(), editText.getText().toString()));
            adapter = new CommentAdapter(data.getComments());
            commentView.setAdapter(adapter);
            Snackbar.make(view, "댓글 작성 성공!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
