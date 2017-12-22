package com.enpeck.RFID.inventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.common.ModelDeailyReport;
import com.enpeck.RFID.common.SampleTableAdapter;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.common.TableFixHeaders;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import static com.enpeck.RFID.inventory.AssignTag.TAG_CONTENT_FRAGMENT;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle inventory operations and UI.
 */
public class InventoryFragment extends Fragment /*implements Spinner.OnItemSelectedListener, ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler*/ {
    private TableFixHeaders tableFixHeaders;
    private TextView hintetext;
    Button show,all;
    Spinner billno,truck,container,port;
    String billl,truckk,contt,portt;
    private BroadcastReceiver mReceiver;
    TextView billno11,port11,containerr11,truck11,time,datetime;
    String billno111,port111,container111,truck111,serialno111,date;
    String formattedDate;
    SessionManagement session;


    private String[] tableHeaders={"Serial No","IEC Code","Bill No","Bill Date","Vehicle No","Source Port","Destination Port","Container No","e-Seal No"};
    public static ArrayList<ModelDeailyReport> listReport = new ArrayList<>();


    Boolean serverissue = false;

    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String Soap_ACTIONAll = "http://tempuri.org/GetNonAssignTag1";
    // specifies the action
    private static final String METHOD_NAMEAll = "GetNonAssignTag1";
    private static final String Soap_ACTION = "http://tempuri.org/GetInformation1";
    // specifies the action
    private static final String METHOD_NAME = "GetInformation1";

    private static final String Soap_ACTIONBill = "http://tempuri.org/GetBillNo";
    // specifies the action
    private static final String METHOD_NAMEBill = "GetBillNo";

    private static final String Soap_ACTIONtruck = "http://tempuri.org/GetTruckNo";
    // specifies the action
    private static final String METHOD_NAMEtruck = "GetTruckNo";

    private static final String Soap_ACTIONPort = "http://tempuri.org/GetPort";
    // specifies the action
    private static final String METHOD_NAMEPort = "GetPort";

    private static final String Soap_ACTIONcont = "http://tempuri.org/GetContNo";
    // specifies the action
    private static final String METHOD_NAMEcont = "GetContNo";

    Context context;

    String billno1,truckk1,container1,portt1;

    LinearLayout linearlayoutassign;
    ImageView image;
    String radiostr,imei,username,password;

