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
import android.widget.Spinner;
import android.widget.Toast;

import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.INVENTORY_STATE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.SESSION;
import com.zebra.rfid.api3.SL_FLAG;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link SingulationControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle singulation operations and UI.
 */
public class SingulationControlFragment extends BackPressedFragment implements Spinner.OnItemSelectedListener {

    private Spinner sessionSpinner;
    private Spinner tagPopulationSpinner;
    private Spinner inventoryStateSpinner;
    private Spinner slFlagSpinner;
    private ArrayAdapter<CharSequence> tagPopulationAdapter;
//    private Antennas.SingulationControl singulationControl;

    public SingulationControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SingulationControlFragment.
     */
    public static SingulationControlFragment newInstance() {
        return new SingulationControlFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_singulation_control, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_singulation_control);
        actionBar.setIcon(R.drawable.dl_singl);

        sessionSpinner = (Spinner) getActivity().findViewById(R.id.session);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sessionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.session_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sessionSpinner.setAdapter(sessionAdapter);

        tagPopulationSpinner = (Spinner) getActivity().findViewById(R.id.tagPopulation);
        // Create an ArrayAdapter using the string array and a default spinner layout
        tagPopulationAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.tag_population_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        tagPopulationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tagPopulationSpinner.setAdapter(tagPopulationAdapter);

        inventoryStateSpinner = (Spinner) getActivity().findViewById(R.id.inventoryState);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> inventoryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.inventory_state_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        inventoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        inventoryStateSpinner.setAdapter(inventoryAdapter);

        slFlagSpinner = (Spinner) getActivity().findViewById(R.id.slFlag);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> slAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sl_flags_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        slAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        slFlagSpinner.setAdapter(slAdapter);

        // defaults
        sessionSpinner.setSelection(0, false);
        tagPopulationSpinner.setSelection(0, false);
        inventoryStateSpinner.setSelection(0, false);
        slFlagSpinner.setSelection(0, false);
        // reader settings
        if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected() && Application.mConnectedReader.isCapabilitiesReceived() && Application.singulationControl!= null ) {
            sessionSpinner.setSelection(Application.singulationControl.getSession().getValue());
            tagPopulationSpinner.setSelection(tagPopulationAdapter.getPosition(Application.singulationControl.getTagPopulation() + ""));
            inventoryStateSpinner.setSelection(Application.singulationControl.Action.getInventoryState().getValue());
            //slFlagSpinner.setSelection(Application.singulationControl.Action.getSLFlag().getValue());
            switch (Application.singulationControl.Action.getSLFlag().getValue()) {
                case 0:
                    slFlagSpinner.setSelection(2);
                    break;
                case 1:
                    slFlagSpinner.setSelection(1);
                    break;
                case 2:
                    slFlagSpinner.setSelection(0);
                    break;
            }
        }
        sessionSpinner.setOnItemSelectedListener(this);
        tagPopulationSpinner.setOnItemSelectedListener(this);
        inventoryStateSpinner.setOnItemSelectedListener(this);
        slFlagSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //DO Nothing
    }

    @Override
    public void onBackPressed() {
        if ((sessionSpinner.getSelectedItem() != null && tagPopulationSpinner.getSelectedItem() != null && inventoryStateSpinner.getSelectedItem() != null
                && slFlagSpinner.getSelectedItem() != null)) {
            if (isSettingsChanged()) {
                new Task_SaveSingulationConfiguration(sessionSpinner.getSelectedItemPosition(), tagPopulationSpinner.getSelectedItemPosition(), inventoryStateSpinner.getSelectedItemPosition(), slFlagSpinner.getSelectedItemPosition()).execute();
            } else
                ((SettingsDetailActivity) getActivity()).callBackPressed();
        }
    }

    /**
     * method to know whether singulation control settings has changed
     *
     * @return true if settings has changed or false if settings has not changed
     */
    private boolean isSettingsChanged() {
        if (Application.singulationControl != null) {
            if (Application.singulationControl.getSession().getValue() != sessionSpinner.getSelectedItemPosition())
                return true;
            else if (Application.singulationControl.getTagPopulation() != Integer.parseInt(tagPopulationSpinner.getSelectedItem().toString()))
                return true;
            else if (Application.singulationControl.Action.getInventoryState().getValue() != inventoryStateSpinner.getSelectedItemPosition())
                return true;
            else {
                int pos = slFlagSpinner.getSelectedItemPosition();
                switch (pos) {
                    case 0:
                        if (Application.singulationControl.Action.getSLFlag() != SL_FLAG.SL_ALL)
                            return true;
                        break;
                    case 1:
                        if (Application.singulationControl.Action.getSLFlag() != SL_FLAG.SL_FLAG_DEASSERTED)
                            return true;
                        break;
                    case 2:
                        if (Application.singulationControl.Action.getSLFlag() != SL_FLAG.SL_FLAG_ASSERTED)
                            return true;
                        break;
                }
            }
        }

        return false;
    }


    private class Task_SaveSingulationConfiguration extends AsyncTask<Void, Void, Boolean> {
        private CustomProgressDialog progressDialog;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private final int session;
        private final int tagpopulation;
        private final int inventorystate;
        private final int slflag;

        public Task_SaveSingulationConfiguration(int sessionIndex, int tagPopulationIndex, int invStateIndex, int slflagindex) {
            session = sessionIndex;
            tagpopulation = tagPopulationIndex;
            inventorystate = invStateIndex;
            slflag = slflagindex;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.singulation_progress_title));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Antennas.SingulationControl singulationControl;
            try {
                singulationControl = Application.mConnectedReader.Config.Antennas.getSingulationControl(1);
                singulationControl.setSession(SESSION.GetSession(session));
                singulationControl.setTagPopulation(Short.parseShort(tagPopulationAdapter.getItem(tagpopulation).toString()));
                singulationControl.Action.setInventoryState(INVENTORY_STATE.GetInventoryState(inventorystate));
                switch (slflag) {
                    case 0:
                        singulationControl.Action.setSLFlag(SL_FLAG.SL_ALL);
                        break;
                    case 1:
                        singulationControl.Action.setSLFlag(SL_FLAG.SL_FLAG_DEASSERTED);
                        break;
                    case 2:
                        singulationControl.Action.setSLFlag(SL_FLAG.SL_FLAG_ASSERTED);
                        break;
                }
                Application.mConnectedReader.Config.Antennas.setSingulationControl(1, singulationControl);
                Application.singulationControl = singulationControl;
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
            if (!result) {
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
//    @Override
//    public void handleStatusResponse(final Response_Status statusData) {
//
//        if (statusData.command.trim().equalsIgnoreCase(Constants.COMMAND_QUERYPARAMS))
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (statusData.Status.trim().equalsIgnoreCase("OK")) {
//                        ((BaseReceiverActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_success_message));
//                    } else
//                        ((BaseReceiverActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + statusData.Status);
//
//                    ((SettingsDetailActivity) getActivity()).callBackPressed();
//                }
//            });
//    }
}
