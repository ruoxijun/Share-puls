package com.example.share.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博文表封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    private int articleId;
    private String userId;
    private int sortId;
    private String articleTitle;
    private String articleContext;
    private int articleViews;
    private String articleDate;
    private int articleLikeCount;

}
