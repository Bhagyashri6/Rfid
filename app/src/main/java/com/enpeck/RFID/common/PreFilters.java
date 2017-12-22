package com.enpeck.RFID.common;

/**
 * Class which contains pre filter settings
 */
public class PreFilters {
    private String tag;
    private String memoryBank;
    private int offset;
    private int action;
    private int target;
    private boolean isFilterEnabled;

    /**
     * Constructor of the {@link com.enpeck.RFID.common.PreFilters}
     *
     * @param tag             tag id
     * @param memoryBank      memory bank of the tag
     * @param offset          offset of the pre filter
     * @param action          action
     * @param target          target
     * @param isFilterEnabled whether filter enabled or not
     */
    public PreFilters(String tag, String memoryBank, int offset, int action, int target, boolean isFilterEnabled) {
        this.tag = tag;
        this.memoryBank = memoryBank;
        this.offset = offset;
        this.action = action;
        this.target = target;
        this.isFilterEnabled = isFilterEnabled;
    }

    /**
     * method to get tag pattern of the pre filter
     *
     * @return tag pattern
     */
    public String getTag() {
        return tag;
    }

    /**
     * method to set tag pattern of the pre filter
     *
     * @param tag tag pattern
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * method to get  memory bank
     *
     * @return memory bank
     */
    public String getMemoryBank() {
        return memoryBank;
    }

    /**
     * method to set  memory bank
     *
     * @param memoryBank memory bank
     */
    public void setMemoryBank(String memoryBank) {
        this.memoryBank = memoryBank;
    }

    /**
     * method to get  offset
     *
     * @return offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * method to set  offset
     *
     * @param offset offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * method to get  action
     *
     * @return action
     */
    public int getAction() {
        return action;
    }

    /**
     * method to set  action
     *
     * @param action action
     */
    public void setAction(int action) {
        this.action = action;
    }

    /**
     * method to get  target
     *
     * @return target
     */
    public int getTarget() {
        return target;
    }

    /**
     * method to set  target
     *
     * @param target target
     */
    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * method to know whether pre filter enabled
     *
     * @return true if enabled otherwise it will be false
     */
    public boolean isFilterEnabled() {
        return isFilterEnabled;
    }

    /**
     * method to set whether prefilter enabled
     *
     * @param isFilterEnabled true if enabled otherwise it will be false
     */
    public void setFilterEnabled(boolean isFilterEnabled) {
        this.isFilterEnabled = isFilterEnabled;
    }

    public boolean equals(PreFilters preFilter) {
        if (this.isFilterEnabled == preFilter.isFilterEnabled() && this.tag.equalsIgnoreCase(preFilter.getTag()) && this.memoryBank.equalsIgnoreCase(preFilter.getMemoryBank()) && this.offset == preFilter.getOffset() && this.action == preFilter.getAction() && this.target == preFilter.getTarget())
            return true;
        return false;
    }
}
