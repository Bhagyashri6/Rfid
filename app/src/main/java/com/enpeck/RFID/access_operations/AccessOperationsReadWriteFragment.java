package com.enpeck.RFID.access_operations;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.TagData;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.InputFilterMax;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link AccessOperationsReadWriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle the Access Read/Write Operations.
 */
public class AccessOperationsReadWriteFragment extends Fragment implements com.enpeck.RFID.access_operations.AccessOperationsFragment.OnRefreshListener {

    private EditText offsetEditText;
    private EditText lengthEditText;
    private AutoCompleteTextView tagIDField;
    private ArrayAdapter<String> adapter;

    public AccessOperationsReadWriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccessOperationsReadWriteFragment.
     */
    public static AccessOperationsReadWriteFragment newInstance() {
        return new AccessOperationsReadWriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_access_operations_read_write, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSpinner();
        offsetEditText = (EditText) getActivity().findViewById(R.id.accessRWOffsetValue);
        lengthEditText = (EditText) getActivity().findViewById(R.id.accessRWLengthValue);
        tagIDField = ((AutoCompleteTextView) getActivity().findViewById(R.id.accessRWTagID));

        //handle Seek Operations
        handleSeekOperations();
        Application.updateTagIDs();
        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, Application.tagIDs);
        tagIDField.setAdapter(adapter);
        if (Application.accessControlTag != null) {
            tagIDField.setText(Application.accessControlTag);
            offsetEditText.setText("2");
        } else {
            offsetEditText.setText("0");
        }
    }

    /**
     * Method to initialize the seekbars
     */
    private void handleSeekOperations() {
        offsetEditText.setFilters(new InputFilter[]{new InputFilterMax(Long.valueOf(Constants.MAX_OFFSET))});
        lengthEditText.setFilters(new InputFilter[]{new InputFilterMax(Long.valueOf(Constants.MAX_LEGTH))});
    }

    private void initializeSpinner() {
        Spinner memoryBankSpinner = (Spinner) getActivity().findViewById(R.id.accessRWMemoryBank);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> memoryBankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.acess_read_write_memory_bank_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        memoryBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        memoryBankSpinner.setAdapter(memoryBankAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void handleTagResponse(final TagData response_tagData) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response_tagData != null) {
                    ACCESS_OPERATION_CODE readAccessOperation = response_tagData.getOpCode();
                    if (readAccessOperation != null) {
                        if (response_tagData.getOpStatus() != null && !response_tagData.getOpStatus().equals(ACCESS_OPERATION_STATUS.ACCESS_SUCCESS)) {
                            String strErr = response_tagData.getOpStatus().toString().replaceAll("_", " ");
                            Toast.makeText(getActivity(), strErr.toLowerCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (response_tagData.getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ) {
                                TextView text = (TextView) getActivity().findViewById(R.id.accessRWData);
                                if (text != null)
                                    text.setText(response_tagData.getMemoryBankData());
                                Toast.makeText(getActivity(), R.string.msg_read_succeed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.err_access_op_failed, Toast.LENGTH_SHORT).show();
                        Constants.logAsMessage(Constants.TYPE_DEBUG, "ACCESS READ", "memoryBankData is null");
                    }
                } else
                    Toast.makeText(getActivity(), R.string.err_access_op_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdate() {
        if (isVisible() && tagIDField != null) {
            Application.accessControlTag = tagIDField.getText().toString();
        }
    }

    @Override
    public void onRefresh() {
        if (Application.accessControlTag != null && tagIDField != null) {
            tagIDField.setText(Application.accessControlTag);
        }
    }
}
