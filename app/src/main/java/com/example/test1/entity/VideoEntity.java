package com.example.test1.entity;

import android.net.Uri;

import java.io.Serializable;

public class VideoEntity implements Serializable {
    private int id;
    private String title;
    private String name;
    private int dzCount;
    private int collectCount;
    private int commentCOUNT;

    private int categoryId;

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    private String headUrl;
    private String imgCover;
    private String playUrl;

    public VideoEntity(int id,String vtitle, String author, int collectNum, int likeNum, int commentNum,String headUrl,String imgCover,int categoryId,String playUrl) {
        this.playUrl = playUrl;
        this.id = id;
        this.title = vtitle;
        this.name = author;
        this.collectCount = collectNum;
        this.dzCount = likeNum;
        this.commentCOUNT = commentNum;
        this.headUrl = headUrl;
        this.imgCover = imgCover;
        this.categoryId = categoryId;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getImgCover() {
        return imgCover;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setImgCover(String imgCover) {
        this.imgCover = imgCover;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDzCount() {
        return dzCount;
    }

    public void setDzCount(int dzCount) {
        this.dzCount = dzCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getCommentCOUNT() {
        return commentCOUNT;
    }

    public void setCommentCOUNT(int commentCOUNT) {
        this.commentCOUNT = commentCOUNT;
    }
}
