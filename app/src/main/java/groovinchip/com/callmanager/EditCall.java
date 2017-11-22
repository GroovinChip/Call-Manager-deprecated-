package groovinchip.com.callmanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Date;

public class EditCall extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences appPrefs;
    public static final int CONTACT_PICKER = 1;
    public static final int READ_CONTACTS = 0x1;
    public static final String callListPrefs = "callListPrefs";
    public final String APP_PREFS = "appPrefs";
    public static String target;
    private boolean isChecked = false;
    EditText editNameField;
    EditText editPhoneField;
    EditText editDescField;
    Button saveEdit;
    Button cancelEdit;
    Button contactPicker;
    Call callToEdit;
    Date timeCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_edit_call);

        target = getIntent().getStringExtra("CallToEdit");
        Gson gson = new Gson();
        callToEdit = gson.fromJson(target, Call.class);

        // Instantiate components
        editNameField = (EditText)findViewById(R.id.EC_name_field);
        editPhoneField =(EditText)findViewById(R.id.EC_num_field);
        editDescField = (EditText)findViewById(R.id.EC_desc_field);
        editDescField.setSingleLine(false);
        saveEdit = (Button)findViewById(R.id.EC_save_btn);
        cancelEdit = (Button)findViewById(R.id.EC_cancel_btn);
        sharedpreferences = getSharedPreferences(callListPrefs, Context.MODE_PRIVATE);
        editPhoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        contactPicker = (Button) findViewById(R.id.contactsBtnEdit);

        // Set correct icon color for dark theme
        if(isChecked == true){
            contactPicker.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pick_contact_light, 0, 0, 0);
        }

        // Set field values
        editNameField.setText(callToEdit.getName());
        editPhoneField.setText(callToEdit.getNumber());
        editDescField.setText(callToEdit.getDescription());

        // Format phone number field for user's country
        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        editPhoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher(locale));

        contactPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

        // When 'save' button is clicked, create new 'Call'
        // object and save object to Stack
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasValidInput = saveEditedCall(editNameField.getText().toString(),
                        editPhoneField.getText().toString(), editDescField.getText().toString());
                if(hasValidInput == true) {
                    goToMain(view);
                    finish();
                }else{

                }
            }
        });

        // When 'cancel' button is clicked, go back to home screen
        cancelEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                goToMain(view);
                finish();
            }
        });
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

    // Start the contacts intent
    public void pickContact(){
        Intent contacts = new Intent(Intent.ACTION_PICK);
        contacts.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(contacts, CONTACT_PICKER);
    }

    // If picking a contact works, send the contact data to contactPicked()
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CONTACT_PICKER:
                    contactPicked(data);
                    break;
            }
        } else{
            Log.e("AddNewCall", "Failed to pick contact");
        }
    }

    // Retrieve the contact data and set name and number to the appropriate fields
    private void contactPicked(Intent data){
        askForContactPermission(Manifest.permission.READ_CONTACTS, READ_CONTACTS);

        ContentResolver cr = getContentResolver();
        Uri uri = data.getData();
        Cursor cur = cr.query(uri, null, null, null);
        cur.moveToFirst();

        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        editNameField.setText(name);
        String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        editPhoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        editPhoneField.setText(number);
        editPhoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        Log.i("AddNewCall", number);
    }

    // Request Contacts permission
    public void askForContactPermission(String permission, Integer requestCode){
        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_call_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ec_menu_cancel:
                goToMain();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Create the new Call object. Ensure that the phone number has the proper formatting.
    public boolean saveEditedCall(String name, String number, String description){
        Call editedCall = new Call();
        timeCreated = callToEdit.getTimeCreated();

        if(name.equals("") || number.equals("")){
            Toast.makeText(this, "Required field(s) cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            // Get the user's country and format the
            // phone number accordingly
            String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
            String formattedNumber = PhoneNumberUtils.formatNumber(number, locale);

            // Set the details of the edited call
            editedCall.setName(name);
            editedCall.setNumber(formattedNumber);
            editedCall.setDescription(description);
            editedCall.setTimeCreated(timeCreated);

            removeOldCall(callToEdit);
            addToSharedPrefs(editedCall);
            return true;
        }
    }

    // Check if a specific character in a split phone number is numeric
    public boolean isNumeric(String str){
        switch (str.charAt(0)){
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            default:
                return false;
        }
    }

    // Remove the old call from SharedPreferences
    public void removeOldCall(Call oldCall){
        sharedpreferences = this.getSharedPreferences(callListPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        final String key = String.valueOf(oldCall.getTimeCreated());
        editor.remove(key);
        editor.commit();
    }

    // Save new call to SharedPreferences
    public void addToSharedPrefs(Call call){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String callObjGson = gson.toJson(call);
        editor.putString(timeCreated.toString(), callObjGson);
        editor.commit();
    }

    // Go back to main screen when user click 'Cancel' button
    public void goToMain(View view){
        Intent goToMain = new Intent(view.getContext(), MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    // Go back to main screen when user click 'Cancel' menu item
    public void goToMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
