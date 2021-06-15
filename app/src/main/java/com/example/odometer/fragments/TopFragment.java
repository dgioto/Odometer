package com.example.odometer.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.odometer.MainActivity;
import com.example.odometer.R;

import java.util.Locale;

public class TopFragment extends Fragment implements View.OnClickListener {

    //STOPWATCH
    //количество прошедших секунд
    private int seconds = 0;
    //флаг работы секундомера
    private boolean running;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //STOPWATCH
        //сохраняем переменные в объект Bundle
        if (savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_top, container, false);

        //представление макета передается при вызове метода runTime()
        runTimer(layout);

        //связываем слушатель с каждой из кнопок
        Button startButton = layout.findViewById(R.id.start);
        startButton.setOnClickListener(this);
        Button stopButton = layout.findViewById(R.id.stop);
        stopButton.setOnClickListener(this);
        Button saveButton = layout.findViewById(R.id.save);
        saveButton.setOnClickListener(this);
        Button resetButton = layout.findViewById(R.id.reset);
        resetButton.setOnClickListener(this);
        Button exitButton = layout.findViewById(R.id.exit);
        exitButton.setOnClickListener(this);

        return layout;
    }

    //STOPWATCH
    //сохранить состояние секундомера, если он готовится к уничтожению
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
    }

    private void onClickStart(){
//        odometer.resetDistance();
//        bound = true;
//        displayDistance();

        running = true;
    }

    private void onClickStop(){


        running = false;
    }

    private void onClickSave(){

    }

    private void onClickReset(){
//        odometer.resetDistance();
//        bound = false;

        running = false;
        seconds = 0;
    }

    private void onClickExit(){
//        AlertDialog ald = new AlertDialog.Builder(MainActivity.this).create();
//        ald.setTitle("Выход");
//        ald.setMessage("Вы действительно хотите выйти?");
//        ald.setButton(AlertDialog.BUTTON_POSITIVE, "Да", (dialog, i) -> {
//            MainActivity.this.finish();
            System.exit(1);
//        });
//        ald.setButton(AlertDialog.BUTTON_NEGATIVE,"Нет", (dialog, i) -> dialog.cancel());
//        ald.show();
    }

    //STOPWATCH
    //обновление показаний таймера
    private void runTimer(View view){
        final TextView timeView = view.findViewById(R.id.time);
        //объект для выполнения кода в другом программном потоке
        final Handler handler = new Handler();
        //запускаем отдельный поток
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
                //повторное выполнение кода с отсрочкой в 1 секунду
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                onClickStart();
                break;
            case R.id.stop:
                onClickStop();
                break;
            case R.id.save:
                onClickSave();
                break;
            case R.id.reset:
                onClickReset();
                break;
            case R.id.exit:
                onClickExit();
                break;
        }
    }
}