package com.jari.zhihu.biz;


import android.content.Context;

import com.jari.zhihu.entity.Theme;

/**
 * Created by hero on 2016/1/17 0017.
 */
public interface IThemeMenuModel {


    void getThemeFromNet(String url) ;

    void getThemeFromDB(Context context) ;

    public interface IThemeLoadListener{
        void onFail(String msg, Exception e) ;
        void onSuccess(Theme theme) ;
    }
}
