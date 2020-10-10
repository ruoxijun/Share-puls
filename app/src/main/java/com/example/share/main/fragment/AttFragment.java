package com.example.share.main.fragment;

import android.content.Context;
import android.os.Bundle;
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

public class AttFragment extends Fragment {
    private Context context;
    private String user_id;
    private ImageView reset;
    private int i=0;
    public AttFragment(Context context,String user_id){
        this.context=context;
        this.user_id=user_id;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item_att,container,false);
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView attlist=view.findViewById(R.id.attList);
        attlist.setLayoutManager(new LinearLayoutManager(context));
        Sqlexe sqlexe = Sqlexe.getInstance(context); // 调用数据库
        final RecommendAdapter ra=new RecommendAdapter(context,sqlexe.attBlogs(user_id));
        attlist.setAdapter(ra);
        sqlexe.close(); // 关闭数据库
        
        reset=view.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i +=360; // 刷新设置
                reset.animate().rotation(i).setDuration(1500).start();
                Sqlexe sqlexe= Sqlexe.getInstance(context);
                ra.update(sqlexe.attBlogs(user_id));
                sqlexe.close();
                ra.notifyDataSetChanged();
                Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // reset按钮的显示和隐藏
    public void showReset(){ if (reset!=null) reset.setVisibility(View.VISIBLE); }
    public void notShowRset(){ if (reset!=null) reset.setVisibility(View.INVISIBLE); }
}
