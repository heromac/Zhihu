package com.jari.zhihu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.jari.zhihu.entity.LatestNews;
import com.jari.zhihu.entity.NewsContent;
import com.jari.zhihu.entity.Theme;
import com.jari.zhihu.util.ImageDownLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hero on 2016/3/2 0002.
 */
public class DBHelper extends SQLiteOpenHelper {


    private static final int VERSION = 1;
    private static final String DB_NAME = "zhihu.db";

    private static final String T_THEME = "theme" ;
    private static final String T_THEME_ID = "id" ;
    private static final String T_THEME_NAME = "name" ;
    private static final String T_THEME_DESC = "desc" ;
    private static final String T_THEME_THUMB = "thumb" ;


    private static final String T_STORY = "story" ;
    private static final String T_STORY_ID = "id" ;
    private static final String T_STORY_TITLE = "title" ;
    private static final String T_STORY_IMAGE = "image" ;
    private static final String T_STORY_DATE = "date" ;


    private static final String T_TOPSTORY = "top_story" ;
    private static final String T_TOPSTORY_TITLE = "title" ;
    private static final String T_TOPSTORY_IMAGE = "image" ;
    private static final String T_TOPSTORY_ID = "id" ;


    private static final String T_CONTENT = "news_content" ;
    private static final String T_CONTENT_ID = "id" ;
    private static final String T_CONTENT_BODY = "body" ;
    private static final String T_CONTENT_TITLE = "title" ;
    private static final String T_CONTENT_IMAGE = "image" ;

    private Context context ;
    public DBHelper(Context context ) {
        super(context, DB_NAME, null, VERSION);
        this.context = context ;
    }


    private static DBHelper instance ;
    public static DBHelper getInstance(Context context){
        if(instance == null){
            synchronized (DBHelper.class){
                if(instance == null){
                    instance = new DBHelper(context.getApplicationContext()) ;  //防止引用activity，不能正常销毁
                }
            }
        }
        return instance ;
    }

    /**
     * 创建表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(
                "create table %s(%s integer primary key," +
                "%s varchar," +
                "%s varchar)",
                T_TOPSTORY, T_TOPSTORY_ID,
                T_TOPSTORY_TITLE,
                T_TOPSTORY_IMAGE ));

        db.execSQL(String.format(
                "create table %s(%s integer primary key," +
                        "%s varchar," +
                        "%s varchar," +
                        "%s varchar)",
                T_STORY, T_STORY_ID,
                T_STORY_TITLE,
                T_STORY_IMAGE,
                T_STORY_DATE));

        db.execSQL(String.format(
                "create table %s(%s integer primary key," +
                        "%s varchar," +
                        "%s varchar," +
                        "%s varchar)",
                T_THEME, T_THEME_ID,
                T_THEME_NAME,
                T_THEME_DESC,
                T_THEME_THUMB));

        db.execSQL(String.format(
                "create table %s(%s integer primary key," +
                        "%s TEXT," +
                        "%s varchar," +
                        "%s varchar)",
                T_CONTENT, T_CONTENT_ID,
                T_CONTENT_BODY,
                T_CONTENT_TITLE,
                T_CONTENT_IMAGE));

        db.execSQL("create table date(id integer primary key autoincrement, date varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " +T_CONTENT);
        db.execSQL("drop table " +T_STORY);
        db.execSQL("drop table " +T_THEME);
        db.execSQL("drop table " +T_TOPSTORY);
        db.execSQL("drop table date");
        onCreate(db);
    }


    /**
     *把图片的网络地址转换为本地地址
     * http://  ->  file://
     * @param url
     * @return
     */
    private String saveImageFile(String url){
        File file = new File(context.getFilesDir(), url.substring(url.lastIndexOf("/")+1)) ;
        if(ImageDownLoader.get(url, file)){
            return Uri.fromFile(file).toString() ;
        }else
            return null ;
    }