    /*TextView totalNoOfTags;
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!Application.mIsInventoryRunning) {
                toggle(view, position);
                Application.accessControlTag = adapter.getItem(position).getTagID();
                Application.locateTag = adapter.getItem(position).getTagID();
            }
        }
    };*/

    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InventoryFragment.
     */
    public static InventoryFragment newInstance() {
        return new InventoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



   /* public ModifiedInventoryAdapter getAdapter() {
        return adapter;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }
    public void onStart() {
        super.onStart();
        initializeViewObject();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        datetime =(TextView)getActivity().findViewById(R.id.esealdate) ;
        time =(TextView)getActivity().findViewById(R.id.esealtime) ;
/*        totalNoOfTags = (TextView) getActivity().findViewById(R.id.inventoryCountText);
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

        inventoryButton = (Button) getActivity().findViewById(inventoryButton);
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new InventoryFragment2();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

            }
        });
        if (inventoryButton != null) {
            if (Application.mIsInventoryRunning)
                inventoryButton.setText(getString(R.string.stop_title));
        }

        //Set the font size in constants
        Constants.INVENTORY_LIST_FONT_SIZE = (int) getResources().getDimension(R.dimen.inventory_list_font_size);

        batchModeInventoryList = (TextView) getActivity().findViewById(batchModeInventoryList);

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
        listView.setOnItemClickListener(onItemClickListener);*/

        /*if ( listView.getAdapter() != null) {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time =&gt; " + c.getTime());

            Time hours = new Time(System.currentTimeMillis());
            time.setText(String.valueOf(hours));

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            formattedDate = df.format(c.getTime());
            datetime.setText(formattedDate);

        }
        else
        {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time =&gt; " + c.getTime());

            Time hours = new Time(System.currentTimeMillis());
            time.setText(String.valueOf(hours));

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            formattedDate = df.format(c.getTime());
            datetime.setText(formattedDate);
        }
*/

      /*  billno11 = (TextView) getActivity().findViewById(R.id.billnoo);
        port11 = (TextView)  getActivity().findViewById(R.id.destt);
        truck11 = (TextView)  getActivity().findViewById(R.id.trucknoo);
        containerr11 = (TextView)  getActivity().findViewById(containerr);

*/


        new asyncGetBillNo().execute();
        new asyncGetTruckNo().execute();
        new asyncGetContainerNo().execute();
        new asyncGetPort().execute();

        tableFixHeaders = (TableFixHeaders)getActivity(). findViewById(R.id.table);
         hintetext=(TextView)getActivity().findViewById(R.id.hintetext);
        billno =(Spinner)getActivity(). findViewById(R.id.bill_no);
        truck =(Spinner)getActivity(). findViewById(R.id.truckno);
        container =(Spinner)getActivity(). findViewById(R.id.container);
        port =(Spinner)getActivity(). findViewById(R.id.port);
        show =(Button)getActivity().findViewById(R.id.show);

/*

        bill =(TextView)getActivity().findViewById(R.id.billno);
        port1 =(TextView)getActivity().findViewById(R.id.port1);
        truck1 =(TextView)getActivity().findViewById(R.id.truck1);
        container11 =(TextView)getActivity().findViewById(R.id.containerno);

*/

/*
        session = new SessionManagement(getContext());
        HashMap<String, String> user = session.getUserDetails();

        *//*companyname1 = user.get(SessionManagement.KEY_company);
        username1 = user.get(SessionManagement.KEY_username);
        ieccode1 =user.get(SessionManagement.KEY_Ieccode);*//*
        password1 =user.get(SessionManagement.KEY_password);*/


        SessionManagement session = new SessionManagement(getContext().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();


        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);


        image =(ImageView)getActivity().findViewById(R.id.image);

        linearlayoutassign =(LinearLayout)getActivity().findViewById(R.id.linearlayoutassign);

        if (radiostr.equals("Custom Officer")){
            linearlayoutassign.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        }else{
            linearlayoutassign.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
        }

        new asyncAllReport().execute();

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new asyncDailyReport(billl,portt,truckk,contt).execute();

            }
        });
    }

    public class asyncGetBillNo extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        ProgressDialog pd =new ProgressDialog(getActivity());

        private ArrayList<String> brandname = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEBill);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

                httpTransportSE.call(Soap_ACTIONBill, envelope);
                SoapObject obj = (SoapObject) envelope.getResponse();


                for (int i = 0; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }
                Loca = model.split(",");
                Loca[0] = "-- Select Bill No --";
                //  Loca[1] = "All";


                Loca = model.split(",");
                Loca[0] = "-- Select Bill No --";
                //   Loca[1] = "All";

            } catch (Exception e) {
                e.printStackTrace();

                pd.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

            pd.dismiss();
            super.onPostExecute(aVoid);
            try{
                ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
                brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                billno.setAdapter(brandNameAdapter);
                port.setSelection(0);
                truck.setSelection(0);
                container.setSelection(0);
            }catch (Exception e){}



        }
    }
    public class asyncGetTruckNo extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        ProgressDialog pd =new ProgressDialog(getActivity());

        private ArrayList<String> brandname = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEtruck);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

                httpTransportSE.call(Soap_ACTIONtruck, envelope);
                SoapObject obj = (SoapObject) envelope.getResponse();


                for (int i = 0; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }
                Loca = model.split(",");
                Loca[0] = "-- Select Truck No --";
                //   Loca[1] = "All";

               /* if(obj.getPropertyCount() >0){
                    for (int i =0;i<obj.getPropertyCount(); i++){
                        model =obj.getProperty(i).toString();
                        brandname.add(model);
                    }
                }*/
               /* for (int i = 1; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }*/
                Loca = model.split(",");
                Loca[0] = "-- Select Truck No --";
                //  Loca[1] = "All";

            } catch (Exception e) {
                e.printStackTrace();

                pd.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

            pd.dismiss();
            super.onPostExecute(aVoid);
            try{
                ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
                brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                truck.setAdapter(brandNameAdapter);

            }catch (Exception e){

            }

        }
    }
    public class asyncGetContainerNo extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        ProgressDialog pd =new ProgressDialog(getActivity());

        private ArrayList<String> brandname = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEcont);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

                httpTransportSE.call(Soap_ACTIONcont, envelope);
                SoapObject obj = (SoapObject) envelope.getResponse();


                for (int i = 0; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }
                Loca = model.split(",");
                Loca[0] = "-- Select Container No --";
                //   Loca[1] = "All";

               /* if(obj.getPropertyCount() >0){
                    for (int i =0;i<obj.getPropertyCount(); i++){
                        model =obj.getProperty(i).toString();
                        brandname.add(model);
                    }
                }*/
               /* for (int i = 1; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }*/
                Loca = model.split(",");
                Loca[0] = "-- Select Container No --";
                //   Loca[1] = "All";

            } catch (Exception e) {
                e.printStackTrace();

                pd.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

            pd.dismiss();
            super.onPostExecute(aVoid);
            try{
                ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
                brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                container.setAdapter(brandNameAdapter);
            }catch (Exception e){}


        }
    }
    public class asyncGetPort extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;

        private ArrayList<String> brandname = new ArrayList<>();
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
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEPort);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

                httpTransportSE.call(Soap_ACTIONPort, envelope);
                SoapObject obj = (SoapObject) envelope.getResponse();


                for (int i = 0; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }
                Loca = model.split(",");
                Loca[0] = "-- Select Port --";
                //   Loca[1] = "All";

               /* if(obj.getPropertyCount() >0){
                    for (int i =0;i<obj.getPropertyCount(); i++){
                        model =obj.getProperty(i).toString();
                        brandname.add(model);
                    }
                }*/
               /* for (int i = 1; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }*/
                Loca = model.split(",");
                Loca[0] = "-- Select Port --";
                //  Loca[1] = "All";

            } catch (Exception e) {
                e.printStackTrace();

                pd.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

            pd.dismiss();
            super.onPostExecute(aVoid);
            try{
                ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
                brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                port.setAdapter(brandNameAdapter);

            }catch (Exception e){}

        }
    }

    public class asyncDailyReport extends AsyncTask<Void,Void,Void>{
        ProgressDialog pd =new ProgressDialog(getActivity());
        String billl,portt,truckk,contt;

        int flag =0;

        public asyncDailyReport(String billl,String portt,String truckk,String contt ) {
            this.billl = billl;
            this.portt =portt;
            this.truckk =truckk;
            this.contt =contt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.show();
            pd.setCancelable(false);
            billl =billno.getSelectedItem().toString();
            portt =port.getSelectedItem().toString();
            truckk =truck.getSelectedItem().toString();
            contt =container.getSelectedItem().toString();

            if (billl == "-- Select Bill No --")
            {
                billl = " ";
            }
            if (truckk == "-- Select Truck No --")
            {
                truckk = " ";

            }
            if (contt == "-- Select Container No --")
            {
                contt = " ";
            }
            if (portt == "-- Select Port --")
            {
                portt = " ";
            }


        }

        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("billno", billl);
                request.addProperty("truckno",portt);
                request.addProperty("container",truckk);
                request.addProperty("port",contt);
                request.addProperty("username",username);
                request.addProperty("company",password);
                request.addProperty("radiostr",radiostr);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.call(Soap_ACTION, envelope);

                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() > 0) {
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
            if (flag == 0){
                hintetext.setVisibility(View.VISIBLE);
                tableFixHeaders.setVisibility(View.INVISIBLE);
            } else
            {
                 hintetext.setVisibility(View.INVISIBLE);
                tableFixHeaders.setVisibility(View.VISIBLE);
            }
            pd.dismiss();
            super.onPostExecute(aVoid);
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
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            tableFixHeaders.setAdapter(new MyAdapter(getActivity(), tableHeaders, listReport));


        }
    }
    public class asyncAllReport extends AsyncTask<Void,Void,Void>{
        ProgressDialog pd =new ProgressDialog(getActivity());
        String billl,portt,truckk,contt;

        int flag =0;

       /* public asyncDailyReport(String billl,String portt,String truckk,String contt ) {
            this.billl = billl;
            this.portt =portt;
            this.truckk =truckk;
            this.contt =contt;
        }*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
          /*  billl =billno.getSelectedItem().toString();
            portt =port.getSelectedItem().toString();
            truckk =truck.getSelectedItem().toString();
            contt =container.getSelectedItem().toString();
*/
          /*  if (billl == "-- Select Bill No --")
            {
                billl = " ";
            }
            if (truckk == "-- Select Truck No --")
            {
                truckk = " ";

            }
            if (contt == "-- Select Container No --")
            {
                contt = " ";
            }
            if (portt == "-- Select Port --")
            {
                portt = " ";
            }
*/

        }

        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEAll);
                request.addProperty("username",username);
                request.addProperty("company",password);
                request.addProperty("radiostr",radiostr);
             /*   request.addProperty("entryby",username1);
                request.addProperty("iec",ieccode1);*/
              /*  request.addProperty("billno", billl);
                request.addProperty("port",portt);
                request.addProperty("truckno",truckk);
                request.addProperty("container",contt);
*/
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.call(Soap_ACTIONAll, envelope);

                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() > 0) {
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
            if (flag == 0){
                hintetext.setVisibility(View.VISIBLE);
                tableFixHeaders.setVisibility(View.INVISIBLE);
            } else
            {
                hintetext.setVisibility(View.INVISIBLE);
                tableFixHeaders.setVisibility(View.VISIBLE);
            }
            pd.dismiss();
            super.onPostExecute(aVoid);
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
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            tableFixHeaders.setAdapter(new MyAdapter(getActivity(), tableHeaders, listReport));


        }
    }
    public class MyAdapter extends SampleTableAdapter {

        private String[] tableHeaderList;
        private final int widthXSmall;
        private final int widthSmall;
        private final int widthMedium;
        private final int widthLarge;
        private final int widthXLarge;
        private final int height;
        private ArrayList<ModelDeailyReport> list = new ArrayList<>();

        public MyAdapter(Context context, String[] tableHeaderList, ArrayList<ModelDeailyReport> list) {
            super(context);

            this.tableHeaderList = tableHeaderList;
            this.list = list;

            Resources resources = context.getResources();



            widthXSmall = resources.getDimensionPixelSize(R.dimen.table_width_xsmall);
            widthSmall = resources.getDimensionPixelSize(R.dimen.table_width_small);
            widthMedium = resources.getDimensionPixelSize(R.dimen.table_width_medium);
            widthLarge = resources.getDimensionPixelSize(R.dimen.table_width_large);
            widthXLarge = resources.getDimensionPixelSize(R.dimen.table_width_xlarge);
            height = resources.getDimensionPixelSize(R.dimen.table_height);

           //context.registerReceiver(mBroadcastReceiver, new IntentFilter("call.InventoryFragment2.action"));

            /*IntentFilter intentFilter=new IntentFilter("call.InventoryFragment2.action");
            mReceiver =new BroadcastReceiver(){

            }*/
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return tableHeaderList.length - 1;
        }

        @Override
        public int getWidth(int column) {
            switch(column){
                case -1:
                    return widthSmall;
            }
            return widthLarge;
        }

        @Override
        public int getHeight(int row) {
            return height;
        }

        @Override
        public String getCellString(int row, int column) {
            if (getItemViewType(row, column) == 0) {
                return tableHeaderList[column + 1];
            }

            return getColumnString(list.get(row), column + 1);
        }

        private String getColumnString(ModelDeailyReport modelSalesReport, int column) {
            switch (column) {
                case 0:
                    return modelSalesReport.getSerialno();
                case 1:
                    return modelSalesReport.getIECno();
                case 2:
                    return modelSalesReport.getBillno();
                case 3:
                    return modelSalesReport.getS_billDate();
                case 4:
                    return modelSalesReport.getVehicleno();
                case 5 :
                    return modelSalesReport.getSource();
                case 6:
                    return modelSalesReport.getDestination();
                case 7:
                    return modelSalesReport.getContainerno();
                case 8:
                    return modelSalesReport.getE_sealno();

            }
            return null;
        }

        @Override
        public int getLayoutResource(int row, int column) {
            final int layoutResource;
            switch (getItemViewType(row, column)) {
                case 0:
                    layoutResource = R.layout.item_table1_header;
                    break;
                case 1:
                    layoutResource = R.layout.item_table_center;
                    break;
                case 2:
                    layoutResource = R.layout.item_table_center_even;
                    break;
                case 3:
                    layoutResource = R.layout.item_table_center_odd;
                    break;
                default:
                    throw new RuntimeException("wtf?");
            }
            return layoutResource;
        }

        @Override
        public int getItemViewType(int row, int column) {

            if (row < 0) {
                return 0;
            } else if (column == -1) {
                return 1;
            } else if (column == 0){
                return 2;
            } else if (column == 1) {
                return 1;
            } else if (column == 2) {
                return 1;
            } else if (column == 3) {
                return 2;
            } else if (column == 4) {
                return 2;
            } else if (column == 5) {
                return 2;
            } else if(column ==7){
                return 2;
            }else if (column ==8){
                return 2;
            }
            return 2;
        }

        @Override
        public int getViewTypeCount() {
            return 10;
        }
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InventoryFragment2 fragment = new InventoryFragment2();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            Bundle bundle =getArguments();
            fragment.setArguments(bundle);
        }
    };

  /*  private void toggle(View view, final int position) {
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

    *//**
     * Method to access the memory bank set
     *
     * @return - Memory bank set
     *//*
    public String getMemoryBankID() {
        return memoryBankID;
    }

    *//**
     * method to reset the tag info
     *//*
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
                        Button inventoryButton = (Button) getActivity().findViewById(inventoryButton);
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

    *//**
     * method to set inventory status to stopped on reader disconnection
     *//*
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
    public void onStart() {
        super.onStart();
        initializeViewObject();
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


*/

    private void initializeViewObject(){
        IntentFilter intentFilter=new IntentFilter("call.InventoryFragment2.action");
        mReceiver =new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub

                String msg_me = intent.getStringExtra("row");
                port111 = intent.getStringExtra("Port");
                truck111 = intent.getStringExtra("truckno");
                container111 = intent.getStringExtra("Contno");
                billno111 = intent.getStringExtra("bill");
                serialno111 =intent.getStringExtra("serialno");
                date =intent.getStringExtra("date");
            /* billno11.setText(billno111);
                port11.setText(port111);
                truck11.setText(truck111);
                containerr11.setText(container111);*/
                //   Toast.makeText(context, "listen!", Toast.LENGTH_LONG).show();

                try {
                    Fragment fragment = new InventoryFragment2();
                    Bundle bundle = new Bundle();

                    //   Bundle bundle = new Bundle();
                    bundle.putString("bill", billno111);
                    bundle.putString("port", port111);
                    bundle.putString("truck", truck111);
                    bundle.putString("cont", container111);
                    bundle.putString("serialno", serialno111);
                    bundle.putString("date", date);
           /*   bundle.putString("sysdate",formattedDate);
                bundle.putString("systime",time.getText().toString());*/
                    fragment.setArguments(bundle);


                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null).commit();
                }catch (Exception e){

                }


            }
        };

        getActivity().registerReceiver(mReceiver, intentFilter);

    }
}
