package shivamjindal.remindthere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

/**
 * Created by shivam on 9/4/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    Context context;
    SharedPreferences sPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String notificationText = intent.getExtras().getString("TITLE_TEXT");
        int notificationId = intent.getExtras().getInt("CATEGORY_ID");
        sPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        Constants.deleteTimeReminderFromDB(context, notificationId);

        createNotification(notificationId, R.drawable.app_icon_svg, "RemindThere Reminder", notificationText);
    }


    private void createNotification(int nId, int iconRes, String title, String body) {
        Drawable appIcon = ContextCompat.getDrawable(context, R.drawable.app_icon_svg);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri dateUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String notificationTone = sPrefs.getString("DATE_NOTIFICATION_URI", null);
        if (notificationTone != null) {
            dateUri = Uri.parse(notificationTone);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(iconRes)
                .setLargeIcon(Constants.drawableToBitmap(appIcon))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setSound(dateUri)
                .setGroup("new group")
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        if (!sPrefs.getBoolean("DATE_VIBRATION", true)) {
            notificationBuilder.setVibrate(null);
        } else {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(nId, notification);
    }


}
