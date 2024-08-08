package com.example.test1.entity;

import java.io.Serializable;
import java.util.List;
public  class NewsEntity implements Serializable{
    private Integer newsId;
    private String newsTitle;
    private String authorName;
    private String headerUrl;
    private Integer commentCount;
    private String releaseDate;
    private Integer type;
    private List<NewsEntity.NewsThumbsDTO> newsThumbs;

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<NewsEntity.NewsThumbsDTO> getNewsThumbs() {
        return newsThumbs;
    }

    public void setNewsThumbs(List<NewsEntity.NewsThumbsDTO> newsThumbs) {
        this.newsThumbs = newsThumbs;
    }

    public static class NewsThumbsDTO {
        private Integer thumbId;
        private String thumbUrl;
        private Integer newsId;

        public Integer getThumbId() {
            return thumbId;
        }
        public void setThumbId(Integer thumbId) {
            this.thumbId = thumbId;
        }
        public String getThumbUrl() {
            return thumbUrl;
        }
        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }
        public Integer getNewsId() {
            return newsId;
        }
        public void setNewsId(Integer newsId) {
            this.newsId = newsId;
        }
    }
}