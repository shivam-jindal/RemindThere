package shivamjindal.remindthere;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by shivam on 9/4/17.
 */

class Constants {


    /**
     * Function to show snack-bar
     *
     * @param parentView   Parent view in which snack bar is to be shown
     * @param snackbarText Text which is to be displayed in snack bar
     */
    static void showSnackbar(View parentView, String snackbarText) {
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
    static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }



    static void fadeInOutAnimation(final RelativeLayout relativeLayout, final Context context) {
        final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        fadeOut.setDuration(500);
        fadeIn.setDuration(1500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                relativeLayout.setVisibility(View.VISIBLE);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                relativeLayout.startAnimation(fadeOut);
            }
        });
        relativeLayout.startAnimation(fadeIn);
    }


    static void fadeInAnimation(final RelativeLayout relativeLayout, final Context context) {
        final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fadeIn.setDuration(1500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
        relativeLayout.startAnimation(fadeIn);
    }


    static int getNewCategoryID(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context
        );
        int newCategoryId = sharedPreferences.getInt("NEW_CATEGORY_ID", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("NEW_CATEGORY_ID", (newCategoryId + 1));
        editor.apply();
        return newCategoryId;
    }


    static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
