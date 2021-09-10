package com.hermesgamesdk.skin.manager.listener;

import android.view.View;

import com.hermesgamesdk.skin.manager.entity.DynamicAttr;

import java.util.List;


public interface IDynamicNewView {
	void dynamicAddView(View view, List<DynamicAttr> pDAttrs);
}
