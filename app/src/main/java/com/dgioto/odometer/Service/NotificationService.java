package com.dgioto.odometer.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dgioto.odometer.R;
import com.dgioto.odometer.View.TopFragmentPresenter;

public class NotificationService extends Worker {

    public static final String EXTRA_MESSAGE = "message";
    public static final int NOTIFICATION_ID = 5453;

    public NotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String text = getInputData().getString(EXTRA_MESSAGE);
        showText(text);
        return Result.success();
    }

    private void showText(final String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent actionIntent = new Intent(getApplicationContext(), TopFragmentPresenter.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                actionIntent,
                PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(actionPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}