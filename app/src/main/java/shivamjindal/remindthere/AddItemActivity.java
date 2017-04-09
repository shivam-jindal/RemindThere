package shivamjindal.remindthere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivam on 9/4/17.
 */

public class AddItemActivity extends AppCompatActivity {

    TextView addItemButton;
    EditText title;
    EditText firstTask;
    LinearLayout layoutTasks;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        addItemButton = (TextView) findViewById(R.id.add_item_button);
        title = (EditText) findViewById(R.id.title_edit_text);
        firstTask = (EditText) findViewById(R.id.first_task);
        layoutTasks = (LinearLayout) findViewById(R.id.tasks_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        );


        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newTask = new EditText(AddItemActivity.this);

                newTask.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutTasks.addView(newTask);
                newTask.requestFocus();
            }
        });
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
            saveItemInDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveItemInDatabase(){
        List<String> tasks = new ArrayList<>();
        for(int i=0; i<layoutTasks.getChildCount(); i++){
            tasks.add(((EditText)layoutTasks.getChildAt(i)).getText().toString());
        }
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.insertTask(title.getText().toString(), tasks, getNewCategoryID(), true);

        goToMainActivity();
    }


    private int getNewCategoryID(){
        int newCategoryId = sharedPreferences.getInt("NEW_CATEGORY_ID", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("NEW_CATEGORY_ID", (newCategoryId+1));
        editor.apply();
        return newCategoryId;
    }


    private void goToMainActivity(){
        startActivity(new Intent(AddItemActivity.this, MainActivity.class));
    }
}
