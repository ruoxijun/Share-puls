package com.example.share.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.blog.BlogActivity;
import com.example.share.pojo.Article;
import java.util.ArrayList;
import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    private Context context;
    private List<Article> item = new ArrayList<>();
    
    public RecommendAdapter(Context context){
        this.context=context;
    }
    
    @NonNull
    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_recom,parent,false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecommendAdapter.ViewHolder holder, final int position) {
        final Article article = item.get(position); // 当前博文信息
        final View view = holder.view;
        TextView title = holder.title;
        TextView mess = holder.mess;
        TextView userName = holder.userName;
        TextView praise = holder.praise; // 点赞数
        title.setText(article.getTitle());
        mess.setText(article.getContext());
        userName.setText(article.getUserName());
        praise.setText(article.getLikeCount()+"");
        
        // 点击项
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, BlogActivity.class);
                // 跳转博文界面并携带博文信息
                intent.putExtra("article",article);
                context.startActivity(intent);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return item.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title,mess,userName,praise,comment;
        private View view;
        public ViewHolder(@NonNull View item) {
            super(item);
            title=item.findViewById(R.id.title);
            mess=item.findViewById(R.id.mess);
            userName=item.findViewById(R.id.userName);
            praise=item.findViewById(R.id.praise);
            view=item.findViewById(R.id.view);
        }
    }
    // 更新文章数据
    public void update(List<Article> item){
        this.item=item;
    }
}
