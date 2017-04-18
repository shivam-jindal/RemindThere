package shivamjindal.remindthere;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivam on 9/4/17.
 */

public class AddItemActivity extends AppCompatActivity
        implements TimeReminderDialog.dialogListener,
        LocationReminderDialog.LocationDialogListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {

    final String TAG = "Add item activity";
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    TextView addItemButton;
    EditText title;
    EditText firstTask;
    LinearLayout layoutTasks;
    CheckBox addCheckboxRadio;
    ImageView locationReminderButton, timeReminderButton;
    int categoryId;
    int PLACE_PICKET_REQUEST_CODE = 111;
    Place place;


    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    private final int REQ_PERMISSION = 11;


    public AddItemActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        addItemButton = (TextView) findViewById(R.id.add_item_button);
        title = (EditText) findViewById(R.id.title_edit_text);
        firstTask = (EditText) findViewById(R.id.first_task);
        layoutTasks = (LinearLayout) findViewById(R.id.tasks_layout);
        addCheckboxRadio = (CheckBox) findViewById(R.id.add_checkboxes_radio);
        locationReminderButton = (ImageView) findViewById(R.id.location_alarm_button);
        timeReminderButton = (ImageView) findViewById(R.id.time_alarm_button);
        categoryId = Constants.getNewCategoryID(getApplicationContext());

        title.requestFocus();


        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newTask = new EditText(AddItemActivity.this);

                newTask.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutTasks.addView(newTask);
                newTask.setHint("Task " + layoutTasks.getChildCount());
                newTask.requestFocus();
            }
        });


        timeReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("")) {
                    Constants.showToast(getApplicationContext(), "First enter a title to set reminder!");
                } else {
                    TimeReminderDialog reminderDialog = TimeReminderDialog.newInstance(
                            title.getText().toString(),
                            categoryId,
                            AddItemActivity.this);
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
                        Intent intent = builder.build(AddItemActivity.this);
                        startActivityForResult(intent, PLACE_PICKET_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        createGoogleApi();
    }


    private void createGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKET_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                String details = (String) place.getAddress();
                LatLng placeLatLng = place.getLatLng();
                String placeLatitude = String.valueOf(placeLatLng.latitude);
                String placeLongitude = String.valueOf(placeLatLng.longitude);
                Log.i("place details : ", details);


                LocationReminderDialog locationReminderDialog = LocationReminderDialog.newInstance(
                        title.getText().toString(),
                        categoryId,
                        (String) place.getName(),
                        AddItemActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                locationReminderDialog.show(fragmentManager, "Location Reminder Dialog");

                // startGeofence((String) place.getName(), place.getLatLng(), 100);
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
                Constants.showToast(getApplicationContext(), "First enter a title to set reminder!");
            } else {
                saveItemInDatabase();
                Constants.showSnackbar(MainActivity.mainParentLayout, "Item added successfully!");
            }
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


    private void goToMainActivity() {
        startActivity(new Intent(AddItemActivity.this, MainActivity.class));
    }

    @Override
    public void onDialogDismiss() {

    }

    @Override
    public void onLocationDialogDismiss() {
        startGeofence((String) place.getName(), place.getLatLng(), 100);
    }


    public Geofence createGeofence(String id, LatLng latLng, float radius) {
        return new Geofence.Builder()
                .setRequestId(id)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .build();
    }


    @NonNull
    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(geofence)
                .build();
    }


    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        } else askPermission();
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        //   textLat.setText("Lat: " + location.getLatitude());
        //    textLong.setText("Long: " + location.getLongitude());
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }


    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if (status.isSuccess()) {
            Constants.showToast(this, "geofence added successfully");
        } else {
            Log.e(TAG, "onResult: " + status);
            // inform about fail
        }
    }


    public void startGeofence(String titleID, LatLng latLng, float radius) {
        Log.i(TAG, "startGeofence()");
        Geofence geofence = createGeofence(titleID, latLng, radius);
        GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
        addGeofence(geofenceRequest);
    }


}
