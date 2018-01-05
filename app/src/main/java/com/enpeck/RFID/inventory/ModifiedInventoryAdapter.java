package com.enpeck.RFID.inventory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;

import java.util.ArrayList;

/**
 * Adapter to provide the data for inventory list
 */
public class ModifiedInventoryAdapter extends ArrayAdapter<InventoryListItem> {
    //private ArrayList<InventoryListItem> listItems;
    private Context context;

    //List to preserve the values when a search takes place
    private ArrayList<InventoryListItem> originalInventoryList;

    //List to store the searched inventory items
    private ArrayList<InventoryListItem> searchItemsList = new ArrayList<>();

    //Implement the filter for searching
    private Filter filter;

    /**
     * Constructor. Handles the initialization
     *
     * @param context            - context to be used
     * @param textViewResourceId - layout to be used
     */
    public ModifiedInventoryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId, Application.tagsReadInventory);
        originalInventoryList = Application.tagsReadInventory;
        searchItemsList.addAll(Application.tagsReadInventory);
        this.context = context;
    }

    @Override
    public synchronized void add(InventoryListItem object) {
        if (searchItemsList != null) {
            searchItemsList.add(object);
        }
    }

    @Override
    public synchronized void clear() {
        if (searchItemsList != null)
            searchItemsList.clear();
    }

    @Override
    public synchronized InventoryListItem getItem(int position) {
        if (searchItemsList != null)
            return searchItemsList.get(position);
        else
            return null;
    }

    @Override
    public synchronized int getCount() {
        if (searchItemsList != null)
            return searchItemsList.size();
        else
            return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public synchronized View getView(int position, View convertView, ViewGroup parent) {
        InventoryViewHolder holder = null;
        InventoryListItem listItem = searchItemsList.get(position);

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.inventory_list_item, null);
            //Layouts and Views used for displaying a list item.
            LinearLayout textViewWrap = (LinearLayout) convertView
                    .findViewById(R.id.text_wrap);
            TextView text = (TextView) convertView.findViewById(R.id.tagData);
            TextView count = (TextView) convertView.findViewById(R.id.tagCount);
            TextView memoryBank = (TextView) convertView.findViewById(R.id.memoryBankTitle);
            TextView memoryBankData = (TextView) convertView.findViewById(R.id.memoryBankData);
            TextView pcView = (TextView) convertView.findViewById(R.id.pc);
            TextView rssiView = (TextView) convertView.findViewById(R.id.rssi);
            TextView phaseView = (TextView) convertView.findViewById(R.id.phase);
            TextView channelView = (TextView) convertView.findViewById(R.id.channel);

            holder = new InventoryViewHolder(textViewWrap, text,  memoryBank, memoryBankData, pcView, rssiView, phaseView, channelView);
        } else {
            //The item is already inflated. Use it.
            holder = (InventoryViewHolder) convertView.getTag();
        }

        holder.getTextView().setText(listItem.getText());
       // holder.getCountView().setText("" + listItem.getCount());

        if (listItem.getMemoryBankData() == null || (listItem.getMemoryBankData() != null && listItem.getMemoryBankData().isEmpty())) {
            convertView.findViewById(R.id.memoryBankData).setVisibility(View.GONE);
            convertView.findViewById(R.id.memoryBankTitle).setVisibility(View.GONE);
        } else {
            if (listItem.getMemoryBank() != null)
                holder.getMemoryBank().setText(listItem.getMemoryBank().toUpperCase() + " MEMORY");
            holder.getMemoryBankData().setText(listItem.getMemoryBankData());
        }
        LinearLayout dataLayout = (LinearLayout) convertView.findViewById(R.id.dataLinearLayout);
        if (dataLayout != null) {
            if (listItem.getPC() != null) {
                holder.getPcView().setText(listItem.getPC());
            } else
                ((LinearLayout) (holder.getPcView()).getParent()).setVisibility(View.GONE);
            if (listItem.getPhase() != null) {
                holder.getPhaseView().setText(listItem.getPhase());
            } else
                ((LinearLayout) (holder.getPhaseView()).getParent()).setVisibility(View.GONE);
            if (listItem.getChannelIndex() != null) {
                holder.getChannelView().setText(listItem.getChannelIndex());
            } else
                ((LinearLayout) (holder.getChannelView()).getParent()).setVisibility(View.GONE);
            if (listItem.getRSSI() != null) {
                holder.getRssiView().setText(listItem.getRSSI());
            } else
                ((LinearLayout) (holder.getRssiView()).getParent()).setVisibility(View.GONE);
        }
        LinearLayout tagDetails = (LinearLayout) convertView.findViewById(R.id.tagDetails);
        if (!listItem.isVisible()) {
            tagDetails.setVisibility(View.GONE);
            holder.getTextViewWrap().setBackgroundColor(Color.WHITE);
        } else {
            tagDetails.setVisibility(View.VISIBLE);
            holder.getTextViewWrap().setBackgroundColor(0x66444444);
        }
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new TagIDFilter();

        return filter;
    }

    /**
     * Class to act as a custom filter for handling searches
     */
    private class TagIDFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if (prefix.length() == 0) {
                results.values = new ArrayList<>();
                ((ArrayList) (results.values)).addAll(originalInventoryList);
                results.count = originalInventoryList.size();
            } else {
                final ArrayList<InventoryListItem> nlist = new ArrayList<InventoryListItem>();

                for (final InventoryListItem inventoryItem : originalInventoryList) {
                    final String value = inventoryItem.getText().toString().toLowerCase();

                    if (value.contains(prefix)) {
                        nlist.add(inventoryItem);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }

            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyDataSetChanged();
            clear();
            searchItemsList = (ArrayList<InventoryListItem>) filterResults.values;
            notifyDataSetInvalidated();
        }
    }
}