    /**
     * 更新theme数据库
     * @param theme
     */
    public void updateTheme(Theme theme){
        if(theme == null)
            return;

        SQLiteDatabase db = getWritableDatabase() ;
        db.delete(T_THEME, null, null) ;

        ContentValues contentValues = new ContentValues() ;
        for(Theme.OthersEntity th : theme.getOthers()){
            contentValues.put(T_THEME_ID, th.getId());
            contentValues.put(T_THEME_NAME, th.getName());
            contentValues.put(T_THEME_DESC, th.getDescription());
            /*contentValues.put(T_THEME_THUMB, );*/
            db.insert(T_THEME, null, contentValues) ;
        }
        db.close();
    }

    /**
     * 获取所有主题
     * @return
     */
    public Theme getTheme(){
        SQLiteDatabase db = getReadableDatabase() ;
        Cursor cursor = db.query(T_THEME, null, null, null, null, null, null) ;
        if(cursor == null)
            return null ;

        Theme theme = new Theme() ;
        List<Theme.OthersEntity> others = new ArrayList<>(cursor.getCount()) ;
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(T_THEME_ID)) ;
            String name = cursor.getString(cursor.getColumnIndex(T_THEME_NAME)) ;
            String desc = cursor.getString(cursor.getColumnIndex(T_THEME_DESC)) ;
            Theme.OthersEntity entity = new Theme.OthersEntity() ;
            entity.setId(id);
            entity.setName(name);
            entity.setDescription(desc);
            others.add(entity) ;
            theme.setOthers(others);
        }
        cursor.close();
        db.close();
        return theme ;
    }

    /**
     * 添加日期为date的新闻
     * @param date
     * @param storiesEntities
     */
    public void addStories(String date, List<LatestNews.StoriesEntity> storiesEntities){
        removeStories(date) ;

        SQLiteDatabase db = getWritableDatabase() ;
        ContentValues contentValues = new ContentValues() ;
        for (LatestNews.StoriesEntity storiesEntity : storiesEntities){
            contentValues.put(T_STORY_ID, storiesEntity.getId());
            contentValues.put(T_STORY_TITLE, storiesEntity.getTitle());
            contentValues.put(T_STORY_DATE, date);
            contentValues.put(T_STORY_IMAGE, saveImageFile(storiesEntity.getImages().get(0)));
            db.insert(T_STORY, null, contentValues) ;
        }
        db.close();
        insertDate(date) ;
    }

    /**
     * 删除日期为date的新闻
     * @param date
     */
    public void removeStories(String date){
        SQLiteDatabase db = getWritableDatabase() ;
        db.delete(T_STORY, T_STORY_DATE + "=?", new String[]{date}) ;
        db.close();
    }

    private void insertDate(String date) {
        SQLiteDatabase db = getWritableDatabase() ;
        Cursor cursor = db.query("date", null, "date=" + date, null, null, null, null) ;
        if(cursor.getCount() == 0){
            ContentValues values = new ContentValues(1) ;
            values.put("date", date);
            db.insert("date", null, values) ;
        }
        cursor.close();
        db.close();
    }

    /**
     * 得到最新日期
     * @return
     */
    public String getLastDate(){
        SQLiteDatabase db = getReadableDatabase() ;
        Cursor cursor = db.query("date", null, null, null, null, null, "date desc") ;
        if(cursor.getCount() > 0){
            cursor.moveToFirst() ;
            String date = cursor.getString(cursor.getColumnIndex("date")) ;
            return date ;
        }
        return null ;
    }

    /**
     * 获取日期为date的新闻
     * @param date
     * @return
     */
    public List<LatestNews.StoriesEntity> getStories(String date){
        SQLiteDatabase db = getWritableDatabase() ;
        List<LatestNews.StoriesEntity> storiesEntities = new ArrayList<>() ;
        Cursor cursor = db.query(T_STORY, null, T_STORY_DATE + "=?", new String[]{date}, null, null, null) ;
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(T_STORY_ID)) ;
            String title = cursor.getString(cursor.getColumnIndex(T_STORY_TITLE)) ;
            String image = cursor.getString(cursor.getColumnIndex(T_STORY_IMAGE)) ;
            LatestNews.StoriesEntity entity = new LatestNews.StoriesEntity() ;
            entity.setId(id);
            entity.setTitle(title);
            entity.setType(LatestNews.StoriesEntity.TYPE_ITEM);
            List<String> imageList = new ArrayList<>() ;
            imageList.add(image) ;
            entity.setImages(imageList);
            storiesEntities.add(entity) ;
        }
        cursor.close();
        db.close();

        return storiesEntities ;
    }


    /**
     * 添加topStories
     * @param topStoriesEntities
     */
    public void addTopStories(List<LatestNews.TopStoriesEntity> topStoriesEntities){
        if(topStoriesEntities==null || topStoriesEntities.size()==0)
            return;

        SQLiteDatabase db = getWritableDatabase() ;
        db.delete(T_TOPSTORY, null, null) ;     //clear topstories

        ContentValues values = new ContentValues() ;
        for (LatestNews.TopStoriesEntity topStoriesEntity : topStoriesEntities){
            values.put(T_TOPSTORY_ID, topStoriesEntity.getId());
            values.put(T_TOPSTORY_TITLE, topStoriesEntity.getTitle());
            values.put(T_TOPSTORY_IMAGE, saveImageFile(topStoriesEntity.getImage()));
            db.insert(T_TOPSTORY, null, values) ;
        }
        db.close();
    }


    /**
     * 获取topStories
     * @return
     */
    public List<LatestNews.TopStoriesEntity> getTopStories(){
        SQLiteDatabase db = getReadableDatabase() ;
        List<LatestNews.TopStoriesEntity> result = new ArrayList<>() ;
        Cursor cursor = db.query(T_TOPSTORY, null, null, null, null, null,null);    //get all
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(T_TOPSTORY_ID)) ;
            String title = cursor.getString(cursor.getColumnIndex(T_TOPSTORY_TITLE)) ;
            String image = cursor.getString(cursor.getColumnIndex(T_TOPSTORY_IMAGE)) ;
            LatestNews.TopStoriesEntity entity = new LatestNews.TopStoriesEntity() ;
            entity.setId(id);
            entity.setTitle(title);
            entity.setImage(image);
            result.add(entity) ;
        }
        cursor.close();
        db.close();
        return result ;
    }


    /**
     * 添加详细新闻
     * @param newsContent
     */
    public void addNewsContent(NewsContent newsContent){
        if(getNewsContent(newsContent.getId()) != null)     //新闻已存在
            return;

        SQLiteDatabase db = getWritableDatabase() ;
        ContentValues values = new ContentValues() ;
        values.put(T_CONTENT_ID, newsContent.getId());
        values.put(T_CONTENT_TITLE, newsContent.getTitle());
        values.put(T_CONTENT_IMAGE, saveImageFile(newsContent.getImage()));
        values.put(T_CONTENT_BODY, newsContent.getBody());
        db.insert(T_CONTENT, null, values) ;
        db.close();
    }


    /**
     * 删除详细新闻
     * @param newsContent
     */
    public void removeNewsContent(NewsContent newsContent){
        SQLiteDatabase db = getWritableDatabase() ;
        db.delete(T_CONTENT, T_CONTENT_ID + "=" + newsContent.getId(), null) ;
        db.close();
    }


    /**
     * 获取详细新闻
     * @param id
     * @return
     */
    public NewsContent getNewsContent(int id){
        SQLiteDatabase db = getReadableDatabase() ;
        Cursor cursor = db.query(T_CONTENT, null, T_CONTENT_ID + "=" + id, null, null, null, null) ;
        NewsContent content = null ;
        if(cursor.getCount() > 0){
            content = new NewsContent() ;
            cursor.moveToFirst() ;
            content.setId(id);
            content.setTitle(cursor.getString(cursor.getColumnIndex(T_CONTENT_TITLE)));
            content.setImage(cursor.getString(cursor.getColumnIndex(T_CONTENT_IMAGE)));
            content.setBody(cursor.getString(cursor.getColumnIndex(T_CONTENT_BODY)));
        }

        cursor.close();
        db.close();
        return content;
    }
}
