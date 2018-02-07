package com.enpeck.RFID.home;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.access_operations.AccessOperationsFragment;
import com.enpeck.RFID.access_operations.AccessOperationsLockFragment;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Bean;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;
import com.enpeck.RFID.common.CustomToast;
import com.enpeck.RFID.common.Inventorytimer;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.common.ResponseHandlerInterfaces.BatteryNotificationHandler;
import com.enpeck.RFID.common.ResponseHandlerInterfaces.BluetoothDeviceFoundHandler;
import com.enpeck.RFID.common.ResponseHandlerInterfaces.TriggerEventHandler;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.data_export.DataExportTask;
import com.enpeck.RFID.inventory.InventoryFragment2;
import com.enpeck.RFID.inventory.InventoryListItem;
import com.enpeck.RFID.inventory.ModifiedInventoryAdapter;
import com.enpeck.RFID.locate_tag.LocationingFragment;
import com.enpeck.RFID.locate_tag.RangeGraph;
import com.enpeck.RFID.notifications.NotificationsService;
import com.enpeck.RFID.rapidread.RapidReadFragment;
import com.enpeck.RFID.reader_connection.ReadersListFragment;
import com.enpeck.RFID.settings.BatteryFragment;
import com.enpeck.RFID.settings.PreFilterFragment;
import com.enpeck.RFID.settings.SettingListFragment;
import com.enpeck.RFID.settings.SettingsContent;
import com.enpeck.RFID.vertifyTag.IncomingContainer;
import com.enpeck.RFID.vertifyTag.Verifiy_Tag;
import com.enpeck.RFID.vertifyTag.Verify_Container;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.BATCH_MODE;
import com.zebra.rfid.api3.Events;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.LOCK_DATA_FIELD;
import com.zebra.rfid.api3.LOCK_PRIVILEGE;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TAG_FIELD;
import com.zebra.rfid.api3.TagAccess;
import com.zebra.rfid.api3.TagData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity implements Readers.RFIDReaderEventHandler {
    //Tag to identify the currently displayed fragment
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    //Messages for progress bar
    private static final String MSG_READ = "Reading Tags";
    private static final String MSG_WRITE = "Writing Data";
    private static final String MSG_LOCK = "Executing Lock Command";
    private static final String MSG_KILL = "Executing Kill Command";
    public static Timer t;
    private static ArrayList<BluetoothDeviceFoundHandler> bluetoothDeviceFoundHandlers = new ArrayList<>();
    private static ArrayList<BatteryNotificationHandler> batteryNotificationHandlers = new ArrayList<>();
    protected boolean isInventoryAborted;
    protected boolean isLocationingAborted;
    protected int accessTagCount;
    //To indicate indeterminate progress
    protected CustomProgressDialog progressDialog;
    protected Menu menu;
    //Special layout for Navigation Drawer
    private DrawerLayout mDrawerLayout;
    //List view for navigation drawer items
    private ListView mDrawerList;
    LinearLayout drawer;
    //For navigation drawer UI
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mOptionTitles;
    private Boolean isTriggerRepeat;
    private boolean pc = false;
    private boolean rssi = false;
    private boolean phase = false;
    private boolean channelIndex = false;
    private boolean tagSeenCount = false;
    private boolean isDeviceDisconnected = false;
    private AsyncTask<Void, Void, Boolean> DisconnectTask;
    SessionManagement session;
    private ModifiedInventoryAdapter adapter;


    private static final String URL = "http://atm-india.in/EnopeckService.asmx";
    // private static final String URL = "http://atm-india.in/RFIDDemoService.asmx";

    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String METHOD_NAMEimemi = "Rfidimei1";
    private static final String SOAP_ACTIONimei = "http://tempuri.org/Rfidimei1";
    Boolean serverissue = false;
    String uname,deviceIMEI;


    public static ImageView batteryLevelImage;

    ImageView image;

    LinearLayout master,tag;

    String radiostr,imei,username,password;
    /**
     * method to know whether bluetooth is enabled or not
     *
     * @return - true if bluetooth enabled
     * - false if bluetooth disabled
     */
    public static boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();


    }

    /**
     * Method for registering the classes for device events like paired,unpaired, connected and disconnected.
     * The registered classes will get notified when device event occurs.
     *
     * @param bluetoothDeviceFoundHandler - handler class to register with base receiver activity
     */
    public static void addBluetoothDeviceFoundHandler(BluetoothDeviceFoundHandler bluetoothDeviceFoundHandler) {
        bluetoothDeviceFoundHandlers.add(bluetoothDeviceFoundHandler);
    }

    public static void addBatteryNotificationHandler(BatteryNotificationHandler batteryNotificationHandler) {
        batteryNotificationHandlers.add(batteryNotificationHandler);
    }

    /**
     * method to start a timer task to get device battery status per every 6 sec
     */
    public static void startTimer() {
        if (t == null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (Application.mConnectedReader != null)
                            Application.mConnectedReader.Config.getDeviceStatus(true, false, false);
                        else
                            stopTimer();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                }
            };
            t = new Timer();
            t.scheduleAtFixedRate(task, 0, 60000);
        }
    }

    /**
     * method to clear reader's settings on disconnection
     */
    public static void clearSettings() {
        Application.antennaPowerLevel = null;
        Application.antennaRfConfig = null;
        Application.singulationControl = null;
        Application.rfModeTable = null;
        Application.regulatory = null;
        Application.batchMode = -1;
        Application.tagStorageSettings = null;
        Application.reportUniquetags = null;
        Application.dynamicPowerSettings = null;
        Application.settings_startTrigger = null;
        Application.settings_stopTrigger = null;
        Application.beeperVolume = null;
        Application.preFilters = null;
        if (Application.versionInfo != null)
            Application.versionInfo.clear();
        Application.regionNotSet = false;
        Application.isBatchModeInventoryRunning = null;
        Application.BatteryData = null;
        Application.is_disconnection_requested = false;
        Application.mConnectedDevice = null;
//        Application.mConnectedReader = null;
    }

    /**
     * method to stop timer
     */
    public static void stopTimer() {
        if (t != null) {
            t.cancel();
            t.purge();
        }
        t = null;
    }

    public static void UpdateReaderConnection(Boolean fullUpdate) throws InvalidUsageException, OperationFailureException {
        Application.mConnectedReader.Events.setBatchModeEvent(true);
        Application.mConnectedReader.Events.setReaderDisconnectEvent(true);
        Application.mConnectedReader.Events.setInventoryStartEvent(true);
        Application.mConnectedReader.Events.setInventoryStopEvent(true);
        Application.mConnectedReader.Events.setTagReadEvent(true);
        Application.mConnectedReader.Events.setHandheldEvent(true);
        Application.mConnectedReader.Events.setBatteryEvent(true);
        Application.mConnectedReader.Events.setPowerEvent(true);
        Application.mConnectedReader.Events.setOperationEndSummaryEvent(true);

        if (fullUpdate)
            Application.mConnectedReader.PostConnectReaderUpdate();

        Application.regulatory = Application.mConnectedReader.Config.getRegulatoryConfig();
        Application.regionNotSet = false;
        Application.rfModeTable = Application.mConnectedReader.ReaderCapabilities.RFModes.getRFModeTableInfo(0);
        Application.antennaRfConfig = Application.mConnectedReader.Config.Antennas.getAntennaRfConfig(1);
        Application.singulationControl = Application.mConnectedReader.Config.Antennas.getSingulationControl(1);
        Application.settings_startTrigger = Application.mConnectedReader.Config.getStartTrigger();
        Application.settings_stopTrigger = Application.mConnectedReader.Config.getStopTrigger();
        Application.tagStorageSettings = Application.mConnectedReader.Config.getTagStorageSettings();
        Application.dynamicPowerSettings = Application.mConnectedReader.Config.getDPOState();
        Application.beeperVolume = Application.mConnectedReader.Config.getBeeperVolume();
        Application.batchMode = Application.mConnectedReader.Config.getBatchModeConfig().getValue();
        Application.reportUniquetags = Application.mConnectedReader.Config.getUniqueTagReport();
        Application.mConnectedReader.Config.getDeviceVersionInfo(Application.versionInfo);
        //Log.d("RFIDDEMO","SCANNERNAME: " + Application.mConnectedReader.ReaderCapabilities.getScannerName());
        startTimer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        master =(LinearLayout)findViewById(R.id.master);
        tag =(LinearLayout)findViewById(R.id.tag);

        batteryLevelImage = (ImageView) findViewById(R.id.batteryLevelImage);

        mTitle = mDrawerTitle = getTitle();
        mOptionTitles = getResources().getStringArray(R.array.options_array11);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        TelephonyManager tManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        deviceIMEI = tManager.getDeviceId();
        Log.d("rfid", "onCreate: "+deviceIMEI);

        new AsynkImei(deviceIMEI).execute();


     //   mDrawerList.performItemClick(mDrawerList, 0, mDrawerList.getVisibility());

     //   drawer =(LinearLayout)findViewById(R.id.left_drawer);

        //Initialize collapsed height for inventory list
        // initializeCollapsedHeight();


        // set a custom shadow that overlays the no_items content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_list_item, DrawerListContent.ITEMS   ));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());





        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
       /* mDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.your_custom_icon, getActivity().getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);*/

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_menu,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                int drawableRsourceId = getActionBarIcon();
                if (drawableRsourceId != -1)
                    getSupportActionBar().setIcon(drawableRsourceId);
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setIcon(R.drawable.ic_launcher);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {

            selectItem(0);
        }

        Application.eventHandler = new EventHandler();
        Inventorytimer.getInstance().setActivity(this);
        initializeConnectionSettings();

        if (Application.readers == null) {
            Application.readers = new Readers();
        }
        Application.readers.attach(this);


        if (!isBluetoothEnabled()) {


            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
        }




    }

    /**
     * Method to initialize the connection settings like notifications, auto detection, auto reconnection etc..
     */
    private void initializeConnectionSettings() {
        SharedPreferences settings = getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        Application.AUTO_DETECT_READERS = settings.getBoolean(Constants.AUTO_DETECT_READERS, true);
        Application.AUTO_RECONNECT_READERS = settings.getBoolean(Constants.AUTO_RECONNECT_READERS, false);
        Application.NOTIFY_READER_AVAILABLE = settings.getBoolean(Constants.NOTIFY_READER_AVAILABLE, false);
        Application.NOTIFY_READER_CONNECTION = settings.getBoolean(Constants.NOTIFY_READER_CONNECTION, false);
        Application.NOTIFY_BATTERY_STATUS = settings.getBoolean(Constants.NOTIFY_BATTERY_STATUS, true);
        Application.EXPORT_DATA = settings.getBoolean(Constants.EXPORT_DATA, false);
    }

    @Override
    protected void onDestroy() {
        //
        if (DisconnectTask != null)
            DisconnectTask.cancel(true);
        //disconnect from reader
        try {
            if (Application.mConnectedReader != null) {
                Application.mConnectedReader.Events.removeEventsListener(Application.eventHandler);
                Application.mConnectedReader.disconnect();
            }
        } catch (InvalidUsageException e) {
            e.printStackTrace();
        } catch (OperationFailureException e) {
            e.printStackTrace();
        }
        Application.mConnectedReader = null;
        //stop Timer
        Inventorytimer.getInstance().stopTimer();
        stopTimer();
        //update dpo icon in settings list
        SettingsContent.ITEMS.get(8).icon = R.drawable.title_dpo_disabled;
        clearSettings();
        Application.mConnectedDevice = null;
//        Application.mConnectedReader = null;
        ReadersListFragment.readersList.clear();
        Application.readers.deattach(this);
        Application.readers.deattach(this);
        Application.reset();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReadersListFragment fragment2 = new ReadersListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment2);
        fragmentTransaction.commit();

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if (drawerOpen) {
            //Hide the keyboard if it's showing when the drawer opens
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            if (getSupportActionBar() != null) {
                //Hide the tabs if they are visible
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        } else {
            if (getSupportActionBar() != null) {
                //If we are showing pre-filters or access options, show tabs
                if (getSupportFragmentManager() != null && getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT) != null &&
                        (getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT) instanceof PreFilterFragment ||
                                getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT) instanceof AccessOperationsFragment)) {
                    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_home:

                getSupportActionBar().setTitle("Home");
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        this.menu = menu;
        if (Application.BatteryData != null)
            setActionBarBatteryStatus(Application.BatteryData.getLevel());
        return true;
    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * Method called on the click of a NavigationDrawer item to update the UI with the new selection
     *
     * @param position - postion of the item selected
     */
    private void selectItem(int position) {


        // update the no_items content by replacing fragments
        Fragment fragment = null;

        switch (position) {

            case 0:
                fragment = HomeFragment.newInstance();
                break;

         /*   case 1:
                fragment = MasterData.newInstance();
                break;

            case 2:
                fragment = InventoryFragment.newInstance();
                break;
*/
            case 1:
                fragment = IncomingContainer.newInstance();
                break;

            case 2:
                fragment = Verify_Container.newInstance();
                break;

            case 3:
                fragment = Verifiy_Tag.newInstance();
                break;

            case 4:
                fragment = ReadersListFragment.newInstance();
                break;

            case 5:

                fragment =AboutFragment.newInstance();
                break;

            case 6:
                fragment = BatteryFragment.newInstance();
                break;

        /*    case 4:
                fragment = DailyReportFragment.newInstance();
                break;

            case 5:
                fragment = DeviceCompFragment.newInstance();
                break;
*/


/*

            case 7:
                fragment = Locationfragment.newInstance();
                break;

            case 8:
                fragment = AboutFragment.newInstance();
                break;

            case 9:
                fragment = StockSealReportFragment.newInstance();
                break;

            case 10:
                fragment = BatteryFragment.newInstance();
                break;
*/


        }

            FragmentManager fragmentManager =getSupportFragmentManager();
            if (position == 0) {
                //Pop the back stack since we want to maintain only one level of the back stack
                //Don't add the transaction to back stack since we are navigating to the first fragment
                //being displayed and adding the same to the backstack will result in redundancy
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).commit();
            } else {
                //Pop the back stack since we want to maintain only one level of the back stack
                //Add the transaction to the back stack since we want the state to be preserved in the back stack
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
            }

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mOptionTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    /**
     * method to get currently displayed action bar icon
     *
     * @return resource id of the action bar icon
     */


    private int getActionBarIcon() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
     /*   if (fragment instanceof MasterData)
            return R.mipmap.master;
        else if (fragment instanceof InventoryFragment)
            return R.mipmap.tag;
        else*/ if (fragment instanceof Verifiy_Tag)
            return R.mipmap.vertify3;
        else if (fragment instanceof ReadersListFragment)
            return R.mipmap.connectivity;
        else if (fragment instanceof IncomingContainer)
            return R.drawable.ic_truck;
        else if (fragment instanceof Verify_Container)
            return R.drawable.ic_check_box_black_24dp;
        else if (fragment instanceof AboutFragment)
            return R.mipmap.about;
        else if (fragment instanceof BatteryFragment)
            return R.drawable.battery_level;
        else
            return -1;



            /*if (fragment instanceof DailyReportFragment)
            return R.mipmap.report;
        else if (fragment instanceof DeviceCompFragment)
            return R.drawable.dl_sett;
        else*/  /*if (fragment instanceof Locationfragment)
            return R.mipmap.location;
        else if (fragment instanceof AboutFragment)
            return R.mipmap.about;
        else if (fragment instanceof StockSealReportFragment)
            return R.mipmap.sheet;
        else if (fragment instanceof BatteryFragment)
            return R.drawable.title_batt;
        else */
    }


    @Override
    public void onResume() {
        super.onResume();
        Application.activityResumed();


    }

    /**
     * call back of activity,which will call before activity went to paused
     */
    @Override
    public void onPause() {
        super.onPause();
        Application.activityPaused();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment != null && fragment instanceof InventoryFragment2 && newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            findViewById(R.id.inventoryDataLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.inventoryButton).setVisibility(View.INVISIBLE);
        } else if (fragment != null && fragment instanceof InventoryFragment2 && newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            findViewById(R.id.inventoryDataLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.inventoryButton).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
     /*   //update the selected item in the drawer and the title
        mDrawerList.setItemChecked(0, true);
        setTitle(mOptionTitles[0]);
        //We are handling back pressed for saving pre-filters settings. Notify the appropriate fragment.
        //{@link BaseReceiverActivity # onBackPressed should be called by the fragment when the processing is done}
        //super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment != null && fragment instanceof BackPressedFragment) {
            ((BackPressedFragment) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }*/
    }

    /**
     * Callback method to handle the click of start/stop button in the inventory fragment
     *
     * @param v - Button Clicked
     */
    public void inventoryStartOrStop(View v) {
        Button button = (Button) v;
       /* View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        String tagg =adapter.getItem(position).getTagID();*/

        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                if (!Application.mIsInventoryRunning) {
                    clearInventoryData();
                   // button.setText("STOP");
                    //Here we send the inventory command to start reading the tags
                    if (fragment != null && fragment instanceof InventoryFragment2) {
                        Spinner memoryBankSpinner = ((Spinner) findViewById(R.id.inventoryOptions));
                        memoryBankSpinner.setSelection(Application.memoryBankId);
                        memoryBankSpinner.setEnabled(false);
                        ((InventoryFragment2) fragment).resetTagsInfo();
                    }
                    //set flag value
                    isInventoryAborted = false;
                    Application.mIsInventoryRunning = true;
                    getTagReportingfields();

                    if (fragment != null && fragment instanceof InventoryFragment2 && !((InventoryFragment2) fragment).getMemoryBankID().equalsIgnoreCase("none")) {
                        //If memory bank is selected, call read command with appropriate memory bank
                        inventoryWithMemoryBank(((InventoryFragment2) fragment).getMemoryBankID());
                    } else {
                        if (fragment != null && fragment instanceof RapidReadFragment) {
                            Application.memoryBankId = -1;
                            ((RapidReadFragment) fragment).resetTagsInfo();
                        }
                        // unique read is enabled
                        if(Application.reportUniquetags!= null && Application.reportUniquetags.getValue() == 1) {
                            Application.mConnectedReader.Actions.purgeTags();
                        }
                        //Perform inventory
                        if (Application.inventoryMode == 0) {
                            try {
                                Application.mConnectedReader.Actions.Inventory.perform();
                            } catch (InvalidUsageException e) {
                                e.printStackTrace();
                            } catch (final OperationFailureException e) {
                                e.printStackTrace();
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                                            if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                                                ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(e.getResults());
                                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getVendorMessage());
                                        }
                                    });
                                }
                            }
                            if (Application.batchMode != -1) {
                                if (Application.batchMode == BATCH_MODE.ENABLE.getValue())
                                    Application.isBatchModeInventoryRunning = true;
                            }
                        }
                    }
                } else if (Application.mIsInventoryRunning) {
                    if (fragment != null && fragment instanceof InventoryFragment2) {
                        ((Spinner) findViewById(R.id.inventoryOptions)).setEnabled(true);
                    }
                 //   button.setText("START");
                    isInventoryAborted = true;
                    //Here we send the abort command to stop the inventory
                    try {
                        Application.mConnectedReader.Actions.Inventory.stop();
                        if (((Application.settings_startTrigger != null && (Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC)))
                                || (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning))
                            operationHasAborted();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }

                }
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    private void getTagReportingfields() {
        pc = false;
        phase = false;
        channelIndex = false;
        rssi = false;
        if (Application.tagStorageSettings != null) {
            TAG_FIELD[] tag_field = Application.tagStorageSettings.getTagFields();
            for (int idx = 0; idx < tag_field.length; idx++) {
                if (tag_field[idx] == TAG_FIELD.PEAK_RSSI)
                    rssi = true;
                if (tag_field[idx] == TAG_FIELD.PHASE_INFO)
                    phase = true;
                if (tag_field[idx] == TAG_FIELD.PC)
                    pc = true;
                if (tag_field[idx] == TAG_FIELD.CHANNEL_INDEX)
                    channelIndex = true;
                if (tag_field[idx] == TAG_FIELD.TAG_SEEN_COUNT)
                    tagSeenCount = true;
            }
        }
    }

    /**
     * Method to call when we want inventory to happen with memory bank parameters
     *
     * @param memoryBankID id of the memory bank
     */
    public void inventoryWithMemoryBank(String memoryBankID) {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                TagAccess tagAccess = new TagAccess();
                TagAccess.ReadAccessParams readAccessParams = tagAccess.new ReadAccessParams();
                //Set the param values
                readAccessParams.setByteCount(0);
                readAccessParams.setByteOffset(0);

                if ("RESERVED".equalsIgnoreCase(memoryBankID))
                    readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_RESERVED);
                if ("EPC".equalsIgnoreCase(memoryBankID))
                    readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_EPC);
                if ("TID".equalsIgnoreCase(memoryBankID))
                    readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_TID);
                if ("USER".equalsIgnoreCase(memoryBankID))
                    readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_USER);
                try {
                    //Read command with readAccessParams and accessFilter as null to read all the tags
                    Application.mConnectedReader.Actions.TagAccess.readEvent(readAccessParams, null, null);
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                    if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                        ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(e.getResults());
                    Toast.makeText(getApplicationContext(), e.getVendorMessage(), Toast.LENGTH_SHORT).show();
                }

            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called when read button in AccessOperationsFragment is clicked
     *
     * @param v - Read Button
     */
    public void accessOperationsReadClicked(View v) {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                if (Application.mConnectedReader.isCapabilitiesReceived()) {
                    AutoCompleteTextView tagIDField = ((AutoCompleteTextView) findViewById(R.id.accessRWTagID));
                    if (tagIDField != null && tagIDField.getText() != null) {
                        final String tagId = tagIDField.getText().toString();
                        if (!tagId.isEmpty()) {
                            Application.accessControlTag = tagId;
                            EditText offsetText = (EditText) findViewById(R.id.accessRWOffsetValue);
                            EditText lengthText = (EditText) findViewById(R.id.accessRWLengthValue);
                            if (!offsetText.getText().toString().isEmpty()) {
                                if (!lengthText.getText().toString().isEmpty()) {
                                    progressDialog = new CustomProgressDialog(this, MSG_READ);
                                    progressDialog.show();
                                    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);

                                    TextView accessRWData = (TextView) findViewById(R.id.accessRWData);
                                    if (accessRWData != null) {
                                        accessRWData.setText("");
                                    }
                                    timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Read");
                                    //Stop the read after a tag is read to avoid performing a continuous read operation
                                    //((AccessOperationsFragment) fragment).setStartStopTriggers();
                                    Application.isAccessCriteriaRead = true;

                                    TagAccess tagAccess = new TagAccess();
                                    final TagAccess.ReadAccessParams readAccessParams = tagAccess.new ReadAccessParams();
                                    readAccessParams.setAccessPassword(Long.decode("0X" + ((EditText) findViewById(R.id.accessRWPassword)).getText().toString()));
                                    readAccessParams.setByteCount(Integer.parseInt(lengthText.getText().toString()));

                                    if ("RESV".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                        readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_RESERVED);
                                    if ("EPC".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                        readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_EPC);
                                    if ("TID".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                        readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_TID);
                                    if ("USER".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                        readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_USER);

                                    readAccessParams.setByteOffset(Integer.parseInt(offsetText.getText().toString()));

                                    new AsyncTask<Void, Void, Boolean>() {
                                        private InvalidUsageException invalidUsageException;
                                        private OperationFailureException operationFailureException;

                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            try {
                                                final TagData tagData = Application.mConnectedReader.Actions.TagAccess.readWait(tagId, readAccessParams, null);
                                                if (Application.isAccessCriteriaRead && !Application.mIsInventoryRunning) {
                                                    if (fragment instanceof AccessOperationsFragment)
                                                        ((AccessOperationsFragment) fragment).handleTagResponse(tagData);
                                                }
                                            } catch (InvalidUsageException e) {
                                                invalidUsageException = e;
                                                e.printStackTrace();
                                            } catch (OperationFailureException e) {
                                                operationFailureException = e;
                                                e.printStackTrace();
                                            }
                                            return true;
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean result) {
                                            if (invalidUsageException != null) {
                                                progressDialog.dismiss();
                                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                                            } else if (operationFailureException != null) {
                                                progressDialog.dismiss();
                                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                                            }
                                        }
                                    }.execute();
                                } else
                                    Toast.makeText(getApplicationContext(), getString(R.string.empty_length), Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), getString(R.string.empty_offset), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), Constants.TAG_EMPTY, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_reader_not_updated), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called when write button in AccessOperationsFragment is clicked
     *
     * @param v - Write Button
     */
    public void accessOperationsWriteClicked(View v) {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                if (Application.mConnectedReader.isCapabilitiesReceived()) {
                    AutoCompleteTextView tagIDField = ((AutoCompleteTextView) findViewById(R.id.accessRWTagID));
                    if (tagIDField != null && tagIDField.getText() != null) {
                        final String tagId = tagIDField.getText().toString();
                        if (!tagId.isEmpty()) {
                            Application.accessControlTag = tagId;
                            EditText offsetText = (EditText) findViewById(R.id.accessRWOffsetValue);
                            if (!offsetText.getText().toString().isEmpty()) {
                                progressDialog = new CustomProgressDialog(this, MSG_WRITE);
                                progressDialog.show();
                                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                                timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Write");
                                Application.isAccessCriteriaRead = true;

                                //((AccessOperationsFragment) fragment).setStartStopTriggers();

                                TagAccess tagAccess = new TagAccess();
                                final TagAccess.WriteAccessParams writeAccessParams = tagAccess.new WriteAccessParams();

                                writeAccessParams.setAccessPassword(Long.decode("0X" + ((EditText) findViewById(R.id.accessRWPassword)).getText().toString()));
                                writeAccessParams.setWriteDataLength(((TextView) findViewById(R.id.accessRWData)).getText().toString().length()/4);

                                if ("RESV".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                    writeAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_RESERVED);
                                if ("EPC".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                    writeAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_EPC);
                                if ("TID".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                    writeAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_TID);
                                if ("USER".equalsIgnoreCase(((Spinner) findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString()))
                                    writeAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_USER);

                                writeAccessParams.setByteOffset(Integer.parseInt(((EditText) findViewById(R.id.accessRWOffsetValue)).getText().toString()));
                                writeAccessParams.setWriteData(((TextView) findViewById(R.id.accessRWData)).getText().toString());

                                new AsyncTask<Void, Void, Boolean>() {
                                    private InvalidUsageException invalidUsageException;
                                    private OperationFailureException operationFailureException;
                                    private Boolean bResult = false;

                                    @Override
                                    protected Boolean doInBackground(Void... voids) {
                                        try {
                                            Application.mConnectedReader.Actions.TagAccess.writeWait(tagId, writeAccessParams, null, null);
                                            bResult = true;
                                        } catch (InvalidUsageException e) {
                                            invalidUsageException = e;
                                            e.printStackTrace();
                                        } catch (OperationFailureException e) {
                                            operationFailureException = e;
                                            e.printStackTrace();
                                        }
                                        return bResult;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        if (!result) {
                                            if (invalidUsageException != null) {
                                                progressDialog.dismiss();
                                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                                            } else if (operationFailureException != null) {
                                                progressDialog.dismiss();
                                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                                            }
                                        } else
                                            Toast.makeText(getApplicationContext(), getString(R.string.msg_write_succeed), Toast.LENGTH_SHORT).show();
                                    }
                                }.execute();
                            } else
                                Toast.makeText(getApplicationContext(), getString(R.string.empty_offset), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), Constants.TAG_EMPTY, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_reader_not_updated), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called when lock button in AccessOperationsFragment is clicked
     *
     * @param v - Lock button
     */
    public void accessOperationLockClicked(View v) {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                if (Application.mConnectedReader.isCapabilitiesReceived()) {
                    AutoCompleteTextView tagIDField = ((AutoCompleteTextView) findViewById(R.id.accessLockTagID));
                    if (tagIDField != null && tagIDField.getText() != null) {
                        final String tagId = tagIDField.getText().toString();
                        if (!tagId.isEmpty()) {
                            Application.accessControlTag = tagId;
                            progressDialog = new CustomProgressDialog(this, MSG_LOCK);
                            progressDialog.show();
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                            timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Lock");
                            Application.isAccessCriteriaRead = true;

                            ((AccessOperationsFragment) fragment).setStartStopTriggers();

                            //Set the param values
                            TagAccess tagAccess = new TagAccess();
                            final TagAccess.LockAccessParams lockAccessParams = tagAccess.new LockAccessParams();
                            if (fragment != null && fragment instanceof AccessOperationsFragment) {
                                Fragment innerFragment = ((AccessOperationsFragment) fragment).getCurrentlyViewingFragment();
                                if (innerFragment != null && innerFragment instanceof AccessOperationsLockFragment) {
                                    AccessOperationsLockFragment lockFragment = ((AccessOperationsLockFragment) innerFragment);
                                    String lockMemoryBank = lockFragment.getLockMemoryBank();
                                    if (lockMemoryBank != null && !lockMemoryBank.isEmpty()) {
                                        if (lockMemoryBank.equalsIgnoreCase("epc")) {

                                            if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_EPC_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_EPC_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_EPC_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_EPC_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK);

                                        } else if (lockMemoryBank.equalsIgnoreCase("tid")) {

                                            if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_TID_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_TID_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_TID_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_TID_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK);

                                        } else if (lockMemoryBank.equalsIgnoreCase("user")) {

                                            if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_USER_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_USER_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_USER_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_USER_MEMORY, LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK);

                                        } else if (lockMemoryBank.equalsIgnoreCase("access pwd")) {

                                            if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_ACCESS_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_ACCESS_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_ACCESS_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_ACCESS_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK);

                                        } else if (lockMemoryBank.equalsIgnoreCase("kill pwd")) {
                                            if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_KILL_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_READ_WRITE);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_KILL_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_LOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_KILL_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_PERMA_UNLOCK);
                                            else if (lockFragment.getLockAccessPermission().equals(LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK))
                                                lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_KILL_PASSWORD, LOCK_PRIVILEGE.LOCK_PRIVILEGE_UNLOCK);
                                        }
                                    }
                                }
                            }
                            lockAccessParams.setAccessPassword(Long.decode("0X" + ((EditText) findViewById(R.id.accessLockPassword)).getText().toString()));
                            new AsyncTask<Void, Void, Boolean>() {
                                private InvalidUsageException invalidUsageException;
                                private OperationFailureException operationFailureException;
                                private Boolean bResult = false;

                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    try {
                                        Application.mConnectedReader.Actions.TagAccess.lockWait(tagId, lockAccessParams, null);
                                        bResult = true;
                                    } catch (InvalidUsageException e) {
                                        invalidUsageException = e;
                                        e.printStackTrace();
                                    } catch (OperationFailureException e) {
                                        operationFailureException = e;
                                        e.printStackTrace();
                                    }
                                    return bResult;
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if (!result) {
                                        if (invalidUsageException != null) {
                                            progressDialog.dismiss();
                                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                                        } else if (operationFailureException != null) {
                                            progressDialog.dismiss();
                                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                                        }
                                    } else
                                        Toast.makeText(getApplicationContext(), getString(R.string.msg_lock_succeed), Toast.LENGTH_SHORT).show();
                                }
                            }.execute();
                        } else
                            Toast.makeText(getApplicationContext(), Constants.TAG_EMPTY, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_reader_not_updated), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called when kill button in AccessOperationsFragment is clicked
     *
     * @param v - Kill button
     */
    public void accessOperationsKillClicked(View v) {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                if (Application.mConnectedReader.isCapabilitiesReceived()) {
                    AutoCompleteTextView tagIDField = ((AutoCompleteTextView) findViewById(R.id.accessKillTagID));
                    if (tagIDField != null && tagIDField.getText() != null) {
                        final String tagId = tagIDField.getText().toString();
                        if (!tagId.isEmpty()) {
                            Application.accessControlTag = tagId;
                            progressDialog = new CustomProgressDialog(this, MSG_KILL);
                            progressDialog.show();
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                            timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Kill");
                            Application.isAccessCriteriaRead = true;

                            //((AccessOperationsFragment) fragment).setStartStopTriggers();
                            //Set the param values
                            TagAccess tagAccess = new TagAccess();
                            final TagAccess.KillAccessParams killAccessParams = tagAccess.new KillAccessParams();
                            killAccessParams.setKillPassword(Long.decode("0X" + ((EditText) findViewById(R.id.accessKillPassword)).getText().toString()));
                            new AsyncTask<Void, Void, Boolean>() {
                                private InvalidUsageException invalidUsageException;
                                private OperationFailureException operationFailureException;
                                private Boolean bResult = false;

                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    try {
                                        Application.mConnectedReader.Actions.TagAccess.killWait(tagId, killAccessParams, null);
                                        bResult = true;
                                    } catch (InvalidUsageException e) {
                                        invalidUsageException = e;
                                        e.printStackTrace();
                                    } catch (OperationFailureException e) {
                                        operationFailureException = e;
                                        e.printStackTrace();
                                    }
                                    return bResult;
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if (!result) {
                                        if (invalidUsageException != null) {
                                            progressDialog.dismiss();
                                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                                        } else if (operationFailureException != null) {
                                            progressDialog.dismiss();
                                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                                        }
                                    } else
                                        Toast.makeText(getApplicationContext(), getString(R.string.msg_kill_succeed), Toast.LENGTH_SHORT).show();
                                }
                            }.execute();
                        } else
                            Toast.makeText(getApplicationContext(), Constants.TAG_EMPTY, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_reader_not_updated), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called when stop in locationing is clicked
     *
     * @param v - Locationing stop clicked
     */
    public void locationingButtonClicked(View v) {
        if (isBluetoothEnabled()) {
            if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
                if (!Application.isLocatingTag) {
                    //clear previous proximity details
                    Application.TagProximityPercent = 0;
                    Application.locateTag = ((AutoCompleteTextView) findViewById(R.id.lt_et_epc)).getText().toString();
                    if (!Application.locateTag.isEmpty()) {
                        ((AutoCompleteTextView) findViewById(R.id.lt_et_epc)).setFocusable(false);
                        ((Button) v).setText(getResources().getString(R.string.stop_title));

                        RangeGraph locationBar = (RangeGraph) findViewById(R.id.locationBar);
                        locationBar.setValue(0);
                        locationBar.invalidate();
                        locationBar.requestLayout();
                        Application.isLocatingTag = true;
                        new AsyncTask<Void, Void, Boolean>() {
                            private InvalidUsageException invalidUsageException;
                            private OperationFailureException operationFailureException;

                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                try {
                                    Application.mConnectedReader.Actions.TagLocationing.Perform(Application.locateTag, null, null);
//                                    Application.isLocatingTag = true;
                                } catch (InvalidUsageException e) {
                                    e.printStackTrace();
                                    invalidUsageException = e;
                                } catch (OperationFailureException e) {
                                    e.printStackTrace();
                                    operationFailureException = e;
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Boolean result) {
                                if (invalidUsageException != null) {
                                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                                } else if (operationFailureException != null) {
                                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                                    if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                                        ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(operationFailureException.getResults());
                                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                                }
                            }
                        }.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), Constants.TAG_EMPTY, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    new AsyncTask<Void, Void, Boolean>() {
                        private InvalidUsageException invalidUsageException;
                        private OperationFailureException operationFailureException;

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                Application.mConnectedReader.Actions.TagLocationing.Stop();
                                if (((Application.settings_startTrigger != null && (Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC)))
                                        || (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning))
                                    operationHasAborted();
                            } catch (InvalidUsageException e) {
                                invalidUsageException = e;
                                e.printStackTrace();
                            } catch (OperationFailureException e) {
                                operationFailureException = e;
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (invalidUsageException != null) {
                                progressDialog.dismiss();
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                            } else if (operationFailureException != null) {
                                progressDialog.dismiss();
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                            }
                        }
                    }.execute();
                    ((Button) v).setText(getResources().getString(R.string.start_title));
                    isLocationingAborted = true;
                    (findViewById(R.id.lt_et_epc)).setFocusableInTouchMode(true);
                    (findViewById(R.id.lt_et_epc)).setFocusable(true);
                }
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * method to stop progress dialog on timeout
     *
     * @param time
     * @param d
     * @param command
     */
    public void timerDelayRemoveDialog(long time, final Dialog d, final String command) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (d != null && d.isShowing()) {
                    d.dismiss();
                    if (Application.isAccessCriteriaRead) {
                        if (accessTagCount == 0)
                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.err_access_op_failed));
                        Application.isAccessCriteriaRead = false;
                    } else {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, command + " timeout");
                        if (Application.isActivityVisible())
                            callBackPressed();
                    }
                }
            }
        }, time);
    }

    /*
     * Method to send the notification
     *
     * @param action - intent action
     * @param data   - notification message
     */
    public void sendNotification(String action, String data) {
        if (Application.isActivityVisible()) {
            if (action.equalsIgnoreCase(Constants.ACTION_READER_BATTERY_CRITICAL) || action.equalsIgnoreCase(Constants.ACTION_READER_BATTERY_LOW)) {
                new CustomToast(MainActivity.this, R.layout.toast_layout, data).show();
            } else {
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent i = new Intent(MainActivity.this, NotificationsService.class);
            i.putExtra(Constants.INTENT_ACTION, action);
            i.putExtra(Constants.INTENT_DATA, data);
            startService(i);
        }
    }

    /**
     * Method to be called from Fragments of this activity after handling the response from the reader(success / failure)
     */
    public void callBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.super.onBackPressed();
            }
        });
    }

    /**
     * Method to change operation status and ui in app on recieving abort status
     */
    private void operationHasAborted() {
        //retrieve get tags if inventory in batch mode got aborted
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            if (isInventoryAborted) {
                Application.isBatchModeInventoryRunning = false;
                isInventoryAborted = true;
                Application.isGettingTags = true;
                if (Application.settings_startTrigger == null) {
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                if (Application.mConnectedReader.isCapabilitiesReceived())
                                    UpdateReaderConnection(false);
                                else
                                    UpdateReaderConnection(true);
                                // update fields before getting tags
                                getTagReportingfields();
                                //
                                Application.mConnectedReader.Actions.getBatchedTags();
                            } catch (InvalidUsageException e) {
                                e.printStackTrace();
                            } catch (OperationFailureException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                } else
                    Application.mConnectedReader.Actions.getBatchedTags();
            }
        }

        if (Application.mIsInventoryRunning) {
            if (isInventoryAborted) {
                Application.mIsInventoryRunning = false;
                isInventoryAborted = false;
                isTriggerRepeat = null;
                if (Inventorytimer.getInstance().isTimerRunning())
                    Inventorytimer.getInstance().stopTimer();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fragment instanceof InventoryFragment2)
                            ((InventoryFragment2) fragment).resetInventoryDetail();
                        else if (fragment instanceof RapidReadFragment)
                            ((RapidReadFragment) fragment).resetInventoryDetail();
                        //export Data to the file
                        if (Application.EXPORT_DATA)
                            if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                                new DataExportTask(getApplicationContext(), Application.tagsReadInventory, Application.mConnectedReader.getHostName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, Application.mRRStartedTime).execute();
                            }
                    }
                });
            }
        } else if (Application.isLocatingTag) {
            if (isLocationingAborted) {
                Application.isLocatingTag = false;
                isLocationingAborted = false;
                if (fragment instanceof LocationingFragment)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((LocationingFragment) fragment).resetLocationingDetails(false);
                        }
                    });
            }
        }
    }

    /**
     * method to set DPO status on Action bar
     *
     * @param level
     */
    public void setActionBarBatteryStatus(final int level) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (menu != null && menu.findItem(R.id.action_bett) != null) {
                    if (Application.dynamicPowerSettings != null && Application.dynamicPowerSettings.getValue() == 1) {
                        menu.findItem(R.id.action_bett).setIcon(R.drawable.action_battery_dpo_level);
                    } else {
                        menu.findItem(R.id.action_bett).setIcon(R.drawable.action_battery_level);
                    }
                    menu.findItem(R.id.action_bett).getIcon().setLevel(level);
                }
                }

        });
    }




    /**
     * method lear inventory data like total tags, unique tags, read rate etc..
     */
    public void clearInventoryData() {
        Application.TOTAL_TAGS = 0;
        Application.mRRStartedTime = 0;
        Application.UNIQUE_TAGS = 0;
        Application.TAG_READ_RATE = 0;
        if (Application.tagIDs != null) {
            Application.tagIDs.clear();
        }
        if (Application.tagsReadInventory.size() > 0) {
            Application.tagsReadInventory.clear();
        }
        if (Application.tagsReadInventory.size() > 0) {
            Application.tagsReadInventory.clear();
        }
        if (Application.inventoryList != null && Application.inventoryList.size() > 0) {
            Application.inventoryList.clear();
        }
    }

    /**
     * RR button in {@link com.enpeck.RFID.rapidread.RapidReadFragment} is clicked
     *
     * @param view - Button clicked
     */
    /*public void rrClicked(View view) {
        selectItem();
    }*/

    /**
     * Inventory button in {@link com.enpeck.RFID.MasterData.MasterData} is clicked
     *
     * @param view - Button clicked
     */
  /*  public void masterdadaclick(View view) {

        selectItem(1);
    }
*/
    /**
     * Locationing button in {@link com.enpeck.RFID.inventory.InventoryFragment} is clicked
     *
     * @param view - Button clicked
     */
  /*  public void assignclick(View view) {

        selectItem(2);
    }
*/
    /**
     * Settings button in {@link com.enpeck.RFID.vertifyTag.Verifiy_Tag} is clicked
     *
     * @param view - Button clicked
     */

    public void incomingcontainer(View view) {
        selectItem(1);
    }

    public void verfiedcontainer(View view) {
        selectItem(2);
    }

    public void verfiedclick(View view) {
        if (adapter==null) {

        }else {
            adapter.clear();
        }
        selectItem(3);
    }

    public void readerclick(View view) {
        selectItem(4);
    }

    public void aboutClicked() {
        selectItem(5);
    }

    public void batteryclick(View view){
        selectItem(6);
    }
    /**
     * Access button in {@link com.enpeck.RFID.DailyReport.DailyReportFragment} is clicked
     *
     * @param view - Button clicked
     */
   /* public void dailyclick(View view) {
        selectItem(4);
    }*/

    /**
     * Filter button in {@link com.enpeck.RFID.Device.DeviceCompFragment} is clicked
     *
     * @param view - Button clicked
     */
