package com.example.share.blog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.share.R;
import com.example.share.sql.Sqlexe;
import com.example.share.util.Alert;

public class AddBlogActivity extends AppCompatActivity {
    private String userName,user_id;
    private ImageView close;
    private Button add;
    private EditText title,mess;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);
        
        Intent intent = getIntent();
        userName=intent.getStringExtra("userName"); // 用户名
        SharedPreferences sp=getSharedPreferences("user",MODE_PRIVATE);
        user_id=sp.getString("userId",null);
        
        init();
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
                Alert alert = new Alert(AddBlogActivity.this,"提示","确定提交吗？");
                alert.setLeft("确定", new Alert.OnClick() {
                    @Override
                    public void onClick() {
                        Sqlexe sqlexe=Sqlexe.getInstance(AddBlogActivity.this);
                        sqlexe.addBlog(user_id,title.getText().toString(),
                                mess.getText().toString());
                        sqlexe.close();
                        finish();
                    }
                }).show();
            }
        });
    }
    
    public void init(){
        close=findViewById(R.id.close);
        add=findViewById(R.id.add);
        title=findViewById(R.id.title);
        mess=findViewById(R.id.mess);
    }
}