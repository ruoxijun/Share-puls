package com.example.share.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLCreateMess extends SQLiteOpenHelper {
    
    public static final String DATABASENAME="share.db"; // 数据库名
    public static final String TABLE_USER="users"; // 用户表
    public static final String TABLE_FRIENDS="friends"; // 好友表
    public static final String TABLE_ARTICLES="articles"; // 博文表
    public static final String TABLE_COMMENTS="comments"; // 评论表
    public static final String TABLE_SORTS="sorts"; // 博文分类
    
    public SQLCreateMess(@Nullable Context context) {
        super(context, DATABASENAME, null, 1);
    }
    
    @Override // 创建表
    public void onCreate(SQLiteDatabase db) {
        // 用户表：users
        db.execSQL("CREATE TABLE "+TABLE_USER+" (" +
                " user_id varchar(20) PRIMARY KEY," + // 用户id
                " user_name varchar(20) UNIQUE NOT NULL," + // 用户昵称
                " user_password varchar(15) NOT NULL," + // 用户密码
                " user_power INTEGER NOT NULL," + // 用户权力
                " user_registration_time DATETIME DEFAULT null)"); // 注册时间
        // 好友表：friends
        db.execSQL("CREATE TABLE "+TABLE_FRIENDS+" (" +
                "user_id varchar(20) NOT NULL," + // 用户 id
                " user_friends_id varchar(20) NOT NULL)"); // 好友 id
        // 博文表：articles
        db.execSQL("CREATE TABLE "+TABLE_ARTICLES+" (" +
                " article_id INTEGER PRIMARY KEY," + // 博文id
                " user_id varchar(20) NOT NULL," + // 用户id
                " sort_id bigint(20) NOT NULL," + // 分类 id
                " article_title text NOT NULL," + // 标题
                " article_content longtext NOT NULL," + // 内容
                " article_views bigint(20) NOT NULL," + // 浏览量
                " article_date datetime DEFAULT NULL," + // 时间
                " article_like_count bigint(20) NOT NULL)"); // 点赞数
        // 评论表：comments
        db.execSQL("CREATE TABLE "+TABLE_COMMENTS+" (" +
                " comment_id INTEGER PRIMARY KEY," +  // 评论id
                " user_id varchar(20) NOT NULL," + // 用户id
                " article_id INTEGER NOT NULL," + // 博文id
                " comment_like_count bigint(20) NOT NULL," + // 点赞数
                " comment_date datetime DEFAULT NULL," + // 时间
                " comment_content text NOT NULL)"); // 内容
        // 博文分类：sorts
        db.execSQL("CREATE TABLE "+TABLE_SORTS+" (" +
                " sort_id INTEGER PRIMARY KEY," + // 分类id
                " sort_name varchar(50) NOT NULL," + // 分类名
                " comment_date datetime DEFAULT NULL," + // 时间
                " sort_description text NOT NULL)"); // 说明
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}