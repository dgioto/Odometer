package com.dgioto.odometer.View;

import android.content.Context;
import android.content.ServiceConnection;
import android.widget.Button;
import android.widget.TextView;

public interface TopFragmentContract {

    interface View {

        Context getContext();

        TextView getDistanceView();

        TextView getTimeView();

        Button getStartButton();

        Button getNoteButton();

        Button getDischargeButton();
    }

    interface Presenter {

        ServiceConnection getConnection();

        boolean getBound();

        void setBound(boolean bound);

        void runTimer(TextView timeView);

        void onClickStart();

        void onClickNote();

        void onClickDischarge();

        void addLocationDialog();

        void onRequestPermissionsResultToPresenter(int requestCode, String[] permissions, int[] grantResults);
    }
}
