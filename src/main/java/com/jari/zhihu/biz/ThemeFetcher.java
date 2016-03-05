package com.jari.zhihu.biz;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.jari.zhihu.db.DBHelper;
import com.jari.zhihu.entity.Theme;
import com.jari.zhihu.util.OkHttpManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by hero on 2016/1/17 0017.
 */
public class ThemeFetcher implements IThemeMenuModel  {

    public static final String TAG = ThemeFetcher.class.getName() ;

    IThemeLoadListener themeLoadListener ;

    public ThemeFetcher(IThemeLoadListener themeLoadListener) {
        this.themeLoadListener = themeLoadListener ;
    }

    @Override
    public void getThemeFromDB(Context context) {
        new LoadDBThemeAsyncTask(context, themeLoadListener).execute() ;
    }


    @Override
    public void getThemeFromNet(String url){
        OkHttpManager.getInstance().getStringAsync(url, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Theme load error") ;
                if(themeLoadListener != null)
                    themeLoadListener.onFail("Theme load error", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i(TAG, "Theme load success") ;
                if(themeLoadListener != null){
                    Gson gson = new Gson() ;
                    Theme theme = gson.fromJson(response.body().string(), Theme.class) ;
                    themeLoadListener.onSuccess(theme);
                }

            }
        });
    }


    private static class LoadDBThemeAsyncTask extends AsyncTask<Void, Void, Theme>{
        private WeakReference<Context> contextWeakReference ;
        private WeakReference<IThemeLoadListener> themeLoadListenerWeakReference ;
        public LoadDBThemeAsyncTask(Context context, IThemeLoadListener themeLoadListener){
            contextWeakReference = new WeakReference<Context>(context) ;
            themeLoadListenerWeakReference = new WeakReference<IThemeLoadListener>(themeLoadListener) ;
        }


        @Override
        protected Theme doInBackground(Void... params) {
            Context context = contextWeakReference.get();
            if(context == null)
                return null ;

            return DBHelper.getInstance(context).getTheme() ;
        }

        @Override
        protected void onPostExecute(Theme theme) {
            super.onPostExecute(theme);
            if(themeLoadListenerWeakReference.get() != null){
                themeLoadListenerWeakReference.get().onSuccess(theme);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(themeLoadListenerWeakReference.get() != null){
                themeLoadListenerWeakReference.get().onFail("Load DB Theme error", null);
            }
        }
    }

}
