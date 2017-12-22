package com.enpeck.RFID.home;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.common.SessionManagement;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.HashMap;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to show the HomeScreen
 */
public class HomeFragment extends Fragment {

    String sttt ="Custom Officer";
    String st1 ="vendor";
    LinearLayout linearLayout,master,tag,stocklayout,layout2,maincustom,main1;
    String radiostr,imei,username,password;
    SessionManagement session;
    TextView name;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        return inflater.inflate(R.layout.home_layout, container, false);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        menu.removeItem(R.id.action_home);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_info:
                ((MainActivity) getActivity()).aboutClicked();
                return true;


        }


        int id = item.getItemId();

        if (id == R.id.action_shutdown) {
            new MaterialStyledDialog.Builder(getContext())
                    .setTitle("Exit")
                    .setDescription("Do you really want to exit the application?")
                    .setIcon(R.mipmap.sale1)
                    .setHeaderDrawable(R.color.colorPrimary)
                    .setPositiveText("Yes")
                    .withIconAnimation(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            BluetoothAdapter.getDefaultAdapter().disable();
                            getActivity().moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeText("No")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        }
                    })
                    .setCancelable(true)
                    .show();

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView textView = (TextView)getActivity().findViewById(R.id.text);
         linearLayout =(LinearLayout)getActivity().findViewById(R.id.lineer);
        stocklayout =(LinearLayout)getActivity().findViewById(R.id.stocklayout);
        layout2=(LinearLayout)getActivity().findViewById(R.id.layout2);
        maincustom=(LinearLayout)getActivity().findViewById(R.id.maincustom);
        main1=(LinearLayout)getActivity().findViewById(R.id.main1);
        name =(TextView)getActivity().findViewById(R.id.name);

        session = new SessionManagement(getContext().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);
        textView.setText(radiostr);
        name.setText("Your are at Port : " +imei);

        if (textView.getText().toString().equals(sttt) ) {
            main1.setVisibility(View.VISIBLE);
            maincustom.setVisibility(View.GONE);
        }else {
            main1.setVisibility(View.GONE);
            maincustom.setVisibility(View.VISIBLE);
        }


        if (((ActionBarActivity) getActivity()).getSupportActionBar() != null) {
            //Set the navigation mode to standard
            ((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            //Change the icon
            ((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
        }
    }



}
