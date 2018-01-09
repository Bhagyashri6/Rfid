package com.enpeck.RFID.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;

import java.util.Timer;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link BatteryFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to show the battery information.
 */
public class BatteryFragment extends Fragment {
    public static ImageView batteryLevelImage;
    public static  TextView batteryStatusText;
    public static  TextView batteryLevel;
    public static  Timer t;

    public BatteryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BatteryFragment.
     */
    public static BatteryFragment newInstance() {
        return new BatteryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battery, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.battery));
        actionBar.setIcon(R.drawable.title_batt);

        batteryLevel = (TextView) getActivity().findViewById(R.id.batteryLevelText);
        batteryLevelImage = (ImageView) getActivity().findViewById(R.id.batteryLevelImage);
        batteryStatusText = (TextView) getActivity().findViewById(R.id.batteryStatusText);
        if (Application.BatteryData != null)
            deviceStatusReceived(Application.BatteryData.getLevel(), Application.BatteryData.getCharging(), Application.BatteryData.getCause());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

   /* @Override
    public void handleStatusResponse(final Response_Status statusData) {
       *//* if(statusData.Status.equalsIgnoreCase("OK")){

            final TextView batteryLevel = (TextView)getActivity().findViewById(R.id.batteryLevelText);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO - Set the battery status
                }
            });
        }
    }*/

    public  void  deviceStatusReceived(final int level, final boolean charging, final String cause) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (level >= Constants.BATTERY_FULL) {
                    batteryLevelImage.setImageLevel(10);
                    batteryLevel.setText(Constants.BATTERY_FULL + "%");
                } else {
                    batteryLevelImage.setImageLevel((int) (level / 10));
                    batteryLevel.setText(level + "%");
                }
                if (cause != null && cause.trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_CRITICAL)) {
                    batteryStatusText.setText(getString(R.string.battery_critical_message));
                    batteryStatusText.setTextAppearance(getActivity(), R.style.style_red_font);
                } else if (cause != null && cause.trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_LOW)) {
                    batteryStatusText.setText(getString(R.string.battery_low_message));
                    batteryStatusText.setTextAppearance(getActivity(), R.style.style_red_font);
                } else {
                    if (charging) {
                        if (level >= Constants.BATTERY_FULL)
                            batteryStatusText.setText(R.string.battery_full_message);
                        else
                            batteryStatusText.setText(R.string.battery_charging_message);
                    } else
                        batteryStatusText.setText(R.string.battery_discharging_message);
                    batteryStatusText.setTextAppearance(getActivity(), R.style.style_green_font);
                }
                batteryLevel.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * method to update battery screen when device got disconnected
     */
    public void deviceDisconnected() {
        batteryLevelImage.setImageLevel(0);
        batteryLevel.setText(0 + "%");
        batteryLevel.setVisibility(View.INVISIBLE);
        batteryStatusText.setTextAppearance(getActivity(), R.style.style_grey_font);
        batteryStatusText.setText(R.string.battery_no_active_connection_message);
    }

}
