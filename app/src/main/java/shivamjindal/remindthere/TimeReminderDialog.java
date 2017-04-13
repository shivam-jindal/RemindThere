package shivamjindal.remindthere;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shivam on 9/4/17.
 */

public class TimeReminderDialog extends DialogFragment {

    int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    String titleText;
    int categoryId;
    public static AlarmManager alarmManager;
    private static dialogListener mdialogListener;
    String reminderDate, reminderTime;


    /**
     * Creates new instance of reminder dialog
     * notificationText, id and listener are passed as arguments
     * so that dialog may get these values when required
     *
     * @param titleText  text of clicked notification
     * @param categoryId id of clicked notification
     * @param listener   listener to handle dismiss events
     * @return new instance of ReminderDialog
     */
    public static TimeReminderDialog newInstance(String titleText, int categoryId, dialogListener listener) {
        Bundle args = new Bundle();
        args.putString("TITLE_TEXT", titleText);
        args.putInt("CATEGORY_ID", categoryId);
        TimeReminderDialog fragment = new TimeReminderDialog();
        fragment.setArguments(args);
        mdialogListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            titleText = args.getString("TITLE_TEXT");
            categoryId = args.getInt("CATEGORY_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_dialog, container, false);
        final EditText dateInput = (EditText) view.findViewById(R.id.date_input);
        final EditText timeInput = (EditText) view.findViewById(R.id.time_input);
        Button setReminderButton = (Button) view.findViewById(R.id.set_reminder);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_action);

        final int currentYear, currentMonth, currentDay, currentHour, currentMinute;
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);

        selectedYear = currentYear;
        selectedMonth = currentMonth;
        selectedDay = currentDay;
        selectedHour = currentHour;
        selectedMinute = currentMinute;

        dateInput.setKeyListener(null);
        timeInput.setKeyListener(null);

        //initialize with current date-time
        reminderDate = DateFormat.getDateInstance().format(new Date());
        reminderTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        dateInput.setText(reminderDate);
        timeInput.setText(reminderTime);

        dateInput.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                selectedYear = year;
                                selectedMonth = monthOfYear;
                                selectedDay = dayOfMonth;

                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                cal.set(Calendar.MONTH, monthOfYear);
                                reminderDate = DateFormat.getDateInstance().format(cal.getTime());
                                dateInput.setText(reminderDate);

                            }
                        }, currentYear, currentMonth, currentDay);
                dpd.show();
            }
        });


        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                selectedHour = hourOfDay;
                                selectedMinute = minute;

                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);

                                reminderTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime());
                                timeInput.setText(reminderTime);
                            }

                        }, currentHour, currentMinute, false);
                tpd.show();
            }
        });


        //Dismiss the dialog when clicked on Cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss(getDialog());
            }
        });


        //When clicked on save, get the entered date-time
        //Check if it is valid, if it is then set alarm for that date-time
        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar current = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);

                if (calendar.compareTo(current) <= 0) {
                    //The set Date/Time already passed
                    Toast.makeText(getActivity(),
                            "Invalid Date/Time",
                            Toast.LENGTH_SHORT).show();
                } else {
                    setAlarm(calendar);
                }
            }
        });
        return view;
    }


    /**
     * Create a pending intent which has details(text,id) of notification and set the alarm
     * insert this in database and dismiss the dialog
     *
     * @param targetCal Calendar object having time to be set as reminder
     */
    private void setAlarm(Calendar targetCal) {
        alarmManager = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("TITLE_TEXT", titleText);
        intent.putExtra("CATEGORY_ID", categoryId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), categoryId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(getActivity(), "set successfully", Toast.LENGTH_LONG).show();
        insertReminderInDatabase();
        mdialogListener.onDialogDismiss();
        onDismiss(getDialog());
    }


    /**
     * Inserts new reminder in offline database
     */
    private void insertReminderInDatabase() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.insertTimeReminder(categoryId, reminderDate, reminderTime);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    interface dialogListener {
        void onDialogDismiss();
    }
}
