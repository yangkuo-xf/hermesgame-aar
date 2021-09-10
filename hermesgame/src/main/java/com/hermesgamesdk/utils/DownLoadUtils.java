package com.hermesgamesdk.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.hermesgamesdk.manager.DataManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownLoadUtils {
    private static int mFinished = 0;
    public static final int MSG_INIT = 0;
    public static final int MSG_UPDATE = 1;
    public static final int MSG_END = 2;
    public static ExecutorService sExecutorService = Executors.newFixedThreadPool(20);
    private  static DownLoadUtils sInstance;
    private  static Context  mContext;
    private  static DataManager.DownloadUpdateListener mListener;
    public static DownLoadUtils getInstance(Context context) {
         mContext=context;
        if (sInstance == null) {
            sInstance = new DownLoadUtils();
        }
        return sInstance;
    }

    static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    Log.e("hermesgame","MSG_INIT");
                    ApkInfo fileInfo = (ApkInfo) msg.obj;
                    DownloadThread downloadThread=new DownloadThread(fileInfo);
                    sExecutorService.execute(downloadThread);
                    break;
                case MSG_UPDATE:
                    Log.e("hermesgame","MSG_UPDATE");
                    ApkInfo apkInfo = (ApkInfo) msg.obj;
                    int progress = (int) ((apkInfo.getHasDown() / (apkInfo.getSize() * 1.0f)) * 100);
                    mListener.onUpdate(progress,(int)apkInfo.getHasDown(),(int)apkInfo.getSize());
                    break;
                case MSG_END:
                    Log.e("hermesgame","MSG_END");
                    ApkInfo mApkInfo = (ApkInfo) msg.obj;

                    mListener.onUpdate(100,(int)mApkInfo.getHasDown(),(int)mApkInfo.getSize());
                    break;
            }
        };
    };

    public void setDownListener( DataManager.DownloadUpdateListener listener){
        mListener=listener;
    }
   public static class InitThread extends Thread{

        private ApkInfo apkInfo = null;
        public InitThread(ApkInfo mApkInfo) {
            super();
            this.apkInfo = mApkInfo;
        }
        @Override
        public void run() {

            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(apkInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                int length = -1;
                if (code == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                //如果文件长度为小于0，表示获取文件失败，直接返回
                if (length <= 0) {
                    return;
                }
                // 判斷文件路徑是否存在，不存在這創建
                File dir = new File(apkInfo.getPath());

                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 創建本地文件
                File file = new File(dir, apkInfo.getName());
                raf = new RandomAccessFile(file, "rwd");
                raf.setLength(length);
                // 設置文件長度

                apkInfo.setSize(length);
                // 將FileInfo對象傳遞給Handler
                Message msg = Message.obtain();
                msg.obj = apkInfo;
                msg.what = MSG_INIT;
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }


    static class DownloadThread extends Thread {
        private ApkInfo mApkInfo = null;
        // ��ʶ�߳��Ƿ�ִ�����

        public DownloadThread(ApkInfo apkInfo) {
            this.mApkInfo = apkInfo;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                URL url = new URL(mApkInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                String startString=QGSdkUtils.getString(mContext,mApkInfo.getName()).equals("")?"0":QGSdkUtils.getString(mContext,mApkInfo.getName());
                int start = Integer.valueOf(startString);
                if (start==mApkInfo.getSize()){
                    QGSdkUtils.saveString(mContext,mApkInfo.getName(),""+start);
                    mApkInfo.setHasDown(start);
                    Message message=new Message();
                    message.what=MSG_END;
                    message.obj=mApkInfo;
                    mHandler.sendMessage(message);
                    return;
                }

                conn.setRequestProperty("Range", "bytes=" + start + "-" + mApkInfo.getSize());
                Log.d("hermesgame","DownloadThread Range: "+start+"  --- "+ mApkInfo.getSize());
                File file = new File(mApkInfo.getPath(), mApkInfo.getName());

                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                mFinished +=start;

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_PARTIAL||code == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                    byte[] bt = new byte[1024];
                    int len = -1;
                    int hasWrited=0;
                    long time = System.currentTimeMillis();
                    while ((len = is.read(bt)) != -1) {
                        if (code==HttpURLConnection.HTTP_OK&&hasWrited<start){

                        }else {
                            raf.write(bt, 0, len);
                            mFinished += len;
                        }

                        hasWrited+=len;

                        QGSdkUtils.saveString(mContext,mApkInfo.getName(),"");
                        mApkInfo.setHasDown(mFinished);
                        Message message=new Message();
                        message.what=MSG_UPDATE;
                        message.obj=mApkInfo;
                        mHandler.sendMessage(message);
                    }
                }
                Log.d("hermesgame","conn.getResponseCode();: "+code);
                QGSdkUtils.saveString(mContext,mApkInfo.getName(),""+mFinished);
                mApkInfo.setHasDown(mFinished);
                Message message=new Message();
                message.what=MSG_END;
                message.obj=mApkInfo;
                mHandler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }
    public static class ApkInfo{
        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String path;
        public String url;
        public int size;
        public String name;

        public double getHasDown() {
            return hasDown;
        }

        public void setHasDown(double hasDown) {
            this.hasDown = hasDown;
        }

        public double hasDown;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }



    }
}
