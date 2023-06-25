package com.dgioto.odometer.View;

import android.content.ServiceConnection;
import android.widget.Button;
import android.widget.TextView;

public interface TopFragmentContract {

    interface View {

        TextView getDistanceView();

        TextView getTimeView();

        Button getStartButton();

        Button getNoteButton();

        Button getDischargeButton();
    }

    interface Presenter {

        boolean getBound();

        void setBound(boolean bound);

        ServiceConnection getConnection();

        void runTimer(TextView timeView);

        void onClickStart();

        void onClickNote();

        void onClickDischarge();

        void addLocationDialog();

        void onRequestPermissionsResultToPresenter(int requestCode, String[] permissions, int[] grantResults);
    }


}
