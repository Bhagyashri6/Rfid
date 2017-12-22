package com.enpeck.RFID.settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;
import com.enpeck.RFID.home.MainActivity;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.ReaderCapabilities;
import com.zebra.rfid.api3.RegionInfo;
import com.zebra.rfid.api3.RegulatoryConfig;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link RegulatorySettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle Regulatory Settings operations and UI.
 */
public class RegulatorySettingsFragment extends BackPressedFragment implements Spinner.OnItemSelectedListener,
        View.OnClickListener {
    private ArrayAdapter<String> currentRegionAdapter;
    private LinearLayout scrollView;
    private Spinner currentRegionSpinner;
    private boolean isselectedChannelsChanged = false;

    private String selectedChannels;
    private boolean hoppingConfigurable;
    private ArrayList<String> supportedRegions = new ArrayList<>();
    private RegionInfo selectedRegionInfo = null;

    public RegulatorySettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegulatorySettingsFragment.
     */
    public static RegulatorySettingsFragment newInstance() {
        return new RegulatorySettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_regulatory_settings, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scrollView = ((LinearLayout) getActivity().findViewById(R.id.regChannelCheckBoxes));

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_regulatory_settings);
        actionBar.setIcon(R.drawable.dl_reg);


        currentRegionSpinner = (Spinner) getActivity().findViewById(R.id.currentRegionSpinner);
        ReaderCapabilities readerCapabilities = null;
        if (Application.mConnectedReader != null)
            readerCapabilities = Application.mConnectedReader.ReaderCapabilities;
        if (readerCapabilities != null && (Application.mConnectedReader.isCapabilitiesReceived() || Application.regionNotSet == true))
            for (int idx = 0; idx < readerCapabilities.SupportedRegions.length(); idx++) {
                supportedRegions.add(readerCapabilities.SupportedRegions.getRegionInfo(idx).getRegionCode());
            }

        // Create an ArrayAdapter using the string array and a default spinner layout
        currentRegionAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, supportedRegions);
        // Specify the layout to use when the list of choices appears
        currentRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        currentRegionSpinner.setAdapter(currentRegionAdapter);

        if (Application.mConnectedReader != null && Application.regulatory == null) {
            try {
                Application.regulatory = Application.mConnectedReader.Config.getRegulatoryConfig();
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
        }
        //To handle unwanted call back, when we visit the fragment from elsewhere
        //
        if (readerCapabilities != null && Application.regulatory != null && Application.regulatory.getRegion() != null /*&& !Application.regulatorySettings.getregion().isEmpty()*/) {
            currentRegionSpinner.setSelection(supportedRegions.indexOf(Application.regulatory.getRegion()), false);
            if (Application.regulatory.getRegion() != null && !Application.regulatory.getRegion().equalsIgnoreCase("NA")/*&& Application.regulatoryConfigResponse.RegionCode != null && Application.regulatoryConfigResponse.RegionCode.equalsIgnoreCase(Application.regulatorySettings.getregion())*/) {
                selectedChannels = "";
                //selectedRegionInfo = Application.mConnectedReader.ReaderCapabilities.SupportedRegions.getRegionInfo(regionNotSet.indexOf(Application.regulatory.getRegion()));
                // get default channel list
                RegionInfo regionInfo = Application.mConnectedReader.ReaderCapabilities.SupportedRegions.getRegionInfo(supportedRegions.indexOf(Application.regulatory.getRegion()));
                try {
                    selectedRegionInfo = Application.mConnectedReader.Config.getRegionInfo(regionInfo);
                    setSelectedChannels();
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
            } else if (getActivity() != null && currentRegionSpinner.getSelectedItem() != null) {
                //((SettingsDetailActivity) getActivity()).getRegionDetails(currentRegionSpinner.getSelectedItem().toString());
                selectedChannels = "";
                try {
                    selectedRegionInfo = Application.mConnectedReader.Config.getRegionInfo(Application.mConnectedReader.ReaderCapabilities.SupportedRegions.getRegionInfo(0));
                    setSelectedChannels();
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
            }
        }
        currentRegionSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /* @Override
     public void handleStatusResponse(final Response_Status statusData) {
         getActivity().runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 if (statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_REGULATORY)) {
                     if (statusData.Status.trim().equalsIgnoreCase("OK")) {
                         ((BaseReceiverActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_success_message));
                     } else
                         ((BaseReceiverActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + statusData.Status);

                     ((SettingsDetailActivity) getActivity()).callBackPressed();
                 }
             }
         });
     }
 */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if (currentRegionSpinner != null && currentRegionSpinner.getSelectedItem() != null) {
            scrollView.removeAllViews();
            //  ((SettingsDetailActivity) getActivity()).getRegionDetails(currentRegionSpinner.getSelectedItem().toString());
            RegionInfo regionInfo = Application.mConnectedReader.ReaderCapabilities.SupportedRegions.getRegionInfo(pos);
            try {
                selectedRegionInfo = Application.mConnectedReader.Config.getRegionInfo(regionInfo);
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
            setSelectedChannels();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //DO Nothing
    }

    //@Override
/*    public void handleRegulatoryConfigResponse(final Response_RegulatoryConfig statusData) {
        if (statusData != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setSelectedChannels();
                    } catch (Exception e) {
                        Constants.logAsMessage(Constants.TYPE_ERROR, "RegulatorySettings", e.getMessage());
                    }
                }
            });
        }
    }*/

    @Override
    public void onBackPressed() {
        selectedChannels = "";
        if (currentRegionSpinner.getSelectedItem() != null) {
            if (scrollView.getChildCount() > 0) {
                for (int i = 0; i < scrollView.getChildCount(); i++) {
                    if (scrollView.getChildAt(i) instanceof CheckBox) {
                        CheckBox c = (CheckBox) scrollView.getChildAt(i);
                        if (c.isChecked())
                            selectedChannels = selectedChannels + c.getText() + " ";
                    }
                }
                selectedChannels = selectedChannels.trim();
                selectedChannels = selectedChannels.replaceAll(" ", ",");
                if (Application.regulatory == null)
                    isselectedChannelsChanged = true;
                else if (!currentRegionSpinner.getSelectedItem().toString().equalsIgnoreCase(Application.regulatory.getRegion()))
                    isselectedChannelsChanged = true;
                else if (Application.regulatory.getEnabledchannels() != null) {
                    if (Application.regulatory.getEnabledchannels().length != selectedChannels.split(",").length)
                        isselectedChannelsChanged = true;
                    else {
                        ArrayList<String> enabledChannels = new ArrayList<>();
                        Collections.addAll(enabledChannels, Application.regulatory.getEnabledchannels());
                        for (String s : selectedChannels.split(",")) {
                            if (!enabledChannels.contains(s)) {
                                isselectedChannelsChanged = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (currentRegionSpinner.getSelectedItem() != null && isselectedChannelsChanged) {
            new Task_SaveRegionConfiguration().execute();
        } else {
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        }
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * method to set selected Channels for the region on the screen
     */
    public void setSelectedChannels() {
        if (selectedRegionInfo != null) {
            scrollView.removeAllViews();
            hoppingConfigurable = selectedRegionInfo.isHoppingConfigurable();
            String[] channels = selectedRegionInfo.getSupportedChannels();
            if (hoppingConfigurable) {
                ArrayList<String> enabledChannels = new ArrayList<>();
                if (Application.regulatory != null && Application.regulatory.getEnabledchannels() != null
                        && Application.regulatory.getRegion().equalsIgnoreCase(selectedRegionInfo.getRegionCode())) {
                    Collections.addAll(enabledChannels, Application.regulatory.getEnabledchannels());
                }
                if (channels.length > 0) {
                    for (String s : channels) {
                        CheckBox checkBox = new CheckBox(getActivity());
                        checkBox.setText(s);
                        checkBox.setEnabled(hoppingConfigurable);
                        if (enabledChannels != null && enabledChannels.contains(s)) {
                            checkBox.setChecked(true);
                        }
                        checkBox.setOnClickListener(RegulatorySettingsFragment.this);
                        scrollView.addView(checkBox);
                    }
                }
            } else {
                if (channels.length > 0) {
                    for (String s : channels) {
                        CheckBox checkBox = new CheckBox(getActivity());
                        checkBox.setText(s);
                        checkBox.setEnabled(hoppingConfigurable);
                        checkBox.setChecked(true);
                        checkBox.setOnClickListener(RegulatorySettingsFragment.this);
                        scrollView.addView(checkBox);
                    }
                }
            }
        }
    }

    private class Task_SaveRegionConfiguration extends AsyncTask<Void, Void, Boolean> {
        private CustomProgressDialog progressDialog;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private final RegulatoryConfig regulatoryConfig;

        public Task_SaveRegionConfiguration() {
            regulatoryConfig = new RegulatoryConfig();
            regulatoryConfig.setRegion(selectedRegionInfo.getRegionCode());
            regulatoryConfig.setEnabledChannels(selectedChannels.split(","));
            if (selectedRegionInfo.isHoppingConfigurable())
                regulatoryConfig.setIsHoppingOn(true);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.regulatory_progress_title));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Application.mConnectedReader.Config.setRegulatoryConfig(regulatoryConfig);
                Application.regulatory = regulatoryConfig;
                return true;
            } catch (InvalidUsageException e) {
                e.printStackTrace();
                invalidUsageException = e;
            } catch (OperationFailureException e) {
                e.printStackTrace();
                operationFailureException = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.cancel();
            if (result) {
                ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_success_message));
                try {
                    // update trigger in case of no region set scenario
                    if (Application.settings_startTrigger == null) {
                        MainActivity.UpdateReaderConnection(true);
                    } else
                        MainActivity.UpdateReaderConnection(false);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
            } else {
                if (invalidUsageException != null)
                    ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                if (operationFailureException != null)
                    ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + operationFailureException.getVendorMessage());
            }
            if (invalidUsageException == null && operationFailureException == null)
                Toast.makeText(getActivity(), R.string.status_success_message, Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        }
    }


    /**
     * method to get channels for the selected region after device got connected in case reconnection
     */
    public void deviceConnected() {
/*        if (getActivity() != null && currentRegionSpinner.getSelectedItem() != null)
            ((SettingsDetailActivity) getActivity()).getRegionDetails(currentRegionSpinner.getSelectedItem().toString());*/
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Create an ArrayAdapter using the string array and a default spinner layout
                currentRegionAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, supportedRegions);
                // Specify the layout to use when the list of choices appears
                currentRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                currentRegionSpinner.setAdapter(currentRegionAdapter);

                if (Application.mConnectedReader != null) {
                    try {
                        Application.regulatory = Application.mConnectedReader.Config.getRegulatoryConfig();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                }
                if (Application.regulatory.getRegion() != null /*&& !Application.regulatorySettings.getregion().isEmpty()*/)
                    currentRegionSpinner.setSelection(supportedRegions.indexOf(Application.regulatory.getRegion()), false);
            }
        });
    }

    public void deviceDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (Application.regulatoryConfigResponse.SupportedChannels != null)
                {
                    scrollView.removeAllViews();
                }
                currentRegionSpinner.setAdapter(null);
            }
        });
    }
}
