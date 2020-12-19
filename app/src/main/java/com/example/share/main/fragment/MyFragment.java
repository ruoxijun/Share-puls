package com.example.share.main.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.main.addition.AttActivity;
import com.example.share.pojo.Article;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class MyFragment extends Fragment {
    private String user_id = AppContext.user.getId();
    private ImageView reset;
    private TextView userName,time, attNumTv, friendNumTv;
    private RecyclerView myblog;
    private int i=0;
    private RecommendAdapter ra;
    private int attNum,friendNum; // 关注数与粉丝数
    MyRequest request = new MyRequest(); // 请求类
    List<Article> item = new ArrayList<>(); // 数据
    ProgressBar evenPro; // 加载更多滚动
    TextView enenText; // 加载更多文字
    // 线程消息处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200: // 数据刷新
                    attNumTv.setText(String.valueOf(attNum));
                    friendNumTv.setText(String.valueOf(friendNum));
                    evenPro.setVisibility(View.VISIBLE);
                    enenText.setText("加载更多...");
                    ra.update(item);
                    ra.notifyDataSetChanged();
                    break;
                case 101:
                    String s = "好像没有了哦！";
                    evenPro.setVisibility(View.INVISIBLE);
                    enenText.setText(s);
                    break;
                case 111:
                    String n = "已无数据可加载!! 请刷新后再试!!!";
                    evenPro.setVisibility(View.INVISIBLE);
                    enenText.setText(n);
                    break;
                case 404:
                    Toast.makeText(getContext(), "网络错误！！！",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item_my,container,false);
        return view;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 基本信息
        userName = view.findViewById(R.id.userName);
        time = view.findViewById(R.id.time);
        attNumTv = view.findViewById(R.id.attNum);
        friendNumTv = view.findViewById(R.id.friendNum);
        
        // 设置用户名和注册时间
        userName.setText(AppContext.user.getName());
        time.setText(AppContext.user.getRegistrationTime());
        
        myblog=view.findViewById(R.id.myblog); // RecyclerView
        myblog.setNestedScrollingEnabled(false); // 关闭自动获取焦点
        myblog.setLayoutManager(new LinearLayoutManager(getContext()));
        ra=new RecommendAdapter(getContext());
        myblog.setAdapter(ra);
    
        // 加载更多初始化
        evenPro = view.findViewById(R.id.evenPro);
        enenText = view.findViewById(R.id.evenText);
        // ScrollView
        final ScrollView scro = view.findViewById(R.id.Scro);
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
                                // 防止activity开启后卡死现象
                                if (AppContext.user !=null && handler != null) {
                                    // 请求数据中当前页不为0时
                                    if (AppContext.userNextPage != 0) {
                                        // 将新请求的数据加入原列表中
                                        item.addAll(request.queryUserArticles(AppContext.user.getId(),
                                                AppContext.userNextPage));
                                        System.out.println("刷新");
                                        Thread.sleep(500);
                                        handler.sendMessage(MyMessage.getMsg(200));
                                    } else {
                                        Thread.sleep(500);
                                        handler.sendMessage(MyMessage.getMsg(111));
                                    }
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
        
        // 刷新设置
        reset=view.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                // 滚动到顶部
                scro.fullScroll(ScrollView.FOCUS_UP);
                new Thread(){
                    @SneakyThrows
                    @Override
                    public void run() {
                        super.run();
                        updateList(); // 刷新数据
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        
        View att=view.findViewById(R.id.att); // 跳转我的关注页面
        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AttActivity.class);
                intent.putExtra("case",1);
                startActivity(intent);
            }
        });
        View friend=view.findViewById(R.id.friend); // 跳转我的粉丝页面
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AttActivity.class);
                intent.putExtra("case",2);
                startActivity(intent);
            }
        });
    }
    
    @Override // 当页面可见时刷新数据
    public void onResume() {
        super.onResume();
        updateList();
    }
    
    // 更新我的页面数据
    private void updateList(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // 解析关注数与粉丝数
                    Thread.sleep(200);
                    String json = request.userFriendNum(AppContext.user.getId());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(json);
                    JsonNode msg = jsonNode.get("msg");
                    attNum = msg.get("attNum").asInt();
                    friendNum = msg.get("friendNum").asInt();
                    Log.e("userFriendNum", "关注数："+attNum+"，粉丝数："+friendNum);
                    //
                    item = request.queryUserArticles(user_id, 1);
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
    
    // reset按钮的显示和隐藏
    public void showReset(){ if (reset!=null) reset.setVisibility(View.VISIBLE); }
    public void notShowRset(){ if (reset!=null) reset.setVisibility(View.INVISIBLE); }
}
