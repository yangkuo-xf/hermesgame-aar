package com.hermesgamesdk.skin.manager.entity;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.hermesgamesdk.skin.manager.loader.SkinManager;

public class BackgroundAttr extends SkinAttr {

	@Override
	public void apply(View view) {
		
		if(RES_TYPE_NAME_COLOR.equals(attrValueTypeName)){
			view.setBackgroundColor(SkinManager.getInstance().getColor(attrValueRefId));

		}else if(RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)){
			Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
			view.setBackground(bg);

		}
	}
}
