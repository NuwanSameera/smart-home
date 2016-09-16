package lk.smarthome.smarthomeagent;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.model.SmartDevice;
import lk.smarthome.smarthomeagent.model.SmartRegion;
import lk.smarthome.smarthomeagent.view.MainActivity;

public class SmartHomeApplication extends Application {

    private static final String TAG = SmartHomeApplication.class.getSimpleName();

    private static BeaconManager beaconManager;
    private static Region currentRegion;
    private static MqttAndroidClient mqttAndroidClient;

    private DbHandler dbHandler;


    public static BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public static Region getCurrentRegion() {
        return currentRegion;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHandler = DbHandler.getInstance(getApplicationContext());

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), Constants.ACTIVEMQ_URL,
                Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID));
        mqttAndroidClient.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, topic + " " + message.toString());
                Intent intent = new Intent(Constants.MESSAGE_ARRIVED);
                intent.putExtra("message", message.toString());
                LocalBroadcastManager.getInstance(SmartHomeApplication.this).sendBroadcast(intent);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Unable to connect", exception);
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                showNotification("You have entered", region.getIdentifier());
                currentRegion = region;
                onRegionChange(region.getIdentifier(), true);
            }

            @Override
            public void onExitedRegion(Region region) {
                showNotification("You have leaved", region.getIdentifier());
                currentRegion = null;
                onRegionChange(region.getIdentifier(), false);
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                List<SmartRegion> regions = dbHandler.getRegions();
                for (SmartRegion region : regions) {
                    beaconManager.startMonitoring(new Region(region.getName(),
                            UUID.fromString(Constants.UUID), region.getMajor(), region.getMinor()));
                }
            }
        });
    }

    private void onRegionChange(String regionIdentifier, boolean isEntrance) {
        SmartRegion smartRegion = dbHandler.getRegion(regionIdentifier);
        List<SmartDevice> devices = dbHandler.getDevices(smartRegion.getId());
        publishMessage(devices, isEntrance ? "On" : "Off");
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

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe("SmartHome/Phone", 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to subscribe", exception);
                }
            });
        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public static void publishMessage(List<SmartDevice> devices, String action) {
        List<String> deviceIds = new ArrayList<>();
        for (SmartDevice d : devices) {
            deviceIds.add(String.valueOf(d.getId()));
        }
        String payload = android.text.TextUtils.join(",", deviceIds) + ":" + action;
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            mqttAndroidClient.publish("SmartHome/Device", message);
            Log.i(TAG, "Message Published: " + payload);
        } catch (MqttException e) {
            Log.e("MQTT client", e.getMessage());
        }
    }
}
