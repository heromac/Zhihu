package com.jari.zhihu.view;

import android.graphics.Color;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.jari.zhihu.R;
import com.jari.zhihu.util.Contants;


public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setupViews() ;

    }

    private void setupViews() {
        toolbar.setTitle(R.string.homepage);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close) ;
        toggle.syncState();
        drawerLayout.setDrawerListener(toggle);

        loadMenus() ;

        loadContents() ;
    }

    private void loadMenus() {
        FragmentManager fragmentManager = getSupportFragmentManager() ;

        Fragment fragment = fragmentManager.findFragmentById(R.id.frag_menu) ;
        if(fragment == null) {
            fragment = MenuFragment.getInstance(Contants.URL_THEME_LIST);
            fragmentManager.beginTransaction()
                    .add(R.id.frag_menu, fragment)
                    .commit();
        }
    }


    public void openDrawer(){
        if(! drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.openDrawer(Gravity.LEFT);
    }


    public void closeDrawer(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
    }


    private void loadContents() {
        FragmentManager fragmentManager = getSupportFragmentManager() ;

        Fragment fragment = fragmentManager.findFragmentById(R.id.main_content) ;
        if(fragment == null) {
            fragment = new HomePageFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.main_content, fragment)
                    .commit();
        }
    }


    public void setToolbarTitle(String name){
        if(name != null)
            toolbar.setTitle(name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("MainActivity.onSaveInstanceState");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_home_page, menu);
        return true ;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.message :
                Toast.makeText(this, "Open Loggin Page", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings :
                Toast.makeText(this, "Open Settings Page", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    long lastBackClickedTime = 0 ;
    @Override
    public void onBackPressed() {
        long current = System.currentTimeMillis() ;
        if(current - lastBackClickedTime < 3000){
            this.finish();
            android.os.Process.killProcess(Process.myPid());
        }else {
            Toast.makeText(this, "再按一次 软件退出", Toast.LENGTH_SHORT).show();
        }
        lastBackClickedTime = current ;
    }
}