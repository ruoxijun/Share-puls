package com.example.share.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.blog.BlogActivity;
import com.example.share.main.addition.UserActivity;
import com.example.share.sql.Sqlexe;

import java.util.ArrayList;
import java.util.HashMap;


public class AttAdapter extends RecyclerView.Adapter<AttAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HashMap<String,String>> item;
    private String user_id;
    
    public AttAdapter(Context context, ArrayList<HashMap<String,String>> item){
        this.context=context;
        this.item=item;
        SharedPreferences sp = context.getSharedPreferences("user",context.MODE_PRIVATE);
        user_id=sp.getString("userId",null);
    }
    @NonNull
    @Override
    public AttAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_att_list,parent,false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull AttAdapter.ViewHolder holder, final int position) {
        View view = holder.view;
        TextView userName = holder.userName;
        TextView time = holder.time;
        final TextView att = holder.att;
        final HashMap<String,String> i= item.get(position); // 当前项数据
        userName.setText(i.get("name"));
        time.setText(i.get("time"));
        
        // 设置关注按钮
        Sqlexe sqlexe= Sqlexe.getInstance(context);
        if (sqlexe.isFriend(user_id,i.get("id"))){
            att.setText("已关注");
            att.setBackground(context.getDrawable(R.drawable.bu_att_y));
        } else {
            att.setText("关注");
            att.setBackground(context.getDrawable(R.drawable.bu_att));
        }
        sqlexe.close();
        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlexe sqlexe= Sqlexe.getInstance(context);
                if (sqlexe.isFriend(user_id,i.get("id"))){
                    att.setText("关注");
                    att.setBackground(context.getDrawable(R.drawable.bu_att));
                    sqlexe.notAtt(user_id,i.get("id"));
                } else {
                    att.setText("已关注");
                    att.setBackground(context.getDrawable(R.drawable.bu_att_y));
                    sqlexe.att(user_id,i.get("id"));
                }
                sqlexe.close();
            }
        });
        
        // 点击项
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserActivity.class);
                intent.putExtra("id",i.get("id"));
                context.startActivity(intent);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return item.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userName,time,att;
        private View view;
        public ViewHolder(@NonNull View item) {
            super(item);
            userName=item.findViewById(R.id.userName);
            time=item.findViewById(R.id.time);
            att=item.findViewById(R.id.att);
            view=item.findViewById(R.id.view);
        }
    }
    // 更新数据
    public void update(ArrayList<HashMap<String,String>> item){
        this.item=item;
    }
}
