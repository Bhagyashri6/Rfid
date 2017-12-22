package com.enpeck.RFID.MasterData;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.inventory.InventoryListItem;
import com.enpeck.RFID.inventory.ModifiedInventoryAdapter;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.zebra.rfid.api3.RFIDResults;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static com.enpeck.RFID.MasterData.MasterData.TAG_CONTENT_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MasterEntryDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MasterEntryDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterEntryDataFragment extends Fragment implements Spinner.OnItemSelectedListener, ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler {

    Button save;
    private TextView date,ieccode1;
    Calendar calendar;
    static int month_x,day_x,year_x;
    static final int DIALOG_ID = 0;
    private String getData;

    static final int REQUEST_LOCATION =1;
    LocationManager locationManager;


    EditText billno,contno,truckno;
    TextView Latitude,Longitude;
    String uniqueno1,billno1,contno1,truckno1;
    AutoCompleteTextView dest,source;
    Boolean serverissue = false;
    ArrayAdapter autoadapter;

    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String Soap_ACTION = "http://tempuri.org/GetDestination";
    private static final String METHOD_NAME= "GetDestination";


    TextView totalNoOfTags;
    TextView uniqueTags;
    private ListView listView;
    private ModifiedInventoryAdapter adapter;
    private ArrayAdapter<CharSequence> invAdapter;

    //ID to maintain the memory bank selected
    private String memoryBankID = "none";
    private Button inventoryButton;

    private long prevTime = 0;
    private TextView timeText,trxtview;
    private Spinner invSpinner;
    private TextView batchModeInventoryList;
    SessionManagement session;

    String radiostr,imei,username,password;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!Application.mIsInventoryRunning) {
                toggle(view, position);
                Application.accessControlTag = adapter.getItem(position).getTagID();
                Application.locateTag = adapter.getItem(position).getTagID();
            }
        }
    };


    private OnFragmentInteractionListener mListener;

    public MasterEntryDataFragment() {
        // Required empty public constructor
    }
    public static MasterEntryDataFragment newInstance() {
        return new MasterEntryDataFragment();
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
        return inflater.inflate(R.layout.fragment_master_entry_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        billno =(EditText)getActivity().findViewById(R.id.bill_no);
        dest =(AutoCompleteTextView) getActivity().findViewById(R.id.dest);
        source =(AutoCompleteTextView)getActivity(). findViewById(R.id.source);
        contno =(EditText)getActivity().findViewById(R.id.conter);
        truckno =(EditText)getActivity().findViewById(R.id.truckk);
        Latitude =(TextView) getActivity().findViewById(R.id.latitude);
        Longitude =(TextView) getActivity().findViewById(R.id.longitude);


        locationManager =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();

        session = new SessionManagement(getContext());
        HashMap<String, String> user = session.getUserDetails();
        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);



        new AsynDestination().execute();
        dest.setThreshold(1);
        source.setThreshold(1);
        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data =parent.getItemAtPosition(position).toString();

            }
        });
        dest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data =parent.getItemAtPosition(position).toString();



            }
        });



        save =(Button)getActivity().findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   uniqueno1 =uniqueno.getText().toString();
                billno1 =billno.getText().toString();
                contno1 =contno.getText().toString();
                truckno1 =truckno.getText().toString();

                if (TextUtils.isEmpty(billno.getText()) && TextUtils.isEmpty(contno.getText()) && TextUtils.isEmpty(contno.getText()) && TextUtils.isEmpty(truckno.getText())){
                    new MaterialStyledDialog.Builder(getContext())
                            .setTitle("Oops!")
                            .setDescription("You left a Mandatory Field Blank....Please fill it to continue with the Master Entry process.")
                            .setIcon(R.mipmap.eseal)
                            .setHeaderDrawable(R.mipmap.header_image)
                            .setPositiveText(R.string.button)
                            .withIconAnimation(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                else {
                    getLocation();
                    initializeViewObject();
                }

              /*  Intent intent =new Intent(getActivity(),InformationActivity.class);

                //  intent.putExtra("Uniqueno",uniqueno1);
                intent.putExtra("Billno",billno1);
                intent.putExtra("Contno",contno1);
                intent.putExtra("Truckno",truckno1);

                startActivity(intent);*/

           /*   InformationFragment fragment = new InformationFragment();
                fragment =InformationFragment.newInstance();

                FragmentManager manager =getFragmentManager();
                manager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

*/

            }
        });

        date =(TextView)getActivity().findViewById(R.id.datetoday);

        SimpleDateFormat dateFormat = new SimpleDateFormat(" MM/dd/yyyy");
        Calendar currentCal = Calendar.getInstance();
        getData = dateFormat.format(currentCal.getTime());
        date.setText(getData);
        calendar = Calendar.getInstance();

        billno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    new DatePickerDialog(getActivity(), listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        getData = date.getText().toString();




        totalNoOfTags = (TextView) getActivity().findViewById(R.id.inventoryCountText);
        uniqueTags = (TextView) getActivity().findViewById(R.id.inventoryUniqueText);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        //Change the icon
        ((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.dl_inv);

        if (totalNoOfTags != null)
            totalNoOfTags.setText(String.valueOf(Application.TOTAL_TAGS));

        if (uniqueTags != null)
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));

        invSpinner = (Spinner) getActivity().findViewById(R.id.inventoryOptions);
        // Create an ArrayAdapter using the string array and a default spinner layout
        invAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.inv_menu_items, R.layout.spinner_small_font);
        // Specify the layout to use when the list of choices appears
        invAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        invSpinner.setAdapter(invAdapter);
        if (Application.memoryBankId != -1)
            invSpinner.setSelection(Application.memoryBankId);
        invSpinner.setOnItemSelectedListener(this);
        if (Application.mIsInventoryRunning) {
            invSpinner.setEnabled(false);
        }

        inventoryButton = (Button) getActivity().findViewById(R.id.inventoryButton);
        if (inventoryButton != null) {
            if (Application.mIsInventoryRunning)
                inventoryButton.setText(getString(R.string.stop_title));
        }

        //Set the font size in constants
        Constants.INVENTORY_LIST_FONT_SIZE = (int) getResources().getDimension(R.dimen.inventory_list_font_size);

        batchModeInventoryList = (TextView) getActivity().findViewById(R.id.batchModeInventoryList);

        listView = (ListView) getActivity().findViewById(R.id.inventoryList);
        //  trxtview = (TextView) getActivity().findViewById(R.id.inventoryList1);
        adapter = new ModifiedInventoryAdapter(getActivity(), R.layout.inventory_list_item);

        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);

        //   trxtview.setText((CharSequence) listView);

        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            listView.setEmptyView(batchModeInventoryList);
            batchModeInventoryList.setVisibility(View.VISIBLE);
        } else {
            listView.setAdapter(adapter);
            batchModeInventoryList.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(onItemClickListener);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date.setText("" + (month + 1) + "/" + dayOfMonth + "/" + year);
            getData = date.getText().toString();

        }
    };


  /*  protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new DatePickerDialog(getActivity(), dpClickListener, year_x, month_x, day_x);
        }
        return null;
    }*/
