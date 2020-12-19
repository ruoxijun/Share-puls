package com.example.share.http;

import android.util.Log;

import com.example.share.pojo.Article;
import com.example.share.pojo.Comment;
import com.example.share.pojo.User;
import com.example.share.util.AppContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyRequest {
    private ObjectMapper mapper = new ObjectMapper();
    public static final String IPV4 = "39.100.66.82:8080";
    public static final String URL = "http://"+IPV4+"/share/";
    private final OkHttpClient client ;
    
    public MyRequest(){
        client = new OkHttpClient()
                .newBuilder()
                .build();
    }
    
    // 获取get请求Call
    public Call getGetCall(String req) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(URL+req)
                .build();
        // 客户端创建请求任务
        Call call = client.newCall(request);
        return call;
    }
    // 获取同步get请求数据
    public String getGetExecute(String req) throws IOException {
        return getGetCall(req).execute().body().string();
    }
    
    // 获取post请求Call
    public Call getPostCall(String req, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .post(requestBody)
                .url(URL+req)
                .build();
        // 客户端创建请求任务
        Call call = client.newCall(request);
        return call;
    }
    // 获取同步Post请求数据
    public String getPostExecute(String req,RequestBody requestBody) throws IOException {
        return getPostCall(req,requestBody).execute().body().string();
    }
    
    // 获取推荐博文内容
    public ArrayList<Article> queryArticles(int pageNum) throws IOException {
        ArrayList<Article> blogs = null;
        String url = "queryArticlePageNum";
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("pageNum",String.valueOf(pageNum))
                .build();
        // 获取响应数据
        String body = getPostExecute(url,requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(body);
        JsonNode pageInfo = jsonNode.get("msg").get("pageInfo");
        AppContext.recommendNextPage = pageInfo.get("nextPage").asInt();
        Log.e("pageInfo" , "当前页码："+pageNum);
        String list = pageInfo.get("list").toString();
        Log.e("list",list);
        // 将json转化为指定对象
        blogs = mapper.readValue(list,
                new TypeReference<ArrayList<Article>>(){});
        return blogs;
    }
    
    // 获取某用户的全部文章
    public List<Article> queryUserArticles(String id,int pageNum) throws IOException {
        ArrayList blogs = null;
        String url = "queryUserArticles"; // 请求
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id).add("pageNum",String.valueOf(pageNum))
                .build();
        String body = getPostExecute(url, requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(body);
        JsonNode pageInfo = jsonNode.get("msg").get("pageInfo");
        AppContext.userNextPage = pageInfo.get("nextPage").asInt();
        Log.e("pageInfo" , "当前页码："+pageNum);
        String list = pageInfo.get("list").toString();
        Log.e("list",list);
        // 将json转化为指定对象
        blogs = mapper.readValue(list,
                new TypeReference<ArrayList<Article>>(){});
        return blogs;
    }
    
    // 获取关注用户的文章
    public List<Article> queryAttArticles(String id,int pageNum) throws IOException {
        ArrayList blogs = null;
        String url = "queryAttArticles"; // 请求
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id).add("pageNum",String.valueOf(pageNum))
                .build();
        String body = getPostExecute(url, requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(body);
        JsonNode pageInfo = jsonNode.get("msg").get("pageInfo");
        AppContext.attNextPage = pageInfo.get("nextPage").asInt();
        Log.e("pageInfo" , "当前页码："+pageNum);
        String list = pageInfo.get("list").toString();
        Log.e("list",list);
        // 将json转化为指定对象
        blogs = mapper.readValue(list,
                new TypeReference<ArrayList<Article>>(){});
        return blogs;
    }
    
    // 搜索博文
    public List<Article> findArticles(String con,int pageNum) throws IOException {
        ArrayList blogs = null;
        String url = "findArticles"; // 请求
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("con",con)
                .add("pageNum",String.valueOf(pageNum))
                .build();
        String body = getPostExecute(url, requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(body);
        JsonNode pageInfo = jsonNode.get("msg").get("pageInfo");
        AppContext.findNextPage = pageInfo.get("nextPage").asInt();
        Log.e("pageInfo" , "当前页码："+pageNum);
        String list = pageInfo.get("list").toString();
        Log.e("list",list);
        // 将json转化为指定对象
        blogs = mapper.readValue(list,
                new TypeReference<ArrayList<Article>>(){});
        return blogs;
    }
    
    // 添加博文
    public int addArticle(String title,String context) throws IOException {
        String url = "addArticle";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", AppContext.user.getId())
                .add("title", title)
                .add("context", context)
                .add("date", String.valueOf(new Date().getTime()))
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        int i = jsonNode.get("msg").get("add").asInt();
        return i;
    }
    
    // 修改博文的
    public int updateArticle(int articleId,String title,String context) throws IOException {
        String url = "updateArticle";
        RequestBody requestBody = new FormBody.Builder()
                .add("articleId", String.valueOf(articleId))
                .add("title", title)
                .add("context", context)
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        int i = jsonNode.get("msg").get("updateArticle").asInt();
        return i;
    }
    
    // 删除博文
    public int deleteArticle(int articleId) throws IOException {
        String url = "deleteArticle";
        RequestBody requestBody = new FormBody.Builder()
                .add("articleId", String.valueOf(articleId))
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        int i = jsonNode.get("msg").get("deleteArticle").asInt();
        return i;
    }
    
    // 验证用户信息
    public User queryUser(String id,String password) throws IOException {
        User user = null;
        String url = "queryUser";
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id).add("password",password)
                .build();
        // 获取响应数据
        String body = getPostExecute(url,requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(body);
        String jsonUser = jsonNode.get("msg").get("user").toString();
        user = mapper.readValue(jsonUser,User.class);
        return user;
    }
    // 获取某用户信息
    public User queryUserExist(String id) throws IOException {
        User user = null;
        String url = "queryUserExist";
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id)
                .build();
        // 获取响应数据
        String body = getPostExecute(url,requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(body);
        String jsonUser = jsonNode.get("msg").get("user").toString();
        user = mapper.readValue(jsonUser,User.class);
        return user;
    }
    
    // 添加用户
    public boolean addUser(User user) throws IOException {
        String url = "addUser";
        // 请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("id",user.getId())
                .add("name",user.getName())
                .add("password",user.getPassword())
                .add("power",String.valueOf(user.getPower()))
                .add("registrationTime",user.getRegistrationTime())
                .build();
        String json = getPostExecute(url, requestBody);
        // 读json
        JsonNode jsonNode = mapper.readTree(json);
        String jsonUser = jsonNode.get("msg").get("user").toString();
        int i = mapper.readValue(jsonUser,Integer.class);
        if (i==1){ return true; }
        return false;
    }
    
    // 查询关注数与粉丝数
    public String userFriendNum(String id) throws IOException {
        return getGetExecute("userFriendNum?id=" + id);
    }
    
    // 增加点赞数
    public boolean addLikeArticle(int articleId) throws IOException {
        String url = "addLikeArticle";
        RequestBody requestBody = new FormBody.Builder()
                .add("articleId",String.valueOf(articleId))
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        boolean b = jsonNode.get("msg").get("like").asBoolean(false);
        return b;
    }
    
    // 是否关注了某用户
    public boolean isFriend(String friendsId) throws IOException {
        String url = "isFriend";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId",AppContext.user.getId())
                .add("friendsId",friendsId)
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        boolean b = jsonNode.get("msg").get("friend").asBoolean(false);
        return b;
    }
    
    // 关注某用户
    public boolean attUser(String friendsId) throws IOException {
        String url = "attUser";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId",AppContext.user.getId())
                .add("friendsId",friendsId)
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        boolean b = jsonNode.get("msg").get("attUser").asBoolean(false);
        return b;
    }
    
    // 取消关注某人
    public boolean canAttUser(String friendsId) throws IOException {
        String url = "canAttUser";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId",AppContext.user.getId())
                .add("friendsId",friendsId)
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        boolean b = jsonNode.get("msg").get("canAttUser").asBoolean(false);
        return b;
    }
    
    // 关注的用户列表
    public List<User> attUsers(String id) throws IOException {
        String url = "attUsers";
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id)
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        String attUsers = jsonNode.get("msg").get("attUsers").toString();
        ArrayList<User> users = mapper.readValue(attUsers,
                new TypeReference<ArrayList<User>>(){});
        return users;
    }
    
    // 我的粉丝列表
    public List<User> friendUsers(String id) throws IOException {
        String url = "friendUsers";
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id)
                .build();
        String json = getPostExecute(url, requestBody);
        JsonNode jsonNode = mapper.readTree(json);
        String friendUsers = jsonNode.get("msg").get("friendUsers").toString();
        ArrayList<User> users = mapper.readValue(friendUsers,
                new TypeReference<ArrayList<User>>(){});
        return users;
    }
    
    // 评论列表
    public List<Comment> queryComments(int articleId) throws IOException {
        String url = "queryComments";
        RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(articleId))
                .build();
        String json = getPostExecute(url, requestBody);
        String commentsStr = mapper.readTree(json).get("msg").get("comments").toString();
        ArrayList<Comment> comments = mapper.readValue(commentsStr,
                new TypeReference<ArrayList<Comment>>() {});
        return comments;
    }
    
    // 添加评论
    public int addComment(Comment comment) throws IOException {
        String url = "addComment";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId" , comment.getUserId())
                .add("articleId" , String.valueOf(comment.getArticleId()))
                .add("date" , comment.getDate())
                .add("content" , comment.getContent())
                .build();
        String json = getPostExecute(url, requestBody);
        String s = mapper.readTree(json).get("msg").get("addComment").toString();
        int i = mapper.readValue(s, Integer.class);
        return i;
    }
    
    // 删除评论
    public int deleteComment(int id) throws IOException {
        String url = "deleteComment";
        RequestBody requestBody = new FormBody.Builder()
                .add("id" , String.valueOf(id)).build();
        String json = getPostExecute(url, requestBody);
        String s = mapper.readTree(json).get("msg").get("deleteComment").toString();
        int i = mapper.readValue(s, Integer.class);
        return i;
    }
}
