package com.jari.zhihu.entity;

import java.util.List;

/**
 * Created by hero on 2016/2/27 0027.
 */
public class LatestNews {
    /**
     * date : 20160227
     * stories : [{"images":["http://pic2.zhimg.com/8d7ca3434a850183e81d93b9170e47f5.jpg"],"type":0,"id":7924610,"ga_prefix":"022714","title":"特别实用的老司机经验总结"},{"images":["http://pic2.zhimg.com/424f0327ade31a5eeb3a5ba12f5d2419.jpg"],"type":0,"id":7914360,"ga_prefix":"022713","title":"目前最尖锐的「针」，针尖只有一个原子"},{"images":["http://pic3.zhimg.com/c25bf30b194914e6baa29d6b1ff71c06.jpg"],"type":0,"id":7746166,"ga_prefix":"022712","title":"大误 · 拿走妙蛙种子背上的种子，会发生什么？"},{"images":["http://pic1.zhimg.com/cd8d80533edadf618733afad8dc897cc.jpg"],"type":0,"id":7919737,"ga_prefix":"022711","title":"不用怀疑，更好的抗癌药已经到来"},{"images":["http://pic1.zhimg.com/80498ee30ed4ee3b7aa75b7ff0f24cb8.jpg"],"type":0,"id":7918433,"ga_prefix":"022710","title":"历史上真的有人尝过糖尿病患者的尿液，没错，是甜的"},{"images":["http://pic3.zhimg.com/d4a6a2f96bb79b82e260adf02310d2a2.jpg"],"type":0,"id":7902353,"ga_prefix":"022709","title":"对婚姻来说，孩子的出世会影响夫妻之间的感情吗？"},{"images":["http://pic3.zhimg.com/b06a949254e363da4dc88ae457bc123a.jpg"],"type":0,"id":7924236,"ga_prefix":"022708","title":"中国科学家终于在「体外」获得功能性精子"},{"images":["http://pic3.zhimg.com/4e6c23bd3fe1efd0d484a1a0747af716.jpg"],"type":0,"id":7880014,"ga_prefix":"022707","title":"为什么只有「普通物理」，没有「史诗物理」、「传说物理」？"},{"title":"我居然被一档央视出品，主持人是《走进科学》同款的节目圈粉了","ga_prefix":"022707","images":["http://pic3.zhimg.com/a0b1b288fdf9960918d7258d8fd85a26.jpg"],"multipic":true,"type":0,"id":7914389},{"images":["http://pic4.zhimg.com/69c6dffe6474806ca10dc2f81b08400b.jpg"],"type":0,"id":7838193,"ga_prefix":"022707","title":"周末干什么 · 学点撩妹技能"},{"images":["http://pic4.zhimg.com/10de9f70f1f7c74b681ea781c117304f.jpg"],"type":0,"id":7926237,"ga_prefix":"022707","title":"读读日报 24 小时热门：中国在北美的「富二代」们"},{"images":["http://pic4.zhimg.com/6879ddd64210c8690d60f4b49bb35e5b.jpg"],"type":0,"id":7903941,"ga_prefix":"022706","title":"瞎扯 · 如何正确地吐槽"}]
     * top_stories : [{"image":"http://pic1.zhimg.com/4ba4b8303b77bc7345767886a3e83894.jpg","type":0,"id":7838193,"ga_prefix":"022707","title":"周末干什么 · 学点撩妹技能"},{"image":"http://pic2.zhimg.com/24a0012e73bc35561a2812a591b0bf1d.jpg","type":0,"id":7914389,"ga_prefix":"022707","title":"我居然被一档央视出品，主持人是《走进科学》同款的节目圈粉了"},{"image":"http://pic3.zhimg.com/25e2d99e6028f708bd6aacf862eb2792.jpg","type":0,"id":7924610,"ga_prefix":"022714","title":"特别实用的老司机经验总结"},{"image":"http://pic4.zhimg.com/57b2be5b4e8fd077a772924db90aa577.jpg","type":0,"id":7924236,"ga_prefix":"022708","title":"中国科学家终于在「体外」获得功能性精子"},{"image":"http://pic1.zhimg.com/f3da736b20d12451a0c80063e90029b0.jpg","type":0,"id":7920682,"ga_prefix":"022621","title":"「这个医生，他希望你死亡」"}]
     */

    private String date;
    /**
     * images : ["http://pic2.zhimg.com/8d7ca3434a850183e81d93b9170e47f5.jpg"]
     * type : 0
     * id : 7924610
     * ga_prefix : 022714
     * title : 特别实用的老司机经验总结
     */

    private List<StoriesEntity> stories;
    /**
     * image : http://pic1.zhimg.com/4ba4b8303b77bc7345767886a3e83894.jpg
     * type : 0
     * id : 7838193
     * ga_prefix : 022707
     * title : 周末干什么 · 学点撩妹技能
     */

    private List<TopStoriesEntity> top_stories;

    public void setDate(String date) {
        this.date = date;
    }

    public void setStories(List<StoriesEntity> stories) {
        this.stories = stories;
    }

    public void setTop_stories(List<TopStoriesEntity> top_stories) {
        this.top_stories = top_stories;
    }

    public String getDate() {
        return date;
    }

    public List<StoriesEntity> getStories() {
        return stories;
    }

    public List<TopStoriesEntity> getTop_stories() {
        return top_stories;
    }

    public static class StoriesEntity {
        public static final int TYPE_ITEM = 0 ;
        public static final int TYPE_ITEM_SUMMARY = 1 ;

        private int type;
        private int id;
        private String title;
        private List<String> images;

        public void setType(int type) {
            this.type = type;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getImages() {
            return images;
        }

        /*判断新闻条目的类型*/
        public boolean isStoryItem(){
            return this.type == TYPE_ITEM ;
        }
    }

    public static class TopStoriesEntity {
        private String image;
        private int type;
        private int id;
        private String title;

        public void setImage(String image) {
            this.image = image;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}
