package com.example.share.firstchoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.pojo.User;
import com.example.share.util.Alert;
import com.example.share.util.MyMessage;
import java.io.IOException;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    String userId,userNameText,userPassText;
    private TextView exit; // 已有账户
    private EditText userName,userPass,userPassY; // 输入框组件
    private Button register; // 注册按钮
    private Alert alert;
    // 线程消息处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    //弹出对话框
                    alert=new Alert(RegisterActivity.this,"注意",
                            "为您注册的账号为 "+userId+" 可使用该账号直接登录。\n" +
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
                    break;
                case 404:
                    Toast.makeText(RegisterActivity.this, "网络错误！！！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 406:
                    alert = new Alert(RegisterActivity.this,"提示"
                            ,"该用户名已存在请更改！",null,null);
                    alert.setRight("清空", new Alert.OnClick() {
                        @Override
                        public void onClick() {
                            userName.setText("");
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
                userNameText = userName.getText().toString();
                userPassText = userPass.getText().toString();
                String userPassYText = userPassY.getText().toString();
                
                // 对输入信息的初步检验
                if ("".equals(userNameText)){
                    userName.setError("请给你的账户取一个昵称!");
                    return;
                }if ("".equals(userPassText)){
                    userPass.setError("密码不能为空");
                    return;
                }if (userPassText.length()<6){
                    userPass.setError("密码至少6位");
                    return;
                }if ("".equals(userPassYText)){
                    userPassY.setError("请确认密码");
                    return;
                }if (!(userPassText.equals(userPassYText))){
                    userPassY.setError("两次密码不一致");
                    return;
                }
                
                // 添加用户
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        MyRequest request = new MyRequest();
                        try {
                            User user = request.queryUserExist(userNameText);
                            if (user!=null){ // 当用户名已存在时
                                handler.sendMessage(MyMessage.getMsg(406));
                            } else { // 添加用户
                                userId = getNumber(request);
                                request.addUser(new User(userId,userNameText,userPassText,
                                        1,String.valueOf(new Date().getTime())));
                                handler.sendMessage(MyMessage.getMsg(200));
                            }
                        } catch (IOException e) { // 网络错误
                            e.printStackTrace();
                            handler.sendMessage(MyMessage.getMsg(404));
                        }
                    }
                }.start();
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
    public String getNumber(MyRequest request) throws IOException {
        String number=String.valueOf((long) (Math.random()*999999999));
        if (request.queryUserExist(number)!=null) {
            number=getNumber(request);
        }
        return number;
    }
}