package com.dgioto.odometer.View;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgioto.odometer.R;
import com.dgioto.odometer.adapter.MainAdapter;
import com.dgioto.odometer.db.DbManager;

public class HistoryFragment extends Fragment{

    private MainAdapter mainAdapter;
    private final Context context;
    private View layout;
    private DbManager dbManager;

    public HistoryFragment( ) {
        this.context = getContext();
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
        dbManager = new DbManager(context);
        RecyclerView rcView = layout.findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(context, dbManager);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
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