package com.hermesgamesdk.skin.manager.loader;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.hermesgamesdk.skin.manager.config.SkinConfig;
import com.hermesgamesdk.skin.manager.entity.AttrFactory;
import com.hermesgamesdk.skin.manager.entity.DynamicAttr;
import com.hermesgamesdk.skin.manager.entity.SkinAttr;
import com.hermesgamesdk.skin.manager.entity.SkinItem;
import com.hermesgamesdk.skin.manager.util.L;
import com.hermesgamesdk.skin.manager.util.ListUtils;

import java.util.ArrayList;
import java.util.List;



/**
 * Supply {@link SkinInflaterFactory} to be called when inflating from a LayoutInflater.
 * 
 * <p>Use this to collect the {skin:enable="true|false"} views availabled in our XML layout files.
 * 
 * @author fengjun
 */
public class SkinInflaterFactory implements LayoutInflater.Factory {
	
	private static final boolean DEBUG = true;
	
	/**
	 * Store the view item that need skin changing in the activity
	 */
	private List<SkinItem> mSkinItems = new ArrayList<SkinItem>();
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// if this is NOT enable to be skined , simplly skip it 
		boolean isSkinEnable = attrs.getAttributeBooleanValue(SkinConfig.NAMESPACE, SkinConfig.ATTR_SKIN_ENABLE, false);
/*		Log.e("mmmmmmmmmmmmmmmmmmmmm","=============== ================================");
		Log.e("mmmmmmmmmmmmmmmmmmmmm","name: "+name);
		int count = attrs.getAttributeCount();
		for (int i = 0; i < count; i++) {
			Log.e("mmmmmmmmmmmmmmmmmmmmm","AttributeName: "+attrs.getAttributeName(i)+"    value: "+attrs.getAttributeValue(i));
		}

		Log.e("mmmmmmmmmmmmmmmmmmmmm","=============== ================================");*/
        if (!isSkinEnable){
        		return null;
        }
		
		View view = createView(context, name, attrs);
		
		if (view == null){
			return null;
		}
		
		parseSkinAttr(context, attrs, view);
		
		return view;
	}
	
	/**
     * Invoke low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * LayoutInflater's ClassLoader.
     * 
     * @param context 
     * @param name The full name of the class to be instantiated.
     * @param attrs The XML attributes supplied for this instance.
     * 
     * @return View The newly instantiated view, or null.
     */
	private View createView(Context context, String name, AttributeSet attrs) {
		View view = null;
		try {
			if (-1 == name.indexOf('.')){
				if ("View".equals(name)) {
					view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
				} 
				if (view == null) {
					Class<? extends View> clazz = null;
					clazz = context.getClassLoader().loadClass(
							"android.widget." != null ? ("android.widget." + name) : name).asSubclass(View.class);
					L.e("mmmmmmmmm create " + clazz.getName());
						view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);


				} 
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
				} 
			}else {
	            view = LayoutInflater.from(context).createView(name, null, attrs);
	        }

			L.i("about to create " + name);

		} catch (Exception e) {
			L.e("error while create 【" + name + "】 : " );
			for (int i=0;i<attrs.getAttributeCount();i++){
				Log.e("mmmmmmmmmmmmmmmmmmmmm","AttributeName: "+attrs.getAttributeName(i)+"    value: "+attrs.getAttributeValue(i));
			}
			L.e("error while create 【" + "=======" + "】 : " + e.getMessage());
			L.e("error while create 【" + "=======" + "】 : " + e.getCause());
			e.printStackTrace();
			view = null;
		}
		return view;
	}

	/**
	 * Collect skin able tag such as background , textColor and so on
	 * 
	 * @param context
	 * @param attrs
	 * @param view
	 */
	private void parseSkinAttr(Context context, AttributeSet attrs, View view) {
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		
		for (int i = 0; i < attrs.getAttributeCount(); i++){
			String attrName = attrs.getAttributeName(i);
			String attrValue = attrs.getAttributeValue(i);
			
			if(!AttrFactory.isSupportedAttr(attrName)){
				continue;
			}
			
		    if(attrValue.startsWith("@")){
				try {
					int id = Integer.parseInt(attrValue.substring(1));
					String entryName = context.getResources().getResourceEntryName(id);
					String typeName = context.getResources().getResourceTypeName(id);
					SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
					if (mSkinAttr != null) {
						viewAttrs.add(mSkinAttr);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
		    }
		}
		
		if(!ListUtils.isEmpty(viewAttrs)){
			SkinItem skinItem = new SkinItem();
			skinItem.view = view;
			skinItem.attrs = viewAttrs;

			mSkinItems.add(skinItem);
			
			if(SkinManager.getInstance().isExternalSkin()){
				skinItem.apply();
			}
		}
	}
	
	public void applySkin(){
		if(ListUtils.isEmpty(mSkinItems)){
			return;
		}
		
		for(SkinItem si : mSkinItems){
			if(si.view == null){
				continue;
			}
			si.apply();
		}
	}
	
	public void dynamicAddSkinEnableView(Context context, View view, List<DynamicAttr> pDAttrs){
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		
		for(DynamicAttr dAttr : pDAttrs){
			int id = dAttr.refResId;
			String entryName = context.getResources().getResourceEntryName(id);
			String typeName = context.getResources().getResourceTypeName(id);
			SkinAttr mSkinAttr = AttrFactory.get(dAttr.attrName, id, entryName, typeName);
			viewAttrs.add(mSkinAttr);
		}
		
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}
	
	public void dynamicAddSkinEnableView(Context context, View view, String attrName, int attrValueResId){
		int id = attrValueResId;
		String entryName = context.getResources().getResourceEntryName(id);
		String typeName = context.getResources().getResourceTypeName(id);
		SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		viewAttrs.add(mSkinAttr);
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}
	
	public void addSkinView(SkinItem item){
		mSkinItems.add(item);
	}
	
	public void clean(){
		if(ListUtils.isEmpty(mSkinItems)){
			return;
		}
		
		for(SkinItem si : mSkinItems){
			if(si.view == null){
				continue;
			}
			si.clean();
		}
	}

}
