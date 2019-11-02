package com.home.paris.smsalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Manz on 18-Nov-17.
 */

public class IncomingSms extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        SharedPreferences prefs = context
                                .getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                        String number = prefs.getString("number", null);
                        String ignoreText = prefs.getString("ignore_text", null);
                        boolean active = prefs.getBoolean("active", false);
                        boolean ignoreMute = prefs.getBoolean("ignore_mute", false);
                        if (!active || msg_from == null || msgBody == null || number == null
                                || number.length() < 3
                                || (ignoreText != null && ignoreText.length() > 1 && msgBody.contains(ignoreText))) {
                            return;
                        }

                        if(msg_from.contains(number) || number.contains(msg_from)) {
                            Intent startIntent = new Intent(context, RingtonePlayingService.class);
                            startIntent.putExtra("ignore_mute", ignoreMute);
                            context.startService(startIntent);

                            showNotification(context, "House Alarm");
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }

    private void showNotification(Context context, String msgBody) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel
                    ("my_channel_01", "my_channel_01",
                            NotificationManager.IMPORTANCE_HIGH);
            mBuilder = new NotificationCompat.Builder(context, "my_channel_01")
                    .setSmallIcon(R.drawable.alert)
                    .setContentTitle("SMS ALERT")
                    .setContentText("House Alarm");
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            mBuilder = new NotificationCompat.Builder(context, null)
                    .setSmallIcon(R.drawable.alert)
                    .setContentTitle("SMS ALERT")
                    .setContentText("House Alarm");
        }
        Intent mAction = new Intent(context, StopRingtoneService.class);
        mAction.setAction(StopRingtoneService.ACTION_STOP);
        PendingIntent piAction1 = PendingIntent.getService
                (context, 0, mAction, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(piAction1);

        mNotificationManager.notify(001, mBuilder.build());
    }
}