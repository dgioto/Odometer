package com.example.odometer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.odometer.adapter.MainAdapter;
import com.example.odometer.db.DbHelper;
import com.example.odometer.db.DbManager;

public class HistoryFragment extends ListFragment {

    private RecyclerView rcView;
    private MainAdapter mainAdapter;
    MainActivity mainActivity;
    View layout;

    DbManager dbManager;

    public HistoryFragment(MainActivity _mainActivity){
        this.mainActivity = _mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_history, container, false);

        init();

        return layout;
    }

    private void init() {
        dbManager = new DbManager(mainActivity);
        rcView = layout.findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(mainActivity);
        rcView.setLayoutManager(new LinearLayoutManager(mainActivity));
        rcView.setAdapter(mainAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        dbManager.openDb();
        mainAdapter.updateAdapter(dbManager.getFromDb());
    }

    public void onClickSave(View view){
        dbManager.insertToDb("Test", 10, "Test", "Test");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dbManager.closeDb();
    }
}