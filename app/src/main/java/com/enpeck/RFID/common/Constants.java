package com.enpeck.RFID.common;

import android.util.Log;

import java.util.ArrayList;

/**
 * Class to maintain the strings used for notifications and intent actions
 */
public class Constants {

    public static final String IMMEDIATE = "Immediate";
    public static final String HANDHELD = "Handheld";
    public static final String PERIODIC = "Periodic";
    public static final String DURATION = "Duration";
    public static final String TAG_OBSERVATION = "Tag Observation";
    public static final String N_ATTEMPTS = "N attempts";
    public static final int BEEPER_ATTR_NUM = 140;
    public static final int BATCH_MODE_ATTR_NUM = 1500;
    public static final int QUIET_BEEPER = 3;
    public static final String READER_PASSWORDS = "ReadersPasswords";
    public static final String CONNECTION_SUCCESS = "Connection Successful";
    public static final String PASSWORD_MISMATCH = "Password mismatch error";
    public static final String BATCH_MODE_STATUS = "Inventory Started in Batch Mode";
    public static final String ZERO_TIME = "00:00";
    public static final String FROM_NOTIFICATION = "fromNotification";

    //commands List
    public static final String COMMAND_CONNECT = "connect";
    public static final String COMMAND_GET_REGION = "getregion";
    public static final String COMMAND_REPORTCONFIG = "setreportconfig";
    public static final String COMMAND_SELECTRECORDS = "setselectrecords";
    public static final String COMMAND_ANTENNACONFIG = "setantennaconfiguration";
    public static final String COMMAND_DYNAMICPOWER = "setdynamicpower";
    public static final String COMMAND_QUERYPARAMS = "setqueryparams";
    public static final String COMMAND_STARTTRIGGER = "setstarttrigger";
    public static final String COMMAND_STOPTRIGGER = "setstoptrigger";
    public static final String COMMAND_SET_ATTR = "setattr";
    public static final String COMMAND_GET_ATTR = "getattrinfo";
    public static final String COMMAND_REGULATORY = "setregulatory";
    public static final String COMMAND_CHANGECONFIG = "changeconfig";
    public static final String COMMAND_ABORT = "abort";
    public static final String COMMAND_ACCESS_CRITERIA = "setaccesscriteria";
    public static final String COMMAND_PROTOCOL_CONFIG = "protocolconfig";

