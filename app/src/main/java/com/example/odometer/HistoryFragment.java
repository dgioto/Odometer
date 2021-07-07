package com.example.odometer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.odometer.R;
import com.example.odometer.adapter.MainAdapter;
import com.example.odometer.db.OdometerDatabaseHelper;

import java.util.List;

public class HistoryFragment extends ListFragment {

    private RecyclerView rcView;
    private MainAdapter mainAdapter;
    MainActivity mainActivity;

    OdometerDatabaseHelper myDb;

    public HistoryFragment(MainActivity _mainActivity){
        this.mainActivity = _mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        rcView = rcView.findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(mainActivity);
        rcView.setLayoutManager(new LinearLayoutManager(mainActivity));
        rcView.setAdapter(mainAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mainAdapter.updateAdapter(myDb.getReadableDatabase());
    }
}