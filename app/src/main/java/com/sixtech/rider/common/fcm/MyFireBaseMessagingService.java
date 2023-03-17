package com.sixtech.rider.common.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sixtech.rider.R;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.ui.activity.main.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MyFireBaseMessagingService extends FirebaseMessagingService {

    int notificationId = 0;
    private static final String TAG = "RRR FireBaseMessaging";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_PROVIDER = "INTENT_PROVIDER";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.d("DEVICE_ID: ", deviceId);
        Log.e("FCM_TOKEN", s);

        SharedHelper.putKey(this, "device_token", s);
        SharedHelper.putKey(this, "device_id", deviceId);


    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Message: " + remoteMessage.toString());
//        if (remoteMessage.getData().size() > 0) {
        if (remoteMessage.getData()!=null) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

//            if (remoteMessage.getFrom().startsWith("/topics/")) {
//                Intent intent = new Intent(INTENT_PROVIDER);
//                intent.putExtra("latitude", Double.valueOf(remoteMessage.getData().get("latitude")));
//                intent.putExtra("longitude", Double.valueOf(remoteMessage.getData().get("longitude")));
//                sendBroadcast(intent);
//                System.out.println("RRR INTENT_PROVIDER intent = " + intent);
//            } else {
                sendBroadcast(new Intent(INTENT_FILTER));
                sendNotification(remoteMessage.getData().get("message"));
//            }

        } else sendBroadcast(new Intent(INTENT_FILTER));

    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d("TAG", "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

        private void sendNotification(String messageBody) {
            Log.e("messageBody", "" + messageBody);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;
            try {
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_push)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setPriority(Notification.PRIORITY_HIGH)
//                            .setPriority(Notification.PRIORITY_MAX )
                            .setSound(defaultSoundUri)
//                            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(notificationId++ /* ID of notification */, notificationBuilder.build());

        }
        }

