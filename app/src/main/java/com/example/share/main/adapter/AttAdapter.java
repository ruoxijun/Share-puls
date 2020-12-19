package com.example.share.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.main.addition.UserActivity;
import com.example.share.pojo.User;
import com.example.share.util.MyMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AttAdapter extends RecyclerView.Adapter<AttAdapter.ViewHolder> {
    private Context context;
    private List<User> item = new ArrayList<>();
    private MyRequest request = new MyRequest();
    
    public AttAdapter(Context context){
        this.context=context;
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
        final User user= item.get(position); // 当前项数据
        userName.setText(user.getName());
        time.setText(user.getRegistrationTime());
    
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 101:
                        att.setText("关注");
                        att.setBackground(context.getDrawable(R.drawable.bu_att));
                        break;
                    case 102:
                        att.setText("已关注");
                        att.setBackground(context.getDrawable(R.drawable.bu_att_y));
                        break;
                    case 404:
                        Toast.makeText(context, "网络错误",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // 是否已关注该作者
                    boolean isatt = request.isFriend(user.getId());
                    if (isatt) handler.sendMessage(MyMessage.getMsg(102)); // 显示已关注
                    else handler.sendMessage(MyMessage.getMsg(101)); // 显示关注
                    att.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        if (request.isFriend(user.getId())) { // 取消关注
                                            if (request.canAttUser(user.getId())){
                                                handler.sendMessage(MyMessage.getMsg(101));
                                            } else {
                                                handler.sendMessage(MyMessage.getMsg(102));
                                            }
                                        }
                                        else { // 关注
                                            if (request.attUser(user.getId())){
                                                handler.sendMessage(MyMessage.getMsg(102));
                                            } else {
                                                handler.sendMessage(MyMessage.getMsg(101));
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        handler.sendMessage(MyMessage.getMsg(404));
                                    }
                                }
                            }.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(MyMessage.getMsg(404));
                }
            }
        }.start();
        
        // 点击项
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserActivity.class);
                intent.putExtra("user",user);
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
    public void update(List<User> item){
        this.item=item;
    }
}
