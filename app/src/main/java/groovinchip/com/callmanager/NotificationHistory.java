package groovinchip.com.callmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class NotificationHistory extends AppCompatActivity {

    SharedPreferences appPrefs;
    public final String APP_PREFS = "appPrefs";
    private boolean isChecked = false;
    private RecyclerView recyclerView;
    private List<Call> upcomingNotifs;
    SharedPreferences reminder_recs;
    public final String REMINDER_RECS = "reminder_recs";
    private NotificationHistoryAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_notification_history);

        reminder_recs = getSharedPreferences(REMINDER_RECS, Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.notif_hist_recycler_view);
        recyclerView.setHasFixedSize(true); // ?
        RecyclerView.LayoutManager lin = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lin);

        upcomingNotifs = new ArrayList<>();
        getCallsFromSharedPreferences();
        Collections.reverse(upcomingNotifs);

        listAdapter = new NotificationHistoryAdapter(upcomingNotifs, this);
        recyclerView.setAdapter(listAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    // Get Call objects from SharedPreferences and sort by key
    public void getCallsFromSharedPreferences(){
        Map<String, ?> allCalls = reminder_recs.getAll();
        SortedSet<String> keys = new TreeSet<String>(allCalls.keySet());
        for(String key : keys){
            Gson gson = new Gson();
            String json = reminder_recs.getString(key, "");
            Call call = gson.fromJson(json, Call.class);
            upcomingNotifs.add(call);
        }
    }

    // Set up the overflow menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification_history_menu, menu);

        return true;
    }

    // Set up menu item actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clearNotifHist:
                if (upcomingNotifs.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "There are no notifications to clear", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    clearNotificationHistory();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearNotificationHistory(){
        reminder_recs = this.getSharedPreferences(REMINDER_RECS, Context.MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.clearAllHistory_actions_title);
        builder.setPositiveButton(R.string.clearAll_yes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = reminder_recs.edit();
                editor.clear();
                editor.commit();
                Toast toast = Toast.makeText(getApplicationContext(), "Notification History cleared", Toast.LENGTH_SHORT);
                toast.show();
                goToMain();
            }
        }).setNegativeButton(R.string.clearAll_no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void goToMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
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
