package com.aideus.tasky;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.aideus.tasky.adapter.TabAdapter;
import com.aideus.tasky.alarm.AlarmHelper;
import com.aideus.tasky.database.DBHelper;
import com.aideus.tasky.dialog.AddingTaskDialogFragment;
import com.aideus.tasky.dialog.EditTaskDialogFragment;
import com.aideus.tasky.fragment.CurrentTaskFragment;
import com.aideus.tasky.fragment.DoneTaskFragment;
import com.aideus.tasky.fragment.SplashFragment;
import com.aideus.tasky.fragment.TaskFragment;
import com.aideus.tasky.model.ModelTask;

public class MainActivity extends AppCompatActivity
        implements AddingTaskDialogFragment.AddingTaskListener,
        CurrentTaskFragment.OnTaskDoneListener, DoneTaskFragment.OnTaskRestoreListener,
        EditTaskDialogFragment.EditingTaskListener {

    // To manage the fragments in your activity, you need to use FragmentManager.
    // Also use FragmentManager to open a FragmentTransaction, which allows you to perform transactions, such as add and remove fragments.
    FragmentManager fragmentManager;

    // Instance of my class for saving key-value of splash screen showing setting.
    PreferenceHelper preferenceHelper;

    // My implementation of FragmentStatePagerAdapter - PagerAdapter that uses a Fragment to manage each page.
    TabAdapter tabAdapter;

    // Instance of my class for current tasks list tab.
    TaskFragment currentTaskFragment;

    // Instance of my class for done tasks list tab.
    TaskFragment doneTaskFragment;

    // SearchView
    SearchView searchView;

    // DBHelper extends from SQLiteOpenHelper helper class which manages database creation and version management.
    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance of PreferenceHelper and initialize instance of SharedPreferences.
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();

        // Get instance of AlarmHelper.
        AlarmHelper.getInstance().init(getApplicationContext());

        // Init DBHelper.
        dbHelper = new DBHelper(getApplicationContext());

        // Get FragmentManager to manage the fragments in activity.
        fragmentManager = getFragmentManager();

        // My method for showing splash screen.
        runSplash();

        // My method for initializing MainActivity UI.
        setUI();
    }


    //TODO
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    // onResume of MainActivity set activityVisible to true.
    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    // onPause of MainActivity set activityVisible to false.
    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Finding MenuItem splashItem by its id.
        MenuItem splashItem = menu.findItem(R.id.action_splash);

        // Getting saved boolean (by key from constant SPLASH_IS_INVISIBLE) from preferenceHelper's sharedPreferences.
        // Setting splashItem checked or not depending on this boolean.
        splashItem.setChecked(preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        // The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Setting checkbox action_splash state when clicked opposite to previous one.
        // Saving new state of checkbox in boolean to preferenceHelper's sharedPreferences using key from constant SPLASH_IS_INVISIBLE.
        if (id == R.id.action_splash) {
            item.setChecked(!item.isChecked());
            preferenceHelper.putBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE, item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // My method for showing splash screen.
    public void runSplash() {

        // If boolean with SPLASH_IS_INVISIBLE key from preferenceHelper is false the show splash screen.
        if (!preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE)) {

            // Create new object from my SplashFragment class.
            SplashFragment splashFragment = new SplashFragment();

            // Use fragmentManager to beginTransaction with fragments, and replace content in content_frame container with splashFragment.
            // Add this transaction to the back stack and schedule its commit.
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, splashFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // My method for initializing MainActivity UI.
    private void setUI() {

        // Initializing Toolbar object by its id.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // If toolbar exists set title text color.
        // And set android.widget.Toolbar Toolbar to act as the android.support.v7.app.ActionBar for this Activity.
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            setSupportActionBar(toolbar);
        }

        // Initializing TabLayout by its id. TabLayout provides a horizontal layout to display tabs.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // Adding new tabs to tabLayout and setting their titles.
        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_task));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_task));

        // Initializing ViewPager by its id.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Initializing new TabAdapter object.
        tabAdapter = new TabAdapter(fragmentManager, 2);

        // Set PagerAdapter (tabAdapter) that will supply views for this viewPager.
        viewPager.setAdapter(tabAdapter);

        // Add TabLayoutOnPageChangeListener as viewPager.OnPageChangeListener.
        // TabLayoutOnPageChangeListener contains calls back to the provided tabLayout so that the tab position is kept in sync.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // setOnTabSelectedListener with callback interface invoked when a tab's selection state changes.
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Set the currently selected page.
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Initializing currentTaskFragment and doneTaskFragment instances from tabAdapter using its position constants.
        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);

        // Initializing SearchView by its id.
        searchView = (SearchView) findViewById(R.id.search_view);

        // Set listener for user actions within the SearchView. Callbacks for changes to the query text.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // When text in SearchView is changed do text search in tasks titles.
                currentTaskFragment.findTasks(newText);
                doneTaskFragment.findTasks(newText);
                return false;
            }
        });

        // Initializing FloatingActionButton by its id.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // setOnClickListener to FloatingActionButton.
        // onClick creates new instance of AddingTaskDialogFragment (implementation of DialogFragment - fragment that displays a dialog window, floating on top of its activity's window)
        // Shows it.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
                addingTaskDialogFragment.show(fragmentManager, "AddingTaskDialogFragment");
            }
        });
    }

    // Callback interface method of AddingTaskDialogFragment, when positive button is pressed.
    @Override
    public void onTaskAdded(ModelTask newTask) {
        currentTaskFragment.addTask(newTask, true);
    }

    // Callback interface method of AddingTaskDialogFragment, when negative button is pressed.
    @Override
    public void onTaskAddingCancel() {
        Toast.makeText(this, "Task adding canceled", Toast.LENGTH_LONG).show();
    }

    // Callback interface method of CurrentTaskFragment, to move task to doneTask list.
    @Override
    public void onTaskDone(ModelTask task) {
        doneTaskFragment.addTask(task, false);
    }

    // Callback interface method of DoneTaskFragment, to move task to currentTask list.
    @Override
    public void onTaskRestore(ModelTask task) {
        currentTaskFragment.addTask(task, false);
    }

    // Callback interface method of EditTaskDialogFragment, to update edited task in ArrayList and in database.
    @Override
    public void onTaskEdited(ModelTask updatedTask) {
        currentTaskFragment.updateTask(updatedTask);
        dbHelper.update().task(updatedTask);
    }
}