/*
    public void devicesclick(View view) {
        selectItem(5);
    }
*/
    /**
     * About option in {@link com.enpeck.RFID.reader_connection.ReadersListFragment} is selected
     */


    /**
     * About option in {@link com.enpeck.RFID.home.AboutFragment} is selected
     */


    /**
     * About option in {@link com.enpeck.RFID.location.Locationfragment} is selected
     */
 /*   public void locationclick(View view) {
        selectItem(7);
    }

    public void aboutclick(View view) {
        selectItem(8);
    }

    public void stockclick(View view){selectItem(9);}

    public void batteryclick(View view) {
        selectItem(10);
    }*/


    /* public void batteryclick(View view) {
        selectItem(10);
    }*/
    private void readerReconnected(ReaderDevice readerDevice) {
        // store app reader
        Application.mConnectedDevice = readerDevice;
        Application.mConnectedReader = readerDevice.getRFIDReader();
        //
        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            clearInventoryData();
            Application.mIsInventoryRunning = true;
            Application.memoryBankId = 0;
            startTimer();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment instanceof ResponseHandlerInterfaces.BatchModeEventHandler)
                ((ResponseHandlerInterfaces.BatchModeEventHandler) fragment).batchModeEventReceived();
        } else
            try {
                UpdateReaderConnection(false);
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
        // connect call
        bluetoothDeviceConnected(readerDevice);
    }

    /**
     * Method to notify device disconnection
     *
     * @param readerDevice
     */
    private void readerDisconnected(ReaderDevice readerDevice) {
        stopTimer();
        //updateConnectedDeviceDetails(readerDevice, false);
        if (Application.NOTIFY_READER_CONNECTION)
            sendNotification(Constants.ACTION_READER_DISCONNECTED, "Disconnected from " + readerDevice.getName());
        clearSettings();
        setActionBarBatteryStatus(0);
        bluetoothDeviceDisConnected(readerDevice);
        Application.mConnectedDevice = null;
        Application.mConnectedReader = null;
        Application.is_disconnection_requested = false;
    }

    public void inventoryAborted() {
        Inventorytimer.getInstance().stopTimer();
        Application.mIsInventoryRunning = false;
    }

    public void bluetoothDeviceConnected(ReaderDevice device) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof ReadersListFragment) {
            ((ReadersListFragment) fragment).bluetoothDeviceConnected(device);
        } /*else if (fragment instanceof AboutFragment) {
            ((AboutFragment) fragment).deviceConnected();*/
         else if (fragment instanceof SettingListFragment) {
            ((SettingListFragment) fragment).settingsListUpdated();
        }
