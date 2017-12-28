package com.enpeck.RFID.vertifyTag;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Bean;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.ModelDeailyReport;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.inventory.InventoryListItem;
import com.enpeck.RFID.inventory.ModifiedInventoryAdapter;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.zebra.rfid.api3.RFIDResults;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static com.enpeck.RFID.application.Application.accessControlTag;


/**
 * A simple {@link Fragment} subclass.
 */
public class Verifiy_Tag extends Fragment implements Spinner.OnItemSelectedListener, ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler {

    // private TableFixHeaders tableFixHeaders;
    private TextView hintetext;
    TextView Serial, Iec, Bill, Vechicle, Container, Dest, sealdate, sealtime, shipdate, Sealno, latitude, longitude, imei;


    TextView totalNoOfTags;
    TextView uniqueTags, taggg;
    private ListView listView;
    LocationManager locationManager;
    static final int REQUEST_LOCATION = 1;
    String str = "353857080012475";
    String lati = "77.199621", longi = "28.614360";
    //  private String[] tableHeaders={"Serial No","IEC Code","Bill No","Vehicle No","Container No","Destination Port","Sealing_Date","Sealing_Time","Shipping_Date"};
    public static ArrayList<ModelDeailyReport> listReport = new ArrayList<>();
    Boolean serverissue = false;


    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String Soap_ACTION = "http://tempuri.org/GetAssignTag";
    // specifies the action
    private static final String METHOD_NAME = "GetAssignTag";
    private static final String Soap_ACTIONflag = "http://tempuri.org/UpdateFlagEseal";
    // specifies the action
    private static final String METHOD_NAMEflag = "UpdateFlagEseal";
    String tagg, date, deviceIMEI;
    String serial, iec, bill, vechicle, container, dest, sealingdate, sealingtime, shippingdate, SealNo;

    SessionManagement session;
    LinearLayout linear;
    Button reset;

    String radiostr, imei1, username, password;
    private ModifiedInventoryAdapter adapter;
    private ArrayAdapter<CharSequence> invAdapter;

    //ID to maintain the memory bank selected
    private String memoryBankID = "none";
    private Button inventoryButton;

    private long prevTime = 0;
    private TextView timeText, trxtview;
    private Spinner invSpinner;
    private TextView batchModeInventoryList;
    List<InventoryListItem> items;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
            InventoryListItem inventoryListItem =items.get(position);
            items.remove(inventoryListItem);*/
            if (!Application.mIsInventoryRunning) {
                toggle(view, position);


                // String a =((TextView)view.findViewById(R.id.abc).getText().toString());
                accessControlTag = adapter.getItem(position).getTagID();
                Application.locateTag = adapter.getItem(position).getTagID();
               /* tagg = adapter.getItem(0).getTagID();
                tag.setText(tagg);*/

                listView.setAdapter(null);
             //   new asyncAssignTag(tagg).execute();


            }
        }
    };

    public Verifiy_Tag() {
        // Required empty public constructor
    }

    public static Verifiy_Tag newInstance() {
        return new Verifiy_Tag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public ModifiedInventoryAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verifiy__tag, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taggg = (TextView) getActivity().findViewById(R.id.abc);
        totalNoOfTags = (TextView) getActivity().findViewById(R.id.inventoryCountText);
        linear = (LinearLayout) getActivity().findViewById(R.id.linear);
        uniqueTags = (TextView) getActivity().findViewById(R.id.inventoryUniqueText);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        //Change the icon
        ((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.dl_inv);

        if (totalNoOfTags != null)
            totalNoOfTags.setText(String.valueOf(Application.TOTAL_TAGS));

        if (uniqueTags != null)
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));

        // new asyncAssignTag(tagg).execute();
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

        //  new asyncAssignTag(tagg).execute();
        //Set the font size in constants
        Constants.INVENTORY_LIST_FONT_SIZE = (int) getResources().getDimension(R.dimen.inventory_list_font_size);

        batchModeInventoryList = (TextView) getActivity().findViewById(R.id.batchModeInventoryList);

        listView = (ListView) getActivity().findViewById(R.id.inventoryList);
        //  trxtview = (TextView) getActivity().findViewById(R.id.inventoryList1);
        adapter = new ModifiedInventoryAdapter(getActivity(), R.layout.inventory_list_item);

        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);

        listView.setAdapter(null);
        if (adapter != null){
            adapter.clear();
        }
