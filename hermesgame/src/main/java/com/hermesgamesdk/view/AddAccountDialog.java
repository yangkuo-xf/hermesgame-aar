package com.hermesgamesdk.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hermesgamesdk.utils.QGSdkUtils;

public abstract class AddAccountDialog extends Dialog {
    private Context mContext;

    private EditText account_edit;
    private ImageView closeButton;
    private Button btnConfim;
    private String mDefaultUsername = "";

    public AddAccountDialog(Context context) {
        super(context, QGSdkUtils.getResId(context, "R.style.qg_dialog_style_fullscreen"));
        setHideVirtualKey(getWindow());
        setContentView(QGSdkUtils.getResId(context, "R.layout.qg_fragment_add_account"));
        mContext = context;

        init();

    }

    public void init() {

        btnConfim = (Button) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_add_account_btn_confim"));
        account_edit = (EditText) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_add_account_edit"));
		closeButton = (ImageView) findViewById(QGSdkUtils.getResId(mContext, "R.id.qg_add_account_close"));
		mDefaultUsername = QGSdkUtils.createRandom(10);

        account_edit.setText(mDefaultUsername);
        account_edit.setSelection(mDefaultUsername.length());
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onDismiss(account_edit.getText().toString());
                AddAccountDialog.this.dismiss();
            }
        });
		btnConfim.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onDismiss(account_edit.getText().toString());
				AddAccountDialog.this.dismiss();
			}
		});
        setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                onDismiss(account_edit.getText().toString());
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    public abstract void onDismiss(String s);

    public void setHideVirtualKey(Window window) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
}
