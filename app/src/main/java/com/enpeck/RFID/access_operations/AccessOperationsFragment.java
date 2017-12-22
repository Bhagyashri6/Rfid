package com.enpeck.RFID.access_operations;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TagData;
import com.zebra.rfid.api3.TriggerInfo;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link AccessOperationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to act as a Holder for Access Tabs(Read/Write, Lock and Kill)
 */
public class AccessOperationsFragment extends Fragment implements ActionBar.TabListener {
    private ViewPager viewPager;
    private AccessOperationsAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    private int accessOperationCount = -1;

    // Tab titles
    private String[] tabs = {"Read \\ Write", "Lock", "Kill"};

    public AccessOperationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccessOperationsFragment.
     */
    public static AccessOperationsFragment newInstance() {
        return new AccessOperationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_access_operations, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initilization
        viewPager = (ViewPager) getActivity().findViewById(R.id.accessOperationsPager);
        actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.removeAllTabs();
        //Change the icon
        actionBar.setIcon(R.drawable.dl_access);
        mAdapter = new AccessOperationsAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        // mAdapter.notifyDataSetChanged();

        /**          * on swiping the viewpager make respective tab selected          * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                try {
                    Constants.logAsMessage(Constants.TYPE_DEBUG, getClass().getSimpleName(), " Position is --- " + position);
                    // on changing the page
                    // make respected tab selected
                    actionBar.setSelectedNavigationItem(position);
                    mAdapter.notifyDataSetChanged();
                } catch (IllegalStateException e) {
                    Constants.logAsMessage(Constants.TYPE_ERROR, getClass().getSimpleName(), e.getMessage());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        accessOperationCount = -1;
    }

    @Override
    public void onDetach() {
        super.onDetach();
       /* if (accessOperationCount > 0) {
            if (Application.accessControlTag != null && Application.setStopTriggerSettings != null) {
                ((MainActivity) getActivity()).sendStopTriggerCommand(Application.setStopTriggerSettings);
            }
            if (Application.accessControlTag != null && Application.setStartTriggerSettings != null) {
                ((MainActivity) getActivity()).sendStartTriggerCommand(Application.setStartTriggerSettings);
            }
        }
        Application.isAccessCriteriaRead = false;*/
        if (accessOperationCount > 0) {
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
            if (Application.accessControlTag != null && Application.settings_stopTrigger != null) {
                try {
                    Application.mConnectedReader.Config.setStopTrigger(triggerInfo.StopTrigger);
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                }
            }
            if (Application.accessControlTag != null && Application.settings_startTrigger != null) {
                try {
                    Application.mConnectedReader.Config.setStartTrigger(triggerInfo.StartTrigger);
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                }
            }
        }
        Application.isAccessCriteriaRead = false;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Constants.logAsMessage(Constants.TYPE_DEBUG, getClass().getSimpleName(), "onTabSelected() Position is --- " + tab.getPosition());
        // on tab selected
        //get accesscontroltag from  current tab
        OnRefreshListener fragment = (OnRefreshListener) (mAdapter.getFragment(viewPager.getCurrentItem()));
        if (fragment != null)
            fragment.onUpdate();
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
/*

    @Override
    public void handleStatusResponse(Response_Status statusData) {
        if (Application.isAccessCriteriaRead && statusData.Status.equalsIgnoreCase(Constants.STATUS_OK)) {
            if (statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_ACCESS_CRITERIA))
                accessOperationCount++;
            else if (accessOperationCount == 0 && (statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_STARTTRIGGER) || statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_STOPTRIGGER)))
                accessOperationCount++;
        } else {
            Application.isAccessCriteriaRead = false;
        }
    }
*/

    public void handleTagResponse(TagData tagData) {
        if (mAdapter != null && viewPager != null) {
            Fragment fragment = mAdapter.getFragment(viewPager.getCurrentItem());
            if (fragment != null && fragment instanceof AccessOperationsReadWriteFragment) {
                ((AccessOperationsReadWriteFragment) fragment).handleTagResponse(tagData);
            }
        }
    }

    /**
     * Method to fetch one of (Read/Write, Lock or Kill) fragments currently being displayed
     *
     * @return - {@link android.support.v4.app.Fragment} instance
     */
    public Fragment getCurrentlyViewingFragment() {
        if (mAdapter != null && viewPager != null) {
            return mAdapter.getFragment(viewPager.getCurrentItem());
        } else {
            return null;
        }
    }

    /**
     * method to set start trigger as immediate and stop trigger as duration based with 5sec and stop access count as 1 for access operation
     */
    public void setStartStopTriggers() {
        if (accessOperationCount <= 0) {
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
            try {
                Application.mConnectedReader.Config.setStartTrigger(triggerInfo.StartTrigger);
                Application.mConnectedReader.Config.setStopTrigger(triggerInfo.StopTrigger);
            } catch (OperationFailureException e) {
                e.printStackTrace();
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * interface to maintain last entered access tag id in access control fragments
     */
    public interface OnRefreshListener {
        /**
         * method to update accessControlTag value
         */
        void onUpdate();

        /**
         * method to refresh the fragment details with updated tag id
         */
        void onRefresh();
    }
}
