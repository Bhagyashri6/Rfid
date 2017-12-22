package com.enpeck.RFID.settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zebra.rfid.api3.FILTER_ACTION;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.STATE_AWARE_ACTION;
import com.zebra.rfid.api3.TARGET;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.CustomProgressDialog;
import com.enpeck.RFID.common.PreFilters;
import com.enpeck.RFID.home.MainActivity;

//import com.zebra.rfid.api3.PreFilters;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link PreFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to act as a holder for individual pre-filter fragments
 */
public class PreFilterFragment extends BackPressedFragment implements ActionBar.TabListener {
    private static int prefilterIndex1 = 0, prefilterIndex2 = 0;
    private ViewPager viewPager;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = {"Filter 1", "Filter 2"};
    private com.zebra.rfid.api3.PreFilters.PreFilter preFilters1;
    private com.zebra.rfid.api3.PreFilters.PreFilter preFilters2;
    private boolean deletePrefilter1 = false, deletePrefilter2 = false;
    private int filterArraySize = 0;
    private int prefilterCombination = 0;/* value 1 means 1st prefilter,value 2 means 2nd prefilter,value 3 means both 1st and 2nd prefilter modified */

    public PreFilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PreFilterFragment.
     */
    public static PreFilterFragment newInstance() {
        return new PreFilterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pre_filter, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initilization
        viewPager = (ViewPager) getActivity().findViewById(R.id.preFilterPager);
        actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.removeAllTabs();
        //Change the icon
        actionBar.setIcon(R.drawable.dl_filters);
        PreFilterAdapter mAdapter = new PreFilterAdapter(((ActionBarActivity) getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        mAdapter.notifyDataSetChanged();

        /**          * on swiping the viewpager make respective tab selected          * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                try {
                    Constants.logAsMessage(Constants.TYPE_DEBUG, getClass().getSimpleName(), " Position is --- " + position);
                    // on changing the page
                    // make respected tab selected
                    actionBar.setSelectedNavigationItem(position);
                } catch (IllegalStateException e) {
                    Constants.logAsMessage(Constants.TYPE_ERROR, getClass().getSimpleName(), e.getMessage());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
            Application.preFilters = new PreFilters[2];
            Application.preFilters[0] = null;
            Application.preFilters[1] = null;
            Application.preFilterIndex = 0;
            deletePrefilter1 = false;
            deletePrefilter2 = false;

            try {
                filterArraySize = Application.mConnectedReader.Actions.PreFilters.length();
                if (filterArraySize == 2) {
                    prefilterIndex1 = 0;
                    prefilterIndex2 = 1;

                    preFilters1 = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex1);
                    preFilters2 = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex2);
                } else if (filterArraySize == 1) {
                    prefilterIndex1 = 0;
                    preFilters1 = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex1);
                }
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            }
        }
        if (preFilters1 != null) {
            String memoryBank = "EPC";
            if (preFilters1.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_EPC"))
                memoryBank = "EPC";
            else if (preFilters1.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_TID"))
                memoryBank = "TID";
            else if (preFilters1.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_USER"))
                memoryBank = "USER";
            else if (preFilters1.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_RESERVED"))
                memoryBank = "RESV";
            int target = 0;

            if (preFilters1.StateAwareAction.getTarget().getValue() == 1)
                target = 0;
            else if (preFilters1.StateAwareAction.getTarget().getValue() == 2)
                target = 1;
            else if (preFilters1.StateAwareAction.getTarget().getValue() == 3)
                target = 2;
            else if (preFilters1.StateAwareAction.getTarget().getValue() == 4)
                target = 3;
            else if (preFilters1.StateAwareAction.getTarget().getValue() == 0)
                target = 4;

            Application.preFilters[0] = new PreFilters(preFilters1.getStringTagPattern(), memoryBank, preFilters1.getBitOffset() / 16, preFilters1.StateAwareAction.getStateAwareAction().getValue(),
                    target, true);

        }
        if (preFilters2 != null) {
            String memoryBank = "EPC";
            if (preFilters2.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_EPC"))
                memoryBank = "EPC";
            else if (preFilters2.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_TID"))
                memoryBank = "TID";
            else if (preFilters2.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_USER"))
                memoryBank = "USER";
            else if (preFilters2.getMemoryBank().toString().equalsIgnoreCase("MEMORY_BANK_RESERVED"))
                memoryBank = "RESV";
            int target = 0;

            if (preFilters2.StateAwareAction.getTarget().getValue() == 1)
                target = 0;
            else if (preFilters2.StateAwareAction.getTarget().getValue() == 2)
                target = 1;
            else if (preFilters2.StateAwareAction.getTarget().getValue() == 3)
                target = 2;
            else if (preFilters2.StateAwareAction.getTarget().getValue() == 4)
                target = 3;
            else if (preFilters2.StateAwareAction.getTarget().getValue() == 0)
                target = 4;
            Application.preFilters[1] = new PreFilters(preFilters2.getStringTagPattern(), memoryBank, preFilters2.getBitOffset() / 16, preFilters2.StateAwareAction.getStateAwareAction().getValue(),
                    target, true);
        }
    }

    /**
     * method to know whether pre filter settings has changed on back press of the fragment
     *
     * @return true if settings has changed or false if settings has not changed
     */
    public boolean issettingsChanged() {
        deletePrefilter1 = false;
        deletePrefilter2 = false;
        CheckBox preFilterEnableFilter = ((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter));
        CheckBox preFilter2EnableFilter = ((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter));
        if (preFilter2EnableFilter != null && preFilterEnableFilter != null) {
            if (Application.preFilters == null)
                return false;
            if (Application.preFilters[0] == null && Application.preFilters[1] == null && !preFilterEnableFilter.isChecked() && !preFilter2EnableFilter.isChecked())
                return false;
            else if ((Application.preFilters[0] == null && ((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && (!((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked()) && Application.preFilters[1] == null) {
                prefilterCombination = 1;
                return true;
            } else if ((Application.preFilters[0] == null && !((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && (((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked()) && Application.preFilters[1] == null) {
                prefilterCombination = 2;
                return true;
            } else if ((Application.preFilters[0] == null && ((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) || (((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked()) && Application.preFilters[1] == null) {
                prefilterCombination = 3;
                return true;
            } else if ((Application.preFilters[0] != null && Application.preFilters[1] == null)) {
                if ((((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && !(((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked())) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString());
                    PreFilters preFilterCurrent = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItemPosition(), true);

                    if (!preFilterCurrent.equals(Application.preFilters[0])) {
                        prefilterCombination = 1;
                        return true;
                    }
                } else if ((((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && (((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked())) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString());
                    PreFilters preFilterCurrent = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItemPosition(), true);

                    if (!preFilterCurrent.equals(Application.preFilters[0])) {
                        prefilterCombination = 3;
                        return true;
                    }
                } else {
                    prefilterCombination = 1;
                    deletePrefilter1 = true;
                    return true;
                }
            } else if ((Application.preFilters[0] == null && Application.preFilters[1] != null)) {
                if ((((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked())) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString());
                    PreFilters preFilterCurrent = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilter2TagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItemPosition(), true);

                    if (!preFilterCurrent.equals(Application.preFilters[1])) {
                        prefilterCombination = 2;
                        return true;
                    }
                } else if ((((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && (((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked())) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString());
                    PreFilters preFilterCurrent = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilter2TagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItemPosition(), true);
                    if (!preFilterCurrent.equals(Application.preFilters[1])) {
                        prefilterCombination = 3;
                        return true;
                    }
                } else {
                    prefilterCombination = 2;
                    deletePrefilter2 = true;
                    return true;
                }
            } else if ((Application.preFilters[0] != null && Application.preFilters[1] != null)) {
                if (!(((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && (((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked())) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString());
                    PreFilters preFilterCurrent = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilter2TagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItemPosition(), true);


                    if (!preFilterCurrent.equals(Application.preFilters[1])) {
                        prefilterCombination = 2;
                    } else
                        prefilterCombination = 1;

                    deletePrefilter1 = true;
                    return true;
                } else if ((((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked()) && !(((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked())) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString());
                    PreFilters preFilterCurrent = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItemPosition(), true);

                    if (!preFilterCurrent.equals(Application.preFilters[0])) {
                        prefilterCombination = 1;
                    } else
                        prefilterCombination = 2;
                    deletePrefilter2 = true;
                    return true;
                } else if (((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked() && ((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked()) {
                    int offset = ((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString());
                    int offset2 = ((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString().isEmpty() ? -1 : Integer.parseInt(((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString());

                    PreFilters preFilterCurrent1 = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString(), offset,
                            ((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItemPosition(), true);
                    PreFilters preFilterCurrent2 = new PreFilters(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilter2TagID)).getText().toString(), ((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString(), offset2,
                            ((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItemPosition(), ((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItemPosition(), true);

                    if (!preFilterCurrent1.equals(Application.preFilters[0]) && preFilterCurrent2.equals(Application.preFilters[1])) {
                        prefilterCombination = 3;
                        return true;
                    }
                    if (preFilterCurrent1.equals(Application.preFilters[0]) && !preFilterCurrent2.equals(Application.preFilters[1])) {
                        prefilterCombination = 3;
                        return true;
                    }
                    if (!preFilterCurrent1.equals(Application.preFilters[0]) && !preFilterCurrent2.equals(Application.preFilters[1])) {
                        prefilterCombination = 3;
                        return true;
                    }
                } else if (!((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).isChecked() && !((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter)).isChecked()) {
                    deletePrefilter1 = true;
                    deletePrefilter2 = true;
                    return true;
                }
            }
        }
        return false;
    }

    void fillPrefilter(com.zebra.rfid.api3.PreFilters.PreFilter preFilterPassed1, com.zebra.rfid.api3.PreFilters.PreFilter preFilterPassed2) {

        if (Application.mConnectedReader != null && Application.mConnectedReader.isConnected()) {
            CheckBox enablePreFilter = ((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter));
            CheckBox enablePreFilter2 = ((CheckBox) getActivity().findViewById(R.id.preFilter2EnableFilter));
            String offsetText = ((EditText) getActivity().findViewById(R.id.preFilterOffset)).getText().toString();
            String offsetText2 = ((EditText) getActivity().findViewById(R.id.preFilter2Offset)).getText().toString();
            if ((enablePreFilter.isChecked() && offsetText.isEmpty()) || (enablePreFilter2.isChecked() && offsetText2.isEmpty())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.status_failure_message) + "\n" + getResources().getString(R.string.error_empty_fields_preFilters), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).callBackPressed();
            } else {
                if (enablePreFilter.isChecked()) {

                    Constants.logAsMessage(Constants.TYPE_DEBUG, "Prefilter1TagPattern", ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString());
                    //byte[] data = (byte[]) ASCIIUtil.ParseArrayFromString(((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString(), "byteArray", "HEX");
                    String data = ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).getText().toString();
                    int offset = Integer.parseInt(offsetText);
                    preFilterPassed1.setTagPattern(data);
                    preFilterPassed1.setBitOffset(offset * Constants.NO_OF_BITS);
                    preFilterPassed1.setTagPatternBitCount(data.length() * 4);

                    if ((((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString()).equalsIgnoreCase("EPC"))
                        preFilterPassed1.setMemoryBank(MEMORY_BANK.MEMORY_BANK_EPC);
                    if ((((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString()).equalsIgnoreCase("TID"))
                        preFilterPassed1.setMemoryBank(MEMORY_BANK.MEMORY_BANK_TID);
                    if ((((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).getSelectedItem().toString()).equalsIgnoreCase("USER"))
                        preFilterPassed1.setMemoryBank(MEMORY_BANK.MEMORY_BANK_USER);

                    preFilterPassed1.setFilterAction(FILTER_ACTION.FILTER_ACTION_STATE_AWARE);

                    if ((((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItem().toString()).equalsIgnoreCase("SESSION S0"))
                        preFilterPassed1.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S0);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItem().toString().equalsIgnoreCase("SESSION S1"))
                        preFilterPassed1.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S1);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItem().toString().equalsIgnoreCase("SESSION S2"))
                        preFilterPassed1.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S2);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItem().toString().equalsIgnoreCase("SESSION S3"))
                        preFilterPassed1.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S3);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterTarget)).getSelectedItem().toString().equalsIgnoreCase("SL FLAG"))
                        preFilterPassed1.StateAwareAction.setTarget(TARGET.TARGET_SL);


                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("INV A NOT INV B OR ASRT SL NOT DSRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A_NOT_INV_B);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("INV A OR ASRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("NOT INV B OR NOT DSRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_B);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("INV A2BB2A NOT INV A OR NEG SL NOT ASRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A2BB2A_NOT_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("INV B NOT INV A OR DSRT SL NOT ASRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_B_NOT_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("INV B OR DSRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_B);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("NOT INV A OR NOT ASRT SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilterAction)).getSelectedItem().toString().equalsIgnoreCase("NOT INV A2BB2A OR NOT NEG SL"))
                        preFilterPassed1.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_A2BB2A);
                }

                if (enablePreFilter2.isChecked()) {

                    Constants.logAsMessage(Constants.TYPE_DEBUG, "Prefilter2TagPattern", ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilter2TagID)).getText().toString());
                    String data = ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilter2TagID)).getText().toString();
                    int offset = Integer.parseInt(offsetText2);
                    preFilterPassed2.setTagPattern(data);
                    preFilterPassed2.setBitOffset(offset * Constants.NO_OF_BITS);
                    preFilterPassed2.setTagPatternBitCount(data.length() * 4);

                    if ((((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString()).equalsIgnoreCase("EPC"))
                        preFilterPassed2.setMemoryBank(MEMORY_BANK.MEMORY_BANK_EPC);
                    if ((((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString()).equalsIgnoreCase("TID"))
                        preFilterPassed2.setMemoryBank(MEMORY_BANK.MEMORY_BANK_TID);
                    if ((((Spinner) getActivity().findViewById(R.id.preFilter2MemoryBank)).getSelectedItem().toString()).equalsIgnoreCase("USER"))
                        preFilterPassed2.setMemoryBank(MEMORY_BANK.MEMORY_BANK_USER);

                    preFilterPassed2.setFilterAction(FILTER_ACTION.FILTER_ACTION_STATE_AWARE);

                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItem().toString().equalsIgnoreCase("SESSION S0"))
                        preFilterPassed2.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S0);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItem().toString().equalsIgnoreCase("SESSION S1"))
                        preFilterPassed2.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S1);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItem().toString().equalsIgnoreCase("SESSION S2"))
                        preFilterPassed2.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S2);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItem().toString().equalsIgnoreCase("SESSION S3"))
                        preFilterPassed2.StateAwareAction.setTarget(TARGET.TARGET_INVENTORIED_STATE_S3);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Target)).getSelectedItem().toString().equalsIgnoreCase("SL FLAG"))
                        preFilterPassed2.StateAwareAction.setTarget(TARGET.TARGET_SL);


                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("INV A NOT INV B OR ASRT SL NOT DSRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A_NOT_INV_B);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("INV A OR ASRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("NOT INV B OR NOT DSRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_B);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("INV A2BB2A NOT INV A OR NEG SL NOT ASRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A2BB2A_NOT_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("INV B NOT INV A OR DSRT SL NOT ASRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_B_NOT_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("INV B OR DSRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_B);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("NOT INV A OR NOT ASRT SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_A);
                    if (((Spinner) getActivity().findViewById(R.id.preFilter2Action)).getSelectedItem().toString().equalsIgnoreCase("NOT INV A2BB2A OR NOT NEG SL"))
                        preFilterPassed2.StateAwareAction.setStateAwareAction(STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_A2BB2A);
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Constants.logAsMessage(Constants.TYPE_DEBUG, getClass().getSimpleName(), "onTabSelected() Position is --- " + tab.getPosition());
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    /**
     * Method to be called when back button is pressed by the user
     */
    @Override
    public void onBackPressed() {
        Constants.logAsMessage(Constants.TYPE_DEBUG, "PreFilterFragment", "Back Pressed called in Pre Filter fragment");
        if (issettingsChanged())
            new Task_SavePrefilter().execute();
        else
            ((MainActivity) getActivity()).callBackPressed();
    }


    private class Task_SavePrefilter extends AsyncTask<Void, Void, Boolean> {
        private com.zebra.rfid.api3.PreFilters.PreFilter PreFilterData1 = Application.mConnectedReader.Actions.PreFilters.new PreFilter();
        private com.zebra.rfid.api3.PreFilters.PreFilter PreFilterData2 = Application.mConnectedReader.Actions.PreFilters.new PreFilter();
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private CustomProgressDialog progressDialog;

        public Task_SavePrefilter() {

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.pre_filter));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean bResult = false;
            fillPrefilter(PreFilterData1, PreFilterData2);

            try {
                // Case when only 1st prefilter is to be modified
                if ((prefilterCombination == 1) && !deletePrefilter1 && !deletePrefilter2) {
                    if (filterArraySize > 0) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex1);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    Application.mConnectedReader.Actions.PreFilters.add(PreFilterData1);

                    // Case when only 2nd prefilter is to be modified
                } else if ((prefilterCombination == 2) && !deletePrefilter1 && !deletePrefilter2) {
                    if (filterArraySize > 1) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex2);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    Application.mConnectedReader.Actions.PreFilters.add(PreFilterData2);

                    // Case when both prefilters are modified
                } else if ((prefilterCombination == 3) && !deletePrefilter1 && !deletePrefilter2) {
                    if (filterArraySize > 0) {
                        filterArraySize--;
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    if (filterArraySize > 0) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    Application.mConnectedReader.Actions.PreFilters.add(PreFilterData1);
                    Application.mConnectedReader.Actions.PreFilters.add(PreFilterData2);

                    // Case when prefilter 1 is deleted while prefilter2 is modified
                } else if ((prefilterCombination == 2) && deletePrefilter1) {
                    if (filterArraySize > 0) {
                        filterArraySize--;
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    if (filterArraySize > 0) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    Application.mConnectedReader.Actions.PreFilters.add(PreFilterData2);

                    // Case when prefilter 2 is deleted while prefilter1 is modified
                } else if ((prefilterCombination == 1) && deletePrefilter2) {
                    if (filterArraySize > 0) {
                        filterArraySize--;
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    if (filterArraySize > 0) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    Application.mConnectedReader.Actions.PreFilters.add(PreFilterData1);

                    //Case when prefilter1 is deleted
                } else if ((prefilterCombination == 1) && deletePrefilter1) {
                    if (filterArraySize > 0) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex1);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    prefilterIndex2 = prefilterIndex1;
                    //Case when prefilter2 is deleted
                } else if ((prefilterCombination == 2) && deletePrefilter2) {
                    if (filterArraySize > 1) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(prefilterIndex2);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    prefilterIndex2--;
                    //Case when both prefilters are deleted
                } else if (deletePrefilter1 && deletePrefilter2) {
                   /* if (filterArraySize > 0) {
                        filterArraySize--;
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }
                    if (filterArraySize > 0) {
                        com.zebra.rfid.api3.PreFilters.PreFilter toBeDeleted = Application.mConnectedReader.Actions.PreFilters.getPreFilter(0);
                        if (toBeDeleted != null)
                            Application.mConnectedReader.Actions.PreFilters.delete(toBeDeleted);
                    }*/
                    Application.mConnectedReader.Actions.PreFilters.deleteAll();
                }
                bResult = true;
            } catch (InvalidUsageException e) {
                e.printStackTrace();
                deletePrefilter1 = false;
                deletePrefilter2 = false;
                invalidUsageException = e;
            } catch (OperationFailureException e) {
                e.printStackTrace();
                deletePrefilter1 = false;
                deletePrefilter2 = false;
                operationFailureException = e;
            }
            return bResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.cancel();
            if (!result) {
                if (invalidUsageException != null)
                    ((MainActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                if (operationFailureException != null)
                    ((MainActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + operationFailureException.getVendorMessage());
            }
            else
                Toast.makeText(getActivity(), R.string.status_success_message, Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
            ((MainActivity) getActivity()).callBackPressed();
        }
    }
}