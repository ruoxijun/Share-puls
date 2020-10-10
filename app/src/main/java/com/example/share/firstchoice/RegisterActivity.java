package com.example.share.firstchoice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.share.R;
import com.example.share.sql.Sqlexe;
import com.example.share.util.Alert;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Sqlexe sqlexe ;
    private TextView exit; // 已有账户
    private EditText userName,userPass,userPassY; // 输入框组件
    private Button register; // 注册按钮
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // 已有账号设置
        exit=findViewById(R.id.exit);
        exit.setOnClickListener(this);
        
        //输入框初始化
        userName=findViewById(R.id.userName);
        userPass=findViewById(R.id.userPass);
        userPassY=findViewById(R.id.userPassY);
        
        //注册按钮点击
        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 输入框信息
                final String userNameText = userName.getText().toString();
                final String userPassText = userPass.getText().toString();
                String userPassYText = userPassY.getText().toString();
                
                // 对输入信息的初步检验
                if ("".equals(userNameText)){
                    userName.setError("请给你的账户取一个昵称!");
                    return;
                }if ("".equals(userPassText)){
                    userPass.setError("密码不能为空");
                    return;
                }if ("".equals(userPassYText)){
                    userPassY.setError("请确认密码");
                    return;
                }if (!(userPassText.equals(userPassYText))){
                    userPassY.setError("两次密码不同");
                    return;
                }
                // 向数据库插入数据
                sqlexe=Sqlexe.getInstance(RegisterActivity.this);
                if(sqlexe.isUserExist(userNameText)){ // 用户名存在时
                    Alert alert = new Alert(RegisterActivity.this,"提示"
                            ,"该用户名已存在请更改！",null,null);
                    alert.setRight("清空", new Alert.OnClick() {
                        @Override
                        public void onClick() {
                            userName.setText("");
                        }
                    }).show();
                    sqlexe.close();
                    return;
                }
                // 向数据库插入该账号，创建账号
                String number=getNumber();
                sqlexe.insert(number,userNameText,userPassText);
                sqlexe.close();
                //弹出对话框
                Alert alert=new Alert(RegisterActivity.this,"注意",
                        "为您注册的账号为 "+number+" 可使用该账号直接登录。\n" +
                                "现在是否需要去登录？");
                alert.setLeft("确定", new Alert.OnClick() {
                    @Override
                    public void onClick() {
                        Intent intent = RegisterActivity.this.getIntent();
                        intent.putExtra("userName",userNameText);
                        intent.putExtra("userPass",userPassText);
                        setResult(1,intent);
                        RegisterActivity.this.finish();
                    }
                }).show();
            }
        });
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit://点击已有账号
                finish();
                break;
        }
    }
    
    //生成随机账号
    public String getNumber(){
        String number=String.valueOf((long) (Math.random()*999999999));
        if(sqlexe.isUserExist(number)){
            number=getNumber();
        }
        return number;
    }
}