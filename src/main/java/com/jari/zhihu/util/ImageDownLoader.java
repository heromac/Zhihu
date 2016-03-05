package com.jari.zhihu.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hero on 2016/3/2 0002.
 */
public class ImageDownLoader {

    public static boolean get(String urlStr, File destFile){
        try {
            URL url = new URL(urlStr) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode() ;
            if(!(responseCode>=200 && responseCode<300)){
                connection.disconnect();
                return false ;
            }
            InputStream is = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(destFile) ;
            byte tmp[] = new byte[1024] ;
            int readLength = 0 ;
            while ((readLength=is.read(tmp, 0, 1024)) > 0){
                fos.write(tmp, 0, readLength);
            }
            fos.flush();
            fos.close();
            is.close();
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true ;
    }
}
