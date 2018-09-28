package com.example.jiheepyo.ggung;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    ArrayList<Comment> comments;

    public CommentAdapter(ArrayList<Comment> comments){
        this.comments = comments;
        Collections.sort(comments);
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        holder.tvNickname.setText(comments.get(position).getNickname());
        holder.tvContents.setText(comments.get(position).getContents());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname;
        TextView tvContents;
        public ViewHolder(View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.commentNickname);
            tvContents = itemView.findViewById(R.id.commentContents);
        }
    }
}
