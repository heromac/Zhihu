package com.jari.zhihu.view;

import com.jari.zhihu.entity.Theme;

/**
 * Created by hero on 2016/1/17 0017.
 */
public interface IMenuView {

    void showProgressbar() ;

    void hideProgressbar() ;

    void showMenuContent(Theme theme) ;

    void loadThemeFailed(String errorMsg, Exception e) ;
}
