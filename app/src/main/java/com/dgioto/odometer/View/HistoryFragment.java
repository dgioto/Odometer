package com.dgioto.odometer.View;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.R;
import com.dgioto.odometer.adapter.MainAdapter;
import com.dgioto.odometer.db.DbManager;

public class HistoryFragment extends Fragment{

    private MainAdapter mainAdapter;
    private final MainActivity mainActivity;
    private View layout;
    private DbManager dbManager;

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
        RecyclerView rcView = layout.findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(mainActivity, dbManager);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mainActivity);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rcView.setLayoutManager(mLayoutManager);
        rcView.setAdapter(mainAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        dbManager.openDb();
        mainAdapter.updateAdapter(dbManager.getFromDb());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDb();
    }
}