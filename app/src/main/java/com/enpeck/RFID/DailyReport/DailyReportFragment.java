package com.enpeck.RFID.DailyReport;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.common.ModelDeailyReport;
import com.enpeck.RFID.common.SampleTableAdapterDaily;
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
 * Activities that contain this fragment must implement the
 * {@link DailyReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyReportFragment extends Fragment  {
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private TableFixHeaders tableFixHeaders;
    private TextView date,hintetext;
    Calendar calendar;
    static int month_x,day_x,year_x;
    static final int DIALOG_ID = 0;
    private String getData;
    private Button search;
    private RadioGroup radioGroup;
    private RadioButton assign,nonassign;
    private BroadcastReceiver mReceiver;
    String billno111,port111,container111,truck111,serialno111,date111,iec111,eseal111,sealingdate111,sealingtime111;
    private String[] tableHeaders={"Serial No","IEC Code","Bill No","Bill Date","Vehicle No","Source Port","Destination Port","Container No","e-Seal No","Sealingtime","Sealingdate"};
    public static ArrayList<ModelDeailyReport> listReport = new ArrayList<>();


    Boolean serverissue = false;
    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String Soap_ACTION = "http://tempuri.org/DailyReportData";
    // specifies the action
    private static final String METHOD_NAME = "DailyReportData";
    private static final String Soap_ACTIONassign = "http://tempuri.org/GetAssignTagDetail1";
    // specifies the action
    private static final String METHOD_NAMEassign = "GetAssignTagDetail1";
    private static final String Soap_ACTIONnonassign = "http://tempuri.org/GetNonAssignTagDetail1";
    // specifies the action
    private static final String METHOD_NAMEnonassign = "GetNonAssignTagDetail1";


    SessionManagement session;

    String radiostr,imei,username,password;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DailyReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyReportFragment newInstance(String param1, String param2) {
        DailyReportFragment fragment = new DailyReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DailyReportFragment newInstance() {
        return new DailyReportFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        date =(TextView)getActivity().findViewById(R.id.datetoday);

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

        tableFixHeaders = (TableFixHeaders) getActivity().findViewById(R.id.table);
        hintetext=(TextView)getActivity().findViewById(R.id.hintetext);



        listReport.clear();
        new asyncDailyReport(getData).execute();




        session = new SessionManagement(getContext());
        HashMap<String, String> user = session.getUserDetails();
        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);


/*
        companyname1 = user.get(SessionManagement.KEY_company);
        username1 = user.get(SessionManagement.KEY_username);
        ieccode1 =user.get(SessionManagement.KEY_Ieccode);*/
       // password1 =user.get(SessionManagement.KEY_password);

/*
        radioGroup = (RadioGroup) getActivity().findViewById(R.id.radioGroup);

        assign =(RadioButton)getActivity().findViewById(R.id.assign);
        assign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listReport.clear();
                new asyncAssignTag(getData).execute();

            }
        });

        nonassign =(RadioButton)getActivity().findViewById(R.id.nonassign);
        nonassign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listReport.clear();
                new asyncnonAssignTag(getData).execute();


            }
        });
*/

       /* radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });*/

        assign =(RadioButton)getActivity().findViewById(R.id.assign);
        nonassign =(RadioButton)getActivity().findViewById(R.id.nonassign);
       /* search =(Button)getActivity().findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean checked =((RadioButton)view).isChecked();

                switch (view.getId()){
                    case R.id.assign :
                        if (checked)
                            new asyncAssignTag(getData).execute();
                        break;
                    case R.id.nonassign :
                        if (checked)
                            new asyncnonAssignTag(getData).execute();
                }

            }
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_daily_report, container, false);

       /* return inflater.inflate(R.layout.fragment_daily_report, container, false);*/
        RadioGroup radioGroup =(RadioGroup)myView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                listReport.clear();
                    switch (checkedId){
                        case R.id.assign :
                            listReport.clear();
                                new asyncAssignTag(getData).execute();

                            break;
                        case R.id.nonassign :
                                listReport.clear();
                                new asyncnonAssignTag(getData).execute();
                            break;

                    }
            }
        });

       return  myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* public void OnRadioButtonClicked(View view){
        boolean checked =((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.assign :
                if (checked)
                    new asyncAssignTag(getData).execute();
                break;
            case R.id.nonassign :
                if (checked)
                    new asyncnonAssignTag(getData).execute();
        }
    }*/
   /* public void OnRadioButtonClicked(View view){
        assign =(RadioButton)view.findViewById(R.id.assign);
        nonassign =(RadioButton)view.findViewById(R.id.nonassign);

        boolean checked =((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.assign :
                if (checked)
                    new asyncAssignTag(getData).execute();
                break;
            case R.id.nonassign :
                if (checked)
                    new asyncnonAssignTag(getData).execute();
        }

    }
*/
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




    public class MyAdapter extends SampleTableAdapterDaily {

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
                    return widthSmall;
                case 7:
                    return widthXLarge;
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
                case 9:
                    return modelSalesReport.getS_billDate();
                case 10:
                    return modelSalesReport.getS_billtime();
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
            } else if(column ==6){
                return 2;
            }else if (column ==8){
                return 1;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 11;
        }
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

    public class asyncDailyReport extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd =new ProgressDialog(getActivity());
        String date;
        int flag =0;

        public asyncDailyReport(String date) {
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("sarv", date);
                request.addProperty("username",username);
                request.addProperty("company",password);
                request.addProperty("radiostr",radiostr);

                //  request.addProperty("company",city);
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
                                innerResponse.getProperty("S11").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S11").toString()

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
            }
            else
            {
                hintetext.setVisibility(View.INVISIBLE);
                tableFixHeaders.setVisibility(View.VISIBLE);
            }
            pd.dismiss();
            super.onPostExecute(aVoid);
            if (serverissue == false) {
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


    public class asyncAssignTag extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd =new ProgressDialog(getActivity());
        String tag,portt,truckk,contt;
        String date;
        int flag =0;

        /*   public asyncAssignTag(String tag ) {
               this.tag = tag;
               this.portt =portt;
               this.truckk =truckk;
               this.contt =contt;
           }
   */
        public asyncAssignTag(String date) {
            this.date = date;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();

            //  tag =taggg.getText().toString();


        }

        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEassign);
                request.addProperty("sarv", date);
                request.addProperty("username",username);
                request.addProperty("company",password);
                request.addProperty("radiostr",radiostr);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.call(Soap_ACTIONassign, envelope);

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
                                innerResponse.getProperty("S11").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S11").toString()

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
            if (serverissue == false) {
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

    public class asyncnonAssignTag extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd =new ProgressDialog(getActivity());
        String tag,portt,truckk,contt;
        String date;
        int flag =0;

        /*   public asyncAssignTag(String tag ) {
               this.tag = tag;
               this.portt =portt;
               this.truckk =truckk;
               this.contt =contt;
           }
   */
        public asyncnonAssignTag(String date) {
            this.date = date;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listReport.clear();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();

            //  tag =taggg.getText().toString();


        }

        protected Void doInBackground(Void... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEnonassign);
                request.addProperty("sarv", date);
                request.addProperty("username",username);
                request.addProperty("company",password);
                request.addProperty("radiostr",radiostr);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.call(Soap_ACTIONnonassign, envelope);

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
                                innerResponse.getProperty("S11").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S11").toString()

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
            if (serverissue == false) {
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

    public void onStart() {
        super.onStart();
        initializeViewObject();
    }

    private void initializeViewObject(){
        IntentFilter intentFilter=new IntentFilter("call.DailyReport2.action");
        mReceiver =new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
             /*   Calendar c = Calendar.getInstance();
                System.out.println("Current time =&gt; "+c.getTime());


                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
               // datetime.setText(formattedDate);
// Now formattedDate have current date/time
                Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();*/

                String msg_me = intent.getStringExtra("row");
                port111 = intent.getStringExtra("Port");
                truck111 = intent.getStringExtra("truckno");
                container111 = intent.getStringExtra("Contno");
                billno111 = intent.getStringExtra("bill");
                serialno111 =intent.getStringExtra("serialno");
                date111 =intent.getStringExtra("date");
                iec111 =intent.getStringExtra("ieccode");
                eseal111 =intent.getStringExtra("eseal");
                sealingdate111 =intent.getStringExtra("sealingdate");
                sealingtime111 =intent.getStringExtra("sealingtime");



            /* billno11.setText(billno111);
                port11.setText(port111);
                truck11.setText(truck111);
                containerr11.setText(container111);*/
                //   Toast.makeText(context, "listen!", Toast.LENGTH_LONG).show();

                try {
                    Fragment fragment = new DailyReport2();
                    Bundle bundle = new Bundle();

                    //   Bundle bundle = new Bundle();
                    bundle.putString("bill", billno111);
                    bundle.putString("port", port111);
                    bundle.putString("truck", truck111);
                    bundle.putString("cont", container111);
                    bundle.putString("serialno", serialno111);
                    bundle.putString("date", date111);
                    bundle.putString("ieccode", iec111);
                    bundle.putString("eseal", eseal111);
                    bundle.putString("sealingdate",sealingdate111);
                    bundle.putString("sealingtime",sealingtime111);
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
