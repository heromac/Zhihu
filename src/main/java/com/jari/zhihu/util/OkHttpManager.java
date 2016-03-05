package com.jari.zhihu.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by hero on 2016/1/17 0017.
 */
public class OkHttpManager  {

    private static OkHttpManager instance ;

    public static OkHttpManager getInstance(){
        if(instance == null){
            synchronized (OkHttpManager.class){
                if(instance == null)
                    instance = new OkHttpManager() ;
            }
        }
        return instance ;
    }


    private OkHttpClient httpClient ;
    private Handler handler ;

    public OkHttpManager(){
        httpClient = new OkHttpClient() ;
        handler = new Handler(Looper.getMainLooper()) ;
    }


    public void getString(String url, final ResultCallback syncCallback){
        Request request = new Request.Builder()
                .url(url)
                .build() ;

        Call call = httpClient.newCall(request) ;

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendCallbackFail(syncCallback, request, e) ;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseBody = response.body().string() ;
                sendCallbackSuccess(syncCallback, responseBody) ;
            }
        });
    }


    public void getStringAsync(String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = httpClient.newCall(request) ;
        call.enqueue(callback);
    }



    public void getInputStream(String url, final ResultCallback syncCallback){
        Request request = new Request.Builder()
                .url(url)
                .build() ;

        Call call = httpClient.newCall(request) ;

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendCallbackFail(syncCallback, request, e) ;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream responseBody = response.body().byteStream() ;
                sendCallbackSuccess(syncCallback, responseBody) ;
            }
        });
    }


    public void getInputStreamAsync(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = httpClient.newCall(request) ;
        call.enqueue(callback);
    }





    private void sendCallbackFail(final ResultCallback syncCallback, final Request request, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (syncCallback != null)
                    syncCallback.onFailure(request, e);
            }
        }) ;
    }


    private void sendCallbackSuccess(final ResultCallback syncCallback, final Object obj) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(syncCallback != null)
                    syncCallback.onResponse(obj);
            }
        }) ;
    }





    public static abstract class ResultCallback<T>{

        public abstract void onFailure(Request request, Exception e) ;
        public abstract void onResponse(T response) ;
    }

}
