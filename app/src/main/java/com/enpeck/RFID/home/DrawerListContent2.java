package com.enpeck.RFID.home;

import com.enpeck.RFID.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ABC on 12/08/2017.
 */

public class DrawerListContent2 {
    //An array of sample (Settings) items.
    public static List<DrawerListContent.DrawerItem> ITEMS = new ArrayList<>();

    //A map of sample (Settings) items, by ID.
    public static Map<String, DrawerListContent.DrawerItem> ITEM_MAP = new HashMap<>();

    static {
        // Add items.
        //addItem(new DrawerItem("1", "Home", R.drawable.app_icon));
       /* addItem(new DrawerListContent.DrawerItem("1", "Generate Master Data", R.mipmap.master));
        addItem(new DrawerListContent.DrawerItem("2", "Assign Tag", R.mipmap.tag));*/
        addItem(new DrawerListContent.DrawerItem("3", "Verify Tag", R.mipmap.vertify3));
        addItem(new DrawerListContent.DrawerItem("4", "Daily Report", R.mipmap.report));
        addItem(new DrawerListContent.DrawerItem("5", "Device Compatibility", R.drawable.dl_rdl));
        //addItem(new DrawerItem("5", "Readers List",R.drawable.dl_rdl));
        addItem(new DrawerListContent.DrawerItem("6", "Connectivity", R.mipmap.connectivity));
        //  addItem(new DrawerItem("6", "Pre Filters", R.drawable.dl_filters));
        addItem(new DrawerListContent.DrawerItem("7", "Location Track", R.mipmap.location));
        addItem(new DrawerListContent.DrawerItem("8", "About Us", R.mipmap.about));
        addItem(new DrawerListContent.DrawerItem("9", "Stock Seal Report", R.mipmap.sheet));
        addItem(new DrawerListContent.DrawerItem("10", "Battery Status", R.drawable.title_batt));

     /*   addItem(new DrawerItem("9", "Google+", R.mipmap.googleplus));
        addItem(new DrawerItem("10", "Facebook", R.mipmap.facebook));
        addItem(new DrawerItem("11", "Youtube", R.mipmap.youtube));
        addItem(new DrawerItem("12", "Twitter", R.mipmap.twitter));
        addItem(new DrawerItem("13", "Linkedin", R.mipmap.linkedin));
*/

    }

    /**
     * Method to add a new item
     *
     * @param item - Item to be added
     */
    private static void addItem(DrawerListContent.DrawerItem item) {

        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);

    }



    /**
     * A Drawer item represents an entry in the navigation drawer.
     */
    public static class DrawerItem {
        public String id;
        public String content;
        public int icon;



        public DrawerItem(String id, String content, int icon_id) {
            this.id = id;
            this.content = content;
            this.icon = icon_id;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
