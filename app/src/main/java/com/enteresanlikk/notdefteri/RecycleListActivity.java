package com.enteresanlikk.notdefteri;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecycleListActivity extends AppCompatActivity {

    Database db = new Database(this);
    Functions f = new Functions(this);
    ArrayList<HashMap<String, String>> notes;
    RecycleNoteAdapter adapter;
    List<Note> noteList;

    RecyclerView recyclerView;
    LinearLayout noNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        noNotes = (LinearLayout) findViewById(R.id.noNotes);
        recyclerView = (RecyclerView) findViewById(R.id.items);

        noteList = new ArrayList<>();
        adapter = new RecycleNoteAdapter(this, noteList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, f.dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Notes();
    }

    public void Notes() {
        notes = db.list("0");
        if(notes.size() > 0) {
            noNotes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Note note = null;
            for(int i=0;i<notes.size();i++){
                HashMap<String, String> noteStr = notes.get(i);

                String title = noteStr.get("title");
                String content = noteStr.get("content");

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
        getMenuInflater().inflate(R.menu.recycle_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                f.go(MainActivity.class);
                break;
            case R.id.delete_all:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecycleListActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.delete));
                alertDialogBuilder.setIcon(R.drawable.delete_icon);
                alertDialogBuilder
                        .setMessage(getString(R.string.delete_permanent_all_your_notes))
                        .setCancelable(false)
                        .setNeutralButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Boolean status = db.permanentDeleteAll();
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

            case R.id.restore_all:
                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(RecycleListActivity.this);
                alertDialogBuilder2.setTitle(getString(R.string.delete));
                alertDialogBuilder2.setIcon(R.drawable.delete_icon);
                alertDialogBuilder2
                        .setMessage(getString(R.string.restore_your_all_note))
                        .setCancelable(false)
                        .setNeutralButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Boolean status = db.activeAll();
                                if(status) {
                                    RecycleListActivity.this.finish();
                                    f.go(MainActivity.class);
                                    f.message(getString(R.string.successAllRestored));
                                } else {
                                    f.message(getString(R.string.errorAllRestored));
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder2.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        f.go(MainActivity.class);
    }
}
