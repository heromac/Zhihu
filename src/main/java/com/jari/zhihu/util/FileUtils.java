package com.jari.zhihu.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hero on 2016/1/17 0017.
 */
public class FileUtils {

    public static void saveFile(File target, InputStream is){
//        target.delete() ;
        if(is != null){
            FileOutputStream fis = null ;
            try{
                byte[] buff = new byte[1024] ;
                int len = 0 ;
                fis= new FileOutputStream(target) ;
                while ((len = is.read(buff)) > 0){
                    fis.write(buff, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
