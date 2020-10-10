package com.example.share.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.blog.BlogActivity;
import com.example.share.sql.Sqlexe;

import java.util.ArrayList;
import java.util.HashMap;


public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HashMap<String,String>> item;
    
    public RecommendAdapter(Context context,ArrayList<HashMap<String,String>> item){
        this.context=context;
        this.item=item;
    }
    @NonNull
    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_recom,parent,false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecommendAdapter.ViewHolder holder, final int position) {
        View view = holder.view;
        TextView title = holder.title;
        TextView mess = holder.mess;
        TextView userName = holder.userName;
        TextView praise = holder.praise;
        TextView comment = holder.comment;
        final HashMap<String,String> i= item.get(position);
        title.setText(i.get("title"));
        mess.setText(i.get("content"));
        // 获取用户名
        Sqlexe sqlexe= Sqlexe.getInstance(context);
        HashMap<String,String> u=sqlexe.getUser(i.get("userName"));
        userName.setText(u.get("name"));
        sqlexe.close();
        praise.setText(i.get("like"));
        comment.setText("0");
        
        // 点击项
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, BlogActivity.class);
                intent.putExtra("id",i.get("id"));
                context.startActivity(intent);
            }
        });
        
        // 长按此项
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View view=LayoutInflater.from(context).inflate(R.layout.popup,null);
                final Button in=view.findViewById(R.id.in);
                final Button de=view.findViewById(R.id.de);
                final Button can=view.findViewById(R.id.can);
                PopupWindow pop=new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                //想PopupWindow点击外侧时消失需要设置一个背景，才能成功
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.setFocusable(true);//获取焦点
                pop.setOutsideTouchable(true);//点击外侧消失
                pop.showAtLocation(view, Gravity.CENTER,0,0);//居中
                pop.dismiss(); // 菜单清除
                return true; // 中断事件传递
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
            comment=item.findViewById(R.id.comment);
            view=item.findViewById(R.id.view);
        }
    }
    // 更新文章数据
    public void update(ArrayList<HashMap<String,String>> item){
        this.item=item;
    }
}
