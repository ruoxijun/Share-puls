package com.example.share.main.addition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.main.adapter.AttAdapter;
import com.example.share.pojo.User;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;

import java.io.IOException;
import java.util.List;

public class AttActivity extends AppCompatActivity {
    private String user_id = AppContext.user.getId(); // 用户id
    private TextView title;
    private RecyclerView list;
    private int i=0;
    private ImageView reset;
    private int mycase; // 跳转类型
    private AttAdapter ra;
    private MyRequest request = new MyRequest(); // 请求类
    private List<User> users; // 存储数据集合
    // 线程消息处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    ra.notifyDataSetChanged();
                    Toast.makeText(AttActivity.this, "加载成功",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 404:
                    Toast.makeText(AttActivity.this, "网络错误！！！请稍后刷新",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 沉浸式(透明)状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att);
        
        Intent intent = getIntent(); // 跳转情况
        mycase=intent.getIntExtra("case",0);
        
        title=findViewById(R.id.title);
        list=findViewById(R.id.list);
        
        // 设置布局
        list.setLayoutManager(new LinearLayoutManager(this));
        ra=new AttAdapter(this);
        list.setAdapter(ra);
    
        // 刷新按钮
        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                switch (mycase){
                    case 1:
                        attUsers();
                        break;
                    case 2:
                        friendUsers();
                        break;
                }
            }
        });
    }
    
    @Override // 页面可见时
    protected void onResume() {
        super.onResume();
        
        // 根据用户选择显示视图
        switch (mycase){
            case 1: // 用户选择关注页面时
                attUsers();
                break;
            case 2: // 用户选择为粉丝界面时
                friendUsers();
                title.setText("我的粉丝");
                break;
        }
        
    }
    
    // 请求粉丝数据
    private void friendUsers(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    users = request.friendUsers(user_id);
                    ra.update(users);
                    handler.sendMessage(MyMessage.getMsg(100));
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(MyMessage.getMsg(404));
                }
            }
        }.start();
    }
    
    // 请求关注数据
    private void attUsers(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    users = request.attUsers(user_id);
                    ra.update(users);
                    handler.sendMessage(MyMessage.getMsg(100));
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(MyMessage.getMsg(404));
                }
            }
        }.start();
    }
}