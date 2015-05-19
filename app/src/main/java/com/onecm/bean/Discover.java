package com.onecm.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2015/3/27 0027.
 */
public class Discover extends BmobObject {
    private int disId;
    private String content;
    private String author;
    private String date;
    private BmobFile disImg;
    private BmobRelation comments;
    private Integer like;
    private Integer collect;
    private Integer looked;

    public void setDisId(int disId) {
        this.disId = disId;
    }

    public Integer getLooked() {
        return looked;
    }

    public void setLooked(Integer looked) {
        this.looked = looked;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public void setCollect(Integer collect) {
        this.collect = collect;
    }

    public BmobRelation getComments() {
        return comments;
    }

    public void setComments(BmobRelation comments) {
        this.comments = comments;
    }

    public int getDisId() {
        return disId;
    }

    public Integer getCollect() {
        return collect;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BmobFile getDisImg() {
        return disImg;
    }

    public void setDisImg(BmobFile disImg) {
        this.disImg = disImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getLike() {
        return like;
    }

    @Override
    public String toString() {
        return "Discover{" +
                "disId=" + disId +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", disImg=" + disImg +
                ", like=" + like +
                '}';
    }
}
