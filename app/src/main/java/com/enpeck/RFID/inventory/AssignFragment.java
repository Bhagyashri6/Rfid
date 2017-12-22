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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.ModelDeailyReport;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.common.SampleTableAdapter;
import com.enpeck.RFID.common.TableFixHeaders;
import com.enpeck.RFID.home.MainActivity;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import static com.enpeck.RFID.R.id.truckno;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignFragment extends Fragment implements ResponseHandlerInterfaces.TriggerEventHandler {

    private TableFixHeaders tableFixHeaders;
    private TextView hintetext;
    Button show,all;
    Spinner billno,truck,container,port;
    String billl,truckk,contt,portt;
    private Button inventoryButton;





    public static AssignFragment newInstance() {
        return new AssignFragment();
    }

    private String[] tableHeaders={"Serial No","IEC Code","Bill No","Bill_Date","Vehicle No","Source Port","Destination Port","Container No","ESeal No"};
    static ArrayList<ModelDeailyReport> listReport = new ArrayList<>();


    Boolean serverissue = false;

    private static final String URL = "http://atm-india.in/service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String Soap_ACTION = "http://tempuri.org/GetInformation";
    // specifies the action
    private static final String METHOD_NAME = "GetInformation";

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


    public AssignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assign, container, false);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new asyncGetBillNo().execute();
        new asyncGetTruckNo().execute();
        new asyncGetContainerNo().execute();
        new asyncGetPort().execute();

        tableFixHeaders = (TableFixHeaders)getActivity(). findViewById(R.id.table);
       // hintetext=(TextView)getActivity().findViewById(R.id.hintetext);
        billno =(Spinner) getActivity().findViewById(R.id.bill_no);
        truck =(Spinner)getActivity(). findViewById(truckno);
        container =(Spinner)getActivity(). findViewById(R.id.container);
        port =(Spinner)getActivity(). findViewById(R.id.port);
        show =(Button)getActivity().findViewById(R.id.show);




        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new asyncDailyReport(billl,portt,truckk,contt).execute();

            }
        });


      /*  inventoryButton= (Button)getActivity().findViewById(R.id.inventoryButton);
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryFragment2 fragment2 =new InventoryFragment2();
                FragmentManager manager =getFragmentManager();
                manager.beginTransaction().replace(R.id.content_frame, fragment2, TAG_CONTENT_FRAGMENT).commit();
            }
        });*/
    }

    //   @Override
    public void triggerPressEventRecieved() {
        if (!Application.mIsInventoryRunning)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).inventoryStartOrStop(show);
                }
            });
    }

    //   @Override
    public void triggerReleaseEventRecieved() {
        if (Application.mIsInventoryRunning)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).inventoryStartOrStop(show);
                }
            });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public class asyncGetBillNo extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        ProgressDialog pd =new ProgressDialog(getActivity());

        private ArrayList<String> brandname = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   pd.setMessage("Please wait...");
            pd.show();*/
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


                for (int i = 1; i < obj.getPropertyCount(); i++) {
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

               /* pd.dismiss();*/
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

           /* pd.dismiss();*/
            super.onPostExecute(aVoid);
            ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
            brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            billno.setAdapter(brandNameAdapter);
            port.setSelection(0);
            truck.setSelection(0);
            container.setSelection(0);


        }
    }
    public class asyncGetTruckNo extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        private ArrayList<String> brandname = new ArrayList<>();
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


                for (int i = 1; i < obj.getPropertyCount(); i++) {
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

               /* pd.dismiss();*/
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

           /* pd.dismiss();*/
            super.onPostExecute(aVoid);
            ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
            brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            truck.setAdapter(brandNameAdapter);

        }
    }
    public class asyncGetContainerNo extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        private ArrayList<String> brandname = new ArrayList<>();
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


                for (int i = 1; i < obj.getPropertyCount(); i++) {
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

               /* pd.dismiss();*/
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

           /* pd.dismiss();*/
            super.onPostExecute(aVoid);
            ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
            brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            container.setAdapter(brandNameAdapter);

        }
    }
    public class asyncGetPort extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        private ArrayList<String> brandname = new ArrayList<>();
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


                for (int i = 1; i < obj.getPropertyCount(); i++) {
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

               /* pd.dismiss();*/
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {

           /* pd.dismiss();*/
            super.onPostExecute(aVoid);
            ArrayAdapter<String> brandNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Loca);
            brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            port.setAdapter(brandNameAdapter);

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


            context.registerReceiver(mBroadcastReceiver, new IntentFilter("call.InventoryFragment2.action"));
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
                    return modelSalesReport.getS_Bill_no();
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
                return 3;
            } else if (column == 1) {
                return 2;
            } else if (column == 2) {
                return 1;
            } else if (column == 3) {
                return 2;
            } else if (column == 4) {
                return 2;
            } else if (column == 5) {
                return 2;
            } else if(column ==7){
                return 3;
            }
            return 2;
        }

        @Override
        public int getViewTypeCount() {
            return 9;
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
                request.addProperty("port",portt);
                request.addProperty("truckno",truckk);
                request.addProperty("container",contt);

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
              //  hintetext.setVisibility(View.VISIBLE);
                tableFixHeaders.setVisibility(View.INVISIBLE);
            } else
            {
               // hintetext.setVisibility(View.INVISIBLE);
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
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InventoryFragment2 fragment = new InventoryFragment2();
            fragment = InventoryFragment2.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        }
    };

   /* @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //create a dialog to ask yes no question whether or not the user wants to exit

    }*/

}
