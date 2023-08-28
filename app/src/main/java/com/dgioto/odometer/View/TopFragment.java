package com.dgioto.odometer.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dgioto.odometer.R;

public class TopFragment extends Fragment implements View.OnClickListener, TopFragmentContract.View {

    private final Context context;
    private TopFragmentContract.Presenter presenter;

    public TextView distanceView, timeView;
    public Button startButton, noteButton, dischargeButton;

    public TopFragment(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public TextView getDistanceView() {
        return distanceView;
    }

    @Override
    public TextView getTimeView() {
        return timeView;
    }

    @Override
    public Button getStartButton() {
        return startButton;
    }

    @Override
    public Button getNoteButton() {
        return noteButton;
    }

    @Override
    public Button getDischargeButton() {
        return dischargeButton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new TopFragmentPresenter(this, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_top, container, false);

        distanceView = layout.findViewById(R.id.distance);
        timeView = layout.findViewById(R.id.time);
        presenter.runTimer(timeView);

        startButton = layout.findViewById(R.id.start);
        startButton.setOnClickListener(this);
        noteButton = layout.findViewById(R.id.note);
        noteButton.setOnClickListener(this);
        dischargeButton = layout.findViewById(R.id.discharge);
        dischargeButton.setOnClickListener(this);

        return layout;
    }

    //ODOMETER
    //when permission was requested at runtime, check the result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        presenter.onRequestPermissionsResultToPresenter(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter.getBound()){
            presenter.getOdometerService().unbindService(presenter.getConnection());
            presenter.setBound(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                presenter.onClickStart();
                break;
            case R.id.note:
                presenter.onClickNote();
                break;
            case R.id.discharge:
                presenter.onClickDischarge();
                break;
        }
    }
}