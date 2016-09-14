package lk.smarthome.smarthomeagent;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.model.SmartRegion;
import lk.smarthome.smarthomeagent.view.MainActivity;

public class SmartHomeApplication extends Application {

    private static BeaconManager beaconManager;
    private static Region currentRegion;

    public static BeaconManager getBeaconManager(){
        return  beaconManager;
    }

    public static Region getCurrentRegion() {
        return currentRegion;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                showNotification("You have entered", region.getIdentifier());
                currentRegion = region;
            }
            @Override
            public void onExitedRegion(Region region) {
                showNotification("You have leaved", region.getIdentifier());
                currentRegion = null;
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                DbHandler dbHandler = DbHandler.getInstance(getApplicationContext());
                List<SmartRegion> regions = dbHandler.getRegions();
                for (SmartRegion region : regions){
                    beaconManager.startMonitoring(new Region(region.getName(),
                            UUID.fromString(Constants.UUID), region.getMajor(), region.getMinor()));
                }
            }
        });
    }

    private void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon_smart_house)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
