package shivamjindal.remindthere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import shivamjindal.remindthere.models.Task;
import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 9/4/17.
 */

class DatabaseAdapter {

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    /**
     * Constructor
     *
     * @param context Context object
     */
    DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }


    void insertTask(String title, List<String> tasks, int categoryId, boolean checkVisible) {
        String[] columns = {
                DatabaseHelper.TASK_TEXT
        };
        Cursor cursor = db.query(DatabaseHelper.TASK_TABLE,
                columns,
                DatabaseHelper.CATEGORY_ID + " =? ",
                new String[]{String.valueOf(categoryId)},
                null, null, null, null);

        if (cursor.getCount() > 0) {
            db.delete(DatabaseHelper.TASK_TABLE,
                    DatabaseHelper.CATEGORY_ID + " =? ",
                    new String[]{String.valueOf(categoryId)});
        }
        cursor.close();

        for (int i = 0; i < tasks.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.TASK_TITLE, title);
            contentValues.put(DatabaseHelper.TASK_TEXT, tasks.get(i));
            contentValues.put(DatabaseHelper.CATEGORY_ID, categoryId);
            contentValues.put(DatabaseHelper.CHECK_VISIBLE, checkVisible);

            try {
                db.insert(DatabaseHelper.TASK_TABLE,
                        DatabaseHelper.TASK_TEXT,
                        contentValues);
                Log.i("success", "in inserting task");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    TasksContainer getTaskContainer(int categoryId) {
        TasksContainer tasksContainer = new TasksContainer();
        String[] columns = {
                DatabaseHelper.TASK_TEXT,
                DatabaseHelper.CHECK_VISIBLE,
                DatabaseHelper.CHECKED,
                DatabaseHelper.TASK_TITLE
        };
        Cursor cursor = db.query(DatabaseHelper.TASK_TABLE,
                columns,
                DatabaseHelper.CATEGORY_ID + " =? ",
                new String[]{String.valueOf(categoryId)},
                null, null, null, null);

        List<Task> tasks = new ArrayList<>();
        while (cursor.moveToNext()) {
            tasksContainer.setTitle(cursor.getString(3));
            tasksContainer.setCategoryId(categoryId);
            Task task = new Task();
            task.setTaskName(cursor.getString(0));
            task.setChecked(cursor.getInt(1) > 0);
            task.setCheckVisible(cursor.getInt(2) > 0);
            tasks.add(task);
        }
        tasksContainer.setTasks(tasks);
        cursor.close();
        return tasksContainer;
    }


    List<TasksContainer> getAllTasks() {
        List<TasksContainer> listOfContainers = new ArrayList<>();
        String[] columns = {
                DatabaseHelper.TASK_TEXT,
                DatabaseHelper.CHECK_VISIBLE,
                DatabaseHelper.CHECKED,
        };

        Cursor cursorForCategories = db.query(true, DatabaseHelper.TASK_TABLE,
                new String[]{DatabaseHelper.TASK_TITLE, DatabaseHelper.CATEGORY_ID},
                null, null, null, null, null, null);

        while (cursorForCategories.moveToNext()) {
            TasksContainer newTaskContainer = new TasksContainer();
            newTaskContainer.setTitle(cursorForCategories.getString(0));
            newTaskContainer.setCategoryId(cursorForCategories.getInt(1));
            List<Task> tasks = new ArrayList<>();
            String[] selectionArgs = {String.valueOf(cursorForCategories.getInt(1))};
            Cursor cursorForTasks = db.query(DatabaseHelper.TASK_TABLE,
                    columns,
                    DatabaseHelper.CATEGORY_ID + " =? ",
                    selectionArgs,
                    null, null, null, null);

            while (cursorForTasks.moveToNext()) {
                Task task = new Task();
                task.setTaskName(cursorForTasks.getString(0));
                task.setCheckVisible(cursorForTasks.getInt(1) > 0);
                task.setChecked(cursorForTasks.getInt(2) > 0);
                tasks.add(task);
            }
            cursorForTasks.close();
            newTaskContainer.setTasks(tasks);
            listOfContainers.add(newTaskContainer);
        }
        cursorForCategories.close();
        return listOfContainers;
    }


    void insertTimeReminder(int categoryId, String reminderDate, String reminderTime) {
        String[] columns = {
                DatabaseHelper.REMINDER_DATE
        };
        Cursor cursor = db.query(DatabaseHelper.DATE_REMINDER_TABLE,
                columns,
                DatabaseHelper.CATEGORY_ID + " =? ",
                new String[]{String.valueOf(categoryId)},
                null, null, null, null);

        if (cursor.getCount() > 0) {
            db.delete(DatabaseHelper.DATE_REMINDER_TABLE,
                    DatabaseHelper.CATEGORY_ID + " =? ",
                    new String[]{String.valueOf(categoryId)});
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CATEGORY_ID, categoryId);
        contentValues.put(DatabaseHelper.REMINDER_DATE, reminderDate);
        contentValues.put(DatabaseHelper.REMINDER_TIME, reminderTime);
        db.insert(DatabaseHelper.DATE_REMINDER_TABLE, null, contentValues);
    }


    void insertLocationReminder(int categoryId, String location) {
        String[] columns = {
                DatabaseHelper.REMINDER_LOCATION
        };
        Cursor cursor = db.query(DatabaseHelper.LOCATION_REMINDER_TABLE,
                columns,
                DatabaseHelper.CATEGORY_ID + " =? ",
                new String[]{String.valueOf(categoryId)},
                null, null, null, null);

        if (cursor.getCount() > 0) {
            db.delete(DatabaseHelper.LOCATION_REMINDER_TABLE,
                    DatabaseHelper.CATEGORY_ID + " =? ",
                    new String[]{String.valueOf(categoryId)});
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CATEGORY_ID, categoryId);
        contentValues.put(DatabaseHelper.REMINDER_LOCATION, location);
        db.insert(DatabaseHelper.LOCATION_REMINDER_TABLE, null, contentValues);
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
                    + CATEGORY_ID + " INTEGER, "
                    + TASK_TEXT + " VARCHAR(255), "
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
