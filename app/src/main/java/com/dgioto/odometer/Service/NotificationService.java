package com.dgioto.odometer.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.R;

public class NotificationService extends Service {

    public static final String EXTRA_MESSAGE = "message";
    public static final String CHANNEL_ID = "channel_id";
    public static final String CLOSE_NOTIFICATION = "CLOSE_NOTIFICATION";
    public static final int NOTIFICATION_ID = 5453;
    private static final int DELAY_IN_MILLISECONDS = 2000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String text = intent.getStringExtra(EXTRA_MESSAGE);
            String action = intent.getAction();

            if (CLOSE_NOTIFICATION.equals(action)) {
                stopSelf(); // Close the service after clicking on the "Close" button
                return START_NOT_STICKY;
            }

            // Trigger a delay before displaying a notification
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> showText(text), DELAY_IN_MILLISECONDS);
        }

        return START_STICKY;
    }

    private void showText(final String text) {
        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this,
                0,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(CLOSE_NOTIFICATION);
        PendingIntent closePendingIntent = PendingIntent.getService(
                this,
                0,
                closeIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(actionPendingIntent)
                .addAction(R.drawable.ic_close, getResources().getString(R.string.close), closePendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (notificationManager.areNotificationsEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}