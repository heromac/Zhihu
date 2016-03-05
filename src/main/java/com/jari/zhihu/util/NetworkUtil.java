package com.jari.zhihu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 16-3-3.
 */
public class NetworkUtil {

    private static NetworkUtil instance ;

    private Context context ;
    public NetworkUtil(Context context) {
        this.context = context ;
    }

    public static NetworkUtil getInstance(Context context){
        if(instance == null){
            synchronized (NetworkUtil.class){
                if(instance == null)
                    instance = new NetworkUtil(context.getApplicationContext()) ;
            }
        }
        return instance ;
    }


    /**
     * 判断wifi或mobile链接
     * @return
     */
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo!=null && networkInfo.isConnected()) {
            int connType = networkInfo.getType();
            if (connType == ConnectivityManager.TYPE_MOBILE || connType == ConnectivityManager.TYPE_WIFI) {
                return true ;
            }
        }
        return false ;
    }
}
