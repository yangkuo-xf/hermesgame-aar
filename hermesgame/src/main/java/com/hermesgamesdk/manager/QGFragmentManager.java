package com.hermesgamesdk.manager;

import android.app.Activity;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.hermesgamesdk.fragment.BaseFragment;
import com.hermesgamesdk.utils.QGSdkUtils;
import com.tencent.mm.opensdk.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 用于管理Fragment的退出 返回 添加操作 由于Activity的主题为Dialog样式会造成多个Fragement重叠显示
 * add时需要隐藏底层Fragement 返回时移除当前Fragment显示下一层Fragment
 * 
 * 注意:在Activity返回至后台时候需要提交事务需要调用commitAllowingStateLoss方法用于事务提交
 */
public class QGFragmentManager {
	// 存入不同的Activity的对象对应的Fragment返回栈
	private static HashMap<Activity, ArrayList<Fragment>> mFragmentStacks;
	// 存入不同Activity对应的FragmentManager用于事务提交管理
	private static HashMap<Activity, FragmentManager> mManagers;

	private static QGFragmentManager mQGManager;
	// 每次操作传入的Activity
	private static Activity mActivity;
	// 每个使用QGFragmentManager管理Fragment的Activity布局中
	// 必须都定义一个id为fragment_container的FramentLayout控件
	protected final String mFragmentContainerName = "R.id.fragment_container";

	/**
	 * 初始化mManagers，mFragmentStacks
	 */
	private QGFragmentManager() {
		if (mManagers == null)
			mManagers = new HashMap<Activity, FragmentManager>();
		if (mFragmentStacks == null)
			mFragmentStacks = new HashMap<Activity, ArrayList<Fragment>>();
	}

	/**
	 * @param activity
	 *            用于获取activity对应的FragmentManager和Fragment栈
	 * @return 若mManagers，mFragmentStacks中不存在activity键
	 *         则初始化activity对应的FragmentManager和Fragment栈
	 */
	public static QGFragmentManager getInstance(FragmentActivity activity) {
		mActivity = activity;
		if (mQGManager == null)
			mQGManager = new QGFragmentManager();
		if (!mFragmentStacks.containsKey(activity))
			mFragmentStacks.put(activity, new ArrayList<Fragment>());
		if (!mManagers.containsKey(activity)) {
			FragmentManager fm = activity.getSupportFragmentManager();
			mManagers.put(activity, fm);
		}

		return mQGManager;
	}

	/**
	 * @param fragment
	 *            将fragment添加到栈并显示出来 添加前需要隐藏下层Fragment，避免显示重叠
	 */
	public void add(Fragment fragment) {
		for (Fragment f : mFragmentStacks.get(mActivity))
			hide(f);
		mFragmentStacks.get(mActivity).add(fragment);
		mManagers.get(mActivity).beginTransaction()
				.add(QGSdkUtils.getResId(mActivity, mFragmentContainerName), fragment).commitAllowingStateLoss();
	}

	/**
	 * @param fragment
	 *            隐藏fragment
	 */
	public void hide(Fragment fragment) {
		mManagers.get(mActivity).beginTransaction().hide(fragment).commitAllowingStateLoss();
	}

	/**
	 * @param fragment
	 *            显示fragment
	 */
	public void show(Fragment fragment) {
		mManagers.get(mActivity).beginTransaction().show(fragment).commitAllowingStateLoss();
	}

	/**
	 * 在Activity中onDestroy调用释放资源
	 */
	public void onActivityDestroy() {
		mFragmentStacks.remove(mActivity);
		mManagers.remove(mActivity);
		mActivity = null;
	}

	/**
	 * @param fragment
	 *            在 Fragment 的onDestroy调用
	 */
	public void onFragmentDestroy(Fragment fragment) {
		mFragmentStacks.get(mActivity).remove(fragment);
	}

	/**
	 * 获取当前Fragment
	 */
	private Fragment getCurrentFragment() {
		int lastIndex = mFragmentStacks.get(mActivity).size() - 1;
		if (lastIndex >= 0) {
			Fragment lastFragment = mFragmentStacks.get(mActivity).get(lastIndex);
			return lastFragment;
		}
		return null;
	}

	/**
	 * 移除当前Fragment，返回上一个Fragment 并显示上一个Fragment 回调 BaseFragment中onBackForeground方法
	 * 当fragment不支持返回功能时 调用back无效
	 */
	public void back() {
		back(null);
	}

	// 返回指定Fragment
	public void back(Class<?> clazz) {
		BaseFragment removedFragment = (BaseFragment) getCurrentFragment();
		Log.e("hermesgame.back", "removedFragment=" + removedFragment);
		if (removedFragment != null) {
			// 当返回被调用是调用此方法
			removedFragment.onBackInvoke();
			if (!removedFragment.isSupportBack())
				return;
		}

		int size = mFragmentStacks.get(mActivity).size();
		if (size >= 2) {
			mFragmentStacks.get(mActivity).remove(removedFragment);
			mManagers.get(mActivity).beginTransaction().remove(removedFragment).commitAllowingStateLoss();
			Fragment showFragment = getCurrentFragment();
			show(showFragment);
			// 当fragment从后台显示到前台调用
			((BaseFragment) showFragment).onBackForeground();
		} else {
			mActivity.finish();
		}

		if (clazz != null && !clazz.getName().equals(removedFragment.getClass().getName())) {
			back(clazz);
		} else {
			return;
		}
	}

}
