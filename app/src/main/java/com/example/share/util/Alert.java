package com.example.share.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.share.R;

public class Alert {
    private AlertDialog ad;
    private AlertDialog.Builder bui;
    private View view;
    private TextView title,mess;
    private Button left,right;
    
    public Alert(Context context) {
        this(context,null,null);
    }
    
    public Alert(Context context,String title,String mess){
        this(context,title,mess,null,null,null,null);
    }
    
    public Alert(Context context,String title,String mess,
                 String leftName,OnClick leftOnClick){
        this(context,title,mess,leftName,leftOnClick,null,null);
    }
    
    public Alert(Context context,String title,String mess,
                 String leftName,OnClick leftOnClick,String rightName,OnClick rightOnClick){
        bui=new AlertDialog.Builder(context);
        LayoutInflater li=LayoutInflater.from(context);
        view=li.inflate(R.layout.alertdialog,null,false);
        this.title=view.findViewById(R.id.title);
        this.mess=view.findViewById(R.id.mess);
        this.left=view.findViewById(R.id.left);
        this.right=view.findViewById(R.id.right);
        
        setTitle(title);
        setMess(mess);
        setLeft(leftName,leftOnClick);
        setRight(rightName,rightOnClick);
    }
    
    //显示对话框
    public void show(){
        bui.setView(view).create();
        this.ad=bui.show();
    }
    // 事件回调内部接口
    public interface OnClick{
        void onClick();
    }
    
    // 设置标题
    public Alert setTitle(String title){
        this.title.setText(title);
        return this;
    }
    //设置内容
    public Alert setMess(String mess){
        this.mess.setText(mess);
        return this;
    }
    
    //左按钮设置
    public Alert setLeft(String name, final OnClick onClick){
        if (name!=null&&!"".equals(name)){
            left.setText(name);
        }
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick!=null)
                    onClick.onClick();
                ad.dismiss();
            }
        });
        return this;
    }
    //右按钮设置
    public Alert setRight(String name, final OnClick onClick){
        if (name!=null&&!"".equals(name)){
            right.setText(name);
        }
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick!=null)
                    onClick.onClick();
                ad.dismiss();
            }
        });
        return this;
    }
}
