package com.jari.zhihu.view;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jari.zhihu.R;
import com.jari.zhihu.entity.NewsContent;
import com.jari.zhihu.util.Contants;
import com.jari.zhihu.util.NetworkUtil;
import com.jari.zhihu.util.OkHttpManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class SpecThemeActivity extends AppCompatActivity {
    private static final String TAG = SpecThemeActivity.class.getName();

    public static final String EXTRA_NEWS_ID = "jari.news.id";

    private TextView emptyView;
    private WebView webView;
    private NewsDetailActivity.ProgressDialogFragment loadingDialog ;

    private NewsContent newsContent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec_theme);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("享受阅读的乐趣");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emptyView = (TextView) findViewById(R.id.empty_hint);

        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        webView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        webView.getSettings().setAppCacheEnabled(true);
        //设置图片自适应宽度
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        String newsId = getIntent().getStringExtra(EXTRA_NEWS_ID) ;
        if(NetworkUtil.getInstance(this).isConnected()) {
            String newsUrl = Contants.URL_NEWS_PREFIX + newsId;
            loadNewsContentFromNet(newsUrl);
        }else {
            showEmptyHintView() ;
        }
    }

    private void showEmptyHintView() {
        emptyView.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void loadNewsContentFromNet(String newsUrl) {
        showLoadingDialog() ;
        OkHttpManager.getInstance().getStringAsync(newsUrl, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                Gson gson = new Gson();
                newsContent = gson.fromJson(str, NewsContent.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUpToolbar();
                        populateNewsContent();
                        hideLoadingDialog() ;
                    }
                });
            }
        });
    }


    private void showLoadingDialog(){
        if(loadingDialog == null)
            loadingDialog = new NewsDetailActivity.ProgressDialogFragment("正在加载文章...") ;
        loadingDialog.show(getSupportFragmentManager(), "loading dialog");
    }


    private void hideLoadingDialog(){
        loadingDialog.dismiss();
    }



    private void setUpToolbar() {
        toolbar.setTitle(newsContent.getTitle());
    }

    private void populateNewsContent() {
        if(newsContent == null){
            showEmptyHintView() ;
            return;
        }
        //add css
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + newsContent.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");

        webView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

}
