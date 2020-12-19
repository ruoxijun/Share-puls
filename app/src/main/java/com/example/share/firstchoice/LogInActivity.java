package com.example.share.firstchoice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.http.MyRequest;
import com.example.share.main.MainActivity;
import com.example.share.R;
import com.example.share.pojo.Article;
import com.example.share.pojo.User;
import com.example.share.util.Alert;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;
import java.io.IOException;
import java.util.List;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register; // 注册
    private EditText userName,userPass; // 用户名与密码
    private Button logIn; // 登录按钮
    private Switch memory; // 记住密码
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private String userNameText; // 输入框用户id
    private String userPassText; // 输入框用户密码
    private User user; // 用户对象
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 200: // 账号正确时
                    gotoMain();
                    break;
                case 404:
                    Toast.makeText(LogInActivity.this, "网络错误！！！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 406: // 账号有误时
                    if (sp.getBoolean("memory",false)){
                        edit.clear();
                    } else {
                        edit=sp.edit();
                        edit.putString("userName",userNameText);
                        edit.putString("userPass",userPassText);
                    }
                    edit.apply();
                    Alert alert=new Alert(LogInActivity.this,"提示",
                            "您的账号或密码错误,请检查后登陆!","确定",null);
                    alert.setRight("清空", new Alert.OnClick() {
                        @Override
                        public void onClick() {
                            userName.setText("");
                            userPass.setText("");
                        }
                    }).show();
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
        edit = sp.edit();
        memory.setChecked(sp.getBoolean("memory",false));
        userName.setText(sp.getString("userName",""));
        userPass.setText(sp.getString("userPass",""));
        
        // 登录
        logIn=findViewById(R.id.logIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户名和密码
                userNameText=userName.getText().toString();
                userPassText=userPass.getText().toString();
                
                // 查看输入框是否为空
                if ("".equals(userNameText)){
                    userName.setError("用户账号不能为空");
                    return;
                }
                if("".equals(userPassText)){
                    userPass.setError("密码不能为空");
                    return;
                }
    
                // 请求查看账号是否正确
                final MyRequest request = new MyRequest();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            user = request.queryUser(userNameText, userPassText);
                            if (user!=null){
                                Log.e("user对象为 : ", user.toString());
                                handler.sendMessage(MyMessage.getMsg(200));
                                AppContext.user = user; // 将用户信息存储在APP全文对象中
                            } else {
                                Log.e("user : ", "--==User 对象为 null==--");
                                handler.sendMessage(MyMessage.getMsg(406));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendMessage(MyMessage.getMsg(404));
                        }
                    }
                }.start();
            }
        });
    }
    
    // 登录成功后完成记录账号和跳转主页面工作
    public void gotoMain(){
        // 当用户选择了记住密码
        if (memory.isChecked()){
            // 记录用户数据
            edit.putBoolean("memory",true);
            edit.putString("userPass",userPassText);
        } else {
            edit.clear(); // 清空
        }
        edit.putString("userId",user.getId());
        edit.putString("userName",userNameText);
        edit.putString("power",user.getPower()+"");
        edit.putString("registrTime",user.getRegistrationTime());
        edit.apply();

        // 登陆成功跳转到主界面
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
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
    
    @Override // 注册完成后返回
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