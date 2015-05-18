package com.onecm.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2015/5/18 0018.
 */
public class Comment extends BmobObject {
    private String content;
    private String iconUrl;
    private String nickName;
    private Discover discover;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Discover getDiscover() {
        return discover;
    }

    public void setDiscover(Discover discover) {
        this.discover = discover;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "content='" + content + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", discover=" + discover +
                '}';
    }
}
