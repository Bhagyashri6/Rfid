package com.enpeck.RFID.settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.enpeck.RFID.common.Constants;


/**
 * Adapter for showing prefilters(2 tabs)
 */
public class PreFilterAdapter extends FragmentStatePagerAdapter {
    private static final int NO_OF_TABS = 2;

    /**
     * Constructor. Handles the initialization.
     *
     * @param fm - Fragment Manager to be used for handling fragments.
     */
    public PreFilterAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Constants.logAsMessage(Constants.TYPE_DEBUG, getClass().getSimpleName(), "1st Tab Selected");
                return PreFilter1ContentFragment.newInstance();
            case 1:
                Constants.logAsMessage(Constants.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                return PreFilter2ContentFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NO_OF_TABS;
    }

    @Override
    public int getItemPosition(Object object) {

        return PagerAdapter.POSITION_NONE;
    }
}
