package com.hermesgamesdk.fragment.login;

import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.manager.QGFragmentManager;

import android.view.View;
import android.widget.Button;

public class TryPlayTipsFragment extends BaseFragment {

	private Button sure;

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_try_play_tips";
	}

	@Override
	protected String getTitle() {
		return "R.string.qg_prompt";
	}

	@Override
	protected void onRootViewInflated(View root) {
		sure = (Button) root.findViewById(getResId("R.id.qg_btn_sure"));
		sure.setOnClickListener(listener);
		mTitleBar.hideBackIcon();
		mIsSupportBack = false;

	}

	@Override
	public void onClicks(int id) {
		if (sure.getId() == id) {
			QGFragmentManager.getInstance(mActivity).add(new TryPlayFragment());
		}
	}
}
