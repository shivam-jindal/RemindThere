package shivamjindal.remindthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 9/4/17.
 */

public class EditItemActivity extends AppCompatActivity implements TimeReminderDialog.dialogListener, LocationReminderDialog.LocationDialogListener {

    TextView addItemButton;
    EditText title;
    EditText firstTask;
    LinearLayout layoutTasks;
    CheckBox addCheckboxRadio;
    ImageView locationReminderButton, timeReminderButton;
    int categoryId;

    int PLACE_PICKET_REQUEST_CODE = 101;
    Place place;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        addItemButton = (TextView) findViewById(R.id.add_item_button);
        title = (EditText) findViewById(R.id.title_edit_text);
        firstTask = (EditText) findViewById(R.id.first_task);
        layoutTasks = (LinearLayout) findViewById(R.id.tasks_layout);
        addCheckboxRadio = (CheckBox) findViewById(R.id.add_checkboxes_radio);
        locationReminderButton = (ImageView) findViewById(R.id.location_alarm_button);
        timeReminderButton = (ImageView) findViewById(R.id.time_alarm_button);

        categoryId = getIntent().getIntExtra("CATEGORY_ID", 0);
        if (categoryId != 0) {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
            TasksContainer tasksContainer = databaseAdapter.getTaskContainer(categoryId);

            title.setText(tasksContainer.getTitle());

            if (tasksContainer.getTasks().size() > 0) {
                firstTask.setText(tasksContainer.getTasks().get(0).getTaskName());
                addCheckboxRadio.setChecked(tasksContainer.getTasks().get(0).isCheckVisible());
            }

            for (int i = 1; i < tasksContainer.getTasks().size(); i++) {
                EditText newTask = new EditText(this);

                newTask.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutTasks.addView(newTask);
                newTask.setHint("Task " + (i + 1));
                newTask.setText(tasksContainer.getTasks().get(i).getTaskName());
                newTask.requestFocus();
            }
        }


        timeReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("")) {
                    Constants.showToast(getApplicationContext(), "First enter a title to set reminder!");
                } else {
                    TimeReminderDialog reminderDialog = TimeReminderDialog.newInstance(
                            title.getText().toString(),
                            categoryId,
                            EditItemActivity.this);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    reminderDialog.show(fragmentManager, "Set Reminder Dialog");
                }
            }
        });


        locationReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("")) {
                    Constants.showToast(getApplicationContext(), "First enter a title to set reminder!");
                } else {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        Intent intent = builder.build(EditItemActivity.this);
                        startActivityForResult(intent, PLACE_PICKET_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKET_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);

                LocationReminderDialog locationReminderDialog = LocationReminderDialog.newInstance(
                        title.getText().toString(),
                        categoryId,
                        (String) place.getName(),
                        EditItemActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                locationReminderDialog.show(fragmentManager, "Location Reminder Dialog");
                //  startGeofence((String) place.getName(), place.getLatLng(), 100);
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
            if (title.getText().toString().equals("")) {
                Constants.showToast(getApplicationContext(), "First enter a title to save item!");
            } else {
                editItemInDatabase();
                Constants.showSnackbar(MainActivity.mainParentLayout, "Item edited successfully!");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void editItemInDatabase() {
        List<String> tasks = new ArrayList<>();
        for (int i = 0; i < layoutTasks.getChildCount(); i++) {
            tasks.add(((EditText) layoutTasks.getChildAt(i)).getText().toString());
        }
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.insertTask(title.getText().toString(), tasks, categoryId, addCheckboxRadio.isChecked());

        goToMainActivity();
    }


    private void goToMainActivity() {
        startActivity(new Intent(EditItemActivity.this, MainActivity.class));
    }

    @Override
    public void onDialogDismiss() {

    }

    @Override
    public void onLocationDialogDismiss() {
        //AddItemActivity.startGeofence((String) place.getName(), place.getLatLng(), 100);
    }
}
