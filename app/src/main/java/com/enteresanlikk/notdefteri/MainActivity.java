package com.enteresanlikk.notdefteri;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Database db = new Database(this);
    Functions f = new Functions(this);
    ArrayList<HashMap<String, String>> notes;
    NoteAdapter adapter;
    List<Note> noteList;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    LinearLayout noNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.mipmap.toolbar_icon); //ikon koymak için
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f.go(AddActivity.class);
            }
        });

        noNotes = (LinearLayout) findViewById(R.id.noNotes);
        recyclerView = (RecyclerView) findViewById(R.id.items);

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(this, noteList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, f.dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Notes();

        if(!serviceStatus()) {
            startService(new Intent(getApplicationContext(), NotificationService.class));
        }
    }

    public void Notes() {
        notes = db.list("1");
        if(notes.size() > 0) {
            noNotes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Note note = null;
            for(int i=0;i<notes.size();i++){
                HashMap<String, String> noteStr = notes.get(i);

                String title = noteStr.get("title");
                String content = noteStr.get("content").replaceAll("[\r\n]+", " ");
                if(content.length() > 100) {
                    content = content.substring(0, 97)+"...";
                }

                Integer reminder = 0;
                if(noteStr.get("reminder") != null) {
                    reminder = Integer.valueOf(noteStr.get("reminder"));
                }
                String reminder_date = noteStr.get("reminder_date");
                String reminder_time = noteStr.get("reminder_time");
                Integer status = Integer.valueOf(noteStr.get("status"));
                String date = noteStr.get("date");

                note = new Note(title, content, reminder, reminder_date, reminder_time, status, date);
                noteList.add(note);
            }

        } else {
            noNotes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.delete_all:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.delete));
                    alertDialogBuilder.setIcon(R.drawable.delete_icon);
                    alertDialogBuilder
                            .setMessage(getString(R.string.delete_all_your_notes))
                            .setCancelable(false)
                            .setNeutralButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Boolean status = db.deleteAll();
                                    if(status) {
                                        f.message(getString(R.string.successAllDeleted));
                                        f.go(MainActivity.class);
                                    } else {
                                        f.message(getString(R.string.errorAllDeleted));
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                break;

            case R.id.delete_all_reminder:
                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder2.setTitle(getString(R.string.delete));
                alertDialogBuilder2.setIcon(R.drawable.delete_icon);
                alertDialogBuilder2
                        .setMessage(getString(R.string.delete_all_your_reminders))
                        .setCancelable(false)
                        .setNeutralButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Boolean status = db.deleteAllReminder();
                                if(status) {
                                    f.message(getString(R.string.successAllDeleted));
                                    f.go(MainActivity.class);
                                } else {
                                    f.message(getString(R.string.errorAllDeleted));
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                alertDialog2.show();
                break;

            case R.id.recycle:
                this.finish();
                f.go(RecycleListActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        f.finishApp();
    }

    public boolean serviceStatus(){//Servis Çalışıyor mu kontrol eden fonksiyon

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ( NotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
