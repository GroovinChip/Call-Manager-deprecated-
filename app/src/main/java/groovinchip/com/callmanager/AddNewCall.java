package groovinchip.com.callmanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Date;

public class AddNewCall extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String callListPrefs = "callListPrefs";
    public final String APP_PREFS = "appPrefs";
    public static final int CONTACT_PICKER = 1;
    public static final int READ_CONTACTS = 0x1;
    private boolean isChecked = false;
    SharedPreferences appPrefs;
    Date timeCreated;
    EditText nameField;
    EditText numField;
    EditText descField;
    Button saveNewCallBtn;
    Button cancelNewCallBtn;
    Button contactPicker;
    ImageView nameImage;
    ImageView phoneImage;
    ImageView descriptionImage;
    ImageView contactPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPrefs = getSharedPreferences("appPrefs", Context.MODE_PRIVATE);
        setAppTheme();
        setContentView(R.layout.activity_add_new_call);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);

        // Declare components
        nameField = findViewById(R.id.ANC_name_field);
        numField = findViewById(R.id.ANC_num_field);
        descField = findViewById(R.id.ANC_desc_field);
        descField.setSingleLine(false);
        /*saveNewCallBtn = (Button)findViewById(R.id.ANC_save_btn);
        cancelNewCallBtn = (Button)findViewById(R.id.ANC_cancel_btn);*/
        nameImage = findViewById(R.id.nameImage);
        phoneImage = findViewById(R.id.phoneImage);
        descriptionImage = findViewById(R.id.descriptionImage);
        contactPhoto = findViewById(R.id.contactView);
        contactPicker = findViewById(R.id.contactsBtn);
        sharedpreferences = getSharedPreferences(callListPrefs, Context.MODE_PRIVATE);

        // Set correct icon colors for dark theme
        if(isChecked == true){
            contactPicker.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pick_contact_light, 0, 0, 0);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_dark);
            nameImage.setImageResource(R.drawable.ic_name_dark);
            phoneImage.setImageResource(R.drawable.ic_phone_dark);
            descriptionImage.setImageResource(R.drawable.ic_comment_dark);
        }

        // Format phone number field for user's country
        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        numField.addTextChangedListener(new PhoneNumberFormattingTextWatcher(locale));

        askForContactPermission(Manifest.permission.READ_CONTACTS, READ_CONTACTS);

        contactPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

        // When 'save' button is clicked, create new 'Call'
        // object and save object to Stack
        /*saveNewCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasValidInput = createNewCall(nameField.getText().toString(),
                        numField.getText().toString(), descField.getText().toString());
                if(hasValidInput == true) {
                    goToMain(view);
                    finish();
                }else{

                }
            }
        });

        // When 'cancel' button is clicked, go back to home screen
        cancelNewCallBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                goToMain(view);
                finish();
            }
        });*/
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

        ContentResolver cr = getContentResolver();
        Uri uri = data.getData();
        Cursor cur = cr.query(uri, null, null, null, null);
        cur.moveToFirst();

        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        nameField.setText(name);
        String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        // Get the contact picture and convert to bitmap
        Bitmap bitmap;
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photoUri);
        bitmap = BitmapFactory.decodeStream(input);

        numField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        numField.setText(number);
        numField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        contactPhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        contactPhoto.setImageBitmap(bitmap);
        Log.i("Logger", "Contact Added Successfully");
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
        inflater.inflate(R.menu.add_new_call_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.anc_menu_save:
                boolean hasValidInput = createNewCall(nameField.getText().toString(),
                        numField.getText().toString(), descField.getText().toString());
                if(hasValidInput == true) {
                    goToMain();
                    finish();
                }else{

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Create the new Call object. Ensure that the phone number has the proper formatting.
    public boolean createNewCall(String name, String number, String description){
        Call newCall = new Call();
        timeCreated = new Date();

        if(name.equals("") || number.equals("")){
            Toast.makeText(this, "Required field(s) cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            // Get the user's country and format the
            // phone number accordingly
            String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
            String formattedNumber = PhoneNumberUtils.formatNumber(number, locale);

            // Set the details of the new call
            newCall.setName(name);
            newCall.setNumber(formattedNumber);
            newCall.setDescription(description);
            newCall.setTimeCreated(timeCreated);

            //addToSharedPrefs(newCall, newCall.getName());
            addToSharedPrefs(newCall);
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
