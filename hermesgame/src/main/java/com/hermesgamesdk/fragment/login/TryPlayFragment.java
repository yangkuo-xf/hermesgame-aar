package com.hermesgamesdk.fragment.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.hermesgamesdk.constant.Constant;
import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.fragment.PhoneBindFragment;
import com.hermesgamesdk.QGConfig;
import com.hermesgamesdk.entity.QGUserInfo;
import com.hermesgamesdk.manager.DataManager;
import com.hermesgamesdk.manager.QGFragmentManager;

/**
 * Created by Administrator on 2017/9/13.
 */

public class TryPlayFragment extends BaseFragment {
	private Button enterGameView;
	private Button bindView;
	private ImageView mIcon;
	private boolean mSaveShot;

	@Override
	protected String getRootName() {
		return "R.layout.qg_fragment_try_play";
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected void onRootViewInflated(View root) {
		mIsSupportBack = false;
		initView(root);
		setIcon();
	}

	private void setIcon() {
		PackageManager pm = mActivity.getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(mActivity.getPackageName(), 0);
			Drawable icon = info.loadIcon(pm);
			mIcon.setScaleType(ScaleType.FIT_XY);
			mIcon.setImageDrawable(icon);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void initView(View root) {
		mIcon = (ImageView) findView("R.id.qg_launcher_icon");
		enterGameView = (Button) findView("R.id.qg_enter_game");
		bindView = (Button) findView("R.id.qg_bind_mobile_phone");
		if (QGConfig.isSupportPhone()) {
			bindView.setVisibility(View.VISIBLE);
		}
		bindView.setOnClickListener(listener);
		enterGameView.setOnClickListener(listener);
		QGUserInfo userInfo = (QGUserInfo) DataManager.getInstance().getData(Constant.USERINFO_KEY);
		TextView usernameView = (TextView) findView("R.id.qg_show_username");
		TextView passwordView = (TextView) findView("R.id.qg_show_password");
		passwordView.setText(userInfo.getUserdata().getUpwd());
		usernameView.setText(userInfo.getUserdata().getUsername());
		// mTitleBar.hideCloseIcon();
	}

	@Override
	public void onClicks(int id) {
		if (!mSaveShot) {
			mSaveShot = true;
			takeScreenShot(mRoot);
		}
		if (id == enterGameView.getId())
			switchToCerificationFragment(CerificationNode.ON_LOGIN);
		if (id == bindView.getId())
			if (QGConfig.isSupportPhone()) {
				QGFragmentManager.getInstance(mActivity).add(new PhoneBindFragment());
			} else {
				showToast("R.string.toast_text_function_not_worked");
			}
	}

	public void takeScreenShot(View v) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
		String fname = Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera";
		File f = new File(fname);
		if (!f.exists())
			f.mkdirs();
		fname = fname + File.separator + sdf.format(new Date()) + ".jpg";
		View root = v;

		root.setDrawingCacheEnabled(true);
		root.buildDrawingCache();
		Bitmap bitmap = root.getDrawingCache();
		if (bitmap != null) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(fname);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				showToast(getString("R.string.toast_text_print_screen_success") + fname);
			} catch (Exception e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}

}
