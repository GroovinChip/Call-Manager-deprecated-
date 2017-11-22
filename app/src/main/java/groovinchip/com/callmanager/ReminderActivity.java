package groovinchip.com.callmanager;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity {

    SharedPreferences appPrefs;
    public final String APP_PREFS = "appPrefs";
    SharedPreferences reminder_recs;
    public final String REMINDER_RECS = "reminder_recs";
    private boolean isChecked = false;
    public static String target;
    private Calendar calendar;
    private int year, month, day, hour, min;
    private Call call;
    private EditText dateField;
    private EditText timeField;
    private Button datePickerBtn;
    private Button timePickerBtn;
    private Button cancelBtn;
    private Button saveBtn;
    int runtimeID;
    int notificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_reminder);
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        runtimeID = appPrefs.getInt("runtimeID", runtimeID);
        notificationID = appPrefs.getInt("notificationID", notificationID);

        // Initialize components
        dateField = (EditText)findViewById(R.id.dateField);
        timeField = (EditText)findViewById(R.id.timeField);
        datePickerBtn = (Button)findViewById(R.id.datePicker);
        timePickerBtn = (Button)findViewById(R.id.timePicker);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        saveBtn = (Button)findViewById(R.id.saveBtn);

        // Get current date to display as default to user
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        // Get current time
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        // Retrieve passed call
        target = getIntent().getStringExtra("Reminder");
        Gson gson = new Gson();
        call = gson.fromJson(target, Call.class);

        // Set button icons based on app theme
        if(isChecked == true){
            datePickerBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_pick_dark, 0, 0, 0);
            timePickerBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_pick_dark, 0, 0, 0);
        }

        //---BUTTON CLICK HANDLERS--\\
        // Open the date picker
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(view);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain(view);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReminder(view);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(888);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    myDateListener, year, month, day);
            // Do not allow past dates
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return datePickerDialog;
        }
        else if(id == 888){
            return new TimePickerDialog(this, myTimeListener, hour, min, false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateField.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }

    private TimePickerDialog.OnTimeSetListener myTimeListener = new
            TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    showTime(hourOfDay, minute);
                }
            };

    // Ensure time is formatted properly
    private void showTime(int hour, int minute) {
        if(hour > 11 && (minute >= 0 && minute < 10))
        {
            if (hour == 12){
                timeField.setText(hour + ":0" + minute + " PM");
            }
            else{
                hour = hour - 12;
                timeField.setText(hour + ":0" + minute + " PM");
            }
        }
        else if(hour > 11 && (minute >= 0 && minute >= 10))
        {
            if (hour == 12){
                timeField.setText(hour + ":" + minute + " PM");
            }
            else{
                hour = hour - 12;
                timeField.setText(hour + ":" + minute + " PM");
            }
        }
        else if(hour == 0 && minute < 10){
            hour = 12;
            timeField.setText(hour + ":0" + minute + " AM");
        }
        else if(hour > 11){
            hour = hour - 12;
            timeField.setText(hour + ":" + minute + " PM");
        }
        else if(minute >= 0 && minute < 10){
            timeField.setText(hour + ":0" + minute + " AM");
        }
        else if(hour == 0 && (minute >= 0 && minute >= 10)){
            hour = 12;
            timeField.setText(hour + ":" + minute + " AM");
        }
        else{
            timeField.setText(hour + ":" + minute + " AM");
        }
    }

    // Go back to main screen when user click 'Cancel' button
    public void goToMain(View view){
        Intent goToMain = new Intent(view.getContext(), MainActivity.class);
        startActivity(goToMain);
        finish();
    }


    public void setReminder(View view){
        Boolean isValid = null;
        if(dateField.getText().equals("") || timeField.getText().equals("")){
            Toast toast = Toast.makeText(this, "One or more required fields is empty", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            isValid = scheduleReminder(getNotification("Don't forget to call " + call.getName().toString() + "! Tap to call.", runtimeID), String.valueOf(dateField.getText()), String.valueOf(timeField.getText()));
            if (isValid == true) {
                goToMain(view);
            } else {
            }
        }
    }

    // Schedule the notification reminder
    public boolean scheduleReminder(Notification notification, String date, String time){
        String[] dateArray = date.split("/");
        String[] timeArray = time.split(":|\\s+");
        Date currentDate = new Date();
        int notHour;
        reminder_recs = this.getSharedPreferences(REMINDER_RECS, Context.MODE_PRIVATE);
        Gson reminderRecordGson = new Gson();
        String reminderRecordObj;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        if(timeArray[2].equals("PM")){
            notHour = Integer.parseInt(timeArray[0]);
            if (notHour < 12) {
                notHour = notHour + 12;
            }
            cal.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateArray[0]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[1]));
            cal.set(Calendar.HOUR_OF_DAY, notHour);
            cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        }
        else{
            cal.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateArray[0]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[1]));
            notHour = Integer.parseInt(timeArray[0]);
            if (notHour == 12) {
                notHour = 0;
                cal.set(Calendar.HOUR_OF_DAY, notHour);
            }
            else {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
            }
            cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        }

        Date reminderDate = cal.getTime();
        long diffInMillis  = reminderDate.getTime() - currentDate.getTime();
        if(diffInMillis > 0){
            runtimeID += 1;
            notificationID += 1;
            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putInt("runtimeID", runtimeID);
            editor.commit();
            editor.putInt("notificationID", notificationID);
            editor.commit();
            Intent notificationIntent = new Intent(this, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, runtimeID, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + diffInMillis,
                    pendingIntent);


            // Add reminder time to SharedPreferences file
            SharedPreferences.Editor addToRecs = reminder_recs.edit();
            int record_index = reminder_recs.getAll().size();
            call.setReminderTime(reminderDate);
            reminderRecordObj = reminderRecordGson.toJson(call);
            if(record_index == 0){
                addToRecs.putString(String.valueOf(record_index), reminderRecordObj);
                addToRecs.commit();
            }
            else{
                record_index += 1;
                addToRecs.putString(String.valueOf(record_index), reminderRecordObj);
                addToRecs.commit();
            }

            Toast toast = Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        else{
            Toast toast = Toast.makeText(this, "Reminder not set. Please enter a date/time in the future.",
                    Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }

    public Notification getNotification(String content, int id){
        String channelId = "Call Reminders";
        final String num = "tel:" + call.getNumber();
        final Intent callNumber = new Intent(Intent.ACTION_CALL);
        callNumber.setData(Uri.parse(num));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(callNumber);
        PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(id,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26){
            builder = new Notification.Builder(this, channelId);
        }
        else{
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle("Reminder: Call " + call.getName());
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_call_reminder);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }

    // Set the app theme based on boolean value from a
    // separate shared preferences file
    private void setAppTheme() {
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        Boolean isDark = appPrefs.getBoolean("themeVal", false);
        if(isDark==true){
            setTheme(R.style.AppThemeDark);
            isChecked = true;
        }
        else{
            setTheme(R.style.AppTheme);
            isChecked = false;
        }
    }
}
