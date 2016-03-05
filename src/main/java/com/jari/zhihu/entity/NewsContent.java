package com.jari.zhihu.entity;

/**
 * Created by hero on 2016/2/28 0028.
 */
public class NewsContent {
    /**
     * body : <div class="main-wrap content-wrap">
     <div class="headline">
     ...
     * image_source : 16:9clue / CC BY
     * title : 一条 po 图称「女孩子等地铁时蹲着没教养」的微博火了，这样做合适吗？
     * image : http://pic3.zhimg.com/3412bff8abac77fd3f35eed0c1aecd66.jpg
     * share_url : http://daily.zhihu.com/story/7932006
     * js : []
     * ga_prefix : 022816
     * type : 0
     * id : 7932006
     * css : ["http://news-at.zhihu.com/css/news_qa.auto.css?v=77778"]
     */

    private String body;
    private String image_source;
    private String title;
    private String image;
    private String share_url;
    private int id;

    public void setBody(String body) {
        this.body = body;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public String getImage_source() {
        return image_source;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getShare_url() {
        return share_url;
    }

    public int getId() {
        return id;
    }
}
