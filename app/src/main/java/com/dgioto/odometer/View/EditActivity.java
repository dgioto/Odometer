package com.dgioto.odometer.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dgioto.odometer.R;
import com.dgioto.odometer.adapter.ListItem;
import com.dgioto.odometer.db.DbConstants;
import com.dgioto.odometer.db.DbManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    private EditText idTitle, idMeters, idTimes;
    private DbManager dbManager;
    private boolean isEditState = true;
    ListItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);

        init();
        getMyIntents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_note) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        dbManager = new DbManager(this);
    }

    private void getMyIntents(){
        Intent intent = getIntent();

        if (intent != null){

            //перенос значения METERS в заметку
            String metersText = intent.getStringExtra("distanceView");
            idMeters.setText(metersText);
            //перенос значения TIMES в заметку
            String timeText = intent.getStringExtra("timeView");
            idTimes.setText(timeText);

            //add a number to an idTitle
            String numberStr = intent.getStringExtra("numberStr");
            //add a date to an idTitle
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
            String currentDate = sdf.format( new Date());

            idTitle.setText(currentDate + " (" + numberStr + ")");

            //Перемещаю курсор в конец текста в EditText idTitle
            idTitle.setSelection(idTitle.getText().length());

            item = (ListItem) intent.getSerializableExtra(DbConstants.LIST_ITEM_INTENT);
            isEditState = intent.getBooleanExtra(DbConstants.EDIT_STATE, true);

            if (!isEditState){
                idTitle.setText(item.getTitle());
                idMeters.setText(item.getMeters());
                idTimes.setText(item.getTimes());
            }
        }
    }

    public void onClickSave(View view) {
        save();
    }

    private void save(){
        String title = idTitle.getText().toString();
        String meters = idMeters.getText().toString();
        String times = idTimes.getText().toString();

        if (isEditState) {
            dbManager.insertToDb(title, meters, times);
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        } else {
            dbManager.updateItem(title, meters, times, item.getId());
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        }
        dbManager.closeDb();
        finish();
    }
}