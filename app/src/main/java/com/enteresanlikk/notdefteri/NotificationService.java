package com.enteresanlikk.notdefteri;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    Timer timer;
    Handler handler;
    Database db;
    Functions f;

    int NOTIFICATION_ID = 52; //Notification ID
    NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new Database(NotificationService.this);
        f = new Functions(NotificationService.this);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(f.CHANNEL_ID, f.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);

        handler = new Handler(Looper.getMainLooper());

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                control();
            }
        },0, 30000);
    }

    private void control() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String date = f.getDate();
                String time = f.getTime();

                ArrayList<HashMap<String, String>> notes = db.detailWithReminder(date, time);
                if(notes.size() > 0) {
                    for(int i=0; i<notes.size(); i++) {
                        HashMap<String, String> note = notes.get(i);
                        Integer id = Integer.valueOf(note.get("id"));
                        senNotification(id, note.get("title"), note.get("content"));
                        db.deleteReminder(id); //hatırlatıcıyı silmek için
                    }
                }
            }
        });
    }

    public void senNotification(Integer id, String title, String content) {
        NOTIFICATION_ID = id;

        Intent notificationIntent = new Intent(this, EditActivity.class);
        notificationIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new Notification.Builder(this, f.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle()
                            .bigText(content))
                .build();

        manager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        manager.deleteNotificationChannel(f.CHANNEL_ID);
        super.onDestroy();

        timer.cancel();
    }
}
