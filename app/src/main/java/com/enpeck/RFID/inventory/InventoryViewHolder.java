package com.enpeck.RFID.inventory;

import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class to hold the tags for inventory list items. Used for optimizing inventory list performance.
 */
public class InventoryViewHolder {
    //References for the fields in the inventory list
    private LinearLayout textViewWrap;
    private TextView textView;
    private TextView countView;
    private TextView memoryBank;
    private TextView memoryBankData;
    private TextView pcView;
    private TextView rssiView;
    private TextView phaseView;
    private TextView channelView;

    public InventoryViewHolder(LinearLayout textViewWrap, TextView textView, TextView memoryBank, TextView memoryBankData, TextView pcView,
                               TextView rssiView, TextView phaseView, TextView channelView) {
        super();
        this.textViewWrap = textViewWrap;
        this.textView = textView;
        //this.countView = countView;
        this.memoryBank = memoryBank;
        this.memoryBankData = memoryBankData;
        this.pcView = pcView;
        this.rssiView = rssiView;
        this.phaseView = phaseView;
        this.channelView = channelView;
    }

    public TextView getTextView() {
        return textView;
    }

    public LinearLayout getTextViewWrap() {
        return textViewWrap;
    }

    public TextView getMemoryBank() {
        return memoryBank;
    }

    public TextView getCountView() {
        return countView;
    }

    public TextView getMemoryBankData() {
        return memoryBankData;
    }

    public TextView getPcView() {
        return pcView;
    }

    public TextView getRssiView() {
        return rssiView;
    }

    public TextView getPhaseView() {
        return phaseView;
    }

    public TextView getChannelView() {
        return channelView;
    }
}
