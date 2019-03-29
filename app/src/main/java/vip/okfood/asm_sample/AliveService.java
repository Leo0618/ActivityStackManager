package vip.okfood.asm_sample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * function:
 *
 * <p>
 * Created by Leo on 2019/2/12.
 */
public class AliveService extends Service {
    private static final String TAG = "AliveService";

    @Override
    public void onCreate() {
        super.onCreate();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEDL_ID, CHANNEDL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationManager mNotificationManager;
    private Notification        mNotification;

    private static final int    ID_NOTIFY   = 100;
    private static final String CHANNEDL_ID = "alive";

    private NotificationManager getManager() {
        if(mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    private Notification getNotification() {
        if(mNotification == null) {
            mNotification = new NotificationCompat.Builder(this, CHANNEDL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("通知栏服务")
                    .setContentText("我显示出来说明我的应用还活着")
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(false)
                    .setLocalOnly(true)
                    .setShowWhen(false).setNumber(4)
                    .build();
        }
        return mNotification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand.");
        getManager().notify(ID_NOTIFY, getNotification());
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy.");
        getManager().cancel(ID_NOTIFY);
        mNotificationManager = null;
        mNotification = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind.");
        return null;
    }
}
