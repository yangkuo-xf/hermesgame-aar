package com.hermesgamesdk.fragment.download;


import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.entity.InitData;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGFragmentManager;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DownloadTipsFragement extends BaseFragment {
	private TextView mDownloadTips;
	private Button mUpdate;

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_download_tips";
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_download_tips";
	}

	@Override
	protected void onRootViewInflated(View root) {
		initView();
	}

	private void initView() {
		mDownloadTips = (TextView) findView("R.id.download_tips");
		mUpdate = (Button) findView("R.id.qg_download");
		mUpdate.setOnClickListener(listener);
		InitData data = (InitData) DataManager.getInstance().getData(Constant.INIT_KEY);
		mTitleBar.hideBackIcon();
		mIsSupportBack = false;
		if (data != null) {
			String isMust = data.getVersion().getIsmust();
			if ("1".equals(isMust)) {
				mTitleBar.hideCloseIcon();
			}
			if (data.getVersion().getUpdatetips().isEmpty()){
				mDownloadTips.setText("游戏有新版本更新,请点击更新按钮");
			}else {
				mDownloadTips.setText(data.getVersion().getUpdatetips());
			}

		}
	}

	@Override
	public void onClicks(int id) {
		if (mUpdate.getId() == id) {
			QGFragmentManager.getInstance(mActivity).add(new DownLoadFragment());
		}
	}


}
