package com.enpeck.RFID.location;


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
public class Locationfragment extends Fragment {

    SessionManagement session;


    String radiostr,imei,username,password;
    public Locationfragment() {
        // Required empty public constructor
    }
    public static Locationfragment newInstance() {
        return new Locationfragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locationfragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        session = new SessionManagement(getContext());
        HashMap<String, String> user = session.getUserDetails();
        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);


    }
}
