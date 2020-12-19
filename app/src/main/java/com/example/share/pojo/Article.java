package com.example.share.pojo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博文表封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
    private int articleId;
    private String userId;
    private String userName;
    private int sortId;
    private String title;
    private String context;
    private int views;
    private String date;
    private int likeCount;
}
