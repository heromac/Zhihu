package com.jari.zhihu.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jari.zhihu.R;
import com.jari.zhihu.db.DBHelper;
import com.jari.zhihu.entity.LatestNews;
import com.jari.zhihu.entity.NewsContent;
import com.jari.zhihu.util.Contants;
import com.jari.zhihu.util.NetworkUtil;
import com.jari.zhihu.util.OkHttpManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class NewsDetailActivity extends AppCompatActivity {
    private static final String TAG = NewsDetailActivity.class.getName();

    public static final String EXTRA_NEWS_ID = "jari.news.id";
    public static final String EXTRA_NEWS_INDEX = "jari.news.index";



    private NewsContent newsContent;
    private ImageView toolbarImageView;
    private WebView webView;
    private ProgressDialogFragment loadingDialog ;

    private Handler handler = new Handler() ;
    private Toolbar toolbar;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("享受阅读的乐趣");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setResult(Activity.RESULT_CANCELED);

        toolbarImageView = (ImageView)findViewById(R.id.iv_toolbar);

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

        if(NetworkUtil.getInstance(this).isConnected()){
            String newsUrl = Contants.URL_NEWS_PREFIX + newsId ;
            loadNewsContentFromNet(newsUrl) ;
        }else {
            loadNewsContentFromDB(newsId) ;
        }

    }

    private void loadNewsContentFromDB(String newsId) {
        showLoadingDialog() ;
        new LoadNewsContentAsyncTask(this).execute(newsId) ;
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
                        setReturnResult();
                        populateNewsContent();
                    }
                });
            }
        });
    }

    private void setReturnResult() {
        int position = getIntent().getIntExtra(EXTRA_NEWS_INDEX, -1) ;
        Intent intent = new Intent() ;
        intent.putExtra(EXTRA_NEWS_INDEX, position) ;
        setResult(Activity.RESULT_OK, intent);
    }

    private void setUpToolbar() {
//        toolbar.setTitle(newsContent.getTitle());
    }

    private void populateNewsContent() {
        if(newsContent == null){
            showEmptyHintView() ;
            hideLoadingDialog() ;
            return;
        }
        Picasso.with(this).load(newsContent.getImage()).fit().into(toolbarImageView);
        //add css
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + newsContent.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");

        webView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);

        hideLoadingDialog() ;

        cacheNewsContent() ;
    }

    private void showEmptyHintView() {
        emptyView.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void cacheNewsContent() {
        new CacheNewsContentAsyncTask(this, newsContent).execute() ;
    }


    private void showLoadingDialog(){
        if(loadingDialog == null)
            loadingDialog = new ProgressDialogFragment("正在加载文章...") ;
        loadingDialog.show(getSupportFragmentManager(), "loading dialog");
    }


    private void hideLoadingDialog(){
        loadingDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public static class ProgressDialogFragment extends DialogFragment{
        private ProgressDialog progressDialog ;
        private String title ;
        public ProgressDialogFragment(String title){
            this.title = title ;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            progressDialog = new ProgressDialog(getContext()) ;
            progressDialog.setMessage(title);
            return progressDialog;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_news_detail, menu);

        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nd_share :
                Toast.makeText(this, "Share pressed", Toast.LENGTH_SHORT).show();   return true;
            case R.id.nd_comments :
                Toast.makeText(this, "Comment pressed", Toast.LENGTH_SHORT).show();    return true;
            case R.id.nd_praise :
                Toast.makeText(this, "Praise pressed", Toast.LENGTH_SHORT).show();    return true;
        }
        return super.onOptionsItemSelected(item) ;
    }


    private static class LoadNewsContentAsyncTask extends AsyncTask<String, Void, NewsContent>{
        private WeakReference<NewsDetailActivity> activityWeakReference ;
        public LoadNewsContentAsyncTask(NewsDetailActivity activity ){
            activityWeakReference = new WeakReference<NewsDetailActivity>(activity) ;
        }

        @Override
        protected NewsContent doInBackground(String... params) {
            NewsDetailActivity activity = activityWeakReference.get() ;
            if(activity == null)
                return null ;

            NewsContent newsContent = DBHelper.getInstance(activity).getNewsContent(Integer.parseInt(params[0])) ;
            return newsContent ;
        }

        @Override
        protected void onPostExecute(NewsContent newsContent) {
            super.onPostExecute(newsContent);
            NewsDetailActivity activity = activityWeakReference.get() ;
            if(activity != null ){
                activity.newsContent = newsContent ;
                activity.populateNewsContent();
            }
            activity.hideLoadingDialog();
        }
    }


    private static class CacheNewsContentAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> contextWeakReference ;
        private NewsContent newsContent ;

        public CacheNewsContentAsyncTask(Context context, NewsContent newsContent){
            contextWeakReference = new WeakReference<Context>(context) ;
            this.newsContent = newsContent ;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = contextWeakReference.get() ;
            if(context == null)
                return null ;

            DBHelper dbHelper = DBHelper.getInstance(context) ;
            dbHelper.addNewsContent(newsContent);
            return null ;
        }

    }
}
