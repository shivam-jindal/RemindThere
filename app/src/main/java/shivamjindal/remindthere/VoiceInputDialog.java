package shivamjindal.remindthere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by shivam on 18/4/17.
 */

public class VoiceInputDialog extends DialogFragment {

    private static VoiceInputDialogListener mdialogListener;
    String titleText;
    Button cancelButton, addButton;
    EditText taskText;

    public static VoiceInputDialog newInstance(String titleText, VoiceInputDialogListener listener) {
        Bundle args = new Bundle();
        args.putString("TITLE_TEXT", titleText);
        VoiceInputDialog fragment = new VoiceInputDialog();
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
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_input_dialog, container, false);
        taskText = (EditText) view.findViewById(R.id.task_input);
        cancelButton = (Button) view.findViewById(R.id.cancel_action);
        addButton = (Button) view.findViewById(R.id.add_task);

        taskText.setText(titleText);

        //Dismiss the dialog when clicked on Cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss(getDialog());
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialogListener.onVoiceInputDialogDismiss();
            }
        });

        return view;
    }


    interface VoiceInputDialogListener {
        void onVoiceInputDialogDismiss();
    }
}
