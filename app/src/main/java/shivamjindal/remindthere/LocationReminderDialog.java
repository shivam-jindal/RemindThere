package shivamjindal.remindthere;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by shivam on 10/4/17.
 */

public class LocationReminderDialog extends DialogFragment {

    int categoryId;
    String titleText, location;
    private static LocationDialogListener listener;


    public static LocationReminderDialog newInstance(String titleText, int categoryId,String location, LocationDialogListener mlistener) {
        Bundle args = new Bundle();
        args.putString("TITLE_TEXT", titleText);
        args.putInt("CATEGORY_ID", categoryId);
        args.putString("LOCATION", location);
        LocationReminderDialog fragment = new LocationReminderDialog();
        fragment.setArguments(args);
        listener = mlistener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get values from args
        Bundle args = getArguments();
        if (args != null) {
            titleText = args.getString("TITLE_TEXT");
            categoryId = args.getInt("CATEGORY_ID");
            location = args.getString("LOCATION");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_reminder_dialog, container, false);
        final TextView titleTextView = (TextView) view.findViewById(R.id.title_text);
        final TextView locationTextView = (TextView) view.findViewById(R.id.selected_location);
        Button addReminderButton = (Button) view.findViewById(R.id.add_reminder);
        Button discardButton = (Button) view.findViewById(R.id.discard_action);

        titleTextView.setText(titleText);
        locationTextView.setText(location);

        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLocationDialogDismiss();
                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
                databaseAdapter.insertLocationReminder(categoryId, location);
                onDismiss(getDialog());
                Constants.showToast(getContext(), "Set successfully!");
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss(getDialog());
            }
        });

        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    interface LocationDialogListener {
        void onLocationDialogDismiss();
    }

}
