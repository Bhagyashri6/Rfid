package com.enpeck.RFID.Device;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.enpeck.RFID.R;
import com.enpeck.RFID.common.SessionManagement;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceCompFragment extends Fragment {
    LinearLayout fx7500,mc919,mc319,sled,tc56;
    SessionManagement session;


    String radiostr,imei,username,password;
    public DeviceCompFragment() {
        // Required empty public constructor
    }

    public static DeviceCompFragment newInstance() {
        return new DeviceCompFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_comp, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fx7500 =(LinearLayout)getActivity().findViewById(R.id.fx7500);
        mc919 =(LinearLayout)getActivity().findViewById(R.id.mc9190);
        mc319 =(LinearLayout)getActivity().findViewById(R.id.mc3190);
        sled =(LinearLayout)getActivity().findViewById(R.id.sled);
        tc56 =(LinearLayout)getActivity().findViewById(R.id.tc56);

        session = new SessionManagement(getContext());
        HashMap<String, String> user = session.getUserDetails();

        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        menu.removeItem(R.id.action_shutdown);

    }
}
