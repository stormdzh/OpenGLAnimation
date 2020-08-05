package com.stormdzh.openglanimation.util.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.stormdzh.openglanimation.ui.activity.function.TranslateActivity;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2020-08-03 17:28
 */
//点击事件接收的广播
public class ClickReceiver extends BroadcastReceiver {
    public static final String ACTION_SWITCH_CLICK = "notification.toutiao.com.notificationapp.CLICK";

    NotificationUtil mNotificationUtil;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_SWITCH_CLICK.equals(action)) {
            Toast.makeText(context, "点击事件", Toast.LENGTH_SHORT).show();
        }
        context.startActivity(new Intent(context, TranslateActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        if(mNotificationUtil==null){
            mNotificationUtil=new NotificationUtil();
            mNotificationUtil.init(context);
        }
//        mNotificationUtil.cancel();
    }
}