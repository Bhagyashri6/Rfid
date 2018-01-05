package com.enpeck.RFID.reader_connection;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;
import com.enpeck.RFID.common.Inventorytimer;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.home.HomeFragment;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.settings.SettingsContent;
import com.enpeck.RFID.settings.SettingsDetailActivity;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderDevice;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link ReadersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to maintain the list of readers
 */
public class ReadersListFragment extends Fragment {
    public static ArrayList<ReaderDevice> readersList = new ArrayList<>();
    private com.enpeck.RFID.reader_connection.PasswordDialog passwordDialog;
    public static DeviceConnectTask deviceConnectTask;
    //    private static final String RFD8500 = "RFD8500";
    private com.enpeck.RFID.reader_connection.ReaderListAdapter readerListAdapter;
    public static ListView pairedListView;
    private TextView tv_emptyView;
    private CustomProgressDialog progressDialog;
    SessionManagement session;


    String radiostr,imei,username,password;
    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> av, View v, int pos, long arg3) {
            if (MainActivity.isBluetoothEnabled()) {
                // Get the device MAC address, which is the last 17 chars in the View

                ReaderDevice readerDevice = readerListAdapter.getItem(pos);
                if (Application.mConnectedReader == null) {

                    if (deviceConnectTask == null || deviceConnectTask.isCancelled()) {
                        Application.is_connection_requested = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                            deviceConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                            deviceConnectTask.execute();
                        }
                    }
                } else {
                    {
                        if (Application.mConnectedReader.isConnected()) {
                            Application.is_disconnection_requested = true;
                            try {
                                Application.mConnectedReader.disconnect();
                            } catch (InvalidUsageException e) {
                                e.printStackTrace();
                            } catch (OperationFailureException e) {
                                e.printStackTrace();
                            }
                            //
                            bluetoothDeviceDisConnected(Application.mConnectedDevice);
                            if (Application.NOTIFY_READER_CONNECTION)
                                sendNotification(Constants.ACTION_READER_DISCONNECTED, "Disconnected from " + Application.mConnectedReader.getHostName());
                            //
                            clearSettings();
                        }
                        if (!Application.mConnectedReader.getHostName().equalsIgnoreCase(readerDevice.getName())) {
                            Application.mConnectedReader = null;
                            if (deviceConnectTask == null || deviceConnectTask.isCancelled()) {
                                Application.is_connection_requested = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                                    deviceConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), getReaderPassword(readerDevice.getName()));
                                    deviceConnectTask.execute();
                                }
                            }
                        } else {
                            Application.mConnectedReader = null;
                        }
                    }
                }
                // Create the result Intent and include the MAC address
            } else
                Toast.makeText(getActivity(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
        }
    };

    public ReadersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReadersListFragment.
     */
    public static ReadersListFragment newInstance() {
        return new ReadersListFragment();
    }

    private void clearSettings() {
        MainActivity.clearSettings();
        MainActivity.stopTimer();
       /* AssignTag.clearSettings();
        AssignTag.stopTimer();*/
        Inventorytimer.getInstance().stopTimer();
        Application.mIsInventoryRunning = false;
        if (Application.mIsInventoryRunning) {
            Application.isBatchModeInventoryRunning = false;
        }
        if (Application.isLocatingTag) {
            Application.isLocatingTag = false;
        }
        //update dpo icon in settings list
        SettingsContent.ITEMS.get(8).icon = R.drawable.title_dpo_disabled;
        Application.mConnectedDevice = null;
        Application.isAccessCriteriaRead = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_readers_list, menu);

            menu.removeItem(R.id.action_shutdown);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_readers_list, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeViews();


        SessionManagement session = new SessionManagement(getContext().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();


        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);



        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setIcon(R.drawable.dl_rdl);
        actionBar.setTitle(R.string.title_activity_readers_list);

        readersList.clear();
        loadPairedDevices();

        if (Application.mConnectedDevice != null) {
            int index = readersList.indexOf(Application.mConnectedDevice);
            if (index != -1) {
                readersList.remove(index);
                readersList.add(index, Application.mConnectedDevice);
            } else {
                Application.mConnectedDevice = null;
                Application.mConnectedReader = null;
            }
        }


        readerListAdapter = new com.enpeck.RFID.reader_connection.ReaderListAdapter(getActivity(), R.layout.readers_list_item, readersList);
        /*if(pairedListView!=null) {
            pairedListView.performItemClick(pairedListView, 0, pairedListView.getItemIdAtPosition(0));
        }*/

        if (readerListAdapter.getCount() == 0) {
            pairedListView.setEmptyView(tv_emptyView);
        } else
            pairedListView.setAdapter(readerListAdapter);

        pairedListView.setOnItemClickListener(mDeviceClickListener);
        pairedListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (readerListAdapter.getCount() == 0) {
            pairedListView.setEmptyView(tv_emptyView);

        }else {
            pairedListView.performItemClick(pairedListView, 0, pairedListView.getItemIdAtPosition(0));
            HomeFragment fragment2 = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment2);
            fragmentTransaction.commit();
        }

      //  tagg = adapter.getItem(0).getTagID();


    }

    private void initializeViews() {
        pairedListView = (ListView) getActivity().findViewById(R.id.bondedReadersList);
        tv_emptyView = (TextView) getActivity().findViewById(R.id.empty);
    }

    private void loadPairedDevices() {
        readersList.addAll(Application.readers.GetAvailableRFIDReaderList());
    }

