package com.example.share.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.share.R;
import com.example.share.blog.AddBlogActivity;
import com.example.share.main.Find.FindActivity;
import com.example.share.main.adapter.MainAdapter;
import com.example.share.main.fragment.AttFragment;
import com.example.share.main.fragment.MyFragment;
import com.example.share.main.fragment.Recommend;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {
    private ImageView homePageImg,addImg,personalImg,addA;
    private EditText find;
    private LinearLayout homePage,add,personal;
    private ViewPager pager;
    private AttFragment ft1;
    private Recommend ft2;
    private MyFragment ft3;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 沉浸式(透明)状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ft1=new AttFragment(); // 关注页
        ft2=new Recommend(); // 推荐页
        ft3=new MyFragment(); // 我的页面
        ArrayList<Fragment> fts=new ArrayList<>();
        fts.add(ft1);
        fts.add(ft2);
        fts.add(ft3);
        pager=findViewById(R.id.pager);
        pager.setAdapter(new MainAdapter(getSupportFragmentManager(),fts));
        pager.setCurrentItem(1); // 设置首页
        
        add=findViewById(R.id.add); // 添加博文
        addImg = findViewById(R.id.addImg);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, AddBlogActivity.class);
                startActivity(i);
            }
        });
        addA=findViewById(R.id.addA); // 添加博文
        addA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, AddBlogActivity.class);
                startActivity(i);
            }
        });
        
        // 创作键触摸监听器
        add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                    addImg.setImageResource(R.mipmap.add_c);
                if (event.getAction()==MotionEvent.ACTION_UP)
                    addImg.setImageResource(R.mipmap.add);
                return false;
            }
        });
        
        pager.addOnPageChangeListener(this); // 添加界面切换监听
        
        // 首页键
        homePage=findViewById(R.id.homePage);
        homePageImg=findViewById(R.id.homePageImg);
        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHome();
                pager.setCurrentItem(1); // 跳首页
            }
        });
        // 首页键触摸监听器
        homePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    homePageImg.setImageResource(R.mipmap.home_c);
                }
                return false;
            }
        });
        // 我的键
        personal = findViewById(R.id.personal);
        personalImg = findViewById(R.id.personalImg);
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMy();
                pager.setCurrentItem(2); // 跳到我的页面
            }
        });
        // 我的键触摸监听器
        personal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    personalImg.setImageResource(R.mipmap.my_c);
                }
                return false;
            }
        });
        
        // 搜索框事件监听
        find =findViewById(R.id.find);
        find.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    Intent intent = new Intent(getApplicationContext(), FindActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override // 页面更改监听
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                ft1.showReset();
                ft2.notShowRset();
                ft3.notShowRset();
                toHome();
                break;
            case 1:
                ft1.notShowRset();
                ft2.showReset();
                ft3.notShowRset();
                toHome();
                break;
            case 2:
                ft1.notShowRset();
                ft2.notShowRset();
                ft3.showReset();
                toMy();
                break;
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {}
    
    public void toHome(){ // 主页面
        homePageImg.setImageResource(R.mipmap.home_y);
        personalImg.setImageResource(R.mipmap.my_n);
    }
    public void toMy(){ // 我的页面
        homePageImg.setImageResource(R.mipmap.home);
        personalImg.setImageResource(R.mipmap.my_y);
    }
}