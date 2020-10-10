package com.example.share.firstchoice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.share.main.MainActivity;
import com.example.share.R;
import com.example.share.sql.Sqlexe;
import com.example.share.util.Alert;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register; // 注册
    private EditText userName,userPass; // 用户名与密码
    private Button logIn; // 登录按钮
    private Switch memory; // 记住密码
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        
        // 注册按钮
        register=findViewById(R.id.register);
        register.setOnClickListener(this);
        //输入框
        userName=findViewById(R.id.userName);
        userPass=findViewById(R.id.userPass);
        // 记住密码选项
        memory=findViewById(R.id.memory);
    
        // 查看是否记住密码
        sp=getSharedPreferences("user",MODE_PRIVATE);
        memory.setChecked(sp.getBoolean("memory",false));
        userName.setText(sp.getString("userName",""));
        userPass.setText(sp.getString("userPass",""));
        
        // 登录
        logIn=findViewById(R.id.logIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户名和密码
                String userNameText=userName.getText().toString();
                String userPassText=userPass.getText().toString();
                
                // 查看输入框是否为空
                if ("".equals(userNameText)){
                    userName.setError("用户账号不能为空");
                    return;
                }
                if("".equals(userPassText)){
                    userPass.setError("密码不能为空");
                    return;
                }
                
                // 查看数据库账号数据是否存在
                Sqlexe sqlexe=Sqlexe.getInstance(LogInActivity.this);
                if (!sqlexe.isUserMatch(userNameText,userPassText)){
                    Alert alert=new Alert(LogInActivity.this,"提示",
                            "您的账号或密码错误,请检查后登陆!","确定",null);
                    alert.setRight("清空", new Alert.OnClick() {
                        @Override
                        public void onClick() {
                           userName.setText("");
                           userPass.setText("");
                        }
                    }).show();
                    return;
                }
                
                // 是否记录密码
                edit=sp.edit();
                Cursor cursor=sqlexe.findUser(userNameText,userPassText);
                cursor.moveToNext();
                if (memory.isChecked()){
                    // 记录用户数据
                    edit.putBoolean("memory",true);
                    edit.putString("userName",userNameText);
                    edit.putString("userPass",userPassText);
                    edit.putString("power",cursor.getString(3));
                    edit.putString("registrTime",cursor.getString(4));
                    edit.apply();
                } else {
                    edit.clear(); // 清空
                    edit.apply();
                }
                edit.putString("userId",cursor.getString(0));
                edit.apply();
                cursor.close();
                sqlexe.close();
                
                
                // 登陆成功跳转到主界面
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.putExtra("userName",userNameText);
                startActivity(intent);
            }
        });
    }
    
    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.register: // 跳转注册界面
                intent=new Intent(this,RegisterActivity.class);
                break;
        }
        startActivityForResult(intent,1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                userName.setText(data.getStringExtra("userName"));
                userPass.setText(data.getStringExtra("userPass"));
                break;
        }
    }
}