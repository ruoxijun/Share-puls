package com.example.share.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.main.addition.AttActivity;
import com.example.share.sql.Sqlexe;

import java.util.HashMap;

public class MyFragment extends Fragment {
    private Context context;
    private String user_id;
    private ImageView reset;
    private TextView userName,time,attNum,friendNum;
    private RecyclerView myblog;
    private int i=0;
    RecommendAdapter ra;
    public MyFragment(Context context, String user_id){
        this.context=context;
        this.user_id=user_id;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item_my,container,false);
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 基本信息
        userName = view.findViewById(R.id.userName);
        time = view.findViewById(R.id.time);
        attNum = view.findViewById(R.id.attNum);
        friendNum = view.findViewById(R.id.friendNum);
        
        Sqlexe sqlexe = Sqlexe.getInstance(context); // 调用数据库
        HashMap<String,String> user = sqlexe.getUser(user_id);
        userName.setText(user.get("name"));
        time.setText(user.get("time"));
        // 关注数与粉丝数
        attNum.setText(sqlexe.attNum(user_id));
        friendNum.setText(sqlexe.friendNum(user_id));
        
        myblog=view.findViewById(R.id.myblog); // RecyclerView
        myblog.setLayoutManager(new LinearLayoutManager(context));
        ra=new RecommendAdapter(context,sqlexe.myBlogs(user_id));
        myblog.setAdapter(ra);
        sqlexe.close(); // 关闭数据库

        // 刷新设置
        reset=view.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                Sqlexe sqlexe= Sqlexe.getInstance(context);
                ra.update(sqlexe.myBlogs(user_id));
                attNum.setText(sqlexe.attNum(user_id));
                friendNum.setText(sqlexe.friendNum(user_id));
                sqlexe.close();
                ra.notifyDataSetChanged();
                Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
        
        View att=view.findViewById(R.id.att); // 跳转我的关注页面
        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AttActivity.class);
                intent.putExtra("case",1);
                startActivity(intent);
            }
        });
        View friend=view.findViewById(R.id.friend); // 跳转我的粉丝页面
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AttActivity.class);
                intent.putExtra("case",2);
                startActivity(intent);
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Sqlexe sqlexe= Sqlexe.getInstance(context);
        ra.update(sqlexe.myBlogs(user_id));
        attNum.setText(sqlexe.attNum(user_id));
        friendNum.setText(sqlexe.friendNum(user_id));
        sqlexe.close();
        ra.notifyDataSetChanged();
    }
    
    // reset按钮的显示和隐藏
    public void showReset(){ if (reset!=null) reset.setVisibility(View.VISIBLE); }
    public void notShowRset(){ if (reset!=null) reset.setVisibility(View.INVISIBLE); }
}
