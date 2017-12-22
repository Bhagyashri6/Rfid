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

import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link BeeperFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to show the UI for Beeper settings.
 */
public class BeeperFragment extends BackPressedFragment {
    private CheckBox sledBeeper;
    private Spinner volumeSpinner;
    private ArrayAdapter<CharSequence> volumeAdapter;

    public BeeperFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BeeperFragment.
     */
    public static BeeperFragment newInstance() {
        return new BeeperFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beeper, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_beeper);
        actionBar.setIcon(R.drawable.dl_beep);

        sledBeeper = (CheckBox) getActivity().findViewById(R.id.sledBeeper);
        volumeSpinner = (Spinner) getActivity().findViewById(R.id.volumeSpinner);

        volumeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.beeper_volume_array, R.layout.custom_spinner_layout);
        volumeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeSpinner.setAdapter(volumeAdapter);

        if (Application.beeperVolume != null) {
            if (Application.beeperVolume.equals(BEEPER_VOLUME.QUIET_BEEP))
                sledBeeper.setChecked(false);
            else if (!Application.beeperVolume.equals(BEEPER_VOLUME.QUIET_BEEP)) {
                sledBeeper.setChecked(true);
                volumeSpinner.setSelection(Application.beeperVolume.getValue());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (sledBeeper.isChecked() && Application.beeperVolume != null && Application.beeperVolume.getValue() != volumeSpinner.getSelectedItemPosition()) {
            new Task_SetVolume(volumeSpinner.getSelectedItemPosition()).execute();
        } else if (!sledBeeper.isChecked() && Application.beeperVolume != null && !Application.beeperVolume.equals(BEEPER_VOLUME.QUIET_BEEP))
            new Task_SetVolume(Constants.QUIET_BEEPER).execute();
        else
            ((SettingsDetailActivity) getActivity()).callBackPressed();
    }

    private class Task_SetVolume extends AsyncTask<Void, Void, Boolean> {
        private final int volume;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private CustomProgressDialog progressDialog;

        public Task_SetVolume(int volume) {
            this.volume = volume;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.beeper_volume_title));
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
                if (volume == 0)
                    Application.mConnectedReader.Config.setBeeperVolume(BEEPER_VOLUME.HIGH_BEEP);
                if (volume == 1)
                    Application.mConnectedReader.Config.setBeeperVolume(BEEPER_VOLUME.MEDIUM_BEEP);
                if (volume == 2)
                    Application.mConnectedReader.Config.setBeeperVolume(BEEPER_VOLUME.LOW_BEEP);
                if (volume == 3)
                    Application.mConnectedReader.Config.setBeeperVolume(BEEPER_VOLUME.QUIET_BEEP);
                Application.beeperVolume = Application.mConnectedReader.Config.getBeeperVolume();
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
            }
            else
                Toast.makeText(getActivity(), R.string.status_success_message, Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        }
    }
    /**
     * method to update battery screen when device got disconnected
     */
    public void deviceDisconnected() {
        sledBeeper.setChecked(false);
    }
}
