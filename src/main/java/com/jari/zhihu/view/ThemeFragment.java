package com.jari.zhihu.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jari.zhihu.R;
import com.jari.zhihu.adapter.ThemeListAdapter;
import com.jari.zhihu.entity.ThemeList;
import com.jari.zhihu.util.AnimatorUtil;
import com.jari.zhihu.util.Contants;
import com.jari.zhihu.util.NetworkUtil;
import com.jari.zhihu.util.OkHttpManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThemeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThemeFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private static final String ARG_THEME_ID = "theme.id";

    private Handler handler = new Handler() ;
    private String themeID;
    private ImageView animatorImageView;
    private ListView listView;
    private ThemeListAdapter themeListAdapter;
    private ThemeList themeList ;

    private NewsDetailActivity.ProgressDialogFragment loadingDialog;
    private TextView themeDesc;

    private Picasso picasso ;
    public ThemeFragment() {
        // Required empty public constructor
    }

    public static ThemeFragment newInstance(String themeID ) {
        ThemeFragment fragment = new ThemeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_THEME_ID, themeID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            themeID = getArguments().getString(ARG_THEME_ID);
        }

        loadThemeContent() ;
        picasso =Picasso.with(getContext()) ;
    }

    private void showFloatingImage() {
        Picasso.with(getContext()).load(themeList.getBackground()).fit().into(animatorImageView);
        themeDesc.setText(themeList.getDescription());
    }

    private void loadThemeContent() {
        OkHttpManager.getInstance().getStringAsync(Contants.URL_THEME_ITEM_PREFIX + themeID,
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("ThemeFragment", e.toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Gson gson = new Gson();
                        themeList = gson.fromJson(response.body().string(), ThemeList.class);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showThemeContent();
                                showFloatingImage();
                                showThemeTitle();
                                hideLoadingDialog();
                            }
                        });
                    }
                });

        showLoadingDialog() ;
    }

    private void showThemeTitle() {
        ((MainActivity)getActivity()).setToolbarTitle(themeList.getName());
    }

    private void showThemeContent() {
        themeListAdapter = new ThemeListAdapter(getContext(), themeList.getStories());
        listView.setAdapter(themeListAdapter);
    }


    private void showLoadingDialog(){
        if(loadingDialog == null)
            loadingDialog = new NewsDetailActivity.ProgressDialogFragment("正在加载文章...") ;
        loadingDialog.show(getActivity().getSupportFragmentManager(), "loading dialog");
    }


    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        listView = (ListView)view.findViewById(R.id.list_view);

        FrameLayout f = (FrameLayout)LayoutInflater.from(getContext()).inflate(R.layout.header_theme_list, listView, false);
        animatorImageView = (ImageView)f.findViewById(R.id.float_image_view) ;
        themeDesc = (TextView)f.findViewById(R.id.tv_theme_desc);

        listView.addHeaderView(f);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        FrameLayout f2 = (FrameLayout)LayoutInflater.from(getContext()).inflate(R.layout.footer_theme_list, listView, false) ;
        listView.addFooterView(f2);
        return view ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        AnimatorUtil.actionFloatAnimator(animatorImageView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        int position = pos - 1  ;  //去掉header
        Intent intent = new Intent(this.getActivity(), SpecThemeActivity.class) ;
        intent.putExtra(SpecThemeActivity.EXTRA_NEWS_ID, String.valueOf(themeList.getStories().get(position).getId())) ;
        startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState){
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
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

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //do nothing
    }
}
