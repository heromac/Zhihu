package com.jari.zhihu.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jari.zhihu.biz.IThemeMenuModel;
import com.jari.zhihu.biz.ThemeFetcher;
import com.jari.zhihu.entity.Theme;
import com.jari.zhihu.view.IMenuView;

/**
 * Created by hero on 2016/1/17 0017.
 */
public class MenuPresenter implements IThemeMenuModel.IThemeLoadListener{

    IThemeMenuModel menuModel ;
    IMenuView menuView ;

    Handler handler ;


    public MenuPresenter(IMenuView menuView){
        this.menuView = menuView ;
        this.menuModel = new ThemeFetcher(this) ;
        handler = new Handler(Looper.getMainLooper()) ;
    }


    public void loadThemesFromNet (String url){
        menuModel.getThemeFromNet(url);
    }


    public void loadThemesFromDB (Context context){
        menuModel.getThemeFromDB(context);
    }


    @Override
    public void onFail(final String msg, final Exception e) {
        menuView.hideProgressbar();
        handler.post(new Runnable() {
            @Override
            public void run() {
                menuView.loadThemeFailed(msg, e);
            }
        }) ;
    }

    @Override
    public void onSuccess(final Theme theme) {
        menuView.hideProgressbar();

        handler.post(new Runnable() {
            @Override
            public void run() {
                menuView.showMenuContent(theme);
            }
        }) ;

    }
}
