package com.home.paris.smsalert;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Manz on 18-Nov-17.
 */

public class StopRingtoneService extends IntentService {

    public static final String ACTION_STOP = "STOP";

    public StopRingtoneService(String name) {
        super(name);
    }

    public StopRingtoneService() {
        super("DisplayNotification");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            Intent stopIntent = new Intent(this, RingtonePlayingService.class);
            stopService(stopIntent);
        }
    }
}
