package com.enpeck.RFID.MasterData;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enpeck.RFID.R;
import com.enpeck.RFID.common.SessionManagement;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportMasterFragment extends Fragment {
    SessionManagement session;

    String radiostr,imei,username,password;

    public ExportMasterFragment() {
        // Required empty public constructor
    }
    public static ExportMasterFragment newInstance() {
        return new ExportMasterFragment();
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
        return inflater.inflate(R.layout.fragment_export_master, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SessionManagement session = new SessionManagement(getContext().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();


        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
