package com.reminder.social_activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.reminder.BaseActivity;
import com.reminder.CustomAdapter;
import com.reminder.DAO.RemindersDB;
import com.reminder.DAO.objects.Reminder;
import com.reminder.R;
import com.reminder.social_activities.social_service.FacebookReceiver;
import com.reminder.social_activities.social_service.ViberReceiver;

import java.util.List;

public class AllViberReminders extends BaseActivity {

    private List<Reminder> reminders;
    private SwipeMenuListView listView;
    private CustomAdapter adapter;
    private RemindersDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_viber_reminders);
        setTitle("Viber reminders");

        db = RemindersDB.getInstance(this);
        listView = (SwipeMenuListView) findViewById(R.id.viberRems);
        init();

        ImageView icon = new ImageView(this);
        icon.setImageResource(android.R.drawable.ic_input_add);
        FloatingActionButton fab = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AllViberReminders.this, ViberActivity.class), 0);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                Display display = getWindowManager().getDefaultDisplay();
                deleteItem.setWidth(display.getWidth() / 2);
                deleteItem.setTitle("Delete this reminder?");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setIcon(android.R.drawable.ic_menu_delete);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
        reminders = db.getAllViberReminders();

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                init();
                final Reminder r = reminders.get(position);
                Intent myIntent = new Intent(AllViberReminders.this, ViberReceiver.class);
                PendingIntent pendingIntent;
                boolean alarmUp = (PendingIntent.getBroadcast(AllViberReminders.this, reminders.get(position).getId(),
                        new Intent(AllViberReminders.this, ViberReceiver.class),
                        PendingIntent.FLAG_ONE_SHOT) != null);
                if (alarmUp) {
                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminders.get(position).getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
                db.deleteReminder(reminders.get(position).getId());
                init();
                adapter.notifyDataSetChanged();
                final Snackbar snackbar = Snackbar.make(listView, "Reminder deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.addReminder(r);
                        run(r, r.getId());
                        init();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
                return false;
            }
        });
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        init();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        reminders = db.getAllViberReminders();
        adapter = new CustomAdapter(this, reminders);
        listView.setAdapter(adapter);
    }

    private void run(Reminder r, int id) { // wrong?
        Intent myIntent = new Intent(this, ViberReceiver.class);
        myIntent.putExtra("text", r.getName());
        myIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, r.getTimeInMillis(), pendingIntent);
    }
}
