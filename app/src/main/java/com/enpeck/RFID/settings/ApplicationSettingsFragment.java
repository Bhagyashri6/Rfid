package com.enpeck.RFID.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableRow;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;

import java.util.Hashtable;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link ApplicationSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to show the Connection Settings UI
 */
public class ApplicationSettingsFragment extends Fragment {

    public static Hashtable<String, Boolean> applicationSettings;

    static {
        applicationSettings = new Hashtable<>();
        applicationSettings.put(Constants.AUTO_RECONNECT_READERS, false);
        applicationSettings.put(Constants.NFC, false);
        applicationSettings.put(Constants.NOTIFY_READER_AVAILABLE, false);
        applicationSettings.put(Constants.NOTIFY_READER_CONNECTION, false);
        applicationSettings.put(Constants.NOTIFY_BATTERY_STATUS, false);
        applicationSettings.put(Constants.EXPORT_DATA, false);
    }

    private CheckBox autoReconnectReaders;
    private TableRow NFCSettingsRow;
    private CheckBox nfcSettings;
    private CheckBox readerAvailable;
    private CheckBox readerConnection;
    private CheckBox readerBattery;
    private CheckBox exportData;
    private SharedPreferences settings;

    public ApplicationSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConnectionSettingsFragment.
     */
    public static ApplicationSettingsFragment newInstance() {
        return new ApplicationSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_settings, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadCheckBoxStates();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeViews();

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_application_settings);
        actionBar.setIcon(R.drawable.dl_sett);

//        if (NfcAdapter.getDefaultAdapter(getActivity()) != null) {
//
//            nfcSettings.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    /*if (((CheckBox) view).isChecked())
//                        ((BaseReceiverActivity) getActivity()).enableNFC();
//                    else {
//                        ((BaseReceiverActivity) getActivity()).disableNFC();
//                    }
//                    if(!((BaseReceiverActivity)getActivity()).getNfcBT().EMDKinstalled)
//                        ((CheckBox) view).setChecked(!((CheckBox) view).isChecked());*/
//                }
//            });
//        } else {
        NFCSettingsRow.setVisibility(View.GONE);
//        }
    }

    private void initializeViews() {
        autoReconnectReaders = ((CheckBox) getActivity().findViewById(R.id.autoReconnectReaders));
        NFCSettingsRow = ((TableRow) getActivity().findViewById(R.id.NFCSettingsRow));
        nfcSettings = (CheckBox) getActivity().findViewById(R.id.NFCsettings);
        readerAvailable = (CheckBox) getActivity().findViewById(R.id.readerAvailable);
        readerConnection = ((CheckBox) getActivity().findViewById(R.id.readerConnection));
        readerBattery = ((CheckBox) getActivity().findViewById(R.id.readerBattery));
        exportData = ((CheckBox) getActivity().findViewById(R.id.exportData));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        storeCheckBoxesStatus();
    }

    /**
     * Method to load the checkbox states
     */
    private void loadCheckBoxStates() {
        settings = getActivity().getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        //((CheckBox) getActivity().findViewById(R.id.autoDetectReaders)).setChecked(settings.getBoolean(Constants.AUTO_DETECT_READERS, false));
        autoReconnectReaders.setChecked(settings.getBoolean(Constants.AUTO_RECONNECT_READERS, false));
        readerAvailable.setChecked(settings.getBoolean(Constants.NOTIFY_READER_AVAILABLE, false));
        readerConnection.setChecked(settings.getBoolean(Constants.NOTIFY_READER_CONNECTION, false));
        readerBattery.setChecked(settings.getBoolean(Constants.NOTIFY_BATTERY_STATUS, true));
        exportData.setChecked(settings.getBoolean(Constants.EXPORT_DATA, false));
       /* if (nfcSettings != null) {
            if (NfcAdapter.getDefaultAdapter(getActivity()).isEnabled())
                nfcSettings.setChecked(true);
            else
                nfcSettings.setChecked(false);
        }*/
    }

    /**
     * Method to store the checkbox states
     */
    private void storeCheckBoxesStatus() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        SharedPreferences.Editor editor = settings.edit();

        //Save the chechbox status
        editor.putBoolean(Constants.AUTO_RECONNECT_READERS, autoReconnectReaders.isChecked());
        editor.putBoolean(Constants.NOTIFY_READER_AVAILABLE, readerAvailable.isChecked());
        editor.putBoolean(Constants.NOTIFY_READER_CONNECTION, readerConnection.isChecked());
        editor.putBoolean(Constants.NOTIFY_BATTERY_STATUS, readerBattery.isChecked());
        editor.putBoolean(Constants.EXPORT_DATA, exportData.isChecked());

        // Commit the edits!
        editor.commit();

        //Update the preferences in the Application
        //Application.AUTO_DETECT_READERS = ((CheckBox) getActivity().findViewById(R.id.autoDetectReaders)).isChecked();
        Application.AUTO_RECONNECT_READERS = ((CheckBox) getActivity().findViewById(R.id.autoReconnectReaders)).isChecked();
        Application.NOTIFY_READER_AVAILABLE = ((CheckBox) getActivity().findViewById(R.id.readerAvailable)).isChecked();
        Application.NOTIFY_READER_CONNECTION = ((CheckBox) getActivity().findViewById(R.id.readerConnection)).isChecked();
        Application.NOTIFY_BATTERY_STATUS = ((CheckBox) getActivity().findViewById(R.id.readerBattery)).isChecked();
        Application.EXPORT_DATA = ((CheckBox) getActivity().findViewById(R.id.exportData)).isChecked();
        //if(nfcSettings !=null ) {
        //Application.NFC = nfcSettings.isChecked();
        //}
    }
}
