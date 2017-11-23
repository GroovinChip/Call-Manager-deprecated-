package groovinchip.com.callmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static groovinchip.com.callmanager.AddNewCall.callListPrefs;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences appPrefs;
    public final String APP_PREFS = "appPrefs";
    public static final int PHONE = 0x2;
    public static final int SMS = 0x3;
    private List<Call> callList;
    private RecyclerView recyclerView;
    private CallListAdapter listAdapter;
    private boolean isChecked = false;
    int customChatID;
    Boolean isFabChecked;
    Boolean isMSThemeEnabled;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_main);

        // Declare the shared preferences to being used
        sharedpreferences = this.getSharedPreferences(callListPrefs, Context.MODE_PRIVATE);
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        isFabChecked = appPrefs.getBoolean("fabChecked", false);
        isMSThemeEnabled = appPrefs.getBoolean("msSwitchTheme", false);

        // Set up the recycler view which displays the calls to the user
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true); // ?
        RecyclerView.LayoutManager lin = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lin);

        // Initialize the list to store the calls in
        callList = new ArrayList<>();

        // Retrieve any calls from shared preferences
        getCallsFromSharedPreferences();

        // Set the recyclerView list adapter
        listAdapter = new CallListAdapter(callList, this);
        recyclerView.setAdapter(listAdapter);

        // Add separator lines to recyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));

        getPhonePermission(Manifest.permission.CALL_PHONE, PHONE);

        // Add touch events to the calls in the list
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            // Click event for regular tap
            public void onClick(View view, int position) {
                Call call = callList.get(position);
                openOptionsDialog(call);
            }

            // Click event for long press
            public void onLongClick(View view, int position) {
                Call call = callList.get(position);
                showTimestamp(call);
            }
        }));

        // Set FAB
        if(isFabChecked == true){
            fab = (FloatingActionButton)findViewById(R.id.fab);
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewCall();
                }
            });
        }
        else{
            fab = (FloatingActionButton)findViewById(R.id.fab);
            fab.hide();
        }
    }

    // Set the app theme based on boolean value from a
    // separate shared preferences file
    private void setAppTheme() {
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        Boolean isDark = appPrefs.getBoolean("themeVal", false);
        if (isDark == true) {
            setTheme(R.style.AppThemeDark);
            isChecked = true;
        } else {
            setTheme(R.style.AppTheme);
            isChecked = false;
        }
    }

    // Set up the dialog options menu that appears
    // when a call has been tapped
    private void openOptionsDialog(final Call call) {
        // Initialize data used for calling the number
        // in specified call
        final String num = "tel:" + call.getNumber();
        final Intent callNumber = new Intent(Intent.ACTION_CALL);
        callNumber.setData(Uri.parse(num));

        // Get custom chat from SharedPreferences
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        customChatID = appPrefs.getInt("chatSelectionID", 0);

        // Initialize the dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.call_actions_title);
        if (customChatID == 0) { // if default (sms)
            alertDialogBuilder.setItems(R.array.call_actions_list_default, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // Android Studio prompted this method after upgrading applications
                            // to target API 27
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(callNumber);
                            break;
                        case 1:
                            sendSMS(call);
                            break;
                        case 2:
                            editCall(call);
                            break;
                        case 3:
                            setReminder(call);
                            break;
                        case 4:
                            deleteCall(call);
                            break;
                        default:
                    }
                }
            });
        } else if (customChatID == 1) {
            alertDialogBuilder.setItems(R.array.call_actions_list_whatsapp, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // Android Studio prompted this method after upgrading applications
                            // to target API 27
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(callNumber);
                            break;
                        case 1:
                            sendWhatsApp(call);
                            break;
                        case 2:
                            editCall(call);
                            break;
                        case 3:
                            setReminder(call);
                            break;
                        case 4:
                            deleteCall(call);
                            break;
                        default:
                    }
                }
            });
        } else if (customChatID == 2) {
            alertDialogBuilder.setItems(R.array.call_actions_list_telegram, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // Android Studio prompted this method after upgrading applications
                            // to target API 27
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(callNumber);
                            break;
                        case 1:
                            sendTelegram(call);
                            break;
                        case 2:
                            editCall(call);
                            break;
                        case 3:
                            setReminder(call);
                            break;
                        case 4:
                            deleteCall(call);
                            break;
                        default:
                    }
                }
            });
        }

        // Show the dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // Ask user to grant phone permission
    public void getPhonePermission(String permission, Integer requestCode){
        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {

        }
    }

    // Ask the user to grant SMS permission
    public void getSMSPermission(String permission, Integer requestCode){
        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {

        }
    }

    // Send an sms to the specified call
    public void sendSMS(Call call){
        Intent sms = new Intent(Intent.ACTION_VIEW);
        sms.setData(Uri.parse("sms:" + call.getNumber()));
        startActivity(sms);
    }

    public void sendWhatsApp(Call call){
        try {
            String id = call.getNumber() + "@s.whatsapp.net";
            Uri uri = Uri.parse("smsto:" + id);
            Intent whatsapp = new Intent(Intent.ACTION_SENDTO, uri);
            whatsapp.setPackage("com.whatsapp");
            startActivity(whatsapp);
        } catch (android.content.ActivityNotFoundException e) {
            Toast toast = Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sendTelegram(Call call){
        try {
            Intent telegram = new Intent(Intent.ACTION_SEND);
            telegram.setType("text/plain");
            telegram.setPackage("org.telegram.messenger");
            startActivity(telegram);
        } catch (android.content.ActivityNotFoundException e) {
            Toast toast = Toast.makeText(this, "Telegram not installed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Edit the call
    public void editCall(Call call){
        Gson passAsGson = new Gson();
        String objToPass = passAsGson.toJson(call);

        Intent passToEditActivity = new Intent(this, EditCall.class);
        passToEditActivity.putExtra("CallToEdit", objToPass);
        startActivity(passToEditActivity);
    }

    // Set a reminder for the call
    public void setReminder(Call call){
        Gson passAsGson = new Gson();
        String objToPass = passAsGson.toJson(call);

        Intent passToReminderActivity = new Intent(this, ReminderActivity.class);
        passToReminderActivity.putExtra("Reminder", objToPass);
        startActivity(passToReminderActivity);
    }

    // Show the time the call was created
    public void showTimestamp(Call call){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.call_actions_viewTimeStamp);
        Date timestamp = call.getTimeCreated();
        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        builder.setMessage("This call was created on " + dateFormat.format(timestamp).toString() + " at " + timeFormat.format(timestamp).toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Delete a phone call from the list and shared preferences.
    // Show a confirm box so user can decide.
    private void deleteCall(Call call) {
        sharedpreferences = this.getSharedPreferences(callListPrefs, Context.MODE_PRIVATE);
        final String key = String.valueOf(call.getTimeCreated());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this call?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(key);
                editor.commit();
                finish();
                startActivity(getIntent());
            }
        }).setNegativeButton(R.string.clearAll_no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Set up the overflow menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (isMSThemeEnabled) {
            inflater.inflate(R.menu.menu2, menu);
            if(isChecked == true){
                menu.getItem(0).setIcon(R.drawable.ic_add_dark);
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_add_black_48dp);
            }
        } else {
            inflater.inflate(R.menu.menu, menu);
            MenuItem themeCheckBox = menu.findItem(R.id.menu_theme_btn);
            menu.getItem(0).setIcon(R.drawable.ic_add_dark);
            // Change the default state of the checkbox depending on
            // the boolean value retried from shared preferences earlier
            themeCheckBox.setChecked(isChecked);
            if(isChecked == true){
                menu.getItem(0).setIcon(R.drawable.ic_add_dark);
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_add_black_48dp);
            }
        }

        // Enable or Disable action bar 'Add New Call'
        // based on FAB setting
        if(isFabChecked){
            menu.getItem(0).setEnabled(false);
            menu.getItem(0).setVisible(false);
        }
        else{
            menu.getItem(0).setEnabled(true);
            menu.getItem(0).setVisible(true);
        }
        return true;
    }

    // Set up menu item actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_new_call_btn:
                addNewCall();
                return true;
            case R.id.menu_theme_btn:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                saveThemeAndRefresh(isChecked);
                return true;
            case R.id.menu_clear_all_btn:
                clearAllCalls();
                return true;
            case R.id.notif_hist_btn:
                goToNotifHist();
                return true;
            case R.id.menu_settings_btn:
                settings();
                return true;
            case R.id.about_btn:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // When the theme checkbox is toggled, save the changed
    // boolean value to shared preferences and refresh
    public void saveThemeAndRefresh(Boolean isDark){
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPrefs.edit();
        editor.putBoolean("themeVal", isDark);
        editor.commit();

        refresh();
    }

    // Clear all calls from shared preferences and refresh
    public void clearAllCalls(){
        sharedpreferences = this.getSharedPreferences(callListPrefs, Context.MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.clearAll_actions_title);
        builder.setPositiveButton(R.string.clearAll_yes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();

                refresh();
            }
        }).setNegativeButton(R.string.clearAll_no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void goToNotifHist(){
        Intent notifHist = new Intent(this, NotificationHistory.class);
        startActivity(notifHist);
    }

    // Refresh the app
    public void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Go to 'Add New Call screen'
    public void addNewCall(){
        Intent addNewCall = new Intent(this, AddNewCall.class);
        startActivity(addNewCall);
    }

    public void settings(){
        Intent settings = new Intent(this, Settings.class);
        startActivity(settings);
    }

    // Go to 'About' screen
    public void about(){
        Intent about = new Intent(this, About.class);
        startActivity(about);
    }

    // Get Call objects from SharedPreferences and sort by key
    public void getCallsFromSharedPreferences(){
        Map<String, ?> allCalls = sharedpreferences.getAll();
        SortedSet<String> keys = new TreeSet<String>(allCalls.keySet());
        for(String key : keys){
            Gson gson = new Gson();
            String json = sharedpreferences.getString(key, "");
            Call call = gson.fromJson(json, Call.class);
            callList.add(call);
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_dialog_title);
        builder.setMessage(R.string.exit_app_prompt);
        builder.setPositiveButton(R.string.clearAll_yes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAndRemoveTask();
            }
        }).setNegativeButton(R.string.clearAll_no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}