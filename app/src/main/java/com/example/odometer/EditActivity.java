package com.example.odometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odometer.adapter.ListItem;
import com.example.odometer.db.DbConstants;
import com.example.odometer.db.DbManager;

public class EditActivity extends AppCompatActivity {

    private EditText idTitle, idMeters, idTimes, idDesc;
    TextView timeView;
    private DbManager dbManager;
    private boolean isEditState = true;
    ListItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        init();
        getMyIntents();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbManager.openDb();
    }

    private void init(){
        idTitle = findViewById(R.id.idTitle);
        idMeters = findViewById(R.id.idMeters);
        idTimes = findViewById(R.id.idTimes);
        idDesc = findViewById(R.id.idDesc);

        timeView = findViewById(R.id.time);
        String timeText = timeView.getText().toString();
        idTimes.setText(timeText);

        dbManager = new DbManager(this);
    }

    private void getMyIntents(){
        Intent intent = getIntent();
        if (intent != null){
            item = (ListItem) intent.getSerializableExtra(DbConstants.LIST_ITEM_INTENT);
            isEditState = intent.getBooleanExtra(DbConstants.EDIT_STATE, true);

            if (!isEditState){
                idTitle.setText(item.getTitle());
                idMeters.setText(item.getMeters());
                idTimes.setText(item.getTimes());
                idDesc.setText(item.getDesc());
            }
        }
    }

    public void onClickSave(View view) {

        String title = idTitle.getText().toString();
        String meters = idMeters.getText().toString();
        String times = idTimes.getText().toString();
        String desc = idDesc.getText().toString();

//        if (title.equals("") || meters.equals("") || times.equals("") || desc.equals("")) {
//            Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT).show();
//        } else {
            if (isEditState) {
                dbManager.insertToDb(title, meters, times, desc);
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            } else {
                dbManager.updateItem(title, meters, times, desc, item.getId());
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            }
            dbManager.closeDb();
            finish();
//    }
    }
}