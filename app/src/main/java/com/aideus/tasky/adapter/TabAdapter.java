package com.aideus.tasky.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.aideus.tasky.fragment.CurrentTaskFragment;
import com.aideus.tasky.fragment.DoneTaskFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    int numberOfTabs;

    // Constants for fragments positions related to tabs in TabLayout.
    public static final int CURRENT_TASK_FRAGMENT_POSITION = 0;
    public static final int DONE_TASK_FRAGMENT_POSITION = 1;

    // Instance of my class CurrentTaskFragment for list of current tasks.
    private CurrentTaskFragment currentTaskFragment;

    // Instance of my class DoneTaskFragment for list of finished tasks.
    private DoneTaskFragment doneTaskFragment;

    // Constructor for TabAdapter, sets number of tabs.
    // And initializes instances of CurrentTaskFragment and DoneTaskFragment.
    public TabAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        currentTaskFragment = new CurrentTaskFragment();
        doneTaskFragment = new DoneTaskFragment();
    }

    // Returns instances of CurrentTaskFragment or DoneTaskFragment by id.
    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return currentTaskFragment;
            case 1:
                return doneTaskFragment;
            default:
                return null;
        }
    }

    // Returns number of tabs in TabLayout.
    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
