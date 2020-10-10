package com.example.share.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.sql.Sqlexe;

import java.util.ArrayList;
import java.util.HashMap;

public class Recommend extends Fragment {
    private Context context;
    private ArrayList<HashMap<String,String>> item;
    private int i=0;
    ImageView reset;
    public Recommend (Context context){
        this.context=context;
        
        Sqlexe sqlexe= Sqlexe.getInstance(context);
        item=sqlexe.queryArticles();
        sqlexe.close();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend,container,false);
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recom=view.findViewById(R.id.recom);
        recom.setLayoutManager(new LinearLayoutManager(context));
        final RecommendAdapter ra=new RecommendAdapter(context,item);
        recom.setAdapter(ra);
        
        // 刷新按钮
        reset=view.findViewById(R.id.reset);
        reset.setVisibility(View.VISIBLE);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            i +=360; // 刷新设置
            reset.animate().rotation(i).setDuration(1500).start();
            Sqlexe sqlexe= Sqlexe.getInstance(context);
            item=sqlexe.queryArticles();
            sqlexe.close();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ra.update(item);
            ra.notifyDataSetChanged();
            Log.e("刷新","刷新成功");
            Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // reset按钮的显示和隐藏
    public void showReset(){ if (reset!=null) reset.setVisibility(View.VISIBLE); }
    public void notShowRset(){ if (reset!=null) reset.setVisibility(View.INVISIBLE); }
}
