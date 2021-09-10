package com.hermesgamesdk.gamebox.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class AppInstallReciver extends BroadcastReceiver {

    
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent("PACKAGECHANGE");
        context.sendBroadcast(i);
    }
}