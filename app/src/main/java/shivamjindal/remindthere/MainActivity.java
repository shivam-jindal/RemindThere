package shivamjindal.remindthere;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements VoiceInputDialog.VoiceInputDialogListener {

    boolean linearView = true;
    int REQ_CODE_SPEECH_INPUT = 11;
    static CoordinatorLayout mainParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainFragment mainFragment = new MainFragment();
        FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_content_main, mainFragment, "MainFragment");
        transaction.commit();
        mainParentLayout = (CoordinatorLayout) findViewById(R.id.main_parent_layout);

        com.github.clans.fab.FloatingActionButton fabTextInput =
                (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_text_input);
        fabTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddItemActivity.class));
            }
        });

        com.github.clans.fab.FloatingActionButton fabVoiceInput =
                (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_voice_input);
        fabVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                VoiceInputDialog voiceInputDialog= VoiceInputDialog.newInstance(
                        result.get(0),
                        MainActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                voiceInputDialog.show(fragmentManager, "Voice Input Dialog");
            }
        } else {
            Constants.showToast(MainActivity.this, "Some problem occurred!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (linearView)
            menu.getItem(0).setIcon(R.drawable.view_staggered_grid_layout);
        else
            menu.getItem(0).setIcon(R.drawable.view_linear_layout);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.change_layout) {
            if (linearView)
                MainFragment.outerRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            else
                MainFragment.outerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            linearView = !linearView;
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onVoiceInputDialogDismiss(String taskTitle) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
        List<String> subtasks = new ArrayList<>();
        subtasks.add("");
        databaseAdapter.insertTask(taskTitle, subtasks, Constants.getNewCategoryID(getApplicationContext()), false);
        Constants.showToast(getApplicationContext(), "New task added!");
        refreshFragment();
    }


    void refreshFragment(){
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag("MainFragment");
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(fragment);
        ft.attach(fragment);
        ft.commit();
    }
}
