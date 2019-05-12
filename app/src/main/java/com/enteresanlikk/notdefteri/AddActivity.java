package com.enteresanlikk.notdefteri;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    Database db = new Database(this);
    Functions f = new Functions(this);

    Button btn_add, btn_reminder_date, btn_reminder_time;
    CheckBox cb_reminder_add;
    AutoCompleteTextView edt_title, edt_content;
    LinearLayout reminder_layout;

    String title = "", content = "", reminder_date = "", reminder_time = "";
    Integer reminder = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_add = (Button) findViewById(R.id.btn_add);
        btn_reminder_date = (Button) findViewById(R.id.btn_reminder_date);
        btn_reminder_time = (Button) findViewById(R.id.btn_reminder_time);

        cb_reminder_add = (CheckBox) findViewById(R.id.cb_reminder_add);

        edt_title = (AutoCompleteTextView) findViewById(R.id.edt_title);
        edt_content = (AutoCompleteTextView) findViewById(R.id.edt_content);

        reminder_layout = (LinearLayout) findViewById(R.id.reminder_layout);

        btn_add.setOnClickListener(this);
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
        Calendar calendar = Calendar.getInstance();

        int id = v.getId();
        switch (id) {
            case R.id.btn_add:
                title = edt_title.getText().toString().trim();
                content = edt_content.getText().toString().trim();
                if (title.isEmpty()) { f.message(getString(R.string.noEmptyTitle)); break; }
                if (content.isEmpty()) { f.message(getString(R.string.noEmptyContent)); break; }

                if(reminder == 1) {
                    if(reminder_date.isEmpty()) { f.message(getString(R.string.noEmptyReminderDate)); break; }
                    if(reminder_time.isEmpty()) { f.message(getString(R.string.noEmptyReminderTime)); break; }
                }

                Boolean status = db.add(title, content, reminder, reminder_date, reminder_time);
                if(status) {
                    f.message(getString(R.string.successAdded));
                    f.go(MainActivity.class);
                } else {
                    f.message(getString(R.string.errorAdded));
                }

                break;

            case R.id.btn_reminder_date:
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month += 1;
                                String date = f.zeroConvert(dayOfMonth) + "/" + f.zeroConvert(month) + "/" + f.zeroConvert(year);
                                reminder_date = date;
                                btn_reminder_date.setText(date);
                                btn_reminder_time.setEnabled(true);
                            }
                        }, year, month, day);

                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.select), dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), dpd);

                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.MONTH, 0);
                long minDate = c.getTime().getTime();

                dpd.getDatePicker().setMinDate(minDate);

                dpd.show();

                break;
            case R.id.btn_reminder_time:
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = f.zeroConvert(hourOfDay) + ":" + f.zeroConvert(minute);
                                btn_reminder_time.setText(time);
                                reminder_time = time;
                            }
                        }, hour, minute, true);

                tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, getString(R.string.select), tpd);
                tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), tpd);
                tpd.show();

                break;
        }
    }
}
