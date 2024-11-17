package com.khair.aksms;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        if (remoteMessage.getNotification()!=null) {
            String NotificationBody = remoteMessage.getNotification().getBody();
            String NotificationTitle = remoteMessage.getNotification().getTitle();
            Log.d(TAG,NotificationTitle);
            sendNotification(NotificationTitle, NotificationBody);
        }else {
            String action = remoteMessage.getData().get("action");
            String number = remoteMessage.getData().get("number");
            String body = remoteMessage.getData().get("body");
            sendNotification(number,body);

            if (action!=null && action.contains("send_sms_now")){

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, body, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS Failed to Send", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }





            }
        }




    }

    ////============================================================
    ////============================================================

    ////============================================================




    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //Send this token to your server
        Log.d("firebaseToken", token);

    }

    private void sendNotification(String number, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(number)
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);


        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    //================================================



}