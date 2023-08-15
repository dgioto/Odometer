package com.dgioto.odometer.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.Model;
import com.dgioto.odometer.R;
import com.dgioto.odometer.Service.NotificationService;
import com.dgioto.odometer.Service.OdometerService;

import java.util.Locale;

public class TopFragmentPresenter implements TopFragmentContract.Presenter {

    private final TopFragmentContract.View topFragment;
    private final Context context;
    public LocationManager manager;
    public boolean statusOfGPS;
    private final int PERMISSION_REQUEST_CODE = 698;
    private OdometerService odometerService;
    private final Model model;

    public TopFragmentPresenter(TopFragmentContract.View topFragment, Context context) {
        this.topFragment = topFragment;
        this.context = context;
        odometerService = new OdometerService();
        model = new Model();
    }

    //ODOMETER
    public final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) binder;
            odometerService = odometerBinder.getOdometer();
            model.setBound(true);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            model.setBound(false);
        }
    };

    //ODOMETER
    public void displayDistance(TextView distanceView, OdometerService odometerService){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (model.isBound() && odometerService != null){
                    distance = odometerService.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.0f m", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);
            }
        });
    }

    //STOPWATCH
    public void runTimer(TextView timeView){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = model.getSeconds() / 3600;
                int minutes = (model.getSeconds() % 3600) / 60;
                int secs = model.getSeconds() % 60;
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if(model.isRunning()) model.setSeconds(model.getSeconds() + 1);

                handler.postDelayed(this, 1000);
            }
        });
    }

    public void managerGPS(){
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean getBound() {
        return model.isBound();
    }

    @Override
    public void setBound(boolean bound) {
        model.setBound(bound);
    }

    @Override
    public ServiceConnection getConnection() {
        return connection;
    }

    @Override
    public OdometerService getOdometerService() {
        return odometerService;
    }

    @Override
    public void onClickStart() {

        if (ContextCompat.checkSelfPermission(context, OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){

            addLocationDialog();

        } else {
            Intent intent = new Intent(context, OdometerService.class);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

            managerGPS();
            if (!statusOfGPS){
                Context context = topFragment.getContext();
                androidx.appcompat.app.AlertDialog statusOfGPSDialog =
                        new androidx.appcompat.app.AlertDialog.Builder(context).create();
                statusOfGPSDialog.setTitle(R.string.gps);
                LayoutInflater inflater = LayoutInflater.from(topFragment.getContext());
                LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_status_of_gps, null);
                statusOfGPSDialog.setView(view);
                statusOfGPSDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                statusOfGPSDialog.show();
            } else {
                topFragment.getNoteButton().setVisibility(View.VISIBLE);
                topFragment.getDischargeButton().setVisibility(View.VISIBLE);
                topFragment.getStartButton().setText(R.string.restart);

                //ODOMETER
                odometerService.resetDistance();
                model.setBound(true);
                displayDistance(topFragment.getDistanceView(), odometerService);

                //STOPWATCH
                model.setRunning(true);
                model.setSeconds(0);

                startNotificationService();
            }
        }
    }

    private void startNotificationService(){
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NotificationService.EXTRA_MESSAGE,
                context.getResources().getString(R.string.notification));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onClickNote() {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("distanceView", topFragment.getDistanceView().getText().toString());
        intent.putExtra("timeView", topFragment.getTimeView().getText().toString());
        context.startActivity(intent);
    }

    @Override
    public void onClickDischarge() {
        topFragment.getNoteButton().setVisibility(View.GONE);
        topFragment.getDischargeButton().setVisibility(View.GONE);
        topFragment.getStartButton().setText(R.string.start);

        //ODOMETER
        model.setBound(false);
        odometerService.resetDistance();

        //STOPWATCH
        model.setRunning(false);
        model.setSeconds(0);
    }

    @Override
    public void addLocationDialog() {
        Context context = topFragment.getContext();
        androidx.appcompat.app.AlertDialog locationDialog =
                new androidx.appcompat.app.AlertDialog.Builder(context).create();
        locationDialog.setTitle(R.string.location);
        locationDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(topFragment.getContext());
        LinearLayout view_location = (LinearLayout) inflater.inflate(R.layout.dialog_location, null);
        locationDialog.setView(view_location);
        locationDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE,
                "Yes",
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[]{OdometerService.PERMISSION_STRING},
                        PERMISSION_REQUEST_CODE));
        locationDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE,
                "No",
                (dialog, which) -> dialog.dismiss());
        locationDialog.show();
    }

    @Override
    public void onRequestPermissionsResultToPresenter(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, OdometerService.class);
                odometerService.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                Notification.Builder builder = new Notification.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(context.getResources().getString(R.string.permission_denied))
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                Intent actionIntent = new Intent(context, MainActivity.class);
                PendingIntent actionPendingIntent = PendingIntent.getActivity(context,
                        0,
                        actionIntent,
                        PendingIntent.FLAG_MUTABLE);
                builder.setContentIntent(actionPendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) odometerService.getSystemService(Context.NOTIFICATION_SERVICE);
                int NOTIFICATION_ID = 423;
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
}
