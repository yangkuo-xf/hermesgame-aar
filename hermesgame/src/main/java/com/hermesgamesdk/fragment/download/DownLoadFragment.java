package com.hermesgamesdk.fragment.download;

import java.io.File;
import java.util.concurrent.Executors;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.DataManager.DownloadUpdateListener;
import com.hermesgamesdk.utils.DownLoadUtils;
import com.hermesgamesdk.utils.QGSdkUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownLoadFragment extends BaseFragment {
	private ProgressBar mBar;
	private TextView mDownloadProgress;
	private Button mIntsall;
	private String apkPath;
	private boolean mCanInstall;
	private boolean isMustUpdate;
	private String fileName;
	private String url = "";
	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_download";
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_download_game";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView();
	}

	@Override
	public void onClicks(int id) {
		if (id == mIntsall.getId()) {
			if (mCanInstall) {
				installApk(apkPath+fileName);
			}
		}
	}

	private void initView() {
		mBar = (ProgressBar) findView("R.id.qg_progress_bar");
		mDownloadProgress = (TextView) findView("R.id.download_progress");
		mIntsall = (Button) findView("R.id.qg_install");
		mIntsall.setOnClickListener(listener);
		InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
		mIsSupportBack = false;
		mTitleBar.hideBackIcon();

		if (data != null) {
			url = data.getVersion().getVersionurl();
			String isMust = data.getVersion().getIsmust();
			isMustUpdate = "1".equals(isMust);
			if (isMustUpdate) {
				mTitleBar.hideCloseIcon();
			}
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			 fileName = data.getVersion().getVersionname().replace(".", "_")+"_"+data.getVersion().getVersionNo()+".apk";
			if (mActivity!=null){
				apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hermesgame"
						+ File.separator + mActivity.getPackageName()+File.separator;
			}
			Log.d("hermesgame.DownLoad","apkPath: "+apkPath);
			download(url,apkPath,fileName);

		} else {
			forceBack();
		}
	}

	private void download(final String url, final String savePath,final String fileName) {
		if(!QGSdkUtils.isNetworkAvailable(mActivity)){
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
							.setTitle(getString("R.string.qg_download_failure"))
							.setMessage("网络状态异常，请检查您的网络环境！")
							.setPositiveButton(getString("R.string.qg_download_again"), new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									download( url, savePath,fileName);
								}
							});
					if (!isMustUpdate) {
						builder.setNegativeButton(getString("R.string.qg_download_cancel"), new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mActivity.finish();

							}
						});
					}

					AlertDialog dialog = builder.create();
					dialog.show();
					dialog.setCanceledOnTouchOutside(false);

				}
			});

		}else{
			if (!QGSdkUtils.isWiFiActive(mActivity)){
				final String finalUrl = url;
				final com.hermesgamesdk.view.AlertDialog alertDialog=new com.hermesgamesdk.view.AlertDialog(mActivity, this, "提示", "您当前为非WIFI网络环境,继续更新会产生流量资费，是否继续更新？", "","我已了解,继续") {
					@Override
					public void onDismiss() {
						forceBack();
					}
				};
				alertDialog.setClickListener(new com.hermesgamesdk.view.AlertDialog.onClick() {
					@Override
					public void onLeftClick() {
						alertDialog.dismiss();


					}

					@Override
					public void onRightClick() {
						alertDialog.dismiss();
						downLoadAPK(finalUrl, apkPath,fileName);
					}
				});
				alertDialog.show();
			}else{
				downLoadAPK(url, apkPath,fileName);
			}

		}

	}
	public void downLoadAPK(final String url, final String savePath,final String fileName){

		DownLoadUtils.ApkInfo apkInfo=new DownLoadUtils.ApkInfo();
		apkInfo.setName(fileName);
		apkInfo.setUrl(url);
		apkInfo.setPath(savePath);
		DownLoadUtils.getInstance(mActivity).setDownListener(downloadUpdateListener);
		DownLoadUtils.InitThread initThread=new DownLoadUtils.InitThread(apkInfo);
		Executors.newFixedThreadPool(20).execute(initThread);



/*	DataManager.getInstance().download(url, savePath, new DownloadUpdateListener() {

			@Override
			public void onUpdate(int progress,int downloadLength,int totalLength) {
				mBar.setProgress(progress);
				String tips = String.format("%sM/%sM", downloadLength/(1024*1024),totalLength/(1024*1024));
				mDownloadProgress.setText(tips);
				if (downloadLength == totalLength) {
					installApk(savePath);
					mCanInstall = true;
				}
			}

			@Override
			public void onDownloadError(final String message) {
				//new File(savePath).delete();
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
								.setTitle(getString("R.string.qg_download_failure"))
								.setMessage("网络状态异常，请检查您的网络环境！")
								.setPositiveButton(getString("R.string.qg_download_again"), new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										download( url, savePath);
									}
								});
						if (!isMustUpdate) {
							builder.setNegativeButton(getString("R.string.qg_download_cancel"), new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									mActivity.finish();

								}
							});
						}

						AlertDialog dialog = builder.create();
						dialog.show();
						dialog.setCanceledOnTouchOutside(false);

					}
				});
			}

			@Override
			public boolean beforeDownload(final int size) {
				File file = new File(savePath);
				if (file.exists() && file.length() == size) {
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							installApk(savePath);
							mBar.setProgress(100);
							String tips = String.format("%sM/%sM", size/(1024*1024),size/(1024*1024));
							mDownloadProgress.setText(tips);
							mCanInstall = true;

						}
					});
					Log.e("hermesgame","DownFragment beforeDownload size: "+size+"   file.length()： "+ file.length());


					return false;
				} else {
					//	file.delete();

				}
				return super.beforeDownload(size);
			}

		});*/

	}
	DownloadUpdateListener downloadUpdateListener=new DownloadUpdateListener() {
		@Override
		public void onUpdate(int progress, int downloadLength, int totalLength) {
			mBar.setProgress(progress);
			String tips = String.format("%sM/%sM", downloadLength/(1024*1024),totalLength/(1024*1024));
			mDownloadProgress.setText(tips);
			if (downloadLength == totalLength) {
				if (mCanInstall){
					return;
				}
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						installApk(apkPath+fileName);
						mCanInstall=true;
					}
				});


			}
		}

		@Override
		public void onDownloadError(String message) {
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
							.setTitle(getString("R.string.qg_download_failure"))
							.setMessage("网络状态异常，请检查您的网络环境！")
							.setPositiveButton(getString("R.string.qg_download_again"), new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									download(url , apkPath,fileName);
								}
							});
					if (!isMustUpdate) {
						builder.setNegativeButton(getString("R.string.qg_download_cancel"), new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mActivity.finish();

							}
						});
					}

					AlertDialog dialog = builder.create();
					dialog.show();
					dialog.setCanceledOnTouchOutside(false);

				}
			});
		}
	};

	protected void installApk(String apkPath) {
		Log.d("hermesgame","install new apk  apkPath: "+apkPath);
		if (!isDetached()) {
			File apkfile = new File(apkPath);
			if (!apkfile.exists()) {
				return;
			}

			if (mActivity!=null){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (Build.VERSION.SDK_INT >= 24) {
					intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

					Uri uri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".qgfileprovider", apkfile);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					Log.d("hermesgame","install new apk  uri: "+uri.toString());
				} else {
					Log.d("hermesgame","install new apk 2 uri: "+Uri.fromFile(apkfile).toString());
					intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
				}
				mActivity.startActivity(intent);

			}else{
				Log.d("hermesgame","install new apk Fail: ");
			}

		}
	}

}