/*
    DatePickerDialog.OnDateSetListener dpClickListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            date.setText(month_x + "/" + day_x + "/" + year_x);
        }
    };*/

    public class AsynDestination extends AsyncTask<Void,Void,Void> {
        private ArrayList<String> Destination =new ArrayList<>();
        ProgressDialog pd =new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please Wait We Are Fetching Some Data From DataBase.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                SoapObject request =new SoapObject(NAMESPACE,METHOD_NAME);

                SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet =true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE =new HttpTransportSE(URL,60 * 10000);
                httpTransportSE.call(Soap_ACTION,envelope);

                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() > 0){
                    for (int i =0;i<object.getPropertyCount();i++){
                        String message =object.getProperty(i).toString();
                        Destination.add(message);


                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
                pd.dismiss();

                serverissue = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                autoadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, Destination);
                dest.setAdapter(autoadapter);
                source.setAdapter(autoadapter);
                pd.dismiss();

                if (serverissue) {
                    MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                            .setTitle("Ooops!!!")
                            .setIcon(R.mipmap.eseal)
                            .setHeaderDrawable(R.color.colorPrimary).setDescription("Server is not responding...\n")
                            .withIconAnimation(true)
                            .setPositiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    //    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    Log.d("MaterialStyledDialogs", "Do something!");
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
            catch (Exception e){

            }
        }
    }


    private void initializeViewObject(){
        try {
            Fragment fragment = new com.enpeck.RFID.MasterData.InformationFragment();
            Bundle bundle = new Bundle();

            bundle.putString("billno", billno1);
            bundle.putString("contno", contno1);
            bundle.putString("truckno", truckno1);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();

        }
        catch (Exception e){

        }
    }



    //////assign tag//////////////
    public ModifiedInventoryAdapter getAdapter() {
        return adapter;
    }

    private void toggle(View view, final int position) {
        InventoryListItem listItem = adapter.getItem(position);

        if (!listItem.isVisible()) {
            listItem.setVisible(true);
            view.setBackgroundColor(0x66444444);
        } else {
            listItem.setVisible(false);
            view.setBackgroundColor(Color.WHITE);
        }
        //if(!Application.mIsInventoryRunning)
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        memoryBankID = adapterView.getSelectedItem().toString();
        Application.memoryBankId = invAdapter.getPosition(memoryBankID);
        memoryBankID = memoryBankID.toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Method to access the memory bank set
     *
     * @return - Memory bank set
     */
    public String getMemoryBankID() {
        return memoryBankID;
    }

    /**
     * method to reset the tag info
     */
    public void resetTagsInfo() {
        if (Application.inventoryList != null && Application.inventoryList.size() > 0)
            Application.inventoryList.clear();
        if (totalNoOfTags != null)
            totalNoOfTags.setText(String.valueOf(Application.TOTAL_TAGS));
        if (uniqueTags != null)
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        if (timeText != null) {
            String min = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(Application.mRRStartedTime));
            String sec = String.format("%d", TimeUnit.MILLISECONDS.toSeconds(Application.mRRStartedTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Application.mRRStartedTime)));
            if (min.length() == 1) {
                min = "0" + min;
            }
            if (sec.length() == 1) {
                sec = "0" + sec;
            }
            timeText.setText(min + ":" + sec);
        }
        if (listView.getAdapter() != null) {
            ((ModifiedInventoryAdapter) listView.getAdapter()).clear();
            ((ModifiedInventoryAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        if (listView.getAdapter() == null) {
            listView.setAdapter(adapter);
            batchModeInventoryList.setVisibility(View.GONE);
        }

        totalNoOfTags.setText(String.valueOf(Application.TOTAL_TAGS));
        if (uniqueTags != null)
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        if (isAddedToList) {
            adapter.add(inventoryListItem);
        }
        adapter.notifyDataSetChanged();
    }

    //  @Override
    public void handleStatusResponse(final RFIDResults results) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (results.equals(RFIDResults.RFID_BATCHMODE_IN_PROGRESS)) {
                    if (listView != null && batchModeInventoryList != null) {
                        listView.setEmptyView(batchModeInventoryList);
                        batchModeInventoryList.setText(R.string.batch_mode_inventory_title);
                        batchModeInventoryList.setVisibility(View.VISIBLE);
                    }
                } else if (!results.equals(RFIDResults.RFID_API_SUCCESS)) {
                    //String command = statusData.command.trim();
                    //if (command.equalsIgnoreCase("in") || command.equalsIgnoreCase("inventory") || command.equalsIgnoreCase("read") || command.equalsIgnoreCase("rd"))
                    {
                        Application.isBatchModeInventoryRunning = false;
                        Application.mIsInventoryRunning = false;
                        Button inventoryButton = (Button) getActivity().findViewById(R.id.inventoryButton);
                        if (inventoryButton != null) {
                            inventoryButton.setText(getResources().getString(R.string.start_title));
                        }
                        if (invSpinner != null)
                            invSpinner.setEnabled(true);
                    }
                }
            }
        });
    }
    //   @Override
    public void triggerPressEventRecieved() {
        if (!Application.mIsInventoryRunning)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).inventoryStartOrStop(inventoryButton);
                }
            });
    }

    //   @Override
    public void triggerReleaseEventRecieved() {
        if (Application.mIsInventoryRunning)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).inventoryStartOrStop(inventoryButton);
                }
            });
    }

    /**
     * method to set inventory status to stopped on reader disconnection
     */
    public void resetInventoryDetail() {
        if (getActivity() != null) {
            if (inventoryButton != null)
                inventoryButton.setText(getString(R.string.start_title));
            if (invSpinner != null)
                invSpinner.setEnabled(true);
            if (batchModeInventoryList != null && batchModeInventoryList.getVisibility() == View.VISIBLE) {
                listView.setAdapter(adapter);
                batchModeInventoryList.setText("");
                batchModeInventoryList.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void batchModeEventReceived() {
        if (inventoryButton != null) {
            inventoryButton.setText(getString(R.string.stop_title));
        }
        if (invSpinner != null) {
            invSpinner.setSelection(0);
            invSpinner.setEnabled(false);
        }
        if (listView != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            listView.setEmptyView(batchModeInventoryList);
            batchModeInventoryList.setText(R.string.batch_mode_inventory_title);
            batchModeInventoryList.setVisibility(View.VISIBLE);
        }
    }

    void getLocation(){
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);

        }else {
            Location location =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null){
                double latti =location.getLatitude();
                double logti =location.getLongitude();


              Latitude.setText("Latitude :" +latti);
                Longitude.setText("Longitude :" +logti);

            }
            else
            {
               Latitude.setText("Unable to find Location");
                Longitude.setText("Unable to find Location");

            }
        }

    }

    public void onRequestPermissionsResult(int requestcode, String[] permission,int[] grantResult){
        super.onRequestPermissionsResult(requestcode, permission, grantResult);

        switch (requestcode){
            case RESULT_CANCELED :
                getLocation();
                break;
        }
    }
}
