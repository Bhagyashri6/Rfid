package com.enpeck.RFID.application;

import com.enpeck.RFID.common.MaxLimitArrayList;
import com.enpeck.RFID.common.PreFilters;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.inventory.AssignTag;
import com.enpeck.RFID.inventory.InventoryListItem;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.DYNAMIC_POWER_OPTIMIZATION;
import com.zebra.rfid.api3.Events;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.RFModeTable;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RegulatoryConfig;
import com.zebra.rfid.api3.StartTrigger;
import com.zebra.rfid.api3.StopTrigger;
import com.zebra.rfid.api3.TagStorageSettings;
import com.zebra.rfid.api3.UNIQUE_TAG_REPORT_SETTING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;



/**
 * Created by qvfr34 on 12/31/2015.
 */
public class Application extends android.app.Application {
    public static RFIDReader mConnectedReader;

    //Variable to keep track of the unique tags seen
    public static volatile int UNIQUE_TAGS = 0;

    //variable to keep track of the total tags seen
    public static volatile int TOTAL_TAGS = 0;
    //Arraylist to keeptrack of the tagIDs to act as adapter for autocomplete text views
    public static ArrayList<String> tagIDs;
    //variable to store the tag read rate
    public static volatile int TAG_READ_RATE = 0;
    //Boolean to keep track of whether the inventory is running or not
    public static volatile boolean mIsInventoryRunning;
    public static int inventoryMode = 0;
    public static int memoryBankId = -1;
    public static Boolean isBatchModeInventoryRunning;
    public static String accessControlTag;
    public static String locateTag;
    //Variable to maintain the RR started time to maintain the read rate
    public static volatile long mRRStartedTime;
    public static PreFilters[] preFilters = null;
    public static boolean isAccessCriteriaRead = false;
    public static int preFilterIndex = -1;
    //For Notification
    public static volatile int INTENT_ID = 100;
    public static MainActivity.EventHandler eventHandler;
    public static AssignTag.EventHandler eventHandler1;

    public static TreeMap<String, Integer> inventoryList = new TreeMap<String, Integer>();
    public static HashMap<String, String> versionInfo = new HashMap<>(5);

    //Arraylist to keeptrack of the tags read for Inventory
    public static ArrayList<InventoryListItem> tagsReadInventory = new MaxLimitArrayList();
    public static boolean isGettingTags;
    public static boolean EXPORT_DATA;
    public static ReaderDevice mConnectedDevice;
    public static boolean isLocatingTag;
    //
    public static StartTrigger settings_startTrigger;
    public static StopTrigger settings_stopTrigger;
    public static short TagProximityPercent = -1;
    public static TagStorageSettings tagStorageSettings;
    public static int batchMode;
    public static Events.BatteryData BatteryData = null;
    public static DYNAMIC_POWER_OPTIMIZATION dynamicPowerSettings;
    public static boolean is_disconnection_requested;
    public static boolean is_connection_requested;
    //Application Settings
    public static volatile boolean AUTO_DETECT_READERS;
    public static volatile boolean AUTO_RECONNECT_READERS;
    public static volatile boolean NOTIFY_READER_AVAILABLE;
    public static volatile boolean NOTIFY_READER_CONNECTION;
    public static volatile boolean NOTIFY_BATTERY_STATUS;
    //Beeper
    public static BEEPER_VOLUME beeperVolume = null;
    // Singulation control
    public static Antennas.SingulationControl singulationControl;
    // regulatory
    public static RegulatoryConfig regulatory;
    public static Boolean regionNotSet = false;
    // antenna
    public static RFModeTable rfModeTable;
    public static Antennas.AntennaRfConfig antennaRfConfig;
    public static int[] antennaPowerLevel;
    public static Readers readers;
    private static boolean activityVisible;
    public static ReaderDevice mReaderDisappeared;
    public static boolean isActivityVisible() {
        return activityVisible;
    }
    public static void activityResumed() {
        activityVisible = true;
    }
    public static void activityPaused() {
        activityVisible = false;
    }
    public static UNIQUE_TAG_REPORT_SETTING reportUniquetags = null;
    /**
     * Update the tagIds from tagsReadInventory
     */
    public static void updateTagIDs() {
        if (tagsReadInventory == null)
            return;

        if (tagsReadInventory.size() == 0)
            return;

        if (tagIDs == null) {
            tagIDs = new ArrayList<>();
            for (InventoryListItem i : tagsReadInventory) {
                tagIDs.add(i.getTagID());
            }
        } else if (tagIDs.size() != tagsReadInventory.size()) {
            tagIDs.clear();
            for (InventoryListItem i : tagsReadInventory) {
                tagIDs.add(i.getTagID());
            }
        }/*else{
            //Do Nothing. Array is up to date
        }*/
    }

    //clear saved data
    public static void reset() {

        UNIQUE_TAGS = 0;
        TOTAL_TAGS = 0;
        TAG_READ_RATE = 0;
        mRRStartedTime = 0;

        if (tagsReadInventory != null)
            tagsReadInventory.clear();
        if (tagIDs != null)
            tagIDs.clear();

        mIsInventoryRunning = false;
        inventoryMode = 0;
        memoryBankId = -1;
        if (inventoryList != null) {
            inventoryList.clear();
        }
        mConnectedDevice = null;

        INTENT_ID = 100;
        antennaPowerLevel = null;

        //Triggers
        settings_startTrigger = null;
        settings_startTrigger = null;

        //Beeper
        beeperVolume = null;

        accessControlTag = null;
        isAccessCriteriaRead = false;

        // reader settings
        regulatory = null;
        regionNotSet = false;

        preFilters = null;
        preFilterIndex = -1;

        settings_startTrigger = null;
        settings_stopTrigger = null;

        if (versionInfo != null)
            versionInfo.clear();

        BatteryData = null;

        isLocatingTag = false;
        TagProximityPercent = -1;
        locateTag = null;
        is_disconnection_requested = false;
        is_connection_requested = false;
        readers = null;
    }
}
