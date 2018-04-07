package groovinchip.com.callmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends AppCompatActivity {

    SharedPreferences appPrefs;
    public final String APP_PREFS = "appPrefs";
    private boolean isChecked = false;
    TextView appVersion;
    /*Button changelogBtn;*/
    TextView github;
    TextView xdaDevB;
    TextView xdaProfile;
    TextView telegramChat;
    String telegramChatURL = "<a href='https://t.me/joinchat/GhKIr0rAi47d8buNJMoi4A'> Telegram Chat </a>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPrefs = getSharedPreferences("appPrefs", Context.MODE_PRIVATE);
        setAppTheme();
        setContentView(R.layout.activity_about);

        appVersion = findViewById(R.id.versionName);
        appVersion.setText("Verision " + BuildConfig.VERSION_NAME);

        /*changelogBtn = findViewById(R.id.changelogBtn);
        changelogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
                builder.setTitle(R.string.changelog_dialog_title);
                builder.setView(R.layout.changelog);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });*/

        if (Build.VERSION.SDK_INT > 24) {
            github = findViewById(R.id.githubLink);
            github.setClickable(true);
            github.setMovementMethod(LinkMovementMethod.getInstance());
            String repoURL = "<a href='https://github.com/GroovinChip/Call-Manager'> GitHub Repo </a>";
            github.setText(Html.fromHtml(repoURL, 0));

            xdaDevB = findViewById(R.id.XDA_DevB_link);
            xdaDevB.setClickable(true);
            xdaDevB.setMovementMethod(LinkMovementMethod.getInstance());
            String forumURL = "<a href='https://forum.xda-developers.com/android/apps-games/app-call-list-t3684262'> XDA DevB App Page </a>";
            xdaDevB.setText(Html.fromHtml(forumURL, 0));

            xdaProfile = findViewById(R.id.XDA_Profile);
            xdaProfile.setClickable(true);
            xdaProfile.setMovementMethod(LinkMovementMethod.getInstance());
            String profileURL = "<a href='https://forum.xda-developers.com/member.php?u=7646108'> XDA Member Profile </a>";
            xdaProfile.setText(Html.fromHtml(profileURL, 0));

            telegramChat = findViewById(R.id.TelegramChat);
            telegramChat.setClickable(true);
            telegramChat.setMovementMethod(LinkMovementMethod.getInstance());
            telegramChat.setText((Html.fromHtml(telegramChatURL, 0)));
        } else if (Build.VERSION.SDK_INT <= 22) {
            github = findViewById(R.id.githubLink);
            github.setClickable(true);
            github.setMovementMethod(LinkMovementMethod.getInstance());
            String repoURL = "<a href='https://github.com/GroovinChip/Call-Manager'> GitHub Repo </a>";
            github.setText(Html.fromHtml(repoURL));

            xdaDevB = findViewById(R.id.XDA_DevB_link);
            xdaDevB.setClickable(true);
            xdaDevB.setMovementMethod(LinkMovementMethod.getInstance());
            String forumURL = "<a href='https://forum.xda-developers.com/android/apps-games/app-call-list-t3684262'> XDA DevB App Page </a>";
            xdaDevB.setText(Html.fromHtml(forumURL));

            xdaProfile = findViewById(R.id.XDA_Profile);
            xdaProfile.setClickable(true);
            xdaProfile.setMovementMethod(LinkMovementMethod.getInstance());
            String profileURL = "<a href='https://forum.xda-developers.com/member.php?u=7646108'> XDA Member Profile </a>";
            xdaProfile.setText(Html.fromHtml(profileURL));

            telegramChat = findViewById(R.id.TelegramChat);
            telegramChat.setClickable(true);
            telegramChat.setMovementMethod(LinkMovementMethod.getInstance());
            telegramChat.setText((Html.fromHtml(telegramChatURL)));
        }
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
