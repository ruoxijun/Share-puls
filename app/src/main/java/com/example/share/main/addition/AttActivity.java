package com.example.share.main.addition;

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
import com.example.share.main.adapter.AttAdapter;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.sql.Sqlexe;

import java.util.ArrayList;
import java.util.HashMap;

public class AttActivity extends AppCompatActivity {
    private String user_id;
    private TextView title;
    private RecyclerView list;
    private int i=0;
    private ImageView reset;
    private int mycase;
    AttAdapter ra;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att);
    
        Intent intent = getIntent(); // 跳转情况
        mycase=intent.getIntExtra("case",0);
        
        // 提取用户id
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        user_id=sp.getString("userId",null);
        
        title=findViewById(R.id.title);
        list=findViewById(R.id.list);
        
        list.setLayoutManager(new LinearLayoutManager(this));
        // 调用数据库
        Sqlexe sqlexe = Sqlexe.getInstance(this);
        ArrayList<HashMap<String,String>> attlist = null;
        switch (mycase){
            case 1:
                attlist = sqlexe.getUserAtt(user_id);
                break;
            case 2:
                title.setText("我的粉丝");
                attlist = sqlexe.getUserFriend(user_id);
                break;
        }
        ra=new AttAdapter(this,attlist);
        list.setAdapter(ra);
        sqlexe.close(); // 关闭数据库
    
        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                Sqlexe sqlexe= Sqlexe.getInstance(AttActivity.this);
                ArrayList<HashMap<String,String>> attlist = null;
                switch (mycase){
                    case 1:
                        attlist = sqlexe.getUserAtt(user_id);
                        break;
                    case 2:
                        attlist = sqlexe.getUserFriend(user_id);
                        break;
                }
                ra.update(attlist);
                sqlexe.close();
                ra.notifyDataSetChanged();
                Toast.makeText(AttActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Sqlexe sqlexe= Sqlexe.getInstance(AttActivity.this);
        ArrayList<HashMap<String,String>> attlist = null;
        switch (mycase){
            case 1:
                attlist = sqlexe.getUserAtt(user_id);
                break;
            case 2:
                attlist = sqlexe.getUserFriend(user_id);
                break;
        }
        ra.update(attlist);
        sqlexe.close();
        ra.notifyDataSetChanged();
    }
}