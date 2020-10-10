package com.example.share.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.share.main.MainActivity;
import com.example.share.main.adapter.RecommendAdapter;
import com.example.share.pojo.Article;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Sqlexe implements AutoCloseable{
    private static SQLiteDatabase exe;
    
    /**
     * 单列模式，getInstance 方法获取数据库对象
     */
    private static Sqlexe sqlexe;
    private Sqlexe(){}
    public static Sqlexe getInstance(Context context){
        if (sqlexe==null){
            sqlexe = new Sqlexe();
        }
        SQLCreateMess mess=new SQLCreateMess(context);
        exe=mess.getWritableDatabase();
        return sqlexe;
    }
    // 关闭数据库
    @Override
    public void close(){
        if (exe!=null) exe.close();
    }
    
    // 执行增删改
    public void execSQL(String sql){
        exe.execSQL(sql);
    }
    // 执行查
    public Cursor rawQuery(String sql){
        return exe.rawQuery(sql,null);
    }

    // 查找账户是否有匹配
    public boolean isUserMatch(String user,String pass) {
        boolean isMatch = false;
        // 查看是否有id或账户名和其密码匹配的账号
        Cursor cur = rawQuery("select user_id as id,user_name as name,user_password as pass from " +
                SQLCreateMess.TABLE_USER+" where (id='" + user + "' or name='" +
                user + "') and pass='" + pass + "'");
        if (cur.moveToNext()) isMatch = true;
        cur.close();
        return isMatch;
    }
    
    // 查找账户
    public Cursor findUser(String user,String pass) {
        // 查看是否有id或账户名和其密码匹配的账号
        Cursor cur = rawQuery("select * from " + SQLCreateMess.TABLE_USER+" where (user_id='"+
                user + "' or user_name='" + user + "') and user_password='" + pass + "'");
        return cur;
    }
    
    // 获取某用户信息
    public HashMap<String,String> getUser(String userName){
        HashMap<String,String> user = new HashMap<>();
        Cursor cur=rawQuery("select * from "+ SQLCreateMess.TABLE_USER +
                " where " + "user_id = '"+userName+"' or user_name = '"+userName+"'");
        cur.moveToNext();
        user.put("id",cur.getString(cur.getColumnIndex("user_id")));
        user.put("name",cur.getString(cur.getColumnIndex("user_name")));
        user.put("pass",cur.getString(cur.getColumnIndex("user_password")));
        user.put("power",cur.getString(cur.getColumnIndex("user_power")));
        user.put("time",cur.getString(cur.getColumnIndex("user_registration_time")));
        cur.close();
        return user;
    }
    
    // 查看是已否存在该账号或用户名
    public boolean isUserExist(String user){
        boolean isExist = false;
        Cursor cur=rawQuery("select user_id as id,user_name as name from "+
                SQLCreateMess.TABLE_USER+" where " + "id = '"+user+"' or name = '"+user+"'");
        if (cur.moveToNext()) isExist=true;
        return isExist;
    }
    
    // 插入新用户
    public void insert(String id,String name,String pass){
        execSQL("insert into "+SQLCreateMess.TABLE_USER+" values('"+id
                +"','"+name+"','"+pass+"',1,'"+getTime()+"')");
    }
    
    // 查询博文
    public Cursor findBlog(String sql){
        Cursor cur = rawQuery("select * from "+SQLCreateMess.TABLE_ARTICLES+" where "+ sql);
        return cur;
    }
    
    // 插入新博文
    public void addBlog(String user,String title,String mess){
        execSQL("insert into articles(user_id,sort_id,article_title,article_content,"+
                "article_views,article_date,article_like_count)"+
                "values('"+user+"',1,'"+title+"','"+mess+"',0,'"+getTime()+"',0)");
    }
    
    // 获取所有博文内容
    public ArrayList<HashMap<String,String>> getBlogs(){
        ArrayList<HashMap<String,String>> blogs=new ArrayList<>();
        Cursor cur=findBlog("article_id>0 order by article_id desc");
        while(cur.moveToNext()){
            HashMap<String,String> map=new HashMap<>();
            map.put("id",cur.getString(cur.getColumnIndex("article_id")));
            map.put("userName",cur.getString(cur.getColumnIndex("user_id")));
            map.put("sort",cur.getString(cur.getColumnIndex("sort_id")));
            map.put("title",cur.getString(cur.getColumnIndex("article_title")));
            map.put("content",cur.getString(cur.getColumnIndex("article_content")));
            map.put("views",cur.getString(cur.getColumnIndex("article_views")));
            map.put("date",cur.getString(cur.getColumnIndex("article_date")));
            map.put("like",cur.getString(cur.getColumnIndex("article_like_count")));
            blogs.add(map);
        }
        cur.close();
        return blogs;
    }
    
    // 获取所有博文内容
    public ArrayList<HashMap<String,String>> queryArticles(){
        final ArrayList<HashMap<String,String>> blogs=new ArrayList<>();
        // 客户端
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MILLISECONDS)
                .build();
        // 创建请求内容
        Request request = new Request.Builder()
                .get()
                .url("http://192.168.137.1:8080/share/queryArticles")
                .build();
        // 客户端创建请求任务
        Call call = okHttpClient.newCall(request);
        // 异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("okHttp","网络请求失败");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                if (code== HttpURLConnection.HTTP_OK){
                    Log.e("okHttp","数据请求成功，解析中");
                    String body = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();
                    List<Article> diqus = mapper.readValue(body,
                            new TypeReference<ArrayList<Article>>(){});
                    for (int i = 0;i<diqus.size();i++) {
                        Article article = (Article)diqus.get(i);
                        HashMap<String,String> map=new HashMap<>();
                        map.put("id",article.getArticleId()+"");
                        map.put("userName",article.getUserId());
                        map.put("sort",article.getSortId()+"");
                        map.put("title",article.getArticleTitle());
                        map.put("content",article.getArticleContext());
                        map.put("views",article.getArticleViews()+"");
                        map.put("date",article.getArticleDate());
                        map.put("like",article.getArticleLikeCount()+"");
                        blogs.add(map);
                    }
                }
            }
        });
        Log.e("okHttp","数据解析成功，返回中");
        return blogs;
    }
    
    // 获取某用户关注的好友的文章
    public ArrayList<HashMap<String,String>> attBlogs(String user_id){
        ArrayList<HashMap<String,String>> attblogs=new ArrayList<>();
        Cursor cur=rawQuery("select a.article_id as _id,a.user_id as user,a.sort_id as sort,a.article_title as title," +
                "a.article_content as content,a.article_views as views,a.article_date as date,a.article_like_count as like" +
                " from "+SQLCreateMess.TABLE_FRIENDS+" as f,"+SQLCreateMess.TABLE_ARTICLES+" as a"+
                " where f.user_id='"+user_id+"' and f.user_friends_id=a.user_id order by a.article_id desc");
        while(cur.moveToNext()){
            HashMap<String,String> map=new HashMap<>();
            map.put("id",cur.getString(cur.getColumnIndex("_id")));
            map.put("userName",cur.getString(cur.getColumnIndex("user")));
            map.put("sort",cur.getString(cur.getColumnIndex("sort")));
            map.put("title",cur.getString(cur.getColumnIndex("title")));
            map.put("content",cur.getString(cur.getColumnIndex("content")));
            map.put("views",cur.getString(cur.getColumnIndex("views")));
            map.put("date",cur.getString(cur.getColumnIndex("date")));
            map.put("like",cur.getString(cur.getColumnIndex("like")));
            attblogs.add(map);
        }
        return attblogs;
    }
    
    // 获取指定用户的所有文章
    public ArrayList<HashMap<String,String>> myBlogs(String user_id){
        ArrayList<HashMap<String,String>> blogs=new ArrayList<>();
        Cursor cur=findBlog("user_id="+user_id + " order by article_id desc");
        while(cur.moveToNext()){
            HashMap<String,String> map=new HashMap<>();
            map.put("id",cur.getString(cur.getColumnIndex("article_id")));
            map.put("userName",cur.getString(cur.getColumnIndex("user_id")));
            map.put("sort",cur.getString(cur.getColumnIndex("sort_id")));
            map.put("title",cur.getString(cur.getColumnIndex("article_title")));
            map.put("content",cur.getString(cur.getColumnIndex("article_content")));
            map.put("views",cur.getString(cur.getColumnIndex("article_views")));
            map.put("date",cur.getString(cur.getColumnIndex("article_date")));
            map.put("like",cur.getString(cur.getColumnIndex("article_like_count")));
            blogs.add(map);
        }
        cur.close();
        return blogs;
    }
    
    // 获取某博文内容
    public HashMap<String,String> getBlog(String blog_id){
        HashMap<String,String> blog=new HashMap<>();
        Cursor cur=findBlog("article_id="+blog_id); // 博文信息
        cur.moveToNext();
        blog.put("id",cur.getString(cur.getColumnIndex("article_id")));
        blog.put("user_id",cur.getString(cur.getColumnIndex("user_id")));
        blog.put("sort",cur.getString(cur.getColumnIndex("sort_id")));
        blog.put("title",cur.getString(cur.getColumnIndex("article_title")));
        blog.put("content",cur.getString(cur.getColumnIndex("article_content")));
        blog.put("views",cur.getString(cur.getColumnIndex("article_views")));
        blog.put("date",cur.getString(cur.getColumnIndex("article_date")));
        blog.put("like",cur.getString(cur.getColumnIndex("article_like_count")));
        cur.close();
        return blog;
    }
    
    // 给某博文点赞
    public void addLike(String blog_id,String like){
        execSQL("update "+SQLCreateMess.TABLE_ARTICLES+" set article_like_count="+like
                +" where article_id="+blog_id);
    }
    
    // 获取当前时间
    public String getTime(){
        String time="";
        Date ss = new Date();
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        time = format0.format(ss.getTime());//这个就是把时间戳经过处理得到期望格式的时间
        return time;
    }
    
    // 是否关注某人
    public boolean isFriend(String user_id,String friend_id){
        boolean isfriend=false;
        Cursor cur=rawQuery("select * from "+SQLCreateMess.TABLE_FRIENDS+
                " where user_id='"+user_id +"' and user_friends_id='"+friend_id+"'");
        if (cur.moveToNext()) isfriend=true;
        return isfriend;
    }
    
    // 关注某人
    public void att(String user_id,String friend_id){
        execSQL("insert into "+SQLCreateMess.TABLE_FRIENDS+"(user_id,user_friends_id)" +
                "values("+user_id+","+friend_id+")");
    }
    
    // 取消关注某人
    public void notAtt(String user_id,String friend_id){
        execSQL("delete from "+SQLCreateMess.TABLE_FRIENDS+" where "+
                "user_id="+user_id+" and " +"user_friends_id="+friend_id);
    }
    
    // 关注人数
    public String attNum(String user_id){
        String attnum = "0";
        Cursor cur=rawQuery("select count(*) from "+SQLCreateMess.TABLE_FRIENDS+
                " where user_id='"+ user_id +"'");
        if (cur.moveToNext()) attnum=cur.getString(0);
        if (attnum==null) return "0";
        return attnum;
    }
    
    // 粉丝人数
    public String friendNum(String user_id){
        String friendnum = "0";
        Cursor cur=rawQuery("select count(*) from "+SQLCreateMess.TABLE_FRIENDS+
                " where user_friends_id='"+user_id+"'");
        if (cur.moveToNext()) friendnum=cur.getString(0);
        if (friendnum==null) return "0";
        return friendnum;
    }
    
    // 根据id列表获取用户信息列表
    public ArrayList<HashMap<String,String>> getUserList(ArrayList<String> attIdList){
        ArrayList<HashMap<String,String>> attList = new ArrayList<>();
        for (String id : attIdList){
            attList.add(getUser(id));
        }
        return attList;
    }
    
    // 关注的id列表
    public ArrayList<String> getAttIdList(String user_id){
        ArrayList<String> attIdList=new ArrayList<>();
        Cursor cur=rawQuery("select user_friends_id from "+SQLCreateMess.TABLE_FRIENDS+
                " where user_id='"+ user_id +"'");
        while (cur.moveToNext()) {
            attIdList.add(cur.getString(0));
        }
        cur.close();
        return attIdList;
    }
    // 获取某用户关注的用户列表信息
    public ArrayList<HashMap<String,String>> getUserAtt(String user_id){
        return getUserList(getAttIdList(user_id));
    }
    
    // 粉丝的id列表
    public ArrayList<String> getFriendIdList(String user_id){
        ArrayList<String> attIdList=new ArrayList<>();
        Cursor cur=rawQuery("select user_id from "+SQLCreateMess.TABLE_FRIENDS+
                " where user_friends_id='"+ user_id +"'");
        while (cur.moveToNext()) {
            attIdList.add(cur.getString(0));
        }
        cur.close();
        return attIdList;
    }
    // 获取某用户粉丝列表信息
    public ArrayList<HashMap<String,String>> getUserFriend(String user_id){
        
        return getUserList(getFriendIdList(user_id));
    }
}
