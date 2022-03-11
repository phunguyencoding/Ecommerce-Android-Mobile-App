package group15.finalassignment.ecommerce.View.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartNotificationService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
    }
}