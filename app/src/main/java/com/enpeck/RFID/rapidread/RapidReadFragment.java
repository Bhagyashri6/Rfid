package com.enpeck.RFID.rapidread;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.inventory.InventoryListItem;
import com.zebra.rfid.api3.RFIDResults;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link RapidReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle the rapid read operation and UI
 */
public class RapidReadFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler , ResponseHandlerInterfaces.ResponseStatusHandler {
    private TextView tagReadRate;
    private TextView uniqueTags;
    private TextView totalTags;
    private Button inventoryButton;
    private TextView timeText;
    private TextView batchModeRR;

    public RapidReadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RapidReadFragment.
     */
    public static RapidReadFragment newInstance() {
        return new RapidReadFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rr, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rr, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();

        mainActivity.getSupportActionBar().setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);

        //Change the icon
        mainActivity.getSupportActionBar().setIcon(R.drawable.dl_rr);

        inventoryButton = (Button) mainActivity.findViewById(R.id.rr_inventoryButton);
        uniqueTags = (TextView) mainActivity.findViewById(R.id.uniqueTagContent);
        totalTags = (TextView) mainActivity.findViewById(R.id.totalTagContent);
        tagReadRate = (TextView) getActivity().findViewById(R.id.readRateContent);
        batchModeRR = (TextView) getActivity().findViewById(R.id.batchModeRR);

        if (Application.mIsInventoryRunning)
            inventoryButton.setText(R.string.stop_title);
        else
            inventoryButton.setText(R.string.start_title);
        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            uniqueTags.setVisibility(View.GONE);
            batchModeRR.setVisibility(View.VISIBLE);
        } else {
            batchModeRR.setVisibility(View.GONE);
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
            if (uniqueTags.getTextScaleX() > 0.5 && uniqueTags.getText().length() > 4)
                uniqueTags.setTextScaleX(uniqueTags.getTextScaleX() - (float) 0.1);
            else if (uniqueTags.getTextScaleX() > 0.4 && uniqueTags.getText().length() > 5)
                uniqueTags.setTextScaleX(uniqueTags.getTextScaleX() - (float) 0.03);
        }
        totalTags.setText(String.valueOf(Application.TOTAL_TAGS));
        if (Application.mRRStartedTime == 0)
            Application.TAG_READ_RATE = 0;
        else
            Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS / (Application.mRRStartedTime / (float) 1000));
        tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);

        timeText = (TextView) getActivity().findViewById(R.id.readTimeContent);
        if (timeText != null) {
            String min = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(Application.mRRStartedTime));
            String sec = String.format("%d", TimeUnit.MILLISECONDS.toSeconds(Application.mRRStartedTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Application.mRRStartedTime)));
            if (min.length() == 1) {
                min = "0" + min;
            }
            if (sec.length() == 1) {
                sec = "0" + sec;
            }
            timeText.setText(min + ":" + sec);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * method to reset tags info on the screen before starting inventory operation
     */
    public void resetTagsInfo() {
        uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        totalTags.setText(String.valueOf(Application.TOTAL_TAGS));
        tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
        timeText.setText(Constants.ZERO_TIME);
    }

    /**
     * method to start inventory operation on trigger press event received
     */
    public void triggerPressEventRecieved() {
        if (!Application.mIsInventoryRunning)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inventoryButton.setText(R.string.start_title);
                    ((MainActivity) getActivity()).inventoryStartOrStop(inventoryButton);
                }
            });
    }

    /**
     * method to stop inventory operation on trigger release event received
     */
    public void triggerReleaseEventRecieved() {
        if (Application.mIsInventoryRunning)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inventoryButton.setText(R.string.stop_title);
                    ((MainActivity) getActivity()).inventoryStartOrStop(inventoryButton);
                }
            });
    }

    //@Override
    public void handleStatusResponse(final RFIDResults results) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (results.equals(RFIDResults.RFID_BATCHMODE_IN_PROGRESS)) {
                    if(uniqueTags!=null) {
                        uniqueTags.setVisibility(View.GONE);
                        batchModeRR.setVisibility(View.VISIBLE);
                    }
                }
                else if (!results.equals(RFIDResults.RFID_API_SUCCESS)) {
                    //String command = statusData.command.trim();
                    //if (command.equalsIgnoreCase("in") || command.equalsIgnoreCase("inventory") || command.equalsIgnoreCase("read") || command.equalsIgnoreCase("rd"))
                    {
                        Application.mIsInventoryRunning = false;
                        if (inventoryButton != null) {
                            inventoryButton.setText(getResources().getString(R.string.start_title));
                        }
                        Application.isBatchModeInventoryRunning = false;
                    }

                }
            }
        });
    }


    /**
     * method to update inventory details on the screen on operation end summary received
     */
    public void updateInventoryDetails() {
        uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        totalTags.setText(String.valueOf(Application.TOTAL_TAGS));
        tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
    }

    /**
     * method to reset inventory operation status on the screen
     */
    public void resetInventoryDetail() {
        if (inventoryButton != null)
            inventoryButton.setText(getString(R.string.start_title));
        if (uniqueTags != null) {
            uniqueTags.setVisibility(View.VISIBLE);
        }
        if (batchModeRR != null) {
            batchModeRR.setVisibility(View.GONE);
        }
    }

    @Override
    public void batchModeEventReceived() {
        if (inventoryButton != null)
            inventoryButton.setText(R.string.stop_title);
        if (uniqueTags != null) {
            uniqueTags.setVisibility(View.GONE);
        }
        if (batchModeRR != null) {
            batchModeRR.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        if (uniqueTags != null)
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        if (uniqueTags.getTextScaleX() > 0.5 && uniqueTags.getText().length() > 4)
            uniqueTags.setTextScaleX(uniqueTags.getTextScaleX() - (float) 0.1);
        else if (uniqueTags.getTextScaleX() > 0.4 && uniqueTags.getText().length() > 5)
            uniqueTags.setTextScaleX(uniqueTags.getTextScaleX() - (float) 0.03);
        if (totalTags != null)
            totalTags.setText(String.valueOf(Application.TOTAL_TAGS));
      /* if (tagReadRate != null) {
            if (Application.mRRStartedTime == 0)
                Application.TAG_READ_RATE = 0;
            else
                Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS / (Application.mRRStartedTime / (float) 1000));
            tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
        }*/
    }
}