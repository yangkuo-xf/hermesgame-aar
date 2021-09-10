package com.hermesgamesdk.gamebox.service;


import android.os.Handler;

import android.webkit.WebView;


import com.hermesgamesdk.gamebox.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;


public class GBUtils {
    private static WebView mainv;
    private static Handler mHandler;
    public static GBUtils instance;

    private static String authToken="";

    public List<FileInfo> downloadingFiles=new ArrayList<FileInfo>();

    public static GBUtils getInstance() {
        if (instance == null) {
            instance = new GBUtils();
        }
        return instance;
    }


}
