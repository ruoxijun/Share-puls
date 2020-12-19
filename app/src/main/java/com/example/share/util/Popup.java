package com.example.share.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class Popup {
    private static PopupWindow pop;
    private View view;
    
    private Popup(Context context, int rId){
        //实例化一个布局，作为之后的菜单样式
        view= LayoutInflater.from(context).inflate(rId,null);
    }
    public static Popup getPop(Context context, int rId){
        Popup popup = new Popup(context, rId);
        return popup;
    }
    
    public PopupWindow getWin(){
        //初始化，参1为具体菜单样式的实例化对象，参23为菜单的宽高
        pop=new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //想PopupWindow点击外侧时消失需要设置一个背景，才能成功
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);//获取焦点
        pop.setOutsideTouchable(true);//点击外侧消失
        return pop;
    }
    
    public void show(View view){
        //设置位置，参1表示显示在哪个组件下，参23表示偏移值
        pop.showAsDropDown(view,-150,0);
    }
    
    public View getView() { return view; }
}
