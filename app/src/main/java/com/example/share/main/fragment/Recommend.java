package com.example.share.main.fragment;

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
import com.example.share.pojo.Article;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;
import java.io.IOException;
import java.util.ArrayList;

import lombok.SneakyThrows;

public class Recommend extends Fragment {
    private ArrayList<Article> item = null;
    private int i=0;
    private RecommendAdapter ra;
    private ImageView reset;
    // 请求
    private MyRequest myRequest = new MyRequest();
    ProgressBar evenPro; // 加载更多滚动
    TextView enenText; // 加载更多文字
    // 线程消息接收处理器
    Handler handler = new Handler(){
        @SneakyThrows
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ra.update(item);
                    ra.notifyDataSetChanged();
                    evenPro.setVisibility(View.VISIBLE);
                    enenText.setText("加载更多...");
                    break;
                case 101:
                    String s = "这里空空如也哦！不如出去转转";
                    evenPro.setVisibility(View.INVISIBLE);
                    enenText.setText(s);
                    break;
                case 111:
                    String n = "已无数据可加载!! 请刷新后再试!!!";
                    evenPro.setVisibility(View.INVISIBLE);
                    enenText.setText(n);
                    break;
                case 404:
                    Toast.makeText(getContext(), "网络请求错误",
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend,container,false);
        return view;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView recom=view.findViewById(R.id.recom);
        recom.setLayoutManager(new LinearLayoutManager(getContext()));
        ra=new RecommendAdapter(getContext());
        recom.setAdapter(ra);
        // 嵌套滚动取消
        recom.setNestedScrollingEnabled(false);
        
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
                                // 请求数据中当前页不为0时
                                if (AppContext.recommendNextPage!= 0){
                                    // 将新请求的列加入原列表中
                                    item.addAll(myRequest.queryArticles(AppContext.recommendNextPage));
                                    Thread.sleep(500);
                                    handler.sendMessage(MyMessage.getMsg(1));
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
        
        // 刷新按钮
        reset=view.findViewById(R.id.reset);
        reset.setVisibility(View.VISIBLE);
        reset.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
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
                    try {
                        item=myRequest.queryArticles(1);
                        Thread.sleep(500);
                        if (item.size()<1) //无内容时
                            handler.sendMessage(MyMessage.getMsg(101));
                        else
                            handler.sendMessage(MyMessage.getMsg(1));
                    } catch(IOException e){
                        Log.e("IOException",e.getMessage());
                        handler.sendMessage(MyMessage.getMsg(404));
                        cycleRequest(); // 请求失败时每5秒重新请求一次
                    }
                }
            }.start();
            }
        });
    }
    
    // 网络出错时循环请求
    private void cycleRequest(){
        do{
            try {
                Thread.sleep(5000);
                item=myRequest.queryArticles(1);
                if (item.size()<1) //无内容时
                    handler.sendMessage(MyMessage.getMsg(101));
                else
                    handler.sendMessage(MyMessage.getMsg(1));
                break;
            } catch (Exception e) {
                handler.sendMessage(MyMessage.getMsg(404));
                Log.e("IOException",e.getMessage());
            }
        }while(true);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 初始化界面
        new Thread(){
            @SneakyThrows
            @Override
            public void run() {
                super.run();
                try {
                    item=myRequest.queryArticles(1);
                    Thread.sleep(500);
                    if (item.size()<1) //无内容时
                        handler.sendMessage(MyMessage.getMsg(101));
                    else
                        handler.sendMessage(MyMessage.getMsg(1));
                } catch(IOException e){
                    Log.e("IOException",e.getMessage());
                    handler.sendMessage(MyMessage.getMsg(404));
                    cycleRequest();
                }
            }
        }.start();
    }
    
    // reset按钮的显示和隐藏
    public void showReset(){ if (reset!=null) reset.setVisibility(View.VISIBLE); }
    public void notShowRset(){ if (reset!=null) reset.setVisibility(View.INVISIBLE); }
}
