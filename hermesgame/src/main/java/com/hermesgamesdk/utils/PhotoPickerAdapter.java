package com.hermesgamesdk.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class PhotoPickerAdapter extends BaseAdapter {

	private Activity activity;
	private List<ImageItem> imageItems;
	private List<String> imgPath;
	private BitmapCache cache;

	private ViewHolder holder;

	private Button btnSend;

	private boolean[] checkBoxState;
	private HashMap<Integer, View> viewMap;
	private GridView gv;

	private Uri photoUri;
	private String takePhotoPath;// 拍照的图片的路径
	
	private int colorId;
	private int maxNum;

	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals(imageView.getTag())) {
					imageView.setImageBitmap(bitmap);
				} else {
				}
			} else {
			}
		}
	};

	public PhotoPickerAdapter(Activity activity, List<ImageItem> imageItems, List<String> imgPath, Button btnSend, GridView gv, int colorId,int maxNum) {
		this.activity = activity;
		this.imageItems = imageItems;
		this.imgPath = imgPath;
		this.btnSend = btnSend;
		this.gv = gv;
		this.colorId = colorId;
		this.maxNum=maxNum;
		cache = new BitmapCache();
		checkBoxState = new boolean[imageItems.size()];
		viewMap = new HashMap<>();
	}

	@Override
	public int getCount() {
		return imageItems.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return imageItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
			holder = new ViewHolder();
			convertView = View.inflate(activity, QGSdkUtils.getResId(activity, "R.layout.qg_photo_picker_item"), null);
			holder.img = (ImageView) convertView.findViewById(QGSdkUtils.getResId(activity, "R.id.qg_img_photo_picker_item"));
			holder.checkBox = (CheckBox) convertView.findViewById(QGSdkUtils.getResId(activity, "R.id.qg_cb_photo_picker_item"));
			convertView.setTag(holder);
			viewMap.put(position, convertView);
		} else {
			convertView = viewMap.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.img.setBackgroundResource(QGSdkUtils.getResId(activity, "R.drawable.qg_take_photo"));
			holder.img.setOnClickListener(new ImgOnClickListener(null, position));
			holder.checkBox.setVisibility(View.GONE);
		} else {
			if (imageItems != null && imageItems.size() >= position) {
				ImageItem item = imageItems.get(imageItems.size() - position);
				holder.img.setTag(item.getImagePath());
				cache.displayBmp(holder.img, item.thumbnailPath, item.imagePath, callback);
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.img.setOnClickListener(new ImgOnClickListener(item, position));

				holder.checkBox.setOnClickListener(new ImgCheckBoxOnClickListener(item, holder.checkBox, position));
				holder.checkBox.setChecked(checkBoxState[position - 1]);// 保存checkBox状态
			}
		}
		if (viewMap.size() > 20) {
			synchronized (convertView) {
				for (int i = 1; i < gv.getFirstVisiblePosition() - 3; i++) {
					viewMap.remove(i);
				}
				for (int i = gv.getLastVisiblePosition() + 3; i < getCount(); i++) {
					viewMap.remove(i);
				}
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView img;
		CheckBox checkBox;
	}

	class ImgOnClickListener implements OnClickListener {

		private ImageItem imageItem;
		private int pos;

		public ImgOnClickListener(ImageItem imageItem, int pos) {
			this.imageItem = imageItem;
			this.pos = pos;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onClick(View view) {
			if (pos == 0) {
				Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date(System.currentTimeMillis());
				String fileName = format.format(date);
				takePhotoPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + fileName + ".jpg";
				File photoFile = new File(takePhotoPath);			
			    photoUri = FileProvider.getUriForFile(activity,activity.getPackageName() + ".qgfileprovider", photoFile);
				it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				if(ContextCompat.checkSelfPermission(activity,Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){
					Toast.makeText(activity, "请在设置中打开相机权限", Toast.LENGTH_LONG).show();
					showPermissionDialog(activity);
				}else{
					activity.startActivityForResult(it, 101);
				}
			
			} else {
				Intent it = new Intent(activity, PhotoPreviewActivity.class);
				it.putExtra("img_path", imageItem.getImagePath());
				it.putExtra("currentColor", colorId);
				activity.startActivity(it);
				activity.overridePendingTransition(QGSdkUtils.getAndroidAnim(activity, "qg_in_from_bottom"),
						QGSdkUtils.getAndroidAnim(activity, "qg_remain"));
			}
		}

	}
	public void showPermissionDialog(final Activity activity){
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
		normalDialog.setTitle("权限设置");
		normalDialog.setMessage("请在设置中打开相机权限");
		normalDialog.setPositiveButton("前往应用设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ...To-do
				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
				intent.setData(uri);
				activity.startActivity(intent);			
			}
		});
		normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(activity, "权限被拒绝", Toast.LENGTH_SHORT).show();
			
			}
		});
		// 显示
		normalDialog.show();
	}

	public Uri getPhotoUri() {
		return photoUri;
	}

	public String getTakePhotoPath() {
		return takePhotoPath;
	}

	class ImgCheckBoxOnClickListener implements OnClickListener {

		private ImageItem imageItem;
		private CheckBox checkBox;
		private int pos;

		public ImgCheckBoxOnClickListener(ImageItem imageItem, CheckBox checkBox, int pos) {
			this.checkBox = checkBox;
			this.imageItem = imageItem;
			this.pos = pos - 1;// 由于第一张图片始终是拍照，所以剩下图片的位置统一"减1"
		}

		@Override
		public void onClick(View view) {
			if (checkBox.isChecked()) {
				if (imgPath.size() >= maxNum) {
					checkBox.setChecked(false);
					//CommonUtils.showToast(activity, activity.getResources().getString(QKResourceUtil.getStringId(activity, "qk_cs_at_most_nine")));
				} else {
					checkBoxState[pos] = true;
					imgPath.add(imageItem.getImagePath());

					btnSend.setBackgroundResource(QGSdkUtils.getResId(activity, "R.drawable.qg_shape_button_reply_button_clickable"));
					btnSend.setTextColor(activity.getResources().getColor(QGSdkUtils.getResId(activity, "R.color.qg_white")));
					StringBuilder sb = new StringBuilder();
					sb.append(activity.getResources().getString(QGSdkUtils.getResId(activity, "R.string.qg_send"))).append("(")
							.append(String.valueOf(imgPath.size())).append("/"+maxNum).append(")");
					btnSend.setText(sb.toString());
				}
			} else {
				checkBoxState[pos] = false;
				imgPath.remove(imageItem.getImagePath());
				if (imgPath.size() == 0) {
					btnSend.setBackgroundResource(QGSdkUtils.getResId(activity, "R.drawable.qg_shape_button_reply_button_unclickable"));
					btnSend.setTextColor(activity.getResources().getColor(QGSdkUtils.getResId(activity, "R.color.qg_reply_button_text_disable")));
				} else {
					btnSend.setBackgroundResource(QGSdkUtils.getResId(activity, "R.drawable.qg_shape_button_reply_button_clickable"));
					btnSend.setTextColor(activity.getResources().getColor(QGSdkUtils.getResId(activity, "R.color.qg_white")));
				}
				StringBuilder sb = new StringBuilder();
				sb.append(activity.getResources().getString(QGSdkUtils.getResId(activity, "R.string.qg_send"))).append("(")
						.append(String.valueOf(imgPath.size())).append("/"+maxNum).append(")");
				btnSend.setText(sb.toString());
			}
		}

	}
}
