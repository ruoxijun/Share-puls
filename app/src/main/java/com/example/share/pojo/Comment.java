package com.example.share.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private String userId;
    private String userName;
    private int articleId;
    private int likeCount;
    private String date;
    private String content;
    public Comment (String userId,String userName,int articleId,String date,String content){
        this.userId = userId;
        this.userName = userName;
        this.articleId = articleId;
        this.date = date;
        this.content = content;
    }
}
