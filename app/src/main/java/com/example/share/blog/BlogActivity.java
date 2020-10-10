package com.example.share.blog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.main.adapter.CommentAdapter;
import com.example.share.sql.Sqlexe;

import java.util.HashMap;

public class BlogActivity extends AppCompatActivity {
    private TextView title,mess; // 博文标题和内容
    private ImageView avatar; // 头像
    private TextView userName,time,att; // 作者，时间，是否关注
    private String blog_id;
    private String user_id;
    private ImageView up; // 点赞
    private TextView like; // 点赞数
    private TextView blogtime; // 博文时间
    private HashMap<String,String> blog;
    private HashMap<String, String> user;
    
    private RecyclerView comments;//评论列表
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        
        init(); // 初始化
        Intent intent = getIntent();
        blog_id=intent.getStringExtra("id");
        // 获取用户id
        SharedPreferences sp=getSharedPreferences("user",MODE_PRIVATE);
        user_id=sp.getString("userId",null);
        // 调用数据库
        final Sqlexe sqlexe=Sqlexe.getInstance(this);
        blog=sqlexe.getBlog(blog_id);
        title.setText(blog.get("title"));
        mess.setText(blog.get("content"));
        blogtime.setText("文章最后更改发布与："+blog.get("date"));
        // 用户信息
        user=sqlexe.getUser(blog.get("user_id"));
        userName.setText(user.get("name"));
        time.setText(user.get("time"));
        like.setText(blog.get("like"));
        
        // 关注按钮
        if (user_id.equals(user.get("id"))){
            att.setVisibility(View.GONE);
        } else {
            boolean isatt=sqlexe.isFriend(user_id,user.get("id"));
            if (isatt){
                att.setText("已关注");
                att.setBackground(getDrawable(R.drawable.bu_att_y));
            }
            else{
                att.setText("关注");
                att.setBackground(getDrawable(R.drawable.bu_att));
            }
        }
        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlexe exe=Sqlexe.getInstance(BlogActivity.this);
                if (!exe.isFriend(user_id,user.get("id"))){
                    att.setText("已关注");
                    att.setBackground(getDrawable(R.drawable.bu_att_y));
                    exe.att(user_id,user.get("id"));
                } else {
                    att.setText("关注");
                    att.setBackground(getDrawable(R.drawable.bu_att));
                    exe.notAtt(user_id,user.get("id"));
                }
                exe.close();
            }
        });
        // 关闭数据库
        sqlexe.close();
        
        // 点赞
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(user_id.equals(user.get("id")))) {
                    int i = Integer.parseInt(like.getText().toString());
                    i++;
                    like.setText(String.valueOf(i));
                    Sqlexe s = Sqlexe.getInstance(BlogActivity.this);
                    s.addLike(blog_id, String.valueOf(i));
                    s.close();
                } else {
                    Toast.makeText(BlogActivity.this, "亲不能给自己点赞哦！点赞出门下拐哟！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // 评论列表
        comments.setLayoutManager(new LinearLayoutManager(this));
        comments.setAdapter(new CommentAdapter());
    }
    
    private void init(){
        title=findViewById(R.id.title);
        mess=findViewById(R.id.mess);
        avatar=findViewById(R.id.avatar);
        userName=findViewById(R.id.userName);
        time=findViewById(R.id.time);
        att=findViewById(R.id.att);
        up=findViewById(R.id.up);
        like=findViewById(R.id.like);
        blogtime = findViewById(R.id.blogtime);
        comments = findViewById(R.id.comments);
    }
}