package com.stormdzh.openglanimation.util.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.ui.activity.function.TranslateActivity;

/**
 * @Description: 常驻通知栏
 * 参考文章:https://blog.csdn.net/weixin_43233747/article/details/89366419
 * https://www.jb51.net/article/36567.htm
 * @Author: dzh
 * @CreateDate: 2020-08-03 13:57
 */
public class NotificationUtil {
    private NotificationManager manager;
    private Bitmap icon;
    private Context mContext;

    private static final int NOTIFICATION_ID_8 = 7;

    public void init(Context context) {
        this.mContext = context;
        // 获取通知服务
        manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.yan);
    }


    public void showCustomView() {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.custom_notification);
        Intent intent = new Intent(mContext, TranslateActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.paly_pause_music,
                pendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContent(remoteViews).setSmallIcon(R.drawable.music_icon)
                .setLargeIcon(icon).setOngoing(true)
                .setTicker("music is playing");
        manager.notify(NOTIFICATION_ID_8, builder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showTest() {
        Notification notification = null;
        String myChannelId = "myChannel_01";
        String myChannelName = "myChannel";
        NotificationChannel channel = new NotificationChannel(myChannelId, myChannelName,
                NotificationManager.IMPORTANCE_LOW);
        Toast.makeText(mContext, myChannelId, Toast.LENGTH_SHORT).show();
        manager.createNotificationChannel(channel);
        notification = new NotificationCompat.Builder(mContext, myChannelId)
                .setContentTitle("我是测试")
                .setContentText("你有一个新通知")
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        manager.notify(7, notification);
    }

    public void cancel() {
        manager.cancel(7);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showWx() {
        Notification notification = null;
        String myChannelId = "myChannel_01";
        String myChannelName = "myChannel";
        NotificationChannel channel = new NotificationChannel(myChannelId, myChannelName,
                NotificationManager.IMPORTANCE_LOW);
        Toast.makeText(mContext, myChannelId, Toast.LENGTH_SHORT).show();
        manager.createNotificationChannel(channel);
//        notification = new NotificationCompat.Builder(mContext, myChannelId)
//                .setContentTitle("我是测试")
//                .setContentText("你有一个新通知")
//                .setWhen(System.currentTimeMillis())
//                .setShowWhen(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher))
//                .build();

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.custom_wx_notification);

        Intent intent = new Intent(mContext, ClickReceiver.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setComponent(new ComponentName(mContext.getPackageName(), mContext.getPackageName() + "." + mContext.getLocalClassName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.tvZhichu,
                pendingIntent);

        notification = new NotificationCompat.Builder(mContext, myChannelId)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setCustomBigContentView(remoteViews)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(7, notification);
    }
}
