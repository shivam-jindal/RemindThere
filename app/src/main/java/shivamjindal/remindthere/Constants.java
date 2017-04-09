package shivamjindal.remindthere;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by shivam on 9/4/17.
 */

public class Constants {


    /**
     * Function to show snack-bar
     *
     * @param parentView   Parent view in which snack bar is to be shown
     * @param snackbarText Text which is to be displayed in snack bar
     */
    public static void showSnackbar(View parentView, String snackbarText) {
        Snackbar snack = Snackbar.make(parentView, snackbarText, Snackbar.LENGTH_SHORT)
                //.setActionTextColor()
                .setActionTextColor(Color.YELLOW)
                .setDuration(2000);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            tv.setGravity(Gravity.CENTER_HORIZONTAL);

        snack.show();
    }


    /**
     * Function to show a toast message
     *
     * @param text    text to be shown in toast
     * @param context Context object
     */
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}