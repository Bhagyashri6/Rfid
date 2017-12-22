package com.enpeck.RFID.inventory;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.common.ResponseHandlerInterfaces;
import com.enpeck.RFID.home.MainActivity;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.zebra.rfid.api3.RFIDResults;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.enpeck.RFID.application.Application.accessControlTag;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment2 extends Fragment implements Spinner.OnItemSelectedListener, ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler {

    TextView totalNoOfTags;
    TextView uniqueTags,tag,sealno;
    private ListView listView;
    private ModifiedInventoryAdapter adapter;
    private ArrayAdapter<CharSequence> invAdapter;
    String Count;
    List<InventoryListItem> items;
    //ID to maintain the memory bank selected
    private String memoryBankID = "none";
    private Button inventoryButton,save;

    private long prevTime = 0;

    private TextView timeText,trxtview;
    private Spinner invSpinner;
    private TextView batchModeInventoryList;
    TextView billno,port,containerr,truck,date,datetime,time;
    String billno1,port1,container1,truck1,serialno1,date1,systemdate,systemtime;
    String tagg,Sealno;
    String formattedDate,formattedtime;

    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String Soap_ACTION = "http://tempuri.org/UpdateTag";
    // specifies the action
    private static final String METHOD_NAME = "UpdateTag";
    private static final String Soap_ACTIONcheck = "http://tempuri.org/CheckEseal";
    // specifies the action
    private static final String METHOD_NAMEcheck = "CheckEseal";

    private static final String Soap_ACTIONSeal = "http://tempuri.org/GetSealNO";
    // specifies the action
    private static final String METHOD_NAMESeal = "GetSealNO";

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
                tagg =adapter.getItem(0).getTagID();
                tag.setText(tagg);


            }
        }
    };

    private BroadcastReceiver mReceiver;

    public InventoryFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InventoryFragment.
     */
    public static InventoryFragment2 newInstance() {
        return new InventoryFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inventory, menu);
        // Associate searchable configuration with the SearchView
       /* SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String s) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.getFilter().filter(s);
                        }
                    });
                    return false;
                }*/
        //});
    }

    public ModifiedInventoryAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_inventory2,container,false);
         // getContext().registerReceiver(mBroadcastReceiver, new IntentFilter("call.InventoryFragment.action"));

            Bundle bundle = getArguments();


            billno = (TextView) view.findViewById(R.id.billnoo);
            port = (TextView) view.findViewById(R.id.destt);
            truck = (TextView) view.findViewById(R.id.trucknoo);
            containerr = (TextView) view.findViewById(R.id.containerr);
            date =(TextView)view.findViewById(R.id.date);
            sealno = (TextView) view.findViewById(R.id.sealno);



         billno1 =getArguments().getString("bill");
        date1 =getArguments().getString("date");
         port1 =getArguments().getString("port");
         truck1 =getArguments().getString("truck");
        serialno1 =getArguments().getString("serialno");
         container1 =getArguments().getString("cont");

       //  Sealno =sealno.getText().toString();
    /*    systemdate =getArguments().getString("sysdate");
        systemtime =getArguments().getString("systime");*/


         billno.setText(billno1);
        port.setText(port1);
        truck.setText(truck1);
        containerr.setText(container1);
        date.setText(date1);
       /* datetime.setText(systemdate);
        time.setText(systemtime);*/






        return view;

    /*  *//*  Bundle bundle =this.getArguments();
         billno1 =bundle.getString("Billno");*//*
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory2, container, false);*/
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

        tag =(TextView)getActivity().findViewById(R.id.abc);
        datetime =(TextView) getActivity().findViewById(R.id.esealdate) ;
        time =(TextView) getActivity().findViewById(R.id.esealtime) ;
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

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());


        SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df1.format(c.getTime());
        datetime.setText(formattedDate);

        //   trxtview.setText((CharSequence) listView);

        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            listView.setEmptyView(batchModeInventoryList);
            batchModeInventoryList.setVisibility(View.VISIBLE);
        } else {
            listView.setAdapter(adapter);

            batchModeInventoryList.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(onItemClickListener);

        adapter.notifyDataSetChanged();
        //tag.setText(accessControlTag);
       /* if ( (!listView.equals("")) && (listView!=null)) {

        }else {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time =&gt; " + c.getTime());

            Time hours = new Time(System.currentTimeMillis());
            time.setText(String.valueOf(hours));

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            formattedDate = df.format(c.getTime());
            datetime.setText(formattedDate);
        }*/

// Now formattedDate have current date/time
      //  Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();

        Sealno =sealno.getText().toString();

        new GetSealNO().execute();

        save =(Button)getActivity().findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getCount()==0)
                {
                    new MaterialStyledDialog.Builder(getActivity())
                            .setDescription("u cant not save data without Assign Tag")
                            .setIcon(R.mipmap.eseal)
                            .setHeaderDrawable(R.color.colorPrimary)
                            .setPositiveText("Ok")
                            .withIconAnimation(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })

                            .setCancelable(true)
                            .show();

                }
                else {

                   Checktag(tagg);
                   // new UpdateTag().execute();

                }
            }
        });
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
    public void onStart() {
        super.onStart();
    // initializeViewObject();

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
        listView.performItemClick(listView, 0, listView.getItemIdAtPosition(0));
        tagg =adapter.getItem(0).getTagID();

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
     /*   Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

      *//*  Time hours = new Time(System.currentTimeMillis());
        time.setText(String.valueOf(hours));

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c.getTime());
        datetime.setText(formattedDate);*//*

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
       formattedDate = df.format(c.getTime());
        datetime.setText(formattedDate);
// Now formattedDate have current date/time
        Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();*/

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

        //Time hours = new Time(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        formattedtime = df.format(c.getTime());
        time.setText(formattedtime);
        //time.setText(String.valueOf(hours));


        SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df1.format(c.getTime());
        datetime.setText(formattedDate);
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

       /* Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
         formattedDate = df.format(c.getTime());
        datetime.setText(formattedDate);
// Now formattedDate have current date/time
        Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();*/

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());
/*

        Time hours = new Time(System.currentTimeMillis());
        time.setText(String.valueOf(hours));
*/
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        formattedtime = df.format(c.getTime());
        time.setText(formattedtime);

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd ");
         formattedDate = df1.format(c.getTime());
        datetime.setText(formattedDate);
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

 /*   public class Checkeseal extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
        boolean success = false;
        String result= "false";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait while we Board you in..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEcheck);
            request.addProperty("tag",tagg);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet= true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE =new HttpTransportSE(URL);

            try{
                httpTransportSE.call(Soap_ACTIONcheck,envelope);
                SoapPrimitive soapPrimitive =(SoapPrimitive)envelope.getResponse();
                if(soapPrimitive.toString().equals("true")){
                   Toast.makeText(getContext(),"Tag is allready Assign",Toast.LENGTH_LONG).show();
                    result =soapPrimitive.toString();
                }
                else {
                    result =soapPrimitive.toString();
                    new UpdateTag().execute();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result.contentEquals("true"))
            {
                progressDialog.dismiss();
            }
            else {
                progressDialog.dismiss();
                *//*startActivity(new Intent(LoginActivity.this,Register.class));
                Toast.makeText(LoginActivity.this,"You are not register...",Toast.LENGTH_SHORT).show();*//*
            }
        }
    }*/


    private void Checktag(String tagg){
        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEcheck);

            request.addProperty("methodName", "CheckEseal");

            request.addProperty("mobile", tagg);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(Soap_ACTIONcheck, envelope);

            SoapPrimitive objs = (SoapPrimitive) envelope.getResponse();

            if (objs.toString().equals("true")){
                new UpdateTag().execute();


            }else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class UpdateTag extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
        boolean success = false;


        String abc =tag.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait while we Board you in..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Sealno =sealno.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
            request.addProperty("tag",tagg);
            request.addProperty("serialno",serialno1);
            request.addProperty("time",formattedtime);
            request.addProperty("date",formattedDate);
            request.addProperty("SealNo",Sealno);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet= true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE =new HttpTransportSE(URL);
            try{
                httpTransportSE.call(Soap_ACTION,envelope);
                SoapPrimitive soapPrimitive =(SoapPrimitive)envelope.getResponse();
                if (soapPrimitive.toString().equals("success")){
                    success =true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (success){
                Toast.makeText(getContext(),"Data is not save in database",Toast.LENGTH_LONG).show();
            }
            else
            {
                listView.setAdapter(null);
                Toast.makeText(getContext(),"Data is save in database",Toast.LENGTH_LONG).show();

            }
        }
    }


    class GetSealNO extends AsyncTask<String,Void,String>{

        String result ="";


        @Override
        protected String doInBackground(String... strings) {
            SoapObject soapObject =new SoapObject(NAMESPACE,METHOD_NAMESeal);
           /* PropertyInfo propertyInfo =new PropertyInfo();
            propertyInfo.type =PropertyInfo.STRING_CLASS;
            propertyInfo.name ="date";
            propertyInfo.setValue(date1);*/


         //   soapObject.addProperty(propertyInfo);
            SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet =true;

            envelope.setOutputSoapObject(soapObject);


            HttpTransportSE httpTransportSE =new HttpTransportSE(URL);
            httpTransportSE.debug =true;
            try
            {
                httpTransportSE.call(Soap_ACTIONSeal,envelope);
                SoapPrimitive result =(SoapPrimitive) envelope.getResponse();
                if (result!=null){
                    Count =result.toString();
                    Log.e("found",Count);
                }else {
                    Log.e("obj",result.toString());
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            return Count;
        }

        @Override
        protected void onPostExecute(String result) {
            sealno.setText(result);
            Count =sealno.getText().toString();

           // pieChart.setCenterText(count.getText().toString());


           // Count1 =Integer.parseInt(Count);
        }
    }
}
