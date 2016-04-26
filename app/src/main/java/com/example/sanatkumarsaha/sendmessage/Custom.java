package com.example.sanatkumarsaha.sendmessage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Custom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        com.rey.material.widget.Spinner sp = (com.rey.material.widget.Spinner) findViewById(R.id.spin);
        List<String> categories = new ArrayList<String>();
        categories.add("Type1");
        categories.add("Type2");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_list_item_2, categories);
        dataAdapter.setDropDownViewResource(R.layout.custom_list_item_2);

        sp.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main__screen,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}