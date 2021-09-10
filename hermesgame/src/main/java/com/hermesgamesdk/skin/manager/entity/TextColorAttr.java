package com.hermesgamesdk.skin.manager.entity;

import android.view.View;
import android.widget.TextView;

import com.hermesgamesdk.skin.manager.util.L;
import com.hermesgamesdk.skin.manager.loader.SkinManager;


public class TextColorAttr extends SkinAttr {

	@Override
	public void apply(View view) {
		if(view instanceof TextView){
			TextView tv = (TextView)view;
			if(RES_TYPE_NAME_COLOR.equals(attrValueTypeName)){
				L.e("attr1", "TextColorAttr");
				tv.setTextColor(SkinManager.getInstance().convertToColorStateList(attrValueRefId));
			}
		}
	}
}
