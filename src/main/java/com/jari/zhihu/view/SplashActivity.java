package com.jari.zhihu.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jari.zhihu.R;
import com.jari.zhihu.util.Contants;
import com.jari.zhihu.util.FileUtils;
import com.jari.zhihu.util.OkHttpManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity implements SplashFragment.SplashListener{

    private static final String TAG =  SplashActivity.class.getName() ;
    Handler handler = new Handler() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE) ;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        cacheSplashImage();
    }



    private void cacheSplashImage() {
        OkHttpManager.getInstance().getStringAsync(Contants.URL_SPLASH_IMAGE, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "get splash image url onFailure");
            }

            @Override
            public void onResponse(Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String imageUrl = jsonObject.getString("img");
                    System.out.println("imageUrl = " + imageUrl);

                    OkHttpManager.getInstance().getInputStreamAsync(imageUrl, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.e(TAG, "load splash image onFailure");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            saveImageFile(response.body().byteStream());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void saveImageFile(InputStream is) {
        File file = new File(getFilesDir(), "splash_tmp.jpg") ;
        FileUtils.saveFile(file, is);
        file.renameTo(new File(getFilesDir(), "splash.jpg")) ;
        Log.i(TAG, "SplashActivity saveImageFile successfully!");
    }



    @Override
    public void splashStarted() {

    }


    @Override
    public void splashFinished() {
        Log.i(TAG, "SplashActivity.splashFinished");

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

//TODO 1、数据库记录旧新闻 2、menu item点击 3、夜间模式