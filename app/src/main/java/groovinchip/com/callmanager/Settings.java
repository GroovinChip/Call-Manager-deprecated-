package groovinchip.com.callmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Settings extends AppCompatActivity implements OnItemSelectedListener{

    SharedPreferences appPrefs;
    public final String APP_PREFS = "appPrefs";
    private boolean isChecked = false;
    boolean isDark;
    boolean isFabChecked;
    boolean isMSSwitchChecked;
    Spinner chatPicker;
    Switch themeSwitch;
    Switch fabSwitch;
    Switch msThemeSwitch;
    String infoMsg;
    int chatSelection; // for saving
    int currentChat; // for displaying

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_settings);
        openAppPrefs();
        setChatSpinner();
        themeSwitch = (Switch)findViewById(R.id.themeSwitch);
        fabSwitch = (Switch)findViewById(R.id.fabSwitch);
        msThemeSwitch = findViewById(R.id.msThemeSwitch);

        isFabChecked = appPrefs.getBoolean("fabChecked", true);
        isMSSwitchChecked = appPrefs.getBoolean("msSwitchTheme", false);

        // Set theme switch
        if (isChecked == true) {
            themeSwitch.setText("Dark");
            themeSwitch.setChecked(isChecked);
        } else {
            themeSwitch.setText("Light");
            themeSwitch.setChecked(isChecked);
        }

        // Set FAB switch
        if (isFabChecked == true) {
            fabSwitch.setText("Yes");
            fabSwitch.setChecked(isFabChecked);
        } else {
            fabSwitch.setText("No");
            fabSwitch.setChecked(isFabChecked);
        }

        // Set mainscreen theme checkbox switch
        if (isMSSwitchChecked == false) {
            msThemeSwitch.setText("Yes");
            msThemeSwitch.setChecked(true);
        } else {
            msThemeSwitch.setText("No");
            msThemeSwitch.setChecked(false);
        }
    }

    // Set the app theme based on boolean value from a
    // separate shared preferences file
    private void setAppTheme() {
        openAppPrefs();
        Boolean isDark = appPrefs.getBoolean("themeVal", false);
        if (isDark == true) {
            setTheme(R.style.AppThemeDark);
            isChecked = true;
        } else {
            setTheme(R.style.AppTheme);
            isChecked = false;
        }
    }

    public void saveThemeAndRefresh(Boolean isDark){
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPrefs.edit();
        editor.putBoolean("themeVal", isDark);
        editor.commit();

        refresh();
    }

    public void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void openAppPrefs(){
        appPrefs = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public void setChatSpinner(){
        chatPicker = (Spinner)findViewById(R.id.chatPicker);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.custom_chats, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chatPicker.setAdapter(adapter);

        currentChat = appPrefs.getInt("chatSelectionID", 0);
        chatPicker.setSelection(currentChat);
        chatPicker.setOnItemSelectedListener(this);
    }

    public void onThemeSwitchClick(View v) {
        if (themeSwitch.isChecked()) {
            themeSwitch.setText("Dark");
            isDark = true;
            saveThemeAndRefresh(isDark);
        } else {
            themeSwitch.setText("Light");
            isDark = false;
            saveThemeAndRefresh(isDark);
        }
    }

    public void onMSThemeSwitchClick(View v){
        SharedPreferences.Editor editor = appPrefs.edit();
        if (msThemeSwitch.isChecked()) {
            msThemeSwitch.setText("Yes");
            isMSSwitchChecked = false;
            editor.putBoolean("msSwitchTheme", isMSSwitchChecked);
            editor.commit();
        } else {
            msThemeSwitch.setText("No");
            isMSSwitchChecked = true;
            editor.putBoolean("msSwitchTheme", isMSSwitchChecked);
            editor.commit();
        }
    }

    public void onFabSwitchClick(View v) {
        SharedPreferences.Editor editor = appPrefs.edit();
        if (fabSwitch.isChecked()) {
            fabSwitch.setText("Yes");
            isFabChecked = true;
            editor.putBoolean("fabChecked", isFabChecked);
            editor.commit();
        } else {
            fabSwitch.setText("No");
            isFabChecked = false;
            editor.putBoolean("fabChecked", isFabChecked);
            editor.commit();
        }
    }

    // Do something when a spinner item is chosen
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (pos) {
            case 0:
                infoMsg = "SMS option chosen";
                break;
            case 1:
                infoMsg = "WhatsApp option chosen";
                break;
            case 2:
                infoMsg = "Telegram option chosen";
                break;
        }
        //createUserInfoMessage(infoMsg);
        saveChatChange(pos);
    }

    // Create a toast message to the user to show which settings option they have chosen
    public void createUserInfoMessage(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    // Save user chat preference to SharedPreferences
    public void saveChatChange(int position){
        chatSelection = position;
        openAppPrefs();
        SharedPreferences.Editor editor = appPrefs.edit();
        editor.putInt("chatSelectionID", chatSelection);
        editor.commit();
    }

    // Ensure theme refreshes on navbar back button press
    @Override
    public void onBackPressed(){
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }

    // Required
    public void onNothingSelected(AdapterView<?> parent){
        // Do nothing
    }
}
