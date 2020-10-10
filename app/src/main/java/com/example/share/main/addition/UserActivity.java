package com.example.share.main.addition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.sql.Sqlexe;

import java.util.HashMap;

public class UserActivity extends AppCompatActivity {
    private String user_id;
    private ImageView reset;
    private Button att_but;
    private TextView userName,time,attNum,friendNum;
    private RecyclerView myblog;
    private int i=0;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    
        // 现用户id
        SharedPreferences sp=getSharedPreferences("user",MODE_PRIVATE);
        user_id=sp.getString("userId",null);
        // 跳转传递需要查看的用户id
        Intent intent = getIntent();
        final String id=intent.getStringExtra("id");
        
        // 基本信息
        userName = findViewById(R.id.userName);
        time = findViewById(R.id.time);
        attNum = findViewById(R.id.attNum);
        friendNum = findViewById(R.id.friendNum);
    
        Sqlexe sqlexe = Sqlexe.getInstance(this); // 调用数据库
        HashMap<String,String> user = sqlexe.getUser(id);
        userName.setText(user.get("name"));
        time.setText(user.get("time"));
        // 关注数与粉丝数
        attNum.setText(sqlexe.attNum(id));
        friendNum.setText(sqlexe.friendNum(id));
    
        myblog=findViewById(R.id.myblog); // RecyclerView
        myblog.setLayoutManager(new LinearLayoutManager(this));
        final RecommendAdapter ra=new RecommendAdapter(this,sqlexe.myBlogs(id));
        myblog.setAdapter(ra);
    
        // 关注按钮
        att_but = findViewById(R.id.att_but);
        if (sqlexe.isFriend(user_id,id)){
            att_but.setText("已关注");
            att_but.setBackground(getDrawable(R.drawable.bu_att_y));
        } else {
            att_but.setText("关注");
            att_but.setBackground(getDrawable(R.drawable.bu_att));
        }
        sqlexe.close();
        att_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlexe sqlexe= Sqlexe.getInstance(UserActivity.this);
                if (sqlexe.isFriend(user_id,id)){
                    att_but.setText("关注");
                    att_but.setBackground(getDrawable(R.drawable.bu_att));
                    sqlexe.notAtt(user_id,id);
                } else {
                    att_but.setText("已关注");
                    att_but.setBackground(getDrawable(R.drawable.bu_att_y));
                    sqlexe.att(user_id,id);
                }
                sqlexe.close();
            }
        });
        
        // 刷新设置
        reset=findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                Sqlexe sqlexe= Sqlexe.getInstance(UserActivity.this);
                ra.update(sqlexe.myBlogs(id));
                attNum.setText(sqlexe.attNum(id));
                friendNum.setText(sqlexe.friendNum(id));
                sqlexe.close();
                ra.notifyDataSetChanged();
                Toast.makeText(UserActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
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
    }
}