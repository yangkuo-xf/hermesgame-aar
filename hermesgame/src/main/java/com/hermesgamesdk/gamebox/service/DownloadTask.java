package com.hermesgamesdk.gamebox.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hermesgamesdk.gamebox.db.ThreadDAO;
import com.hermesgamesdk.gamebox.db.ThreadDAOImple;
import com.hermesgamesdk.gamebox.entity.FileInfo;
import com.hermesgamesdk.gamebox.entity.ThreadInfo;

public class DownloadTask {
	private Context mComtext = null;
	private FileInfo mFileInfo = null;
	private ThreadDAO mDao = null;
	private int mFinished = 0;
	private int mThreadCount = 1;
	public boolean mIsPause = false;
	public static ExecutorService sExecutorService = Executors.newFixedThreadPool(20);

	public DownloadTask(Context comtext, FileInfo fileInfo, int threadCount) {
		super();
		this.mThreadCount = threadCount;
		this.mComtext = comtext;
		this.mFileInfo = fileInfo;
		this.mDao = new ThreadDAOImple(mComtext);
	}

	public void download() {
		// �����ݿ��л�ȡ���ص���Ϣ
		List<ThreadInfo> list = mDao.queryThreads(mFileInfo.getUrl());
		ThreadInfo info = null;
		if (list.size() == 0) {
			info = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0,mFileInfo.getPackageName(),mFileInfo.getIcon(),mFileInfo.getShowName());
		}else{
			info= list.get(0);
		}

		DownloadThread thread = new DownloadThread(info);
		DownloadTask.sExecutorService.execute(thread);
	}

	class DownloadThread extends Thread {
		private ThreadInfo threadInfo = null;
		// ��ʶ�߳��Ƿ�ִ�����

		public DownloadThread(ThreadInfo threadInfo) {
			this.threadInfo = threadInfo;
		}

		@Override
		public void run() {
			// ��������첻�������d��Ϣ��������d��Ϣ
			if (!mDao.isExists(threadInfo.getUrl())) {
				mDao.insertThread(threadInfo);
			}
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream is = null;
			try {
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");

				int start = threadInfo.getStart() + threadInfo.getFinished();

				// �O�����d�ļ��_ʼ���Y����λ��
				conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
				Log.d("gamebox","DownloadThread Range: "+start+"   threadInfo.getEnd()： "+threadInfo.getEnd());
				File file = new File(DownloadService.DownloadPath, mFileInfo.getFileName());
				this.threadInfo.setSavePath(file.getPath());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				mFinished += threadInfo.getFinished();
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

						// �O�à�500���׸���һ��
						if (System.currentTimeMillis() - time > 500) {
							time = System.currentTimeMillis();
							Intent intent = new Intent(DownloadService.ACTION_UPDATE);
							int nowFinish = mFinished;
							int allBytes = mFileInfo.getLength();

							float downLoadRate = ((float)nowFinish / allBytes)*100;
							intent.putExtra("finished", downLoadRate);
							intent.putExtra("packageName", threadInfo.getPackageName());
							intent.putExtra("downUrl", threadInfo.getUrl());

							mComtext.sendBroadcast(intent);
							mDao.updateThread(threadInfo.getUrl(), threadInfo.getSavePath(), mFinished);
						}

						if (mIsPause) {
							mDao.updateThread(threadInfo.getUrl(), threadInfo.getSavePath(), mFinished);
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
							return;
						}else{
							Log.e("hermesgame","mIsPause = false ");
						}

					}
				}

				// ���d��ɺ󣬄h����������Ϣ
				mDao.updateThreadSuccess(threadInfo.getUrl(),mFinished,file.getPath());
				GBUtils.getInstance().downloadingFiles.remove(mFileInfo);
				Intent intent = new Intent(DownloadService.ACTION_DOWNLOADEND);
				intent.putExtra("packageName", threadInfo.getPackageName());
				intent.putExtra("downUrl", threadInfo.getUrl());
				intent.putExtra("savePath", file.getPath());
				mComtext.sendBroadcast(intent);

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
}