//        else if(fragment instanceof AccessOperationsFragment)
//            ((AccessOperationsFragment) fragment).deviceConnected(device);
        if (bluetoothDeviceFoundHandlers != null && bluetoothDeviceFoundHandlers.size() > 0) {
            for (BluetoothDeviceFoundHandler bluetoothDeviceFoundHandler : bluetoothDeviceFoundHandlers)
                bluetoothDeviceFoundHandler.bluetoothDeviceConnected(device);
        }
        if (Application.NOTIFY_READER_CONNECTION)
            sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + device.getName());
    }

    public void bluetoothDeviceDisConnected(ReaderDevice device) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (Application.mIsInventoryRunning) {
            inventoryAborted();
            //export Data to the file if inventory is running in batch mode
            if (Application.isBatchModeInventoryRunning != null && !Application.isBatchModeInventoryRunning)
                if (Application.EXPORT_DATA)
                    if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty())
                        new DataExportTask(getApplicationContext(), Application.tagsReadInventory, device.getName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, Application.mRRStartedTime).execute();
            Application.isBatchModeInventoryRunning = false;
        }
        if (Application.isLocatingTag) {
            Application.isLocatingTag = false;
        }
        //update dpo icon in settings list
        SettingsContent.ITEMS.get(8).icon = R.drawable.title_dpo_disabled;

        Application.isAccessCriteriaRead = false;
        accessTagCount = 0;

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof ReadersListFragment) {
            ((ReadersListFragment) fragment).readerDisconnected(device);
            ((ReadersListFragment) fragment).bluetoothDeviceDisConnected(device);
        } else if (fragment instanceof LocationingFragment) {
            ((LocationingFragment) fragment).resetLocationingDetails(true);
        } else if (fragment instanceof InventoryFragment2) {
            ((InventoryFragment2) fragment).resetInventoryDetail();
        } else if (fragment instanceof RapidReadFragment) {
            ((RapidReadFragment) fragment).resetInventoryDetail();
        } /*else if (fragment instanceof AboutFragment) {
            ((AboutFragment) fragment).resetVersionDetail();
        } */else if (fragment instanceof SettingListFragment) {
            ((SettingListFragment) fragment).settingsListUpdated();
        }

        if (bluetoothDeviceFoundHandlers != null && bluetoothDeviceFoundHandlers.size() > 0) {
            for (BluetoothDeviceFoundHandler bluetoothDeviceFoundHandler : bluetoothDeviceFoundHandlers)
                bluetoothDeviceFoundHandler.bluetoothDeviceDisConnected(device);
        }

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
    }

    @Override
    public void RFIDReaderAppeared(ReaderDevice device) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof ReadersListFragment)
            ((ReadersListFragment) fragment).RFIDReaderAppeared(device);
        if (Application.NOTIFY_READER_AVAILABLE) {
            if (!device.getName().equalsIgnoreCase("null"))
                sendNotification(Constants.ACTION_READER_AVAILABLE, device.getName() + " is available.");
        }
    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice device) {
        Application.mReaderDisappeared = device;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof ReadersListFragment)
            ((ReadersListFragment) fragment).RFIDReaderDisappeared(device);
        if (Application.NOTIFY_READER_AVAILABLE)
            sendNotification(Constants.ACTION_READER_AVAILABLE, device.getName() + " is unavailable.");
    }

    private boolean getRepeatTriggers() {
        if ((Application.settings_startTrigger != null && (Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || Application.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC))
                || (isTriggerRepeat != null && isTriggerRepeat))
            return true;
        else
            return false;
    }

    /**
     * Method which will called once notification received from reader.
     * update the operation status in the application based on notification type
     *
     * @param rfidStatusEvents - notification received from reader
     */

    private void notificationFromGenericReader(RfidStatusEvents rfidStatusEvents) {

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            if (Application.mConnectedReader != null)
                DisconnectTask = new UpdateDisconnectedStatusTask(Application.mConnectedReader.getHostName()).execute();
//            Application.mConnectedReader = null;
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.INVENTORY_START_EVENT) {
            if (!Application.isAccessCriteriaRead && !Application.isLocatingTag) {
                //if (!getRepeatTriggers() && Inventorytimer.getInstance().isTimerRunning()) {
                Application.mIsInventoryRunning = true;
                Inventorytimer.getInstance().startTimer();
                //}
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.INVENTORY_STOP_EVENT) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            accessTagCount = 0;
            Application.isAccessCriteriaRead = false;

            if (Application.mIsInventoryRunning) {
                Inventorytimer.getInstance().stopTimer();
            } else if (Application.isGettingTags) {
                Application.isGettingTags = false;
                Application.mConnectedReader.Actions.purgeTags();
                if (Application.EXPORT_DATA) {
                    if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new DataExportTask(getApplicationContext(), Application.tagsReadInventory, Application.mConnectedDevice.getName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, Application.mRRStartedTime).execute();
                            }
                        });
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fragment instanceof ReadersListFragment) {
                            //((ReadersListFragment) fragment).cancelProgressDialog();
                            if (Application.mConnectedReader != null && Application.mConnectedReader.ReaderCapabilities.getModelName() != null) {
                                ((ReadersListFragment) fragment).capabilitiesRecievedforDevice();
                            }
                        }
                    }
                });
            }

            if (!getRepeatTriggers()) {
                if (Application.mIsInventoryRunning)
                    isInventoryAborted = true;
                else if (Application.isLocatingTag)
                    isLocationingAborted = true;
                operationHasAborted();
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.OPERATION_END_SUMMARY_EVENT) {
            if (fragment instanceof RapidReadFragment)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((RapidReadFragment) fragment).updateInventoryDetails();
                    }
                });
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
            Boolean triggerPressed = false;
            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED)
                triggerPressed = true;
            if (fragment instanceof TriggerEventHandler) {
                if (triggerPressed && (Application.settings_startTrigger.getTriggerType().toString().equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE.toString()) || (isTriggerRepeat != null && !isTriggerRepeat)))
                    ((TriggerEventHandler) fragment).triggerPressEventRecieved();

                else if (!triggerPressed && (Application.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE.toString()) || (isTriggerRepeat != null && !isTriggerRepeat)))
                    ((TriggerEventHandler) fragment).triggerReleaseEventRecieved();
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            final Events.BatteryData batteryData = rfidStatusEvents.StatusEventData.BatteryData;
            Application.BatteryData = batteryData;
            setActionBarBatteryStatus(batteryData.getLevel());

            if (batteryNotificationHandlers != null && batteryNotificationHandlers.size() > 0) {
                for (BatteryNotificationHandler batteryNotificationHandler : batteryNotificationHandlers)
                    batteryNotificationHandler.deviceStatusReceived(batteryData.getLevel(), batteryData.getCharging(), batteryData.getCause());
            }
            if (Application.NOTIFY_BATTERY_STATUS && batteryData.getCause() != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (batteryData.getCause().trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_CRITICAL))
                            sendNotification(Constants.ACTION_READER_BATTERY_CRITICAL, getString(R.string.battery_status__critical_message));
                        else if (batteryData.getCause().trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_LOW))
                            sendNotification(Constants.ACTION_READER_BATTERY_CRITICAL, getString(R.string.battery_status_low_message));
                    }
                });
            }

        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATCH_MODE_EVENT) {
            Application.isBatchModeInventoryRunning = true;
            startTimer();
            clearInventoryData();
            Application.mIsInventoryRunning = true;
            Application.memoryBankId = 0;
            isTriggerRepeat = rfidStatusEvents.StatusEventData.BatchModeEventData.get_RepeatTrigger();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fragment instanceof ResponseHandlerInterfaces.BatchModeEventHandler)
                        ((ResponseHandlerInterfaces.BatchModeEventHandler) fragment).batchModeEventReceived();
                    if (fragment instanceof ReadersListFragment) {
                        //((ReadersListFragment) fragment).cancelProgressDialog();
                        if (Application.mConnectedReader != null && Application.mConnectedReader.ReaderCapabilities.getModelName() == null) {
                            ((ReadersListFragment) fragment).capabilitiesRecievedforDevice();
                        }
                    }
                }
            });
        }
    }

    /**
     * method to send connect command request to reader
     * after connect button clicked on connect password dialog
     *
     * @param password     - reader password
     * @param readerDevice
     */
    public void connectClicked(String password, ReaderDevice readerDevice) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof ReadersListFragment) {
            ((ReadersListFragment) fragment).ConnectwithPassword(password, readerDevice);
        }
    }

    /**
     * method which will exe cute after cancel button clicked on connect pwd dialog
     *
     * @param readerDevice
     */
    public void cancelClicked(ReaderDevice readerDevice) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof ReadersListFragment) {
            ((ReadersListFragment) fragment).readerDisconnected(readerDevice);
        }
    }

    /**
     * The click listener for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.getChildAt(0).setClickable(false);
            selectItem(position + 1);

        }
    }

    /**
     * @Override protected void onNewIntent(Intent intent) {
     * int isrepeat = intent.getIntExtra(RFID_EVENT_TYPE.BATCH_MODE_EVENT.toString(), 0);
     * BatchModeHandling(isrepeat==1);
     * }
     */

    public class EventHandler implements RfidEventsListener {

        @Override
        public void eventReadNotify(RfidReadEvents e) {
            final TagData[] myTags = Application.mConnectedReader.Actions.getReadTags(100);
            if (myTags != null) {
                //Log.d("RFID_EVENT","l: "+myTags.length);
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                for (int index = 0; index < myTags.length; index++) {
                    if (myTags[index].getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                            myTags[index].getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
//                        if (myTags[index].getMemoryBankData().length() > 0) {
//                            System.out.println(" Mem Bank Data " + myTags[index].getMemoryBankData());
//                        }
                    }
                    if (myTags[index].isContainsLocationInfo()) {
                        final int tag = index;
                        Application.TagProximityPercent = myTags[tag].LocationInfo.getRelativeDistance();
                        if (fragment instanceof LocationingFragment)
                            ((LocationingFragment) fragment).handleLocateTagResponse();
                    }
                    if (Application.isAccessCriteriaRead && !Application.mIsInventoryRunning) {
                        accessTagCount++;
                    } else {
                        if (myTags[index] != null && (myTags[index].getOpStatus() == null || myTags[index].getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS)) {
                            final int tag = index;
                            new ResponseHandlerTask(myTags[tag], fragment).execute();
                        }
                    }
                }
            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            System.out.println("Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
            notificationFromGenericReader(rfidStatusEvents);
        }
    }

    /**
     * Async Task, which will handle tag data response from reader. This task is used to check whether tag is in inventory list or not.
     * If tag is not in the list then it will add the tag data to inventory list. If tag is there in inventory list then it will update the tag details in inventory list.
     */
    public class ResponseHandlerTask extends AsyncTask<Void, Void, Boolean> {
        private TagData tagData;
        private InventoryListItem inventoryItem;
        private InventoryListItem oldObject;
        private Fragment fragment;
        private String memoryBank;
        private String memoryBankData;

        ResponseHandlerTask(TagData tagData, Fragment fragment) {
            this.tagData = tagData;
            this.fragment = fragment;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean added = false;
            try {
                if (Application.inventoryList.containsKey(tagData.getTagID())) {
                    inventoryItem = new InventoryListItem(tagData.getTagID(), 1, null, null, null, null, null, null);
                    int index = Application.inventoryList.get(tagData.getTagID());
                    if (index >= 0) {
                        Application.TOTAL_TAGS++;
                        //Tag is already present. Update the fields and increment the count
                        if (tagData.getOpCode() != null)
                            if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                                memoryBank = tagData.getMemoryBank().toString();
                                memoryBankData = tagData.getMemoryBankData().toString();
                            }
                        oldObject = Application.tagsReadInventory.get(index);
                        oldObject.incrementCount();
                        if (oldObject.getMemoryBankData() != null && !oldObject.getMemoryBankData().equalsIgnoreCase(memoryBankData))
                            oldObject.setMemoryBankData(memoryBankData);
                        if (pc)
                            oldObject.setPC(Integer.toString(tagData.getPC()));
                        if (phase)
                            oldObject.setPhase(Integer.toString(tagData.getPhase()));
                        if (channelIndex)
                            oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                        if (rssi)
                            oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                    }
                } else {
                    //Tag is encountered for the first time. Add it.
                    if (Application.inventoryMode == 0 || (Application.inventoryMode == 1 && Application.UNIQUE_TAGS <= Constants.UNIQUE_TAG_LIMIT)) {
                        int tagSeenCount = 0;
                        if (Integer.toString(tagData.getTagSeenCount()) != null)
                            tagSeenCount = tagData.getTagSeenCount();
                        if (tagSeenCount != 0) {
                            Application.TOTAL_TAGS += tagSeenCount;
                            inventoryItem = new InventoryListItem(tagData.getTagID(), tagSeenCount, null, null, null, null, null, null);
                        } else {
                            Application.TOTAL_TAGS++;
                            inventoryItem = new InventoryListItem(tagData.getTagID(), 1, null, null, null, null, null, null);
                        }
                        added = Application.tagsReadInventory.add(inventoryItem);
                        if (added) {
                            Application.inventoryList.put(tagData.getTagID(), Application.UNIQUE_TAGS);
                            if (tagData.getOpCode() != null)

                                if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                                    memoryBank = tagData.getMemoryBank().toString();
                                    memoryBankData = tagData.getMemoryBankData().toString();

                                }
                            oldObject = Application.tagsReadInventory.get(Application.UNIQUE_TAGS);
                            oldObject.setMemoryBankData(memoryBankData);
                            oldObject.setMemoryBank(memoryBank);
                            if (pc)
                                oldObject.setPC(Integer.toString(tagData.getPC()));
                            if (phase)
                                oldObject.setPhase(Integer.toString(tagData.getPhase()));
                            if (channelIndex)
                                oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                            if (rssi)
                                oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                            Application.UNIQUE_TAGS++;
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                //logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                oldObject = null;
                added = false;
            } catch (Exception e) {
                // logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                oldObject = null;
                added = false;
            }
            inventoryItem = null;
            memoryBank = null;
            memoryBankData = null;
            return added;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try{
            cancel(true);
          //  String tagg =adapter.getItem(0).getTagID();
            if (oldObject != null && fragment instanceof ResponseHandlerInterfaces.ResponseTagHandler)
           /*     View parentRow = (View) view.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);*/

                ((ResponseHandlerInterfaces.ResponseTagHandler) fragment).handleTagResponse(oldObject, result);
            oldObject = null;
            }catch (Exception e){}
        }
    }

    protected class UpdateDisconnectedStatusTask extends AsyncTask<Void, Void, Boolean> {
        private final String device;
        // store current reader state
        private final ReaderDevice readerDevice;
        long disconnectedTime;

        public UpdateDisconnectedStatusTask(String device) {
            this.device = device;
            disconnectedTime = System.currentTimeMillis();
            // store current reader state
            readerDevice = Application.mConnectedDevice;
            //
            Application.mReaderDisappeared = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (readerDevice != null && readerDevice.getName().equalsIgnoreCase(device))
                        readerDisconnected(readerDevice);
                    else
                        readerDisconnected(new ReaderDevice(device, null));
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //Check if the connected device is one we had comm with
            if (!Application.is_disconnection_requested && Application.AUTO_RECONNECT_READERS && readerDevice != null && device != null && device.equalsIgnoreCase(readerDevice.getName())) {
                if (isBluetoothEnabled()) {
                    boolean bConnected = false;
                    int retryCount = 0;
                    while (!bConnected && retryCount < 10) {
                        if (isCancelled() || isDeviceDisconnected)
                            break;
                        try {
                            Thread.sleep(1000);
                            retryCount++;
                            // check manual connection is initiated
                            if (Application.is_connection_requested || isCancelled())
                                break;
                            readerDevice.getRFIDReader().reconnect();
                            bConnected = true;
                            // break temporary pairing connection if reader is unpaired
                            if (Application.mReaderDisappeared != null && Application.mReaderDisappeared.getName().equalsIgnoreCase(readerDevice.getName())) {
                                readerDevice.getRFIDReader().disconnect();
                                bConnected = false;
                                break;
                            }
                        } catch (InvalidUsageException e) {
                        } catch (OperationFailureException e) {
                            if (e.getResults() == RFIDResults.RFID_BATCHMODE_IN_PROGRESS) {
                                Application.isBatchModeInventoryRunning = true;
                                bConnected = true;
                            }
                            if (e.getResults() == RFIDResults.RFID_READER_REGION_NOT_CONFIGURED) {
                                try {
                                    readerDevice.getRFIDReader().disconnect();
                                    bConnected = false;
                                    break;
                                } catch (InvalidUsageException e1) {
                                    e1.printStackTrace();
                                } catch (OperationFailureException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return bConnected;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!isCancelled()) {
                if (result)
                    readerReconnected(readerDevice);
                else if (!Application.is_connection_requested) {
                    sendNotification(Constants.ACTION_READER_CONN_FAILED, "Connection Failed!! was received");
                    try {
                        readerDevice.getRFIDReader().disconnect();
                    } catch (InvalidUsageException e) {
                        e.printStackTrace();
                    } catch (OperationFailureException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class AsynkImei extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        String result = "false";
        String str;

        public AsynkImei(String str) {
            this.str = str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEimemi);
                request.addProperty("IMEI",deviceIMEI);

                Bean C = new Bean();
                PropertyInfo pi = new PropertyInfo();
                pi.setName("Bean");
                pi.setValue(C);
                pi.setType(C.getClass());
                request.addProperty(pi);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                envelope.addMapping(NAMESPACE, "Bean", new Bean().getClass());

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.debug = true;
                androidHttpTransport.call(SOAP_ACTIONimei, envelope);

                SoapObject response2 = (SoapObject) envelope.getResponse();
                Bean[] personobj = new Bean[response2.getPropertyCount()];
                Bean beanobj = new Bean();

                for (int j = 0; j < personobj.length; j++) {

                    SoapObject pii = (SoapObject) response2.getProperty(j);
                    beanobj.serial = pii.getProperty(0).toString();
                    beanobj.iec = pii.getProperty(1).toString();


                    personobj[j] = beanobj;

                }

                uname = beanobj.serial;
                deviceIMEI = beanobj.iec;

                Log.d("rfid", "doInBackground: "+deviceIMEI);

               /* if (object.getPropertyCount() > 0) {
                    flag =1;
                    for (int i = 0; i < object.getPropertyCount(); i++) {
                        SoapObject innerResponse = (SoapObject) object.getProperty(i);

                        listReport.add(new ModelDeailyReport(
                                innerResponse.getProperty("S1").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S1").toString(),
                                innerResponse.getProperty("S2").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S2").toString(),
                                innerResponse.getProperty("S3").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S3").toString(),
                                innerResponse.getProperty("S4").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S4").toString(),
                                innerResponse.getProperty("S5").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S5").toString(),
                                innerResponse.getProperty("S6").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S6").toString(),
                                innerResponse.getProperty("S7").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S7").toString(),
                                innerResponse.getProperty("S8").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S8").toString(),
                                innerResponse.getProperty("S9").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S9").toString()
                        ));
                    }
                }
                else {
                    flag =0;

                }*/

            } catch (Exception e) {
                e.printStackTrace();
                pd.dismiss();

                serverissue = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            if (uname == null && deviceIMEI == null ) {
                pd.dismiss();
                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setTitle("Oops!")
                        .setDescription("Your Imei no is not in database....")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .setPositiveText(R.string.button)
                        .withIconAnimation(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            } else {

                pd.dismiss();
                session = new SessionManagement(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();

                imei =user.get(SessionManagement.KEY_company);
                password = user.get(SessionManagement.KEY_Ieccode);
                username = user.get(SessionManagement.KEY_username);
                radiostr =user.get(SessionManagement.KEY_password);

            /*    Intent intent =new Intent(MainActivity.this,MainActivity.class);
                SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
                map.put("imei", deviceIMEI);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("password", uname);
                map.put("username",deviceIMEI);
                map.put("radiostr", radiostr);
                sessionManagement.createRegisterationSession(deviceIMEI,uname,deviceIMEI,radiostr);*/
                //   intent.putExtra("radiostr",radiostr);
              //  startActivity(intent);

            }

        /*    if (result.contentEquals("true")) {
                pd.dismiss();
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
                map.put("imei", deviceIMEI);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("password", uname);
                map.put("username",deviceIMEI);
                map.put("radiostr", radiostr);
                sessionManagement.createRegisterationSession(deviceIMEI,uname,deviceIMEI,radiostr);
                //   intent.putExtra("radiostr",radiostr);
                startActivity(intent);

            } else {
                pd.dismiss();
                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setTitle("Oops!")
                        .setDescription("Your Imei no is not in database....")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .setPositiveText(R.string.button)
                        .withIconAnimation(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                Toast.makeText(LoginActivity.this, "Your Imei no is not in database....", Toast.LENGTH_SHORT).show();
            }*/
        }
    }


}
