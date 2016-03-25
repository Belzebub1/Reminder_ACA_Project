package com.reminder.social_activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.reminder.BaseActivity;
import com.reminder.R;
import com.reminder.social_activities.social_service.FacebookReceiver;

import java.util.Calendar;

public class MyFacebookActivity extends BaseActivity {

    private Button shareBtn;
    private EditText text;
    private TextView time;
    private TextView date;
    private Calendar c;
    private Calendar selected;
    private int selectedDay, selectedMonth, selectedYear, selectedHour, selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_facebook);
        setTitle("Facebook");
        text = (EditText) findViewById(R.id.shareText);
        shareBtn = (Button) findViewById(R.id.shareBtn);
        date = (TextView) findViewById(R.id.dateText);
        time = (TextView) findViewById(R.id.timeText);


        selected = Calendar.getInstance();
        selectedDay = selected.get(Calendar.DAY_OF_MONTH);
        selectedMonth = selected.get(Calendar.MONTH) + 1;
        selectedYear = selected.get(Calendar.YEAR);
        selectedHour = selected.get(Calendar.HOUR_OF_DAY);
        selectedMinute = selected.get(Calendar.MINUTE);

        date.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
        time.setText(selectedHour + ":" + selectedMinute);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog d = new DatePickerDialog(MyFacebookActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDay = dayOfMonth;
                        selectedMonth = monthOfYear + 1;
                        selectedYear = year;
                        date.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
                        selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selected.set(Calendar.MONTH, monthOfYear);
                        selected.set(Calendar.YEAR, year);
                    }
                }, selectedYear, selectedMonth - 1, selectedDay);
                d.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog d = new TimePickerDialog(MyFacebookActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        time.setText(selectedHour + ":" + selectedMinute);
                        selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selected.set(Calendar.MINUTE, minute);
                    }
                }, selectedHour, selectedMinute, true);
                d.show();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();

                if (selected.getTimeInMillis() < c.getTimeInMillis()) {
                    Toast.makeText(MyFacebookActivity.this, "Time must be future", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MyFacebookActivity.this, FacebookReceiver.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Facebook post scheduled", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(getApplicationContext(), FacebookReceiver.class);
                    myIntent.putExtra("text", text.getText().toString());

                    int id = (int) (System.currentTimeMillis() / 1000);
                    Calendar c = Calendar.getInstance();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, myIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, selected.getTimeInMillis(), pendingIntent);
                }
            }
        });
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        setContentView(R.layout.activity_my_facebook);
//        setTitle("Facebook");
//
//        share = (Button) findViewById(R.id.share);
//        send = (Button) findViewById(R.id.send);
//        shareText = (EditText) findViewById(R.id.shareText);
//
//        final ShareLinkContent shareLinkContent = new ShareLinkContent.Builder().build();
//
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("label", shareText.getText().toString());
//                clipboard.setPrimaryClip(clip);
//                ShareDialog.show(MyFacebookActivity.this, shareLinkContent);
//            }
//        });
//
//
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MessageDialog.show(MyFacebookActivity.this, shareLinkContent);
//            }
//        });
//    }
}
