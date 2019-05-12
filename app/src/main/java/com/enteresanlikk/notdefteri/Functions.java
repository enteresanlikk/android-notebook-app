package com.enteresanlikk.notdefteri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Functions extends Activity {
    Context mContext;

    public Functions(Context mContext)
    {
        this.mContext = mContext;
    }

    public void go(Class goClass) {
        Intent i = new Intent(mContext.getApplicationContext(), goClass);
        mContext.startActivity(i);
    }

    public void message(String message) {
        Toast.makeText(mContext.getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
    }

    public String getDate() {
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(c1.getTime());
        return date;
    }

    public String getTime() {
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(c1.getTime());
        return time;
    }

    public String zeroConvert(Integer num) {
        String retVal = String.valueOf(num);
        if(num < 10) retVal = "0"+num;
        return retVal;
    }
}