//    /**
//     * method to check whether BT device is RFID reader
//     *
//     * @param device device to check
//     * @return true if {@link android.bluetooth.BluetoothDevice} is RFID Reader, other wise it will be false
//     */
//    public static boolean isRFIDReader(BluetoothDevice device) {
//        if (device.getName().startsWith(RFD8500))
//            return true;
//        return false;
//    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (com.enpeck.RFID.reader_connection.PasswordDialog.isDialogShowing) {
            if (passwordDialog == null || !passwordDialog.isShowing()) {
                showPasswordDialog(Application.mConnectedDevice);
            }
        }
        capabilitiesRecievedforDevice();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (passwordDialog != null && passwordDialog.isShowing()) {
            com.enpeck.RFID.reader_connection.PasswordDialog.isDialogShowing = true;
            passwordDialog.dismiss();
        }
    }

    /**
     * method to update connected reader device in the readers list on device connected event
     *
     * @param device device to be updated
     */
    public void bluetoothDeviceConnected(ReaderDevice device) {
//        if (deviceConnectTask != null)
//            deviceConnectTask.cancel(true);
        if (device != null) {
            Application.mConnectedDevice = device;
            Application.is_connection_requested = false;
            changeTextStyle(device);
        } else
            Constants.logAsMessage(Constants.TYPE_ERROR, "ReadersListFragment", "deviceName is null or empty");
    }

    public void bluetoothDeviceDisConnected(ReaderDevice device) {
        if (deviceConnectTask != null && !deviceConnectTask.isCancelled() && deviceConnectTask.getConnectingDevice().getName().equalsIgnoreCase(device.getName())) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (deviceConnectTask != null)
                deviceConnectTask.cancel(true);
        }
        if (device != null) {
            changeTextStyle(device);
        } else
            Constants.logAsMessage(Constants.TYPE_ERROR, "ReadersListFragment", "deviceName is null or empty");
        MainActivity.clearSettings();
    }

    public void readerDisconnected(ReaderDevice device) {
        if (device != null) {
            if (Application.mConnectedReader != null && !Application.AUTO_RECONNECT_READERS) {
                try {
                    Application.mConnectedReader.disconnect();
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
                Application.mConnectedReader = null;
            }
            for (int idx = 0; idx < readersList.size(); idx++) {
                if (readersList.get(idx).getName().equalsIgnoreCase(device.getName()))
                    changeTextStyle(readersList.get(idx));
            }
        }
    }

    /**
     * method to update reader device in the readers list on device connection failed event
     *
     * @param device device to be updated
     */
    public void bluetoothDeviceConnFailed(ReaderDevice device) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (deviceConnectTask != null)
            deviceConnectTask.cancel(true);
        if (device != null)
            changeTextStyle(device);
        else
            Constants.logAsMessage(Constants.TYPE_ERROR, "ReadersListFragment", "deviceName is null or empty");

        sendNotification(Constants.ACTION_READER_CONN_FAILED, "Connection Failed!! was received");

        Application.mConnectedReader = null;
        Application.mConnectedDevice = null;
    }

    /**
     * check/un check the connected/disconnected reader list item
     *
     * @param device device to be updated
     */
    private void changeTextStyle(ReaderDevice device) {
        int i = readerListAdapter.getPosition(device);
        if (i >= 0) {
            readerListAdapter.remove(device);
            readerListAdapter.insert(device,i);
            readerListAdapter.notifyDataSetChanged();
        }
    }

    public void RFIDReaderAppeared(ReaderDevice readerDevice) {
        if (readerListAdapter != null && readerDevice != null) {
            if (readerListAdapter.getCount() == 0) {
                tv_emptyView.setVisibility(View.GONE);
                pairedListView.setAdapter(readerListAdapter);
            }
            readersList.add(readerDevice);
            readerListAdapter.notifyDataSetChanged();
        }
    }

    public void RFIDReaderDisappeared(ReaderDevice readerDevice) {
        if (readerListAdapter != null && readerDevice != null) {
            readerListAdapter.remove(readerDevice);
            readersList.remove(readerDevice);
            if (readerListAdapter.getCount() == 0) {
                pairedListView.setEmptyView(tv_emptyView);
            }
            readerListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * method to update serial and model of connected reader device
     */
    public void capabilitiesRecievedforDevice() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (readerListAdapter.getPosition(Application.mConnectedDevice) >= 0) {
                    ReaderDevice readerDevice = readerListAdapter.getItem(readerListAdapter.getPosition(Application.mConnectedDevice));
                    //readerDevice.setModel(Application.mConnectedDevice.getModel());
                    //readerDevice.setSerial(Application.mConnectedDevice.getSerial());
                    readerListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * method to show connect password dialog
     *
     * @param connectingDevice
     */
    public void showPasswordDialog(ReaderDevice connectingDevice) {
        if (Application.isActivityVisible()) {
            passwordDialog = new com.enpeck.RFID.reader_connection.PasswordDialog(getActivity(), connectingDevice);
            passwordDialog.show();
        } else
            com.enpeck.RFID.reader_connection.PasswordDialog.isDialogShowing = true;
    }

    /**
     * method to cancel progress dialog
     */
    public void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (deviceConnectTask != null)
            deviceConnectTask.cancel(true);
    }

    public void ConnectwithPassword(String password, ReaderDevice readerDevice) {
        try {
            Application.mConnectedReader.disconnect();
        } catch (InvalidUsageException e) {
            e.printStackTrace();
        } catch (OperationFailureException e) {
            e.printStackTrace();
        }
        deviceConnectTask = new DeviceConnectTask(readerDevice, "Connecting with " + readerDevice.getName(), password);
        deviceConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * method to get connect password for the reader
     *
     * @param address - device BT address
     * @return connect password of the reader
     */
    private String getReaderPassword(String address) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.READER_PASSWORDS, 0);
        return sharedPreferences.getString(address, null);
    }

    private void sendNotification(String action, String data) {
        try {
            if (getActivity().getTitle().toString().equalsIgnoreCase(getString(R.string.title_activity_settings_detail)))
                ((SettingsDetailActivity) getActivity()).sendNotification(action, data);
            else
                ((MainActivity) getActivity()).sendNotification(action, data);
        }catch (Exception e){}
    }

    /**
     * async task to go for BT connection with reader
     */
    private class DeviceConnectTask extends AsyncTask<Void, String, Boolean> {
        private final ReaderDevice connectingDevice;
        private String prgressMsg;
        private OperationFailureException ex;
        private String password;

        DeviceConnectTask(ReaderDevice connectingDevice, String prgressMsg, String Password) {
            this.connectingDevice = connectingDevice;
            this.prgressMsg = prgressMsg;
            password = Password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialog(getActivity(), prgressMsg);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... a) {
            try {
                if (password == null) {
                    connectingDevice.getRFIDReader().setPassword(password);
                    connectingDevice.getRFIDReader().connect();
                }else
                if (password != null) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.READER_PASSWORDS, 0).edit();
                    editor.putString(connectingDevice.getName(), password);
                    editor.commit();
                }
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
                ex = e;
            }
            if (connectingDevice.getRFIDReader().isConnected()) {
                Application.mConnectedReader = connectingDevice.getRFIDReader();
                try {
                    Application.mConnectedReader.Events.addEventsListener(Application.eventHandler);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                }
                connectingDevice.getRFIDReader().Events.setBatchModeEvent(true);
                connectingDevice.getRFIDReader().Events.setReaderDisconnectEvent(true);
                connectingDevice.getRFIDReader().Events.setBatteryEvent(true);
                connectingDevice.getRFIDReader().Events.setInventoryStopEvent(true);
                connectingDevice.getRFIDReader().Events.setInventoryStartEvent(true);
                // if no exception in connect
                if (ex == null) {
                    try {
                        MainActivity.UpdateReaderConnection(false);
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                } else {
                    MainActivity.clearSettings();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.cancel();
            try {
                if (ex != null) {
                    if (ex.getResults() == RFIDResults.RFID_CONNECTION_PASSWORD_ERROR) {
                        showPasswordDialog(connectingDevice);
                        bluetoothDeviceConnected(connectingDevice);
                    } else if (ex.getResults() == RFIDResults.RFID_BATCHMODE_IN_PROGRESS) {
                        Application.isBatchModeInventoryRunning = true;
                        Application.mIsInventoryRunning = true;
                        bluetoothDeviceConnected(connectingDevice);
                        if (Application.NOTIFY_READER_CONNECTION)
                            sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + connectingDevice.getName());
                        //Events.StatusEventData data = Application.mConnectedReader.Events.GetStatusEventData(RFID_EVENT_TYPE.BATCH_MODE_EVENT);
//                    Intent detailsIntent = new Intent(getActivity(), MainActivity.class);
//                    detailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    detailsIntent.putExtra(RFID_EVENT_TYPE.BATCH_MODE_EVENT.toString(), 0/*data.BatchModeEventData.get_RepeatTrigger()*/);
//                    startActivity(detailsIntent);
                    } else if (ex.getResults() == RFIDResults.RFID_READER_REGION_NOT_CONFIGURED) {
                        bluetoothDeviceConnected(connectingDevice);
                        Application.regionNotSet = true;
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.set_region_msg));
                        Intent detailsIntent = new Intent(getActivity(), SettingsDetailActivity.class);
                        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        detailsIntent.putExtra(Constants.SETTING_ITEM_ID, 7);
                        startActivity(detailsIntent);
                    } else
                        bluetoothDeviceConnFailed(connectingDevice);
                } else {
                    if (result) {
                        if (Application.NOTIFY_READER_CONNECTION)
                            sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + connectingDevice.getName());
                        bluetoothDeviceConnected(connectingDevice);
                    } else {
                        bluetoothDeviceConnFailed(connectingDevice);
                    }
                }
            }catch (Exception e){}
            deviceConnectTask = null;
        }

        @Override
        protected void onCancelled() {
            deviceConnectTask = null;
            super.onCancelled();
        }

        public ReaderDevice getConnectingDevice() {
            return connectingDevice;
        }
    }
}
