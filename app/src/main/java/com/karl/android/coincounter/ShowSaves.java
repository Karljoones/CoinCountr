// Copyright of Karl Jones

package com.karl.android.coincounter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ShowSaves extends ActionBarActivity {

    MySQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saves);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saves");

        LinearLayout ll = (LinearLayout) findViewById(R.id.llo2);
        myDB = new MySQLiteHelper(this);

        viewAll();

        ContextThemeWrapper newContext = new ContextThemeWrapper(getBaseContext(), R.style.FancyText);

        for (int i = displays.size() - 1 ; i >= 0 ; i--) {
            TextView b = new TextView(newContext);
            b.setText(displays.get(i));
            b.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            b.setId(i);
            b.setTag(i);
            b.setBackgroundResource(getResources().getColor(android.R.color.transparent));
            ll.addView(b);
        }
        result = (EditText) findViewById(R.id.temp_edit);
    }
    public Cursor res;
    public ArrayList<StringBuffer> displays;

    public void viewAll() {
        displays = new ArrayList<StringBuffer>();
        res = myDB.getAllData();
        if(res.getCount() == 0) {
            Toast.makeText(ShowSaves.this, "No saves found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ShowSaves.this, MainActivity.class);
            startActivity(intent);
        }
        while(res.moveToNext()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(
                    res.getString(2) + "\n"
                    + res.getString(3).toUpperCase() + "\n"
                    + res.getString(1) + "\n"
                    + getResources().getString(R.string.save_id) + " " + res.getString(0) + "\n\n");
            displays.add(buffer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_saves, menu);
        return true;
    }

    public EditText result;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_delete_entry:
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.prompts, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                alert
                        .setCancelable(true)
                        .setTitle("Delete entry")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                result.setText(userInput.getText());
                                deleteEntry(result.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).create().show();
            } // end case

        return super.onOptionsItemSelected(item);
    }

    public void deleteEntry(String id) {
        int result = myDB.deleteData(id);
        if(result > 0) {
            Intent intent = new Intent(ShowSaves.this, ShowSaves.class);
            startActivity(intent);
            Toast.makeText(ShowSaves.this, "Data deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ShowSaves.this, "No data deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        finish();
        super.onDestroy();
    }
}
