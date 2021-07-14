package com.example.odometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.odometer.db.DbManager;

public class EditActivity extends AppCompatActivity {

    private EditText idTitle, idMeters, idTimes, idDesc;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbManager.openDb();

        init();
    }

    private void init(){
        idTitle = findViewById(R.id.idTitle);
        idMeters = findViewById(R.id.idMeters);
        idTimes = findViewById(R.id.idTimes);
        idDesc = findViewById(R.id.idDesc);

        dbManager = new DbManager(this);
    }

    public void onClickSave(View view){

        String title = idTitle.getText().toString();
        String meters = idMeters.getText().toString();
        String times = idTimes.getText().toString();
        String desc = idDesc.getText().toString();

        if (title.equals("") || meters.equals("") || times.equals("") || desc.equals("")) {

            Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT).show();

        } else {
            dbManager.insertToDb(title, meters, times, desc);
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
            dbManager.closeDb();
        }
    }
}