    public static final String MESSAGE_BATTERY_CRITICAL = "Battery level critical";
    public static final String MESSAGE_BATTERY_LOW = "Battery level low";
    public static final int BATTERY_FULL = 100;
    //For Debugging
    public static final boolean DEBUG = false;
    public static final int TYPE_DEBUG = 60;
    public static final int TYPE_ERROR = 61;
    //Intent Data
    public static final String INTENT_ACTION = "intent_action";
    public static final String INTENT_DATA = "intent_data";
    //Action strings for various RFID Events
    public static final String ACTION_READER_BATTERY_LOW = "com.rfidreader.battery.low";
    public static final String ACTION_READER_BATTERY_CRITICAL = "com.rfidreader.battery.critical";
    public static final String ACTION_READER_BATTERY_CHARGING = "com.rfidreader.battery.charging";
    public static final String ACTION_READER_CONNECTED = "com.rfidreader.connected";
    public static final String ACTION_READER_DISCONNECTED = "com.rfidreader.disconnected";
    public static final String ACTION_READER_AVAILABLE = "com.rfidreader.available";
    public static final String ACTION_READER_CONN_FAILED = "com.rfidreader.conn.failed";
    public static final String ACTION_READER_STATUS_OBTAINED = "com.rfidreader.status.received";
    //Data regarding bluetooth
    public static final String DATA_BLUETOOTH_DEVICE = "com.rfidreader.data.bluetooth.device";
    //Data related to notifications
    public static final String NOTIFICATIONS_TEXT = "notifications_text";
    public static final String NOTIFICATIONS_ID = "notifications_id";
    //number of times that device can try for connection when autoreconnect option is enabled
    public static final int AUTO_RECONNECTION_COUNT = 5;
    //timeout for sled response
    public static final int RESPONSE_TIMEOUT = 6000;
    public static final long SAVE_CONFIG_RESPONSE_TIMEOUT = 15000;
    //Strings for storing the checkbox status of connection settings in shared preferences
    public static final String APP_SETTINGS_STATUS = "AppSettingStatus";
    public static final String AUTO_DETECT_READERS = "AutoDetectReaders";
    public static final String AUTO_RECONNECT_READERS = "AutoReconnectReaders";
    public static final String NFC = "NFC";
    public static final String NOTIFY_READER_AVAILABLE = "NotifyReaderAvailable";
    public static final String NOTIFY_READER_CONNECTION = "NotifyReaderConnection";
    public static final String NOTIFY_BATTERY_STATUS = "NotifyBatteryStatus";
    public static final String EXPORT_DATA = "ExportData";
    //Bundle name for setting item selected
    public static final String SETTING_ITEM_ID = "settingItemId";
    //Various Request Strings
    public static final String BATTERY_LEVEL_REQ_STRING = "getdevicestatus .batterylevel";
    public static final int UNIQUE_TAG_LIMIT = 120000;
    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String NGE = "NGE";
    public static final String GENX_DEVICE = "GENX_DEVICE";
    //toast messages
    public static final String TAG_EMPTY = "Please fill Tag Id";
    //key strings for capabilites
    public static final String MAX_POWER = "MAX_POWER";
    public static final String MIN_POWER = "MIN_POWER";
    public static final String DEVICE_MODEL = "MODEL_NAME";
    public static final String DEVICE_SERIAL = "SERIAL_NUMBER";
    public static final String STATUS_OK = "OK";
    public static final int NO_OF_BITS = 16;
    public static final String TAGS_SEC = " t/s";
    //max offset for prefilter and access
    public static final Integer MAX_OFFSET = 1024;
    //max offset for access
    public static final Integer MAX_LEGTH = 1024;
    //timeout for sled disconnection event
    public static long CONNECT_TIMEOUT = 45000;
    //Heights of inventory list rows
    public static int COLLAPSED_HEIGHT = 75;
    public static int INVENTORY_LIST_FONT_SIZE;
    public static int INVENTORY_LIST_MARGIN_SIZE;
    /**
     * settings list
     */
    private static ArrayList<String> settingsList = new ArrayList<>();

    static {
        settingsList.add(COMMAND_REPORTCONFIG);
        settingsList.add(COMMAND_SELECTRECORDS);
        settingsList.add(COMMAND_ANTENNACONFIG);
        settingsList.add(COMMAND_QUERYPARAMS);
        settingsList.add(COMMAND_STARTTRIGGER);
        settingsList.add(COMMAND_STOPTRIGGER);
        settingsList.add(COMMAND_SET_ATTR);
        settingsList.add(COMMAND_GET_ATTR);
        settingsList.add(COMMAND_REGULATORY);
        settingsList.add(COMMAND_CHANGECONFIG);
        settingsList.add(COMMAND_DYNAMICPOWER);
        settingsList.add(COMMAND_ABORT);
    }

    /**
     * Method to be used throughout the app for logging debug messages
     *
     * @param type    - One of TYPE_ERROR or TYPE_DEBUG
     * @param TAG     - Simple String indicating the origin of the message
     * @param message - Message to be logged
     */
    public static void logAsMessage(int type, String TAG, String message) {
        if (DEBUG) {
            if (type == TYPE_DEBUG)
                Log.d(TAG, (message == null || message.isEmpty()) ? "Message is Empty!!" : message);
            else if (type == TYPE_ERROR)
                Log.e(TAG, (message == null || message.isEmpty()) ? "Message is Empty!!" : message);
        }
    }

    public static boolean isSetting(String cmd) {
        if (settingsList.contains(cmd))
            return true;
        return false;
    }
}
