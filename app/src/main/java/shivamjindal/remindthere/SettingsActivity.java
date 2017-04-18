package shivamjindal.remindthere;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by shivam on 18/4/17.
 */

public class SettingsActivity extends AppCompatActivity {

    String chosenRingtone;
    Uri dateUri, locationUri;
    TextView dateNotificationTone, dateNotificationToneSelect;
    TextView locationNotificationTone, locationNotificationToneSelect;
    RelativeLayout dateNotificationToneSelector, locationNotificationToneSelector;
    SwitchCompat dateVibrationSwitch, locationVibrationSwitch;
    SharedPreferences sPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dateNotificationTone = (TextView) findViewById(R.id.date_notification_tone);
        dateNotificationToneSelect = (TextView) findViewById(R.id.date_notification_tone_selector);
        dateNotificationToneSelector = (RelativeLayout) findViewById(R.id.date_notification_tone_rl);
        dateVibrationSwitch = (SwitchCompat) findViewById(R.id.date_vibrate_switch);
        locationNotificationTone = (TextView) findViewById(R.id.location_notification_tone);
        locationNotificationToneSelect = (TextView) findViewById(R.id.location_notification_tone_selector);
        locationNotificationToneSelector = (RelativeLayout) findViewById(R.id.location_notification_tone_rl);
        locationVibrationSwitch = (SwitchCompat) findViewById(R.id.location_vibrate_switch);


        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        dateVibrationSwitch.setChecked(sPrefs.getBoolean("DATE_VIBRATION", true));
        locationVibrationSwitch.setChecked(sPrefs.getBoolean("LOCATION_VIBRATION", true));

        //get the uri string which user has selected last time
        //if it's the first time get the default uri
        String notificationToneString = sPrefs.getString("DATE_NOTIFICATION_URI", null);
        if (notificationToneString == null) {
            notificationToneString = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        }
        dateUri = Uri.parse(notificationToneString);
        String ringtoneTitle = RingtoneManager.getRingtone(this, dateUri).getTitle(this);
        dateNotificationTone.setText(ringtoneTitle);


        String notificationToneString2 = sPrefs.getString("LOCATION_NOTIFICATION_URI", null);
        if (notificationToneString2 == null) {
            notificationToneString2 = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        }
        locationUri = Uri.parse(notificationToneString2);
        String ringtoneTitle2 = RingtoneManager.getRingtone(this, locationUri).getTitle(this);
        locationNotificationTone.setText(ringtoneTitle2);


        dateVibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sPrefs.edit();
                editor.putBoolean("DATE_VIBRATION", isChecked);
                editor.apply();
            }
        });


        locationVibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sPrefs.edit();
                editor.putBoolean("LOCATION_VIBRATION", isChecked);
                editor.apply();
            }
        });


        //show a ringtone picker to get user input ringtone
        dateNotificationToneSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                //set default selected ringtone
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, dateUri);
                startActivityForResult(intent, 5);

            }
        });


        locationNotificationToneSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                //set default selected ringtone
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, locationUri);
                startActivityForResult(intent, 6);
            }
        });


    }


    /**
     * Get user selected ringtone from ringtone-picker and save this in shared preferences
     *
     * @param requestCode request code given at time of request
     * @param resultCode  result code
     * @param intent      intent which calls ringtone picker
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) {
                dateUri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (dateUri != null) {
                    this.chosenRingtone = dateUri.toString();
                    String ringtoneTitle = RingtoneManager.getRingtone(this, dateUri).getTitle(this);
                    dateNotificationTone.setText(ringtoneTitle);
                    SharedPreferences.Editor editor = sPrefs.edit();
                    editor.putString("DATE_NOTIFICATION_URI", chosenRingtone);
                    editor.apply();
                } else {
                    this.chosenRingtone = null;
                }
            }

            if (requestCode == 6) {
                locationUri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (locationUri != null) {
                    this.chosenRingtone = locationUri.toString();
                    String ringtoneTitle = RingtoneManager.getRingtone(this, locationUri).getTitle(this);
                    locationNotificationTone.setText(ringtoneTitle);
                    SharedPreferences.Editor editor = sPrefs.edit();
                    editor.putString("LOCATION_NOTIFICATION_URI", chosenRingtone);
                    editor.apply();
                } else {
                    this.chosenRingtone = null;
                }
            }
        }
    }
}
