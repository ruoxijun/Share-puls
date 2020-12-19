package com.example.share.blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.R;
import com.example.share.http.MyRequest;
import com.example.share.main.adapter.CommentAdapter;
import com.example.share.pojo.Article;
import com.example.share.pojo.Comment;
import com.example.share.pojo.User;
import com.example.share.util.Alert;
import com.example.share.util.AppContext;
import com.example.share.util.MyMessage;
import com.example.share.util.Popup;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class BlogActivity extends AppCompatActivity {
    private TextView title,mess; // 博文标题和内容
    private ImageView exit,menu; // 退出按钮与菜单按钮
    private ImageView avatar; // 头像
    private TextView userName,time,att; // 作者，时间，是否关注
    private TextView commCount;
    private EditText comm; // 评论框
    private TextView addComm; // 发布按钮
    
    private View up; // 点赞
    private TextView like; // 点赞数
    private TextView blogtime; // 博文时间
    
    private String user_id = AppContext.user.getId();// 获取当前用户id
    private boolean isatt=false; // 是否关注了
    private Article article; // 博文信息
    private User user; // 博文用户信息
    public static int commentId;
    
    private MyRequest request = new MyRequest(); // 请求对象
    List<Comment> comms;
    private RecyclerView comments; //评论列表
    private CommentAdapter commentAdapter; // 评论列表适配器
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 101:
                    initUser(); // 博文用户信息初始化
                    break;
                case 102:
                    initAtt(); // 关注按钮初始化
                    break;
                case 103:
                    attBut(); // 关注按钮点击
                    break;
                case 104: // 增加点赞
                    like.setText(String.valueOf(Integer.valueOf(like.getText().toString())+1));
                    break;
                case 105: // 更新评论
                    comm.setText("");
                    commCount.setText("评论（"+comms.size()+")");
                    commentAdapter.update(comms);
                    commentAdapter.notifyDataSetChanged();
                    break;
                case 106: // 关闭页面
                    finish();
                    break;
                case 404:
                    Toast.makeText(BlogActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_blog);
        
        init(); // 初始化
        Intent intent = getIntent();
        article= (Article) intent.getSerializableExtra("article");
        
        // 初始化博文
        updateArticle(article);
        blogtime.setText("文章发布与："+article.getDate());
        like.setText(String.valueOf(article.getLikeCount()));
        
        // 获取用户信息
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    user = request.queryUserExist(article.getUserId());
                    handler.sendMessage(MyMessage.getMsg(101));
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(MyMessage.getMsg(404));
                }
            }
        }.start();
        
        // 点赞
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(user_id.equals(user.getId()))) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                boolean b = request.addLikeArticle(article.getArticleId());
                                if (b) handler.sendMessage(MyMessage.getMsg(104));
                            } catch (IOException e) {
                                e.printStackTrace();
                                handler.sendMessage(MyMessage.getMsg(404));
                            }
                        }
                    }.start();
                } else {
                    Toast.makeText(BlogActivity.this, "亲不能给自己点赞哟！！！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // 评论列表
        comments.setLayoutManager(new LinearLayoutManager(this));
        comments.setNestedScrollingEnabled(false); // 关闭滚动事件
        commentAdapter = new CommentAdapter(article.getUserId());
        comments.setAdapter(commentAdapter);
        comments.addItemDecoration(new MyDecoration()); // 设置间隔实现分隔线
        // 删除评论
        commentAdapter.setClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // 删除评论
                            int i = request.deleteComment(commentId);
                            if (i==1){
                                handler.sendMessage(MyMessage.getMsg(105));
                            } else {
                                handler.sendMessage(MyMessage.getMsg(404));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendMessage(MyMessage.getMsg(404));
                        }
                    }
                }.start();
                handler.sendMessage(MyMessage.getMsg(105));
            }
        });
        
        // 加载评论数据
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(300);
                    comms = request.queryComments(article.getArticleId());
                    handler.sendMessage(MyMessage.getMsg(105));
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendMessage(MyMessage.getMsg(404));
                }
            }
        }.start();
        
        // 添加评论
        addComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 查看评论是否为空
                String content = comm.getText().toString();
                if ("".equals(content.trim())) {
                    Toast.makeText(BlogActivity.this, "评论不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 初始化评论对象
                final Comment comment = new Comment(user_id,
                        AppContext.user.getName(),
                        article.getArticleId(),
                        String.valueOf(new Date().getTime()),
                        content);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // 添加博文
                            int i = request.addComment(comment);
                            if (i==1){
                                comms.add(comment);
                                handler.sendMessage(MyMessage.getMsg(105));
                            } else {
                                handler.sendMessage(MyMessage.getMsg(404));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendMessage(MyMessage.getMsg(404));
                        }
                    }
                }.start();
            }
        });
        
        // 退出事件
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    
        // 菜单
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Popup pop = Popup.getPop(BlogActivity.this, R.layout.popup);
                PopupWindow win = pop.getWin();
                menuListener(win);
                pop.show(menu);
            }
        });
        
    }
    
    // 初始化组件
    private void init(){
        exit = findViewById(R.id.exit);
        title=findViewById(R.id.title);
        mess=findViewById(R.id.mess);
        avatar=findViewById(R.id.avatar);
        userName=findViewById(R.id.userName);
        time=findViewById(R.id.time);
        att=findViewById(R.id.att);
        up=findViewById(R.id.up);
        like=findViewById(R.id.like);
        blogtime = findViewById(R.id.blogtime);
        comments = findViewById(R.id.comments);
        comm = findViewById(R.id.comm);
        addComm = findViewById(R.id.addComm);
        commCount = findViewById(R.id.commCount);
        menu = findViewById(R.id.menu);
    }
    
    public void updateArticle(Article article){
        title.setText(article.getTitle());
        mess.setText(article.getContext());
    }
    
    // 关注按钮点击事件
    private void attBut(){
        if (isatt){
            att.setText("已关注");
            att.setBackground(getDrawable(R.drawable.bu_att_y));
        } else {
            att.setText("关注");
            att.setBackground(getDrawable(R.drawable.bu_att));
        }
    }
    
    // 关注按钮初始化
    private void initAtt(){
        if (isatt){
            att.setText("已关注");
            att.setBackground(getDrawable(R.drawable.bu_att_y));
        }
        else{
            att.setText("关注");
            att.setBackground(getDrawable(R.drawable.bu_att));
        }
        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (isatt) { // 取消关注
                            try {
                                request.canAttUser(article.getUserId());
                                isatt = false;
                                handler.sendMessage(MyMessage.getMsg(103));
                            } catch (IOException e) {
                                e.printStackTrace();
                                handler.sendMessage(MyMessage.getMsg(404));
                            }
                        }
                        else { // 关注
                            try {
                                request.attUser(article.getUserId());
                                isatt = true;
                                handler.sendMessage(MyMessage.getMsg(103));
                            } catch (IOException e) {
                                e.printStackTrace();
                                handler.sendMessage(MyMessage.getMsg(404));
                            }
                        }
                    }
                }.start();
            }
        });
    }
    
    // 菜单监听
    private void menuListener(final PopupWindow win){
        View view = win.getContentView();
        TextView update = view.findViewById(R.id.update); // 修改文章
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.dismiss();
                // 判断是否是作者本人
                if (!article.getUserId().equals(user_id)) {
                    Toast.makeText(BlogActivity.this, "仅作者本人可操作",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(BlogActivity.this,
                        AddBlogActivity.class);
                intent.putExtra("article",article);
                startActivityForResult(intent,1);
            }
        });
        TextView delete = view.findViewById(R.id.delete); // 删除文章
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.dismiss();
                // 判断是否是作者本人
                if (!article.getUserId().equals(user_id)) {
                    Toast.makeText(BlogActivity.this, "仅作者本人可操作",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Alert alert = new Alert(BlogActivity.this);
                alert.setTitle("警告").setMess("确定删除吗？");
                alert.setLeft("确定", new Alert.OnClick() {
                    @Override
                    public void onClick() {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    int i = request.deleteArticle(article.getArticleId());
                                    if (i==1){ handler.sendMessage(MyMessage.getMsg(106)); }
                                    else { handler.sendMessage(MyMessage.getMsg(404)); }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    handler.sendMessage(MyMessage.getMsg(404));
                                }
                            }
                        }.start();
                    }
                }).show();
            }
        });
        TextView can = view.findViewById(R.id.can);
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.dismiss();
            }
        });
    }
    
    // 博文作者信息初始化
    private void initUser(){
        userName.setText(user.getName());
        time.setText(user.getRegistrationTime());
        // 关注按钮
        if (user_id.equals(article.getUserId())){
            att.setVisibility(View.GONE);
        } else {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        // 是否已关注该作者
                        isatt = request.isFriend(article.getUserId());
                        handler.sendMessage(MyMessage.getMsg(102)); // 初始化关注按钮
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.sendMessage(MyMessage.getMsg(404));
                    }
                }
            }.start();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1){ // 当文章修改完成后
            article.setTitle(data.getStringExtra("title"));
            article.setContext(data.getStringExtra("context"));
            updateArticle(article);
        }
    }
}

//此内部类写在类中并继承RecyclerView.ItemDecoration
class MyDecoration extends RecyclerView.ItemDecoration{
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //在下出现1dp间隔(下划线)
        outRect.bottom=1;
    }
}