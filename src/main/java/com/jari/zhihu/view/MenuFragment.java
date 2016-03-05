package com.jari.zhihu.view;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jari.zhihu.R;
import com.jari.zhihu.adapter.MenuListAdapter;
import com.jari.zhihu.db.DBHelper;
import com.jari.zhihu.entity.Theme;
import com.jari.zhihu.presenter.MenuPresenter;
import com.jari.zhihu.util.NetworkUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements IMenuView, AdapterView.OnItemClickListener {

    private static final String ARG_MENU_URL = "arg_url";

    private ListView listView;
    private Button btnFav;
    private Button btnOffDown;
    private TextView userName;
    private ProgressBar progressBar;
    private TextView emptyHintTextView;

    private MenuPresenter presenter ;


    private List<Theme.OthersEntity> data = new ArrayList<>();
    private MenuListAdapter adapter ;

    private String menuUrl ;
    private int lastClickIndex = 1;

    public MenuFragment() {
        presenter = new MenuPresenter(this) ;
    }

    
    public static MenuFragment getInstance(String url){
        MenuFragment menuFragment = new MenuFragment() ;
        Bundle bundle = new Bundle() ;
        bundle.putString(ARG_MENU_URL, url);
        menuFragment.setArguments(bundle);
        return menuFragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments() ;
        String url = bundle.getString(ARG_MENU_URL) ;
        if(url != null)
            menuUrl = url ;
        else
            menuUrl = "" ;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        listView = (ListView)view.findViewById(R.id.theme_list);
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        emptyHintTextView = (TextView) view.findViewById(R.id.empty_hint);


        View headerView = inflater.inflate(R.layout.item_menu_list_header, null, false);
        btnFav = (Button)headerView.findViewById(R.id.btn_fav);
        btnOffDown = (Button)headerView.findViewById(R.id.btn_offline_download);

        userName = (TextView) headerView.findViewById(R.id.account_user_name);
        listView.addHeaderView(headerView);

        return view ;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new MenuListAdapter(this.getActivity(), R.layout.item_menu_list, data) ;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if(NetworkUtil.getInstance(getContext()).isConnected())
            presenter.loadThemesFromNet(menuUrl);   //网络加载
        else
            presenter.loadThemesFromDB(getContext());   //数据库加载
    }



    @Override
    public void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMenuContent(Theme theme) {
        if (theme == null){
            loadThemeFailed("Load Theme Failed.", null);
            return;
        }

        emptyHintTextView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        data.clear();
        Theme.OthersEntity homeEntity = new Theme.OthersEntity() ;
        homeEntity.setId(-1);
        homeEntity.setName("首页") ;
        data.add(0, homeEntity);  //添加“首页”项
        data.addAll(theme.getOthers()) ;
        adapter.notifyDataSetChanged();
        listView.invalidate();

        updateThemeInDB(theme) ;
    }

    private void updateThemeInDB(Theme theme) {
        new CacheThemeAsyncTask(getContext()).execute(theme) ;
    }

    @Override
    public void loadThemeFailed(String errorMsg, Exception e) {
        emptyHintTextView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        emptyHintTextView.setText(errorMsg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        ((MainActivity)getActivity()).closeDrawer();

        if(pos == lastClickIndex)
            return;

        int position = pos - 1 ;
        Theme.OthersEntity theme = data.get(position) ;

        Fragment fragment =null ;
        if(position == 0)
            fragment = new HomePageFragment() ;
        else
            fragment = ThemeFragment.newInstance(String.valueOf(theme.getId())) ;

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager() ;
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit() ;

        lastClickIndex = pos ;
    }


    private static class CacheThemeAsyncTask extends AsyncTask<Theme, Void, Void> {
        private WeakReference<Context> contextWeakReference ;
        public CacheThemeAsyncTask(Context context){
            contextWeakReference = new WeakReference<Context>(context) ;
        }

        @Override
        protected Void doInBackground(Theme... params) {
            Context context = contextWeakReference.get() ;
            if(context == null)
                return null ;

            DBHelper dbHelper = DBHelper.getInstance(context) ;
            dbHelper.updateTheme(params[0]);
            return null ;
        }

    }
}
