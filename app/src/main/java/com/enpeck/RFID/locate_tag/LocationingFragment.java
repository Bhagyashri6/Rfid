package com.enpeck.RFID.locate_tag;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.zebra.rfid.api3.RFIDResults;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.home.MainActivity;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link LocationingFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle locationing
 */
public class LocationingFragment extends Fragment implements ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler {
    private RangeGraph locationBar;
    //private TextView distance;
    private Button btn_locate;
    private AutoCompleteTextView et_locateTag;
    private ArrayAdapter<String> adapter;

    public LocationingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationingFragment.
     */
    public static LocationingFragment newInstance() {
        return new LocationingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locationing, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Change the icon
        ((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.dl_loc);
        locationBar = (RangeGraph) getActivity().findViewById(R.id.locationBar);
        // distance=(TextView)getActivity().findViewById(R.id.distance);
        btn_locate = (Button) getActivity().findViewById(R.id.btn_locate);
        et_locateTag = (AutoCompleteTextView) getActivity().findViewById(R.id.lt_et_epc);
        Application.updateTagIDs();
        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, Application.tagIDs);
        et_locateTag.setAdapter(adapter);
        if (et_locateTag != null && Application.locateTag != null)
            et_locateTag.setText(Application.locateTag);
        locationBar.setValue(0);
        if (Application.isLocatingTag) {
            if (btn_locate != null)
                btn_locate.setText(getResources().getString(R.string.stop_title));
            showTagLocationingDetails();
        } else {
            if (btn_locate != null) {
                btn_locate.setText(getResources().getString(R.string.start_title));
            }
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Application.locateTag = et_locateTag.getText().toString();
    }

    public void showTagLocationingDetails() {
        if (Application.TagProximityPercent != -1) {
           /* if (distance != null)
                distance.setText(Application.tagProximityPercent.Proximitypercent + "%");*/
            if (locationBar != null && Application.TagProximityPercent != -1) {
                locationBar.setValue((short) Application.TagProximityPercent);
                locationBar.invalidate();
                locationBar.requestLayout();
            }
        }
    }

    public void handleLocateTagResponse() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showTagLocationingDetails();
            }
        });
    }

    @Override
    public void triggerPressEventRecieved() {
        if (!Application.isLocatingTag)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_locate.setText(R.string.start_title);
                    ((MainActivity) getActivity()).locationingButtonClicked(btn_locate);
                }
            });
    }

    @Override
    public void triggerReleaseEventRecieved() {
        if (Application.isLocatingTag)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_locate.setText(R.string.stop_title);
                    ((MainActivity) getActivity()).locationingButtonClicked(btn_locate);
                }
            });
    }

    public void resetLocationingDetails(boolean isDeviceDisconnected) {
        if (btn_locate != null) {
            btn_locate.setText(getResources().getString(R.string.start_title));
        }
        if (isDeviceDisconnected && locationBar != null) {
            locationBar.setValue(0);
            locationBar.invalidate();
            locationBar.requestLayout();
        }
        if (et_locateTag != null) {
            et_locateTag.setFocusableInTouchMode(true);
            et_locateTag.setFocusable(true);
        }
    }


    //@Override
    public void handleStatusResponse(final RFIDResults results) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!results.equals(RFIDResults.RFID_API_SUCCESS)) {
                    //String command = statusData.command.trim();
                    //if (command.equalsIgnoreCase("lt") || command.equalsIgnoreCase("locatetag"))
                    {
                        Application.isLocatingTag = false;
                        if (btn_locate != null) {
                            btn_locate.setText(getResources().getString(R.string.start_title));
                        }
                        if(et_locateTag!=null) {
                            et_locateTag.setFocusableInTouchMode(true);
                            et_locateTag.setFocusable(true);
                        }
                    }

                }
            }
        });
    }
}
