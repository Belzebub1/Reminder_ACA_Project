package com.reminder.social_activities.social_service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.reminder.R;


public class ViberService extends IntentService {
    private String text;

    public ViberService() {
        super(ViberService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        text = (String) intent.getExtras().get("text");
        Intent shareIntent = new Intent(ViberService.this, ViberSender.class);
        shareIntent.putExtra("do_action", "sh");
        shareIntent.putExtra("text", text);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e("VIBER", "Viber error");
        }finally {
            createViberNotification();
        }
    }
    public void createViberNotification() {
        Notification notif = new Notification.Builder(this)
                .setContentTitle("Reminder")
                .setContentText("Time to sent you Viber message")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.defaults |= Notification.DEFAULT_SOUND;
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notif);
    }
}