//        adapter.clear();
        //   trxtview.setText((CharSequence) listView);

        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            listView.setEmptyView(batchModeInventoryList);
            batchModeInventoryList.setVisibility(View.VISIBLE);
        } else {
            listView.setAdapter(adapter);
            batchModeInventoryList.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(onItemClickListener);


        latitude = (TextView) getActivity().findViewById(R.id.latitude);
        longitude = (TextView) getActivity().findViewById(R.id.longitude);
        imei = (TextView) getActivity().findViewById(R.id.imei);

        // getLocation();
        reset = (Button) getActivity().findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Verifiy_Tag fragment2 = new Verifiy_Tag();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment2);
                fragmentTransaction.commit();
        */

                if (Serial != null && Iec != null && Bill != null && Vechicle != null && Container != null && Dest != null
                        && sealdate != null && sealtime != null && shipdate != null
                        && Sealno != null && latitude != null && longitude != null && imei != null) {
                    Serial.setText("");
                    Iec.setText("");
                    Bill.setText("");
                    Vechicle.setText("");
                    Container.setText("");
                    Dest.setText("");
                    sealdate.setText("");
                    sealtime.setText("");
                    shipdate.setText("");
                    Sealno.setText("");
                    latitude.setText("");
                    longitude.setText("");
                    imei.setText("");
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(null);
                    adapter.clear();
                }
            }
        });

       /* latitude.setText("Latitude :" +latti);
        longitude.setText("Longitude :" +logti);*/
       /* if (listView !=null) {
            listView.performItemClick(
                    listView.getAdapter().getView(0, null, null),
                    0,
                    listView.getAdapter().getItemId(0));
        }*/


