package com.enpeck.RFID.vertifyTag;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.common.ModelDeailyReport;
import com.enpeck.RFID.common.SampleTableAdapterIncoming;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.common.TableFixHeaders;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingContainer extends Fragment {
    private String getData;
    TextView date;
    Calendar calendar;
    static int month_x,day_x,year_x;
    static final int DIALOG_ID = 0;
    Spinner billno,ieccode;
    private TableFixHeaders tableFixHeaders;
    private TextView hintetext;

    private String[] tableHeaders={"IEC Code","Shipping Bill No","Shipping Bill Date","e-Seal No","Date of Sealing","Time of Sealing","Destination Port","Container No","Truck No","Latitude","Longitude","Verified"};
    public static ArrayList<ModelDeailyReport> listReport = new ArrayList<>();
    Boolean serverissue = false;
    Button search;
    String iec,bill;
    SessionManagement session;
    String radiostr,imei,username,password;


    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String Soap_ACTION = "http://tempuri.org/GetIncomingInformation";
    // specifies the action
    private static final String METHOD_NAME = "GetIncomingInformation";
    private static final String Soap_ACTIONAll = "http://tempuri.org/GetIncomingInformationAll";
    // specifies the action
    private static final String METHOD_NAMEAll = "GetIncomingInformationAll";
    private static final String Soap_ACTIONBill = "http://tempuri.org/GetBillNo";
    // specifies the action
    private static final String METHOD_NAMEBill = "GetBillNo";
    private static final String Soap_ACTIONIec = "http://tempuri.org/GetIECcode";
    // specifies the action
    private static final String METHOD_NAMEIec = "GetIECcode";

    public IncomingContainer() {
        // Required empty public constructor
    }

    public static IncomingContainer newInstance() {
        return new IncomingContainer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_incoming_container, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        date =(TextView)getActivity().findViewById(R.id.datetoday);
        billno =(Spinner)getActivity(). findViewById(R.id.billno);
        ieccode =(Spinner)getActivity(). findViewById(R.id.ieccode);
        search =(Button)getActivity().findViewById(R.id.search) ;

        tableFixHeaders = (TableFixHeaders)getActivity(). findViewById(R.id.table);
        hintetext=(TextView)getActivity().findViewById(R.id.hintetext);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar currentCal = Calendar.getInstance();
        getData = dateFormat.format(currentCal.getTime());
        date.setText(getData);
        calendar = Calendar.getInstance();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        getData = date.getText().toString();

        session = new SessionManagement(getContext().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);


        new asyncGetBillNo().execute();
        new asyncGetIeccode().execute();
        new asyncDailyReport().execute();


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listReport.clear();
                new Asynincomingdata(bill,iec,getData).execute();
            }
        });
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date.setText("" + (month + 1) + "/" + dayOfMonth + "/" + year);
            getData = date.getText().toString();

        }
    };


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new DatePickerDialog(getActivity(), dpClickListener, year_x, month_x, day_x);
        }
        return null;
    }

    DatePickerDialog.OnDateSetListener dpClickListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            date.setText(month_x + "/" + day_x + "/" + year_x);
        }
    };
    public class MyAdapter extends SampleTableAdapterIncoming {

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

                    return widthMedium;
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
                    return modelSalesReport.getIECno();
                case 1:
                    return modelSalesReport.getBillno();
                case 2:
                    return modelSalesReport.getS_Bill_no();
                case 3:
                    return modelSalesReport.getE_sealno();
                case 4:
                    return modelSalesReport.getS_billDate();
                case 5 :
                    return modelSalesReport.getS_billtime();
                case 6:
                    return modelSalesReport.getDestination();
                case 7:
                    return modelSalesReport.getContainerno();
                case 8:
                    return modelSalesReport.getVehicleno();
                case 9:
                    return modelSalesReport.getSource();
                case 10:
                    return modelSalesReport.getSerialno();
                case 11:
                    return modelSalesReport.getTag();
                case 12 :
                    return modelSalesReport.getCount();

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
            }else if (column == 0){
                return 1;
            }else if (column ==8){
                return 3;
            }else if (column ==9){
                return 3;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }
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
                ieccode.setSelection(0);

            }catch (Exception e){}



        }
    }

    public class asyncGetIeccode extends AsyncTask<Void, Void, Void> {
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEIec);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

                httpTransportSE.call(Soap_ACTIONIec, envelope);
                SoapObject obj = (SoapObject) envelope.getResponse();


                for (int i = 0; i < obj.getPropertyCount(); i++) {
                    model = model + obj.getProperty(i).toString() + ',';
                }
                Loca = model.split(",");
                Loca[0] = "-- Select IEC Code --";
                //  Loca[1] = "All";


                Loca = model.split(",");
                Loca[0] = "-- Select IEC Code --";
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
                ieccode.setAdapter(brandNameAdapter);
                billno.setSelection(0);

            }catch (Exception e){}



        }
    }


    public class Asynincomingdata extends AsyncTask<Void,Void,Void>{
        ProgressDialog pd =new ProgressDialog(getActivity());
        String billl,iec,Date,contt;

        int flag =0;

        public Asynincomingdata(String billl,String iec,String Date ) {
            this.billl = billl;
            this.iec =iec;
            this.Date =Date;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.show();
            pd.setCancelable(false);
            billl =billno.getSelectedItem().toString();
            iec =ieccode.getSelectedItem().toString();
            Date =date.getText().toString();
            //  contt =container.getSelectedItem().toString();

            if (billl == "-- Select Bill No --")
            {
                billl = "All";
            }
            if (iec == "-- Select IEC Code --")
            {
                iec = "All";

            }



        }

        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("billno", billl);
                request.addProperty("iec",iec);
                request.addProperty("date",Date);
                request.addProperty("port",imei);


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
                                innerResponse.getProperty("S9").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S9").toString(),
                                innerResponse.getProperty("S10").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S10").toString(),
                                innerResponse.getProperty("S11").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S11").toString(),
                                innerResponse.getProperty("S12").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S12").toString(),
                                innerResponse.getProperty("S13").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S13").toString()

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

            tableFixHeaders.setAdapter(new MyAdapter(getActivity(),
                    tableHeaders, listReport));


        }
    }

    public class asyncDailyReport extends AsyncTask<Void,Void,Void>{
        ProgressDialog pd =new ProgressDialog(getActivity());
        String billl,iec,Date,contt;

        int flag =0;

        /* public asyncDailyReport(String billl,String iec ) {
             this.billl = billl;
             this.iec =iec;
          //   this.Date =Date;

         }
 */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.show();
            pd.setCancelable(false);
            if (billl!=null && iec !=null) {
                billl = billno.getSelectedItem().toString();
                iec = ieccode.getSelectedItem().toString();
            }else {
                billl = "All";
                iec ="All";


            }

            Date =date.getText().toString();
            //  contt =container.getSelectedItem().toString();





        }

        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEAll);

                request.addProperty("billno", billl);
                request.addProperty("iec",iec);
                request.addProperty("port",imei);
                //  request.addProperty("date",Date);

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
                                innerResponse.getProperty("S9").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S9").toString(),
                                innerResponse.getProperty("S10").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S10").toString(),
                                innerResponse.getProperty("S11").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S11").toString(),
                                innerResponse.getProperty("S12").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S12").toString(),
                                innerResponse.getProperty("S13").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S13").toString()

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

            tableFixHeaders.setAdapter(new MyAdapter(getActivity(),
                    tableHeaders, listReport));


        }
    }

}
