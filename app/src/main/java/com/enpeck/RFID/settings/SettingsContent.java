package com.enpeck.RFID.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enpeck.RFID.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.(Added by Pragnesh)
 */
public class SettingsContent {
    /**
     * An array of sample (Settings) items.
     */
    public static List<SettingItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (Settings) items, by ID.
     */
    public static Map<String, SettingItem> ITEM_MAP = new HashMap<>();

    static {
        // Add items.
        addItem(new SettingItem("1", "Readers List"/*,"Available Readers"*/, R.drawable.title_rdl));
        addItem(new SettingItem("2", "Application"/*,"Settings"*/, R.drawable.title_sett));
        addItem(new SettingItem("3", "Antenna",/*"Set Antenna parameters",*/R.drawable.title_antn));
        addItem(new SettingItem("4", "Singulation Control",/*"Set target & action",*/R.drawable.title_singl));
        addItem(new SettingItem("5", "Start\\Stop Triggers",/*"Region and channels",*/R.drawable.title_strstp));
        addItem(new SettingItem("6", "Tag Reporting",/*"Triggers settings",*/R.drawable.title_tags));
        addItem(new SettingItem("7", "Regulatory",/*"Host and sled volumes",*/R.drawable.title_reg));
        addItem(new SettingItem("8", "Battery",/*"Configurations",*/R.drawable.title_batt));
        addItem(new SettingItem("9", "Power Management",/*"Version information",*/R.drawable.title_dpo_disabled));
        addItem(new SettingItem("10", "Beeper",/*"Status",*/R.drawable.title_beep));
        addItem(new SettingItem("11", "Save Configuration",/*"Tag Settings",*/R.drawable.title_save));
        //addItem(new SettingItem("11", "Inventory Settings",/*"Tag Settings",*/R.drawable.title_sett));
    }

    private static void addItem(SettingItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A Settings item representing a piece of content.
     */
    public static class SettingItem {
        public String id;
        public String content;
        //public String subcontent;
        public int icon;

        public SettingItem(String id, String content/*,String subcontent*/, int icon_id) {
            this.id = id;
            this.content = content;
            //this.subcontent = subcontent;
            this.icon = icon_id;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
