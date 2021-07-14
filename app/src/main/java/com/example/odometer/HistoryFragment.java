package com.example.odometer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.odometer.adapter.MainAdapter;
import com.example.odometer.db.DbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HistoryFragment extends Fragment implements View.OnClickListener{

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

        FloatingActionButton fab = layout.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        init();

        return layout;
    }

    private void init() {
        dbManager = new DbManager(mainActivity);
        RecyclerView rcView = layout.findViewById(R.id.rcView);
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

    public void onClickAdd(){
        Intent intent = new Intent(mainActivity, EditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dbManager.closeDb();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) onClickAdd();
    }
}