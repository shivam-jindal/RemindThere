package shivamjindal.remindthere;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.Manifest.permission_group.LOCATION;

/**
 * Created by shivam on 9/4/17.
 */

public class DatabaseAdapter {

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    /**
     * Constructor
     *
     * @param context Context object
     */
    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }



    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mainDatabase";
        private static final int DATABASE_VERSION = 1;
        private final Context context;

        private static final String TASK_TABLE = "taskTable";
        private static final String UID = "_id";
        private static final String TASK_TEXT = "taskText";
        private static final String TASK_TITLE = "taskTitle";
        private static final String CHECK_VISIBLE = "checkVisible";
        private static final String CHECKED = "checked";
        private static final String CATEGORY_ID = "categoryID";

        private static final String DATE_REMINDER_TABLE = "dateReminderTable";
        private static final String REMINDER_DATE = "reminderDate";
        private static final String REMINDER_TIME = "reminderTime";

        private static final String LOCATION_REMINDER_TABLE = "locationReminderTable";
        private static final String REMINDER_LOCATION = "reminderLocation";


        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TASK_TABLE = "CREATE TABLE IF NOT EXISTS " + TASK_TABLE + "("
                    + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CATEGORY_ID + "INTEGER AUTOINCREMENT"
                    + TASK_TEXT + " VARCHAR(555), "
                    + TASK_TITLE + " VARCHAR(255), "
                    + CHECK_VISIBLE + " BOOLEAN DEFAULT 1, "
                    + CHECKED + " BOOLEAN DEFAULT 0 );";

            try {
                db.execSQL(CREATE_TASK_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            String CREATE_DATE_REMINDER_TABLE = "CREATE TABLE IF NOT EXISTS " + DATE_REMINDER_TABLE + "("
                    + CATEGORY_ID + " INTEGER UNIQUE, "
                    + REMINDER_DATE + " VARCHAR(255), "
                    + REMINDER_TIME + " VARCHAR(255) );";

            try {
                db.execSQL(CREATE_DATE_REMINDER_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            String CREATE_LOCATION_REMINDER_TABLE = "CREATE TABLE IF NOT EXISTS " + LOCATION_REMINDER_TABLE + "("
                    + CATEGORY_ID + " INTEGER UNIQUE, "
                    + REMINDER_LOCATION + " VARCHAR(255) );";

            try {
                db.execSQL(CREATE_LOCATION_REMINDER_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TASK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.DATE_REMINDER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.LOCATION_REMINDER_TABLE);
            onCreate(db);
        }
    }
}
