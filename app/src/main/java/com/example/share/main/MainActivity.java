package com.example.share.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.blog.AddBlogActivity;
import com.example.share.main.adapter.MainAdapter;
import com.example.share.main.fragment.AttFragment;
import com.example.share.main.fragment.MyFragment;
import com.example.share.main.fragment.Recommend;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements ViewPager.OnPageChangeListener {
    private ImageView homePage,add,personal;
    private ViewPager pager;
    private TabLayout tab;
    private String userName,user_id;
    private AttFragment ft1;
    private Recommend ft2;
    private MyFragment ft3;
    private static Context context;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        SharedPreferences sp=getSharedPreferences("user",MODE_PRIVATE); // 用户id
        user_id=sp.getString("userId",null);
        
        ft1=new AttFragment(this,user_id); // 关注页
        ft2=new Recommend(this); // 推荐页
        ft3=new MyFragment(this,user_id); // 我的页面
        ArrayList<Fragment> fts=new ArrayList<>();
        fts.add(ft1);
        fts.add(ft2);
        fts.add(ft3);
        pager=findViewById(R.id.pager);
        pager.setAdapter(new MainAdapter(getSupportFragmentManager(),fts));
        pager.setCurrentItem(1); // 设置首页
        
        add=findViewById(R.id.add); // 添加博文
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, AddBlogActivity.class);
                i.putExtra("userName",userName);
                startActivity(i);
            }
        });
        pager.addOnPageChangeListener(this); // 添加界面切换监听
        tab=findViewById(R.id.tab);
        
        // 首页键
        homePage=findViewById(R.id.homePage);
        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHome();
                pager.setCurrentItem(1); // 跳首页
            }
        });
        // 我的键
        personal = findViewById(R.id.personal);
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMy();
                pager.setCurrentItem(2); // 跳到我的页面
            }
        });
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
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
        personal.setImageResource(R.drawable.my_n);
        personal.setBackgroundColor(getResources().getColor(R.color.no_check));
        homePage.setImageResource(R.drawable.home_y);
    }
    public void toMy(){ // 我的页面
        homePage.setImageResource(R.drawable.home);
        homePage.setBackgroundColor(getResources().getColor(R.color.no_check));
        personal.setImageResource(R.drawable.my_y);
    }
}