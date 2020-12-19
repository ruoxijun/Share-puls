package com.example.share.main.addition;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.pojo.Article;
import com.example.share.pojo.User;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private ImageView reset,exit; // 刷新按钮,退出按钮
    private Button att_but; // 关注按钮
    private TextView userName,time,attNum,friendNum;
    private ScrollView scro;
    private int attNumI,friendNumI; // 关注数与粉丝数
    private List<Article> item; // 文章列表
    private ProgressBar evenPro; // 加载更多
    private TextView evenText; // 加载更多的文字提示
    private RecyclerView myblog;
    private RecommendAdapter ra;
    private User user; // 用户信息
    private MyRequest request = new MyRequest(); // 请求对象
    private int i=0;
    // 线程消息处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200: // 数据刷新
                    attNum.setText(String.valueOf(attNumI));
                    friendNum.setText(String.valueOf(friendNumI));
                    evenPro.setVisibility(View.VISIBLE);
                    evenText.setText("加载更多...");
                    ra.update(item);
                    ra.notifyDataSetChanged();
                    break;
                case 101:
                    String s = "好像没有了哦！";
                    evenPro.setVisibility(View.INVISIBLE);
                    evenText.setText(s);
                    break;
                case 103:
                    att_but.setText("关注");
                    att_but.setBackground(getDrawable(R.drawable.bu_att));
                    break;
                case 102:
                    att_but.setText("已关注");
                    att_but.setBackground(getDrawable(R.drawable.bu_att_y));
                    break;
                case 111:
                    String n = "已无数据可加载!! 请刷新后再试!!!";
                    evenPro.setVisibility(View.INVISIBLE);
                    evenText.setText(n);
                    break;
                case 404:
                    Toast.makeText(getApplicationContext(), "网络错误！！！",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 沉浸式(透明)状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        
        // 获取跳转传递的用户信息
        Intent intent = getIntent();
        user= (User) intent.getSerializableExtra("user");

        // 初始化
        init();
        userName.setText(user.getName());
        time.setText(user.getRegistrationTime());
        update(); // 加载数据
        
        myblog.setLayoutManager(new LinearLayoutManager(this));
        ra=new RecommendAdapter(this);
        myblog.setAdapter(ra);
        myblog.setNestedScrollingEnabled(false);
    
        // 添加滚动改变监听
        scro.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY==(scro.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight())){
                    Log.e("--ScrollView--","到达底部");
                    // 请求更多
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                // 请求数据中当前页不为0时
                                if (AppContext.userNextPage != 0){
                                    // 将新请求的数据加入原列表中
                                    item.addAll(request.queryUserArticles(user.getId(),
                                            AppContext.userNextPage));
                                    System.out.println("刷新");
                                    Thread.sleep(500);
                                    handler.sendMessage(MyMessage.getMsg(200));
                                } else {
                                    Thread.sleep(500);
                                    handler.sendMessage(MyMessage.getMsg(111));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendMessage(MyMessage.getMsg(404));
                            }
                        }
                    }.start();
                }
            }
        });
    
        // 关注按钮设置
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // 是否已关注该作者
                    boolean isatt = request.isFriend(user.getId());
                    if (isatt) handler.sendMessage(MyMessage.getMsg(102)); // 显示已关注
                    else handler.sendMessage(MyMessage.getMsg(103)); // 显示关注
                    att_but.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        if (request.isFriend(user.getId())) { // 取消关注
                                            if (request.canAttUser(user.getId())){
                                                handler.sendMessage(MyMessage.getMsg(103));
                                            } else {
                                                handler.sendMessage(MyMessage.getMsg(102));
                                            }
                                        }
                                        else { // 关注
                                            if (request.attUser(user.getId())){
                                                handler.sendMessage(MyMessage.getMsg(102));
                                            } else {
                                                handler.sendMessage(MyMessage.getMsg(103));
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
        
        // 刷新设置
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                update();
            }
        });
    
        View att=findViewById(R.id.att); // 关注页面
        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserActivity.this, "没有权限查看他人关注", Toast.LENGTH_SHORT).show();
            }
        });
        View friend=findViewById(R.id.friend); // 粉丝页面
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserActivity.this, "没有权限查看他人粉丝", Toast.LENGTH_SHORT).show();
            }
        });
        
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    // 刷新方法
    private void update(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // 解析关注数与粉丝数
                    Thread.sleep(200);
                    String json = request.userFriendNum(user.getId());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(json);
                    JsonNode msg = jsonNode.get("msg");
                    attNumI = msg.get("attNum").asInt();
                    friendNumI = msg.get("friendNum").asInt();
                    // 更新文章内容
                    item = request.queryUserArticles(user.getId(), 1);
                    Thread.sleep(300);
                    if (item.size()<7){ //无内容时
                        handler.sendMessage(MyMessage.getMsg(200));
                        handler.sendMessage(MyMessage.getMsg(101));
                    }
                    else
                        handler.sendMessage(MyMessage.getMsg(200));
                } catch (Exception e) {
                    e.printStackTrace();
                    MyMessage.getMsg(404);
                }
            }
        }.start();
    }
    
    // 初始化
    private void init(){
        userName = findViewById(R.id.userName);
        time = findViewById(R.id.time);
        attNum = findViewById(R.id.attNum);
        friendNum = findViewById(R.id.friendNum);
        myblog=findViewById(R.id.myblog); // RecyclerView
        att_but = findViewById(R.id.att_but); // 关注按钮
        reset=findViewById(R.id.reset); // 刷新
        evenPro = findViewById(R.id.evenPro);
        evenText = findViewById(R.id.evenText);
        scro = findViewById(R.id.Scro); // 整个页面
        exit = findViewById(R.id.exit);
    }
}