//        adapter.notifyDataSetChanged();
        // tableFixHeaders = (TableFixHeaders)getActivity(). findViewById(R.id.table);
        hintetext = (TextView) getActivity().findViewById(R.id.hintetext);


        session = new SessionManagement(getContext());
        HashMap<String, String> user = session.getUserDetails();
        imei1 = user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr = user.get(SessionManagement.KEY_password);



 /*       SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar currentCal = Calendar.getInstance();
        date = dateFormat.format(currentCal.getTime());
*/


        // new asyncAssignTag().execute();
       /* MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                .setTitle("Ooops!!!")
                .setIcon(R.mipmap.eseal)
                .setHeaderDrawable(R.color.colorPrimary)
                .setDescription("Please Assign Tag \n")
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
*/
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
            uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
        if (isAddedToList) {
            adapter.add(inventoryListItem);
        }
        adapter.notifyDataSetChanged();

        if (listView.getAdapter()==null){
            batchModeInventoryList.setVisibility(View.VISIBLE);
        }else {

            listView.performItemClick(listView, 0, listView.getItemIdAtPosition(0));
            tagg = adapter.getItem(0).getTagID();

            new asyncAssignTag(tagg).execute();

        }

        listView.setAdapter(null);





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
        if (Application.mIsInventoryRunning) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ((MainActivity) getActivity()).inventoryStartOrStop(inventoryButton);
                }
            });

        }
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


    //webmethod//////


    public class asyncAssignTag extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd = new ProgressDialog(getActivity());
        String tag, portt, truckk, contt;
        Bean beanobj = new Bean();
        int flag = 0;

        public asyncAssignTag(String tag) {
            this.tag = tag;
            this.portt = portt;
            this.truckk = truckk;
            this.contt = contt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.show();

            //  tag =taggg.getText().toString();


        }

        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("Tag", tagg);
               /* request.addProperty("port",portt);
                request.addProperty("truckno",truckk);
                request.addProperty("container",contt);*/

                Bean C = new Bean();
                PropertyInfo pi = new PropertyInfo();
                pi.setName("Bean");
                pi.setValue(C);
                pi.setType(C.getClass());
                request.addProperty(pi);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                envelope.addMapping(NAMESPACE, "Bean", new Bean().getClass());

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.debug = true;
                androidHttpTransport.call(Soap_ACTION, envelope);

                SoapObject response2 = (SoapObject) envelope.getResponse();
                Bean[] personobj = new Bean[response2.getPropertyCount()];
                Bean beanobj = new Bean();

                for (int j = 0; j < personobj.length; j++) {

                    SoapObject pii = (SoapObject) response2.getProperty(j);
                    beanobj.serial = pii.getProperty(0).toString();
                    beanobj.iec = pii.getProperty(1).toString();
                    beanobj.bill = pii.getProperty(2).toString();
                    beanobj.vechicle = pii.getProperty(3).toString();
                    beanobj.container = pii.getProperty(4).toString();
                    beanobj.dest = pii.getProperty(5).toString();
                    beanobj.sealingdate = pii.getProperty(6).toString();
                    beanobj.sealingtime = pii.getProperty(7).toString();
                    beanobj.shippingdate = pii.getProperty(8).toString();
                    beanobj.Sealno = pii.getProperty(9).toString();

                    personobj[j] = beanobj;

                }

                serial = beanobj.serial;
                iec = beanobj.iec;
                bill = beanobj.bill;
                vechicle = beanobj.vechicle;
                container = beanobj.container;
                dest = beanobj.dest;
                sealingdate = beanobj.sealingdate;
                sealingtime = beanobj.sealingtime;
                shippingdate = beanobj.shippingdate;
                SealNo = beanobj.Sealno;

               /* if (object.getPropertyCount() > 0) {
                    flag =1;
                    for (int i = 0; i < object.getPropertyCount(); i++) {
                        SoapObject innerResponse = (SoapObject) object.getProperty(i);

                        listReport.add(new ModelDeailyReport(
                                innerResponse.getProperty("S1").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S1").toString(),
                                innerResponse.getProperty("S2").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S2").toString(),
                                innerResponse.getProperty("S3").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S3").toString(),
                                innerResponse.getProperty("S4").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S4").toString(),
                                innerResponse.getProperty("S5").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S5").toString(),
                                innerResponse.getProperty("S6").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S6").toString(),
                                innerResponse.getProperty("S7").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S7").toString(),
                                innerResponse.getProperty("S8").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S8").toString(),
                                innerResponse.getProperty("S9").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S9").toString()
                        ));
                    }
                }
                else {
                    flag =0;

                }*/

            } catch (Exception e) {
                e.printStackTrace();
                pd.dismiss();

                serverissue = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
/*            if (serial != null && iec != null && bill != null && vechicle != null && container != null && dest != null && sealingdate != null && sealingtime != null && shippingdate != null
                    && SealNo != null && latitude != null && longitude != null && imei != null) {

            }*/
          /*  if (flag == 0){
                hintetext.setVisibility(View.VISIBLE);
                //tableFixHeaders.setVisibility(View.INVISIBLE);
            } else
            {
                hintetext.setVisibility(View.INVISIBLE);
               // tableFixHeaders.setVisibility(View.VISIBLE);
            }*/
            pd.dismiss();
            super.onPostExecute(aVoid);

            if (serial == null && iec == null && bill == null && vechicle == null && container == null && dest == null) {
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_cancel)
                        .setHeaderDrawable(R.color.red)
                        .setDescription("Tampered\n")
                        .withIconAnimation(true)
                        .setPositiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                Log.d("MaterialStyledDialogs", "Do something!");
                                listView.setAdapter(null);
//                                adapter.clear();
                                dialog.dismiss();

                            }
                        })
                        .show();
            } else {
                display();
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Yess !!!")
                        .setIcon(R.drawable.ic_check)
                        .setHeaderDrawable(R.color.green)
                        .setDescription("Not Tampered\n")
                        .withIconAnimation(true)
                        .setPositiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                Log.d("MaterialStyledDialogs", "Do something!");
                                //    adapter.clear();
                                new UpdateFlag().execute();
                                listView.setAdapter(null);

//                                adapter.clear();
                                dialog.dismiss();


                            }
                        })
                        .show();
            }


            if (serverissue) {
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Ooops!!!")
                        .setIcon(R.mipmap.eseal)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .setDescription("Server is not responding...\n")
                        .withIconAnimation(true)
                        .setPositiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                Log.d("MaterialStyledDialogs", "Do something!");
                                listView.setAdapter(null);

                                // adapter.clear();
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
               /* MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Yess!!!")
                        .setIcon(R.drawable.ic_check)
                        .setHeaderDrawable(R.color.green)
                        .setDescription("Not Tampered\n")
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
                        .show();*/
            }

            //tableFixHeaders.setAdapter(new MyAdapter(getActivity(), tableHeaders, listReport));

            adapter.clear();
        }
    }

    public class UpdateFlag extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        boolean success = false;


        String abc = taggg.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait while we Board you in..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            //  Sealno =sealno.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEflag);
            request.addProperty("eseal", tagg);
            //  request.addProperty("date",date);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL, 60 * 100000);
            try {
                httpTransportSE.call(Soap_ACTIONflag, envelope);
                SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
                if (soapPrimitive.toString().equals("success")) {
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(getContext(), "Data is not save in database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Data is save in database", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void display() {

        Serial = (TextView) getActivity().findViewById(R.id.serial);
        Serial.setText(serial);
        Iec = (TextView) getActivity().findViewById(R.id.iec);
        Iec.setText(iec);
        Bill = (TextView) getActivity().findViewById(R.id.billnoo);
        Bill.setText(bill);
        Vechicle = (TextView) getActivity().findViewById(R.id.vechicle);
        Vechicle.setText(vechicle);
        Container = (TextView) getActivity().findViewById(R.id.containerr);
        Container.setText(container);
        Dest = (TextView) getActivity().findViewById(R.id.dest);
        Dest.setText(dest);
        sealdate = (TextView) getActivity().findViewById(R.id.sealingdate);
        sealdate.setText( sealingdate);
        sealtime = (TextView) getActivity().findViewById(R.id.sealingtime);
        sealtime.setText( sealingtime);
        shipdate = (TextView) getActivity().findViewById(R.id.date);
        shipdate.setText(shippingdate);
        Sealno = (TextView) getActivity().findViewById(R.id.sealno);
        Sealno.setText(SealNo);
        TelephonyManager tManager = (TelephonyManager) getActivity().getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        deviceIMEI = tManager.getDeviceId();

        imei.setText(deviceIMEI);
        locationManager =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        //        latitude.setText(lati);
//        longitude.setText(longi);
    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latti = location.getLatitude();
                double logti = location.getLongitude();


                longi =String.valueOf(logti);
                lati =String.valueOf(latti);
                latitude.setText( longi);
                longitude.setText( lati);

            } else {
                latitude.setText("Unable to find Location");
                longitude.setText("Unable to find Location");

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
