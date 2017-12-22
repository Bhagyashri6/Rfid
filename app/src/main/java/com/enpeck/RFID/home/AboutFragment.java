package com.enpeck.RFID.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpeck.RFID.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle the display of application info.
 */
public class AboutFragment extends Fragment {
    TextView tv_radioVersion;
    TextView tv_moduleVersion;
    TextView tv_periodicReport;

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_about);
        actionBar.setIcon(R.drawable.dl_about);


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
