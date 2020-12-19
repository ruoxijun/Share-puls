package com.example.share.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.blog.BlogActivity;
import com.example.share.pojo.Comment;
import com.example.share.util.AppContext;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private View.OnClickListener click;
    private String articleUserId;
    private List<Comment> comments = new ArrayList<>();
    
    public CommentAdapter(String articleUserId){
        this.articleUserId = articleUserId;
    }
    
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment,parent,false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, final int position) {
        final Comment comment = comments.get(position); // 当前评论
        holder.userName.setText(comment.getUserName());
        holder.time.setText(comment.getDate());
        holder.floor.setText(position+1+"楼");
        holder.mgs.setText(comment.getContent());
        // 当前博文用户id等于评论id为楼主
        if (articleUserId.equals(comment.getUserId())){
            holder.tag.setVisibility(View.VISIBLE);
        }
        // 当前评论为当前用户时
        if (AppContext.user.getId().equals(comment.getUserId())) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
        // 删除评论
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlogActivity.commentId = comment.getId();
                comments.remove(position);
                click.onClick(v);
            }
        });
    }
    
    @Override
    public int getItemCount() { return comments.size(); }
    
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userName , time , mgs , tag ,floor,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            time = itemView.findViewById(R.id.time);
            mgs = itemView.findViewById(R.id.mgs);
            tag = itemView.findViewById(R.id.tag);
            floor = itemView.findViewById(R.id.floor);
            delete = itemView.findViewById(R.id.delete);
        }
    }
    
    public void update(List<Comment> comments){
        this.comments = comments;
    }
    
    public void setClick(View.OnClickListener click){
        this.click = click;
    }
}
