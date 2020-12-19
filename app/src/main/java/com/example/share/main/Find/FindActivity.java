package com.example.share.main.Find;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.pojo.Article;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;

import java.io.IOException;
import java.util.List;

public class FindActivity extends AppCompatActivity {
    private ImageView exit; // 退出
    private TextView findBtn; // 搜索按钮
    private EditText findEdit; // 搜索框
    private ScrollView scro;
    private RecyclerView item;
    private View even;
    private ProgressBar evenPro;
    private TextView evenText;
    
    private RecommendAdapter ra;
    private MyRequest request = new MyRequest();
    private String requestCon = "";
    private List<Article> articles;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200: // 数据刷新
                    evenPro.setVisibility(View.VISIBLE);
                    evenText.setText("加载更多...");
                    ra.update(articles);
                    ra.notifyDataSetChanged();
                    break;
                case 101:
                    String s = "好像没有了哦！";
                    evenPro.setVisibility(View.INVISIBLE);
                    evenText.setText(s);
                    break;
                case 111:
                    String n = "已无数据可加载!! 请稍后再试!!!";
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
        // 去除弹出键盘布局改变
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_find);
        
        init();
        item.setLayoutManager(new LinearLayoutManager(this));
        ra = new RecommendAdapter(this);
        item.setNestedScrollingEnabled(false);
        item.setAdapter(ra);
        
        // 点击退出按钮
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 点击搜索
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCon = findEdit.getText().toString().trim();
                if ("".equals(requestCon) || findEdit.getText()==null){
                    Toast.makeText(FindActivity.this, "搜索不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                even.setVisibility(View.VISIBLE);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            articles = request.findArticles(requestCon, 1);
                            handler.sendMessage(MyMessage.getMsg(200));
                            if (articles.size() < 7)
                                handler.sendMessage(MyMessage.getMsg(101));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    
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
                                    if (AppContext.findNextPage != 0) {
                                        // 将新请求的数据加入原列表中
                                        articles.addAll(request.findArticles(requestCon,
                                                AppContext.findNextPage));
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
    }
    
    // 初始化
    private void init(){
        exit = findViewById(R.id.exit);
        findBtn = findViewById(R.id.findBtn);
        findEdit = findViewById(R.id.findEdit);
        scro = findViewById(R.id.Scro);
        item = findViewById(R.id.item);
        even = findViewById(R.id.even);
        evenPro = findViewById(R.id.evenPro);
        evenText = findViewById(R.id.evenText);
    }
}