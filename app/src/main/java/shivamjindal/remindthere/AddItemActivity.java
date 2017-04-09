package shivamjindal.remindthere;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivam on 9/4/17.
 */

public class AddItemActivity extends AppCompatActivity implements TimeReminderDialog.dialogListener {

    TextView addItemButton;
    EditText title;
    EditText firstTask;
    LinearLayout layoutTasks;
    SharedPreferences sharedPreferences;
    RadioButton addCheckboxRadio;
    ImageView locationReminderButton, timeReminderButton;
    int categoryId;
    int PLACE_PICKET_REQUEST_CODE = 11;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        addItemButton = (TextView) findViewById(R.id.add_item_button);
        title = (EditText) findViewById(R.id.title_edit_text);
        firstTask = (EditText) findViewById(R.id.first_task);
        layoutTasks = (LinearLayout) findViewById(R.id.tasks_layout);
        addCheckboxRadio = (RadioButton) findViewById(R.id.add_checkboxes_radio);
        locationReminderButton = (ImageView) findViewById(R.id.location_alarm_button);
        timeReminderButton = (ImageView) findViewById(R.id.time_alarm_button);
        categoryId = getNewCategoryID();


        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newTask = new EditText(AddItemActivity.this);

                newTask.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutTasks.addView(newTask);
                newTask.requestFocus();
            }
        });


        timeReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeReminderDialog reminderDialog = TimeReminderDialog.newInstance(
                        title.getText().toString(),
                        categoryId,
                        AddItemActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                reminderDialog.show(fragmentManager, "Set Reminder Dialog");
            }
        });


        locationReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    Intent intent = builder.build(AddItemActivity.this);
                    startActivityForResult(intent, PLACE_PICKET_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKET_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String details = (String) place.getAddress();
                LatLng placeLatLng = place.getLatLng();
                String placeLatitude = String.valueOf(placeLatLng.latitude);
                String placeLongitude = String.valueOf(placeLatLng.longitude);
                Log.i("place details : ", details);

                title.setText(place.getName());

                /*Intent intent = new Intent(this, QuestionsActivity.class);
                intent.putExtra("ADDRESS", (String) place.getName());
                intent.putExtra("LATITUDE", String.valueOf(placeLatLng.latitude));
                intent.putExtra("LONGITUDE", String.valueOf(placeLatLng.longitude));
                startActivity(intent);
                */
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_item) {
            saveItemInDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveItemInDatabase() {
        List<String> tasks = new ArrayList<>();
        for (int i = 0; i < layoutTasks.getChildCount(); i++) {
            tasks.add(((EditText) layoutTasks.getChildAt(i)).getText().toString());
        }
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.insertTask(title.getText().toString(), tasks, categoryId, addCheckboxRadio.isChecked());

        goToMainActivity();
    }


    private int getNewCategoryID() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        );
        int newCategoryId = sharedPreferences.getInt("NEW_CATEGORY_ID", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("NEW_CATEGORY_ID", (newCategoryId + 1));
        editor.apply();
        return newCategoryId;
    }


    private void goToMainActivity() {
        startActivity(new Intent(AddItemActivity.this, MainActivity.class));
    }

    @Override
    public void onDialogDismiss() {

    }
}
