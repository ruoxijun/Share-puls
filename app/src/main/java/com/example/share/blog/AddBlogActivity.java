package com.example.share.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.pojo.Article;
import com.example.share.util.Alert;
import com.example.share.util.MyMessage;

import java.io.IOException;
import java.io.Serializable;

public class AddBlogActivity extends AppCompatActivity {
    private ImageView close;
    private TextView add; // 发布（修改按钮）
    private EditText title,mess; // 标题和内容
    private MyRequest request = new MyRequest(); // 请求对象
    private String titleStr,contextStr; // 标题文字和内容文字
    private Intent intent;
    private Article article;
    private boolean articleExist = false;
    // 线程消息处理对象
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    Toast.makeText(AddBlogActivity.this, "提交成功！",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 101: // 修改文章
                    Toast.makeText(AddBlogActivity.this, "修改成功！",
                            Toast.LENGTH_SHORT).show();
                    setResult(1,intent);
                    finish();
                    break;
                case 404:
                    Toast.makeText(AddBlogActivity.this, "网络异常，请稍后再试。",
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
        setContentView(R.layout.activity_add_blog);
        
        init(); // 初始化
        
        // 关闭页面
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 提交内容
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleStr = title.getText().toString();
                contextStr = mess.getText().toString();
                if (titleStr==null || titleStr.equals("")){
                    Toast.makeText(AddBlogActivity.this, "标题不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contextStr==null || contextStr.equals("")){
                    Toast.makeText(AddBlogActivity.this, "文章内容不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (articleExist){ // 当前状态为修改文章
                    if (titleStr.equals(article.getTitle()) &&
                            contextStr.equals(article.getContext())){ // 当未做任何更改时
                        Toast.makeText(AddBlogActivity.this, "您还没做更任何改呢！",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Alert alert = new Alert(AddBlogActivity.this,"提示",
                            "确定修改吗？");
                    alert.setLeft("确定", new Alert.OnClick() {
                        @Override
                        public void onClick() {
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        request.updateArticle(article.getArticleId(),
                                                titleStr,contextStr);
                                        intent.putExtra("title",titleStr);
                                        intent.putExtra("context",contextStr);
                                        handler.sendMessage(MyMessage.getMsg(101));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        handler.sendMessage(MyMessage.getMsg(404));
                                    }
                                }
                            }.start();
                        }
                    }).show();
                    return;
                }
                // 添加文章
                Alert alert = new Alert(AddBlogActivity.this,"提示","确定提交吗？");
                alert.setLeft("确定", new Alert.OnClick() {
                    @Override
                    public void onClick() {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    request.addArticle(titleStr,contextStr);
                                    handler.sendMessage(MyMessage.getMsg(100));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    handler.sendMessage(MyMessage.getMsg(404));
                                }
                            }
                        }.start();
                    }
                }).show();
            }
        });
    }
    
    // 初始化
    public void init(){
        close=findViewById(R.id.close);
        add=findViewById(R.id.add);
        title=findViewById(R.id.title);
        mess=findViewById(R.id.mess);
        
        intent = getIntent(); // 获取博文对象
        article = (Article) intent.getSerializableExtra("article");
        if (article!=null) { articleExist = true; } // 是否有博文对象传入
        if (articleExist){ // 是需要修改的页面时
            add.setText("修改");
            title.setText(article.getTitle());
            mess.setText(article.getContext());
        }
    }
}