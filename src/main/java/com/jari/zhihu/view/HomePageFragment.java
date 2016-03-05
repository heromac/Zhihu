package com.jari.zhihu.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jari.zhihu.R;
import com.jari.zhihu.adapter.NewsListAdapter;
import com.jari.zhihu.db.DBHelper;
import com.jari.zhihu.entity.LatestNews;
import com.jari.zhihu.util.Contants;
import com.jari.zhihu.util.NetworkUtil;
import com.jari.zhihu.util.OkHttpManager;
import com.jari.zhihu.view.widget.AutoScrollPager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 */
public class HomePageFragment extends Fragment implements AutoScrollPager.OnPageClickedListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private static final String TAG = HomePageFragment.class.getName() ;
    public static final int HEADER_COUNT = 1;   //给listview添加的header个数

    private List<LatestNews> latestNews ;

    private List<LatestNews.StoriesEntity> stories; //for listview adapter

    private String lastDate ;   //新闻日期，加载过往新闻时使用

    private boolean isLoadingBeforeNews ;

    private Set<Integer> clickedNewsPosition ;

    private AutoScrollPager pager;
    private ListView newsListView;


    private Handler handler = new Handler(Looper.getMainLooper()) ;
    private NewsListAdapter newsListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Picasso picasso ;
    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latestNews = new ArrayList<>() ;
        stories = new ArrayList<>() ;
        clickedNewsPosition = new HashSet<>() ;
        picasso =Picasso.with(getContext()) ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_content, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_main_content);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_orange_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);


        newsListView = (ListView)view.findViewById(R.id.lv_news_list);

        pager = (AutoScrollPager)inflater.inflate(R.layout.header_news_list, newsListView, false);

        pager.setOnPageClickedListener(this);

        newsListView.addHeaderView(pager, null, true);

        newsListAdapter = new NewsListAdapter(getContext(), stories, clickedNewsPosition);
        newsListView.setAdapter(newsListAdapter);

        newsListView.setOnScrollListener(this);
        newsListView.setOnItemClickListener(this);

        return view ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(NetworkUtil.getInstance(getContext()).isConnected())
            loadTopNewsFromNet() ;
        else
            loadTopNewsFromDB();
    }

    private void loadTopNewsFromDB() {
        showLoadProgress();
        new LoadDBStoriesAsyncTask(this).execute() ;
    }

    private void loadTopNewsFromNet(){
        Callback callbackNewsJson = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.toString() ) ;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonStr = response.body().string() ;
                Gson gson = new Gson() ;
                final LatestNews news = gson.fromJson(jsonStr, LatestNews.class) ;
                latestNews.add(news) ;
                lastDate = news.getDate() ;

                populateStoryListView(news.getStories());
                populateTopStories(news.getTop_stories());
                cacheStoriesToDB(news.getStories()) ;
                cacheTopStoriesToDB(news.getTop_stories()) ;
                hideLoadProgress();
            }
        } ;

        OkHttpManager.getInstance().getStringAsync(Contants.URL_LATEST_NEWS, callbackNewsJson);
        showLoadProgress();
    }

    private void cacheTopStoriesToDB(final List<LatestNews.TopStoriesEntity> topnews) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                addTopStoriesToDB(topnews);
            }
        }) ;
    }

    private void cacheStoriesToDB(final List<LatestNews.StoriesEntity> news) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                addStoriesToDB(news);
            }
        }) ;
    }


    /**
     * 将数据显示在ListView和ViewPage中
     * @param news
     */
    private void populateStoryListView(final List<LatestNews.StoriesEntity> news) {
        stories.clear();
        addSummaryForStories() ;    //添加日期标题
        stories.addAll(news) ;

        handler.post(new Runnable() {
            @Override
            public void run() {
                newsListAdapter.notifyDataSetChanged();
            }
        }) ;
    }

    /**
     * 新闻列表缓存到数据库中
     * @param news
     */
    private void addStoriesToDB(List<LatestNews.StoriesEntity> news) {
        new CacheStoryAsyncTask(getContext(), lastDate, news).execute() ;
    }


    private void populateTopStories(final List<LatestNews.TopStoriesEntity> topnews){
        handler.post(new Runnable() {
            @Override
            public void run() {
                setupViewPagerContent(topnews);
            }
        }) ;
    }

    /**
     * 轮播新闻缓存到数据库中
     * @param news
     */
    private void addTopStoriesToDB(List<LatestNews.TopStoriesEntity> news) {
        new CacheTopStoryAsyncTask(getContext(), news).execute() ;
    }


    private void addSummaryForStories() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA) ;
        String today = simpleDateFormat.format(new Date()) ;

        LatestNews.StoriesEntity storiesEntity = new LatestNews.StoriesEntity() ;
        storiesEntity.setType(LatestNews.StoriesEntity.TYPE_ITEM_SUMMARY);

        if(today.compareTo(lastDate) == 0){
            storiesEntity.setTitle("今日热文");
        }else {
            Date date = null ;
            try {
                date = simpleDateFormat.parse(lastDate) ;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            simpleDateFormat.applyLocalizedPattern("MM月dd日 EEE");

            storiesEntity.setTitle(simpleDateFormat.format(date));
        }

        stories.add(storiesEntity) ;
    }


    private void loadBeforeNewsFromNet(){
        Callback callbackNewsJson = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.toString() ) ;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonStr = response.body().string() ;
                Gson gson = new Gson() ;
                LatestNews news = gson.fromJson(jsonStr, LatestNews.class) ;
                latestNews.add(news) ;
                lastDate = news.getDate() ;

                addSummaryForStories() ;
                stories.addAll(news.getStories()) ;


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        newsListAdapter.notifyDataSetChanged();
                        hideLoadProgress();
                        isLoadingBeforeNews = false ;
                    }
                }) ;
            }
        } ;

        OkHttpManager.getInstance().getStringAsync(Contants.URL_NEWS_BEFORE_PREFIX + lastDate, callbackNewsJson);
        showLoadProgress();
        isLoadingBeforeNews = true ;
    }

    private void hideLoadProgress() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }) ;

    }


    private void showLoadProgress() {
        //注意：直接调用swipeRefreshLayout.setRefreshing(true);是出不来效果的
        //须使用swipeRefreshLayout.post( Runnable)
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }) ;
    }


    private void setupListViewContent(LatestNews news) {

    }

    private void setupViewPagerContent( List<LatestNews.TopStoriesEntity> news) {
        pager.setTopStoriesEntity(news);
    }


    /**
     * 点击图片轮播栏的回调
     * @param newsId
     */
    @Override
    public void pageClicked(int newsId) {
        System.out.println("newsId = " + newsId);
        startNewsDetailActivity(newsId, -1) ;
    }


    /**
     * swipeRefreshLayout 回调
     */
    @Override
    public void onRefresh() {
        if(NetworkUtil.getInstance(getContext()).isConnected())
            loadTopNewsFromNet() ;
        else
            loadTopNewsFromDB();
    }


    /**
     * listview 滚动监听回调
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 当不滚动时
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if ((view.getLastVisiblePosition() == view.getCount() - 1) && (! isLoadingBeforeNews)) {
                    System.out.println("滑动到listview底部");
                    if(NetworkUtil.getInstance(getContext()).isConnected())
                        loadBeforeNewsFromNet();
                }
                picasso.resumeTag(getContext());
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                picasso.pauseTag(getContext());
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL :
                picasso.resumeTag(getContext());
                break;
        }
    }

    //更新Toolbar，显示以往文章的日期
    private int listViewFirstVisibleItem = 0 ;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem > listViewFirstVisibleItem){
            if(newsListAdapter.getItemViewType(firstVisibleItem) == LatestNews.StoriesEntity.TYPE_ITEM_SUMMARY){
                //为简便起见，此处不考虑解耦
                ((MainActivity)getActivity()).setToolbarTitle(((LatestNews.StoriesEntity) newsListAdapter.getItem(firstVisibleItem)).getTitle());
            }

            listViewFirstVisibleItem = firstVisibleItem ;
        }else if(firstVisibleItem < listViewFirstVisibleItem){
            if(newsListAdapter.getItemViewType(listViewFirstVisibleItem) == LatestNews.StoriesEntity.TYPE_ITEM_SUMMARY){
                for (int i = firstVisibleItem; i >=0 ; i--) {
                    if(newsListAdapter.getItemViewType(i) == LatestNews.StoriesEntity.TYPE_ITEM_SUMMARY){
                        ((MainActivity)getActivity()).setToolbarTitle(((LatestNews.StoriesEntity) newsListAdapter.getItem(i)).getTitle());
                        break;
                    }
                }
            }

            listViewFirstVisibleItem = firstVisibleItem ;
        }
    }


    //新闻列表点击回调
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int listPosition = position - HEADER_COUNT;   //给listview加入header之后，position随之增加
        LatestNews.StoriesEntity storiesEntity = stories.get(listPosition) ;
        if(storiesEntity.isStoryItem()) {
            startNewsDetailActivity(storiesEntity.getId(), listPosition) ;
        }
    }

    private void startNewsDetailActivity(int newsID, int position) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class) ;
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ID, String.valueOf(newsID)) ;
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS_INDEX, position) ;
        startActivityForResult(intent, 0);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    /**
     * 打开详细新闻后，列表显示为已读
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            int position = data.getIntExtra(NewsDetailActivity.EXTRA_NEWS_INDEX, -1) ;
            if(position != -1) {
                clickedNewsPosition.add(position);
                newsListAdapter.notifyDataSetChanged();
            }
        }
    }



    private static class LoadDBStoriesAsyncTask extends AsyncTask<Void, Void, LatestNews>{
        private WeakReference<HomePageFragment> fragmentWeakReference ;
        public LoadDBStoriesAsyncTask(HomePageFragment fragment ){
            fragmentWeakReference = new WeakReference<HomePageFragment>(fragment) ;
        }

        @Override
        protected LatestNews doInBackground(Void... params) {
            HomePageFragment fragment = fragmentWeakReference.get() ;
            if(fragment == null)
                return null ;

            DBHelper dbHelper = DBHelper.getInstance(fragment.getContext()) ;
            String date = dbHelper.getLastDate() ;

            List<LatestNews.StoriesEntity> storiesEntities = dbHelper.getStories(date) ;
            List<LatestNews.TopStoriesEntity> topStoriesEntities = dbHelper.getTopStories() ;

            LatestNews latestNews = new LatestNews() ;
            latestNews.setStories(storiesEntities);
            latestNews.setTop_stories(topStoriesEntities);
            latestNews.setDate(date);
            return latestNews ;
        }

        @Override
        protected void onPostExecute(LatestNews latestNews) {
            super.onPostExecute(latestNews);
            HomePageFragment fragment = fragmentWeakReference.get() ;
            if(fragment == null)
                return ;

            if(latestNews == null){
                fragment.hideLoadProgress();
                return;
            }

            fragment.lastDate = latestNews.getDate();
            fragment.populateTopStories(latestNews.getTop_stories());
            fragment.populateStoryListView(latestNews.getStories());
            fragment.hideLoadProgress();
        }
    }

    private static class CacheStoryAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> contextWeakReference ;
        private String date ;
        private List<LatestNews.StoriesEntity> storiesEntities ;

        public CacheStoryAsyncTask(Context context, String date, List<LatestNews.StoriesEntity> storiesEntities){
            contextWeakReference = new WeakReference<Context>(context) ;
            this.date = date ;
            this.storiesEntities = storiesEntities ;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = contextWeakReference.get() ;
            if(context == null)
                return null ;

            DBHelper dbHelper = DBHelper.getInstance(context) ;
            dbHelper.addStories(date, storiesEntities);
            return null ;
        }

    }

    private static class CacheTopStoryAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> contextWeakReference ;
        private List<LatestNews.TopStoriesEntity> storiesEntities ;

        public CacheTopStoryAsyncTask(Context context, List<LatestNews.TopStoriesEntity> storiesEntities){
            contextWeakReference = new WeakReference<Context>(context) ;
            this.storiesEntities = storiesEntities ;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = contextWeakReference.get() ;
            if(context == null)
                return null ;

            DBHelper dbHelper = DBHelper.getInstance(context) ;
            dbHelper.addTopStories(storiesEntities);
            return null ;
        }

    }
}
