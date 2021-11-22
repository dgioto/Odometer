package com.dgioto.odometer.model;

import android.os.Handler;
import android.widget.TextView;

import com.dgioto.odometer.Service.OdometerService;

import java.util.Locale;

public class Odometer {

    public boolean bound = false;

    public void displayDistance(TextView distanceView, OdometerService odometerService){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (bound && odometerService != null){
                    distance = odometerService.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.0f m", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);
            }
        });
    }
}
