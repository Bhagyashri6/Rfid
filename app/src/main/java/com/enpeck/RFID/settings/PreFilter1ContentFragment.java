package com.enpeck.RFID.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.InputFilterMax;
import com.enpeck.RFID.common.PreFilters;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link PreFilter1ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to show the pre-filter 1 UI.
 */
public class PreFilter1ContentFragment extends Fragment {
    private EditText preFilterOffset;

    public PreFilter1ContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PreFilter1ContentFragment.
     */
    public static PreFilter1ContentFragment newInstance() {
        return new PreFilter1ContentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pre_filter_content, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSpinner();
        AutoCompleteTextView tagIDField = ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID));
        Application.updateTagIDs();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, Application.tagIDs);
        tagIDField.setAdapter(adapter);
        preFilterOffset = ((EditText) getActivity().findViewById(R.id.preFilterOffset));
        preFilterOffset.setFilters(new InputFilter[]{new InputFilterMax(Long.valueOf(Constants.MAX_OFFSET))});
        //Load the saved states
        loadPreFilterStates();
    }

    /**
     * method to initialize memory bank spinner
     */
    private void initializeSpinner() {
        Spinner memoryBankSpinner = (Spinner) getActivity().findViewById(R.id.preFilterMemoryBank);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> memoryBankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pre_filter_memory_bank_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        memoryBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        memoryBankSpinner.setAdapter(memoryBankAdapter);

        Spinner actionSpinner = (Spinner) getActivity().findViewById(R.id.preFilterAction);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> actionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pre_filter_action_array, R.layout.spinner_small_font);
        // Specify the layout to use when the list of choices appears
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        actionSpinner.setAdapter(actionAdapter);

        Spinner targetSpinner = (Spinner) getActivity().findViewById(R.id.preFilterTarget);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> targetAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pre_filter_target_options, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        targetSpinner.setAdapter(targetAdapter);
    }

    /**
     * Method to load the pre-filter states
     */
    private void loadPreFilterStates() {
        ArrayAdapter<CharSequence> memoryBankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pre_filter_memory_bank_array, R.layout.custom_spinner_layout);

        if (Application.preFilters != null && Application.preFilters[0] != null) {
            PreFilters preFilter = Application.preFilters[0];
            ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).setText(preFilter.getTag());
            preFilterOffset.setText("" + preFilter.getOffset());
            ((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).setChecked(preFilter.isFilterEnabled());
            ((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).setSelection(memoryBankAdapter.getPosition(preFilter.getMemoryBank().trim().toUpperCase()));
            ((Spinner) getActivity().findViewById(R.id.preFilterAction)).setSelection(preFilter.getAction());
            ((Spinner) getActivity().findViewById(R.id.preFilterTarget)).setSelection(preFilter.getTarget());
        } else {
            ((AutoCompleteTextView) getActivity().findViewById(R.id.preFilterTagID)).setText("");
            preFilterOffset.setText("0");
            ((CheckBox) getActivity().findViewById(R.id.preFilterEnableFilter)).setChecked(false);
            ((Spinner) getActivity().findViewById(R.id.preFilterMemoryBank)).setSelection(0);
            ((Spinner) getActivity().findViewById(R.id.preFilterAction)).setSelection(0);
            ((Spinner) getActivity().findViewById(R.id.preFilterTarget)).setSelection(0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
