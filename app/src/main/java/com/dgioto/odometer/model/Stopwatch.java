package com.dgioto.odometer.model;

import android.os.Handler;
import android.widget.TextView;
import java.util.Locale;

public class Stopwatch {

    public int seconds = 0;
    public boolean running;


    public void runTimer(TextView timeView){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if(running) seconds++;

                handler.postDelayed(this, 1000);
            }
        });
    }
}
