package com.enpeck.RFID;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.common.ModelDeailyReport;
import com.enpeck.RFID.common.SampleTableAdapterTagDetail;
import com.enpeck.RFID.common.TableFixHeaders;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StockSealReportFragment extends Fragment {
    private TableFixHeaders tableFixHeaders;
    private TextView date,hintetext;
    private String[] tableHeaders={"Count","Seal1","Seal2","Assign_Date"};
    public static ArrayList<ModelDeailyReport> listReport = new ArrayList<>();
    Boolean serverissue = false;

    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String Soap_ACTION = "http://tempuri.org/GetAllTag";
    // specifies the action
    private static final String METHOD_NAME = "GetAllTag";

    private static final String Soap_ACTIONUtilize = "http://tempuri.org/GetUtilizeTag";
    // specifies the action
    private static final String METHOD_NAMEUtilize = "GetUtilizeTag";

    private static final String Soap_ACTIONUnUtilize = "http://tempuri.org/GetUnUtilizeTag";
    // specifies the action
    private static final String METHOD_NAMEUnUtilize = "GetUnUtilizeTag";



    public StockSealReportFragment() {
        // Required empty public constructor
    }

    public static StockSealReportFragment newInstance() {
        return new StockSealReportFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tableFixHeaders = (TableFixHeaders) getActivity().findViewById(R.id.table);
        hintetext=(TextView)getActivity().findViewById(R.id.hintetext);



        listReport.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_stock_seal_report, container, false);


       /* return inflater.inflate(R.layout.fragment_daily_report, container, false);*/
        RadioGroup radioGroup =(RadioGroup)myView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                listReport.clear();
                switch (checkedId){
                    case R.id.all :
                        listReport.clear();
                        new asyncAllTag().execute();

                        break;
                    case R.id.utilize :
                        listReport.clear();
                        new asyncnonUtilizeTag().execute();
                        break;
                    case R.id.unutilize :
                        listReport.clear();
                        new asyncnonUnutilizeTag().execute();
                        break;

                }
            }
        });

        return  myView;
    }
    public class MyAdapter extends SampleTableAdapterTagDetail {

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
                case 1:
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

        private String getColumnString(ModelDeailyReport modelsale1sReport, int column) {
            switch (column) {
                case 0:
                    return modelsale1sReport.getSerialno();
                case 1:
                    return modelsale1sReport.getIECno();
                case 2:
                    return modelsale1sReport.getBillno();
                case 3:
                    return modelsale1sReport.getS_Bill_no();
               /* case 4:
                    return modelsale1sReport.getVehicleno();
                case 5 :
                    return modelsale1sReport.getSource();
                case 6:
                    return modelsale1sReport.getDestination();
                case 7:
                    return modelsale1sReport.getContainerno();
                case 8:
                    return modelsale1sReport.getE_sealno();
                case 9:
                    return modelsale1sReport.getS_billDate();
                case 10:
                    return modelsale1sReport.getS_billtime();*/
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
                return 2;
            }else if (column ==2){
                return 1;
            }
            return 2;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }
    }


    public class asyncAllTag extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd =new ProgressDialog(getActivity());
        String date;
        int flag =0;


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
            /*    request.addProperty("sarv", date);
                request.addProperty("entryby",username1);
                request.addProperty("iec",ieccode1);*/
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
                                innerResponse.getProperty("S4").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S4").toString()

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
            super.onPostExecute(aVoid);
            pd.dismiss();

            if (flag == 0){
                hintetext.setVisibility(View.VISIBLE);
                tableFixHeaders.setVisibility(View.INVISIBLE);
            }
            else
            {
                hintetext.setVisibility(View.INVISIBLE);
                tableFixHeaders.setVisibility(View.VISIBLE);
            }

            if (serverissue) {
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Ooops!!!")
                        .setIcon(R.mipmap.sale1)
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

    public class asyncnonUtilizeTag extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd =new ProgressDialog(getActivity());
        String date;
        int flag =0;


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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEUtilize);
            /*    request.addProperty("sarv", date);
                request.addProperty("entryby",username1);
                request.addProperty("iec",ieccode1);*/
                //  request.addProperty("company",city);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.call(Soap_ACTIONUtilize, envelope);

                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() > 0) {
                    flag =1;
                    for (int i = 0; i < object.getPropertyCount(); i++) {
                        SoapObject innerResponse = (SoapObject) object.getProperty(i);

                        listReport.add(new ModelDeailyReport(
                                innerResponse.getProperty("S1").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S1").toString(),
                                innerResponse.getProperty("S2").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S2").toString(),
                                innerResponse.getProperty("S3").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S3").toString(),
                                innerResponse.getProperty("S4").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S4").toString()

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

            super.onPostExecute(aVoid);
            pd.dismiss();
            if (flag == 0){
                hintetext.setVisibility(View.VISIBLE);
                tableFixHeaders.setVisibility(View.INVISIBLE);
            }
            else
            {
                hintetext.setVisibility(View.INVISIBLE);
                tableFixHeaders.setVisibility(View.VISIBLE);
            }

            if (serverissue) {
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Ooops!!!")
                        .setIcon(R.mipmap.sale1)
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

    public class asyncnonUnutilizeTag extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd =new ProgressDialog(getActivity());
        String date;
        int flag =0;


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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEUnUtilize);
            /*    request.addProperty("sarv", date);
                request.addProperty("entryby",username1);
                request.addProperty("iec",ieccode1);*/
                //  request.addProperty("company",city);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 60 * 10000);
                androidHttpTransport.call(Soap_ACTIONUnUtilize, envelope);

                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() > 0) {
                    flag =1;
                    for (int i = 0; i < object.getPropertyCount(); i++) {
                        SoapObject innerResponse = (SoapObject) object.getProperty(i);

                        listReport.add(new ModelDeailyReport(
                                innerResponse.getProperty("S1").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S1").toString(),
                                innerResponse.getProperty("S2").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S2").toString(),
                                innerResponse.getProperty("S3").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S3").toString(),
                                innerResponse.getProperty("S4").toString().contentEquals("anyType{}") ? " " : innerResponse.getProperty("S4").toString()

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

            super.onPostExecute(aVoid);
            pd.dismiss();
            if (flag == 0){
                hintetext.setVisibility(View.VISIBLE);
                tableFixHeaders.setVisibility(View.INVISIBLE);
            }
            else
            {
                hintetext.setVisibility(View.INVISIBLE);
                tableFixHeaders.setVisibility(View.VISIBLE);
            }

            if (serverissue) {
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Ooops!!!")
                        .setIcon(R.mipmap.sale1)
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
}
