package com.hermesgamesdk.fragment;

import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;

import com.hermesgamesdk.constant.Constant;

public class AnnouncementFragment extends BaseFragment {
    private WebView announcementWeb;
    @Override
    protected String getRootName() {
        return "R.layout.qg_fragment_announcement";
    }

    @Override
    protected String getTitle() {
        return "R.string.qg_notice";
    }

    @Override
    protected void onRootViewInflated(View root) {
        initView(root);
    }
    private void initView(View view){
    //	mTitleBar.hideCloseIcon();
        announcementWeb=(WebView) findView("R.id.qg_announcement_web");
        announcementWeb.setBackgroundColor(Color.parseColor("#FAFAFA"));
        announcementWeb.loadDataWithBaseURL("", Constant.noticeContent, "text/html", "utf-8", "");
    }

}
