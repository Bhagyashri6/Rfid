package com.enpeck.RFID.settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.zebra.rfid.api3.BATCH_MODE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.TAG_FIELD;
import com.zebra.rfid.api3.TagStorageSettings;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link TagReportingFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle tag reporting operations and UI
 */
public class TagReportingFragment extends BackPressedFragment implements View.OnClickListener {
    private boolean pcChecked, pcfield;
    private boolean rssiChecked, rssifield;
    private boolean phaseChecked, phasefield;
    private boolean channelChecked, channelfield;
    private boolean seenCountChecked, seencountfield;
    private Spinner batchModeSpinner;
    private CheckBox beepOnUniqueTag;
    private boolean uniqueTagChecked, uniqueTagReportfield;

    public TagReportingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TagReportingFragment.
     */
    public static TagReportingFragment newInstance() {
        return new TagReportingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag_reporting, container, false);
        CheckBox pc = (CheckBox) view.findViewById(R.id.incPC);
        pc.setOnClickListener(this);
        CheckBox rssi = (CheckBox) view.findViewById(R.id.incRSSI);
        rssi.setOnClickListener(this);
        CheckBox phase = (CheckBox) view.findViewById(R.id.incPhase);
        phase.setOnClickListener(this);
        CheckBox channelIndex = (CheckBox) view.findViewById(R.id.incChannel);
        channelIndex.setOnClickListener(this);
        CheckBox tagSeen = (CheckBox) view.findViewById(R.id.incTagSeen);
        tagSeen.setOnClickListener(this);
        beepOnUniqueTag= (CheckBox) view.findViewById(R.id.beepOnUniqueTag);
        beepOnUniqueTag.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.tag_reporting));
        actionBar.setIcon(R.drawable.dl_tag);

        batchModeSpinner = (Spinner) getActivity().findViewById(R.id.batchMode);
        ArrayAdapter<CharSequence> batchModeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.batch_modes_array, R.layout.custom_spinner_layout);
        batchModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batchModeSpinner.setAdapter(batchModeAdapter);

        if (Application.batchMode != -1) {
            batchModeSpinner.setSelection(Application.batchMode);
        }
        if (Application.reportUniquetags != null) {
            if (Application.reportUniquetags.getValue() == 1)
                beepOnUniqueTag.setChecked(true);
            else if(Application.reportUniquetags.getValue() == 0)
                beepOnUniqueTag.setChecked(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /* @Override
     public void handleStatusResponse(final Response_Status statusData) {
         if (statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_REPORTCONFIG) || statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_SET_ATTR))
             getActivity().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (statusData.Status.trim().equalsIgnoreCase("OK")) {
                         ((BaseReceiverActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_success_message));
                     } else
                         ((BaseReceiverActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + statusData.Status);

                     ((SettingsDetailActivity) getActivity()).callBackPressed();
                 }
             });
     }
 */
    @Override
    public void onResume() {
        super.onResume();
        loadCheckBoxStates();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Method to load the checkbox states
     */
    private void loadCheckBoxStates() {
        rssifield = phasefield = pcfield = channelfield = seencountfield = uniqueTagReportfield = false;
        if (Application.tagStorageSettings != null) {
            TAG_FIELD[] tag_field = Application.tagStorageSettings.getTagFields();
            for (int idx = 0; idx < tag_field.length; idx++) {
                if (tag_field[idx] == TAG_FIELD.PEAK_RSSI)
                    rssifield = true;
                if (tag_field[idx] == TAG_FIELD.PHASE_INFO)
                    phasefield = true;
                if (tag_field[idx] == TAG_FIELD.PC)
                    pcfield = true;
                if (tag_field[idx] == TAG_FIELD.CHANNEL_INDEX)
                    channelfield = true;
                if (tag_field[idx] == TAG_FIELD.TAG_SEEN_COUNT)
                    seencountfield = true;
            }

            ((CheckBox) getActivity().findViewById(R.id.incRSSI)).setChecked(rssifield);
            ((CheckBox) getActivity().findViewById(R.id.incPhase)).setChecked(phasefield);
            ((CheckBox) getActivity().findViewById(R.id.incPC)).setChecked(pcfield);
            ((CheckBox) getActivity().findViewById(R.id.incChannel)).setChecked(channelfield);
            ((CheckBox) getActivity().findViewById(R.id.incTagSeen)).setChecked(seencountfield);
        }
        if(Application.reportUniquetags != null) {
            if(Application.reportUniquetags != null && Application.reportUniquetags.getValue() == 1)
                uniqueTagReportfield = true;
            ((CheckBox) getActivity().findViewById(R.id.beepOnUniqueTag)).setChecked(uniqueTagReportfield);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isSettingsChanged())
            ((SettingsDetailActivity) getActivity()).callBackPressed();
    }

    private boolean isSettingsChanged() {
        boolean isSettingsChanged = false;
        if (Application.tagStorageSettings != null && (rssiChecked || phaseChecked || pcChecked || channelChecked || seenCountChecked)) {
            isSettingsChanged = true;

            TagStorageSettings tmpTagStorageSettings = null;
            try {
                tmpTagStorageSettings = Application.mConnectedReader.Config.getTagStorageSettings();
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
                ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + e.getVendorMessage());
                // even get fails return, we will not process further for batch mode also
                return false;
            }

            TAG_FIELD[] tag_fields = new TAG_FIELD[5];
            int index = 0;
            boolean incPC = ((CheckBox) getActivity().findViewById(R.id.incPC)).isChecked();
            boolean incRSSI = ((CheckBox) getActivity().findViewById(R.id.incRSSI)).isChecked();
            boolean incPhase = ((CheckBox) getActivity().findViewById(R.id.incPhase)).isChecked();
            boolean incChannel = ((CheckBox) getActivity().findViewById(R.id.incChannel)).isChecked();
            boolean incTagSeen = ((CheckBox) getActivity().findViewById(R.id.incTagSeen)).isChecked();
            if (incRSSI)
                tag_fields[index++] = TAG_FIELD.PEAK_RSSI;
            if (incPhase)
                tag_fields[index++] = TAG_FIELD.PHASE_INFO;
            if (incPC)
                tag_fields[index++] = TAG_FIELD.PC;
            if (incChannel)
                tag_fields[index++] = TAG_FIELD.CHANNEL_INDEX;
            if (incTagSeen)
                tag_fields[index] = TAG_FIELD.TAG_SEEN_COUNT;
            tmpTagStorageSettings.setTagFields(tag_fields);
            new Task_SaveTagReportingConfiguration(tmpTagStorageSettings, null, null).execute();
        }
        if (Application.batchMode != -1 && Application.batchMode != batchModeSpinner.getSelectedItemPosition()) {
            //((SettingsDetailActivity) getActivity()).setBatchMode(batchModeSpinner.getSelectedItemPosition());
            isSettingsChanged = true;
            new Task_SaveTagReportingConfiguration(null, batchModeSpinner.getSelectedItemPosition(), null).execute();
        }
        if(Application.reportUniquetags != null &&  uniqueTagChecked ) {
            isSettingsChanged = true;
            final boolean uniqueTagCheck = beepOnUniqueTag.isChecked();
            new Task_SaveTagReportingConfiguration(null, null, uniqueTagCheck).execute();
        }
        return isSettingsChanged;
    }

    @Override
    public void onClick(View view) {
        CheckBox checkBox = (CheckBox) view;
        if (Application.tagStorageSettings != null) {
            if (checkBox.getId() == R.id.incRSSI) {
                rssiChecked = !(rssifield && checkBox.isChecked() || (!rssifield && !checkBox.isChecked()));
            } else if (checkBox.getId() == R.id.incPhase) {
                phaseChecked = !(phasefield && checkBox.isChecked() || (!phasefield && !checkBox.isChecked()));
            } else if (checkBox.getId() == R.id.incPC) {
                pcChecked = !(pcfield && checkBox.isChecked() || (!pcfield && !checkBox.isChecked()));
            } else if (checkBox.getId() == R.id.incChannel) {
                channelChecked = !(channelfield && checkBox.isChecked() || (!channelfield && !checkBox.isChecked()));
            } else if (checkBox.getId() == R.id.incTagSeen) {
                seenCountChecked = !(seencountfield && checkBox.isChecked() || (!seencountfield && !checkBox.isChecked()));
            }
        }
        if( Application.reportUniquetags != null) {
            if (checkBox.getId() == R.id.beepOnUniqueTag) {
                uniqueTagChecked = !(uniqueTagReportfield && checkBox.isChecked() || (!uniqueTagReportfield && !checkBox.isChecked()));
            }
        }
    }

    private class Task_SaveTagReportingConfiguration extends AsyncTask<Void, Void, Boolean> {
        private final TagStorageSettings fnTagStorageSettings;
        private final Integer fnbatchmodepos;
        private final Boolean uniqueTagReport;
        private CustomProgressDialog progressDialog;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;

        public Task_SaveTagReportingConfiguration(TagStorageSettings tagStorageSettings, Integer batchmodepos, Boolean uniqueTagReport) {
            this.fnTagStorageSettings = tagStorageSettings;
            this.fnbatchmodepos = batchmodepos;
            this.uniqueTagReport = uniqueTagReport;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.tag_reporting_progress_title));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean bResult = true;
            try {
                if (fnTagStorageSettings != null) {
                    Application.mConnectedReader.Config.setTagStorageSettings(fnTagStorageSettings);
                    Application.tagStorageSettings = fnTagStorageSettings;
                }
                if (fnbatchmodepos != null) {
                    Application.mConnectedReader.Config.setBatchMode((BATCH_MODE) BATCH_MODE.GetBatchModeCodeValue(fnbatchmodepos));
                    Application.batchMode = Application.mConnectedReader.Config.getBatchModeConfig().getValue();
                }
                if (uniqueTagReport != null) {
                    Application.mConnectedReader.Config.setUniqueTagReport(uniqueTagReport);
                    Application.reportUniquetags = Application.mConnectedReader.Config.getUniqueTagReport();
                }
            } catch (InvalidUsageException e) {
                e.printStackTrace();
                invalidUsageException = e;
                bResult = false;
            } catch (OperationFailureException e) {
                e.printStackTrace();
                operationFailureException = e;
                bResult = false;
            }
            return bResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.cancel();
            if (!result) {
                if (invalidUsageException != null)
                    ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                if (operationFailureException != null)
                    ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + operationFailureException.getVendorMessage());
            } else
                Toast.makeText(getActivity(), R.string.status_success_message, Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        }
    }
    public void deviceConnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    loadCheckBoxStates();
                if (Application.batchMode != -1) {
                    batchModeSpinner.setSelection(Application.batchMode);
                }
            }
        });
    }
    public void deviceDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    ((CheckBox) getActivity().findViewById(R.id.incRSSI)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.incPhase)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.incPC)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.incChannel)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.incTagSeen)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.beepOnUniqueTag)).setChecked(false);
                batchModeSpinner.setSelection(1);
            }
        });
    }
}
