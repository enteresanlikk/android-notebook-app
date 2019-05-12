package com.enteresanlikk.notdefteri;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    Database db = new Database(this);
    Functions f = new Functions(this);

    Button btn_edit, btn_reminder_date, btn_reminder_time;
    CheckBox cb_reminder_add;
    AutoCompleteTextView edt_title, edt_content;
    LinearLayout reminder_layout;

    String title = "", content = "", reminder_date = "", reminder_time = "", dateStr = "";
    Integer reminder = 0, noteId;
    TextView date;

    TimePickerDialog tpd;
    DatePickerDialog dpd;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        noteId = getIntent().getExtras().getInt("id");

        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_reminder_date = (Button) findViewById(R.id.btn_reminder_date);
        btn_reminder_time = (Button) findViewById(R.id.btn_reminder_time);

        cb_reminder_add = (CheckBox) findViewById(R.id.cb_reminder_add);

        edt_title = (AutoCompleteTextView) findViewById(R.id.edt_title);
        edt_content = (AutoCompleteTextView) findViewById(R.id.edt_content);

        date = (TextView) findViewById(R.id.date);

        reminder_layout = (LinearLayout) findViewById(R.id.reminder_layout);

        btn_edit.setOnClickListener(this);
        btn_reminder_date.setOnClickListener(this);
        btn_reminder_time.setOnClickListener(this);

        cb_reminder_add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_reminder_add.setText(getString(R.string.reminder_remove));
                    reminder_layout.setVisibility(View.VISIBLE);
                    reminder = 1;
                } else {
                    cb_reminder_add.setText(getString(R.string.reminder_add));
                    reminder_layout.setVisibility(View.GONE);
                    reminder = 0;
                }
            }
        });

        setNoteValues();

        setPickers();
    }

    public void setPickers() {
        cal = Calendar.getInstance();

        if(reminder == 1) {
            String[] dates = reminder_date.split("/");
            String[] times = reminder_time.split(":");
            int hour = Integer.valueOf(times[0]);
            int minute = Integer.valueOf(times[1]);

            int year = Integer.valueOf(dates[2]);
            int month = Integer.valueOf(dates[1]);
            int day = Integer.valueOf(dates[0]);

            cal.set(year, month, day, hour, minute, 0);
        }

        int xyear = cal.get(Calendar.YEAR);
        int xmonth = cal.get(Calendar.MONTH);
        int xday = cal.get(Calendar.DAY_OF_MONTH);

        dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        String date = f.zeroConvert(dayOfMonth) + "/" + f.zeroConvert(month) + "/" + f.zeroConvert(year);
                        reminder_date = date;
                        btn_reminder_date.setText(date);
                        btn_reminder_time.setEnabled(true);
                    }
                }, xyear, xmonth, xday);

        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.select), dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), dpd);

        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.MONTH, 0);
        long minDate = c.getTime().getTime();

        dpd.getDatePicker().setMinDate(minDate);

        int xhour = cal.get(Calendar.HOUR_OF_DAY);
        int xminute = cal.get(Calendar.MINUTE);

        tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = f.zeroConvert(hourOfDay) + ":" + f.zeroConvert(minute);
                        btn_reminder_time.setText(time);
                        reminder_time = time;
                    }
                }, xhour, xminute, true);

        tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, getString(R.string.select), tpd);
        tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), tpd);
    }

    public void setNoteValues() {
        HashMap<String, String> note = db.detail(Integer.valueOf(noteId));
        title = note.get("title");
        content = note.get("content");
        dateStr = note.get("date");

        edt_title.setText(title);
        edt_content.setText(content);
        if(note.get("reminder") != null && note.get("reminder").equals("1")) {
            reminder = 1;

            cb_reminder_add.setChecked(true);
            reminder_date = note.get("reminder_date");
            reminder_time = note.get("reminder_time");

            //date
            btn_reminder_date.setText(reminder_date);

            //time
            btn_reminder_time.setEnabled(true);
            btn_reminder_time.setText(reminder_time);
        } else {
            reminder = 0;
        }
        date.setText(dateStr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            f.go(MainActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_edit:

                title = edt_title.getText().toString().trim();
                content = edt_content.getText().toString().trim();
                if (title.isEmpty()) { f.message(getString(R.string.noEmptyTitle)); break; }
                if (content.isEmpty()) { f.message(getString(R.string.noEmptyContent)); break; }

                if(reminder == 1) {
                    if(reminder_date.isEmpty()) { f.message(getString(R.string.noEmptyReminderDate)); break; }
                    if(reminder_time.isEmpty()) { f.message(getString(R.string.noEmptyReminderTime)); break; }
                }

                Boolean status = db.edit(noteId, title, content, reminder, reminder_date, reminder_time);
                if(status) {
                    f.message(getString(R.string.successUpdated));
                    f.go(MainActivity.class);
                } else {
                    f.message(getString(R.string.errorUpdated));
                }

                break;

            case R.id.btn_reminder_date:
                dpd.show();
                break;
            case R.id.btn_reminder_time:
                tpd.show();
                break;
        }
    }
}
