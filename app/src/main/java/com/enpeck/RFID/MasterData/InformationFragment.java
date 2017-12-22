package com.enpeck.RFID.MasterData;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.inventory.InventoryListItem;
import com.enpeck.RFID.inventory.ModifiedInventoryAdapter;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.zebra.rfid.api3.RFIDResults;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment implements Spinner.OnItemSelectedListener, ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler{

        EditText Latitude,Longitude;
    static final int REQUEST_LOCATION =1;
    LocationManager locationManager;

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

    List<InventoryListItem> items;
    TextView uniqueno,billno,contno,truckno;
    String uniqueno1,billno1,contno1,truckno1;
    ListView tag;
    String tagg /*="PWR-BGA12V50W0WW"*/;
    Button save,assign;
    ImageButton fill,lock,track,con;
    TextView datetime,time;
    Bitmap originBitmap = null,photo=null,photo1 = null;

    ImageView filll,lockk,truckk,contu;
    String formattedDate;
    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String Soap_ACTION = "http://tempuri.org/InsertEntryMaster";
    private static final String METHOD_NAME= "InsertEntryMaster";

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


         /*   InventoryListItem inventoryListItem =items.get(position);
            items.remove(inventoryListItem);*/
            if (!Application.mIsInventoryRunning) {
                toggle(view, position);
                Application.accessControlTag = adapter.getItem(position).getTagID();
                Application.locateTag = adapter.getItem(position).getTagID();
                tagg =adapter.getItem(0).getTagID();
                //taggg.setText(tagg);
                adapter.clear();

            }
        }
    };


    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance() {
        return new InformationFragment();
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
        View view =inflater.inflate(R.layout.fragment_information,container,false);
        billno =(TextView)view. findViewById(R.id.Bill);
        contno =(TextView)view. findViewById(R.id.conter);
        truckno =(TextView)view. findViewById(R.id.truckk);


        billno1=getArguments().getString("billno");
        contno1 =getArguments().getString("contno");
        truckno1 =getArguments().getString("truckno");

        billno.setText(billno1);
        contno.setText(contno1);
        truckno.setText(truckno1);


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  uniqueno =(TextView)findViewById(R.id.Unqie);
        datetime =(TextView) getActivity().findViewById(R.id.esealdate) ;
        time =(TextView) getActivity().findViewById(R.id.esealtime) ;
        tag =(ListView)getActivity().findViewById(R.id.inventoryList);
        save =(Button)getActivity().findViewById(R.id.save);
        assign =(Button)getActivity().findViewById(R.id.inventoryButton);
        fill =(ImageButton)getActivity().findViewById(R.id.filll);
        lock =(ImageButton)getActivity().findViewById(R.id.lockk);
        track = (ImageButton)getActivity().findViewById(R.id.tru);
        con =(ImageButton)getActivity().findViewById(R.id.cont);
        filll =(ImageView)getActivity().findViewById(R.id.fill);
        lockk =(ImageView)getActivity().findViewById(R.id.lock);
        truckk =(ImageView)getActivity().findViewById(R.id.track);
        contu =(ImageView)getActivity().findViewById(R.id.container);

        Latitude =(EditText)getActivity().findViewById(R.id.latitude);
        Longitude =(EditText)getActivity().findViewById(R.id.longitude);


        SessionManagement session = new SessionManagement(getContext().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();


        imei =user.get(SessionManagement.KEY_company);
        password = user.get(SessionManagement.KEY_Ieccode);
        username = user.get(SessionManagement.KEY_username);
        radiostr =user.get(SessionManagement.KEY_password);



        locationManager =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();



        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tag.setVisibility(View.VISIBLE);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tagg!=null)
                {
                    getLocation();
                    new AsynMasterData().execute();


                }
                else {

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

            }
        });

       /* Intent intent =getIntent();
        //    uniqueno1 =intent.getStringExtra("Uniqueno");
        billno1 =intent.getStringExtra("Billno");
        contno1 =intent.getStringExtra("Contno");
        truckno1 =intent.getStringExtra("Truckno");
        //  uniqueno.setText(uniqueno1);
        billno.setText(billno1);
        contno.setText(contno1);
        truckno.setText(truckno1);*/

        fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Upload Image?")
                        //  .setDescription("Choose a method by which you want to upload an image")
                        .setPositiveText("Take picture from mobile")
                        .setIcon(R.mipmap.eseal)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .withIconAnimation(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 1888);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeText("Choose picture from gallery")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ChooseImage();
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });


        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Upload Image?")
                        //  .setDescription("Choose a method by which you want to upload an image")
                        .setPositiveText("Take picture from mobile")
                        .withIconAnimation(true)
                        .setIcon(R.mipmap.eseal)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 1889);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeText("Choose picture from gallery")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ChooseImage();
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Upload Image?")
                        //  .setDescription("Choose a method by which you want to upload an image")
                        .setPositiveText("Take picture from mobile")
                        .setIcon(R.mipmap.eseal)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .withIconAnimation(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 1890);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeText("Choose picture from gallery")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ChooseImage();
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("Upload Image?")
                        //  .setDescription("Choose a method by which you want to upload an image")
                        .setPositiveText("Take picture from mobile")
                        .withIconAnimation(true)
                        .setIcon(R.mipmap.eseal)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 1891);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeText("Choose picture from gallery")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ChooseImage();
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });




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
                inventoryButton.setText(getString(R.string.start_title));
        }

        //Set the font size in constants
        Constants.INVENTORY_LIST_FONT_SIZE = (int) getResources().getDimension(R.dimen.inventory_list_font_size);

        batchModeInventoryList = (TextView) getActivity().findViewById(R.id.batchModeInventoryList);

        listView = (ListView) getActivity().findViewById(R.id.inventoryList);
        //  trxtview = (TextView) getActivity().findViewById(R.id.inventoryList1);
        adapter = new ModifiedInventoryAdapter(getActivity(), R.layout.inventory_list_item);

        //enables filtering for the contents of the given ListView
        tag.setTextFilterEnabled(true);

        //   trxtview.setText((CharSequence) listView);

        if (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning) {
            tag.setEmptyView(batchModeInventoryList);
            batchModeInventoryList.setVisibility(View.VISIBLE);
        } else {
            tag.setAdapter(adapter);

            batchModeInventoryList.setVisibility(View.GONE);
        }
        tag.setOnItemClickListener(onItemClickListener);
    }

    public void ChooseImage() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(
                Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        } else {
            Toast.makeText(getActivity(),
                    "No activity found to perform this task",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File getFile() {
        File folder = new File("sdcard/camera_app");

        if (!folder.exists()) {
            folder.mkdir();
        }

        File imageFile = new File(folder, "cam_image.jpg");
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1888 && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            filll.setImageBitmap(photo);
            //truckk.setImageBitmap(photo);

        }
        if (requestCode == 1889 && resultCode == RESULT_OK) {
            photo1 = (Bitmap) data.getExtras().get("data");
            //filll.setImageBitmap(photo);
            truckk.setImageBitmap(photo1);

        }
        if (requestCode == 1890 && resultCode == RESULT_OK) {
            photo1 = (Bitmap) data.getExtras().get("data");
            //filll.setImageBitmap(photo);
            lockk.setImageBitmap(photo1);

        }
        if (requestCode == 1891 && resultCode == RESULT_OK) {
            photo1 = (Bitmap) data.getExtras().get("data");
            //filll.setImageBitmap(photo);
            contu.setImageBitmap(photo1);

        }

        else if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            InputStream imageStream;

            try {
                imageStream = getActivity().getContentResolver().openInputStream(
                        selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           /* if (originBitmap != null) {
                getActivity().filll.setImageBitmap(originBitmap);
            }*/
        } else {
            Toast.makeText(getActivity(),"There's was an error",Toast.LENGTH_SHORT).show();

        }
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
        if (tag.getAdapter() != null) {
            ((ModifiedInventoryAdapter) tag.getAdapter()).clear();
            ((ModifiedInventoryAdapter) tag.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        if (tag.getAdapter() == null) {
            tag.setAdapter(adapter);


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
                    if (tag != null && batchModeInventoryList != null) {
                        tag.setEmptyView(batchModeInventoryList);
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

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        datetime.setText(formattedDate);
// Now formattedDate have current date/time
        Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();
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

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        datetime.setText(formattedDate);
// Now formattedDate have current date/time
        Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();
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
                tag.setAdapter(adapter);
                batchModeInventoryList.setText("");
                batchModeInventoryList.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void batchModeEventReceived() {
        if (inventoryButton != null) {
            inventoryButton.setText(getString(R.string.start_title));
        }
        if (invSpinner != null) {
            invSpinner.setSelection(0);
            invSpinner.setEnabled(false);
        }
        if (tag != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            tag.setEmptyView(batchModeInventoryList);
            batchModeInventoryList.setText(R.string.batch_mode_inventory_title);
            batchModeInventoryList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // initializeViewObject();

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


    public class AsynMasterData extends AsyncTask<Void,Void,Void> {
        ProgressDialog progressDialog;
        TelephonyManager tm ;
        boolean success = false;
        String  billno1,date1,time1,source1,dest1,container1,truck1,longitude1,latitude1,eseal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait while we Board you in..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            billno1 =billno.getText().toString();
            date1 =datetime.getText().toString();
            time1 =datetime.getText().toString();
            container1 =contno.getText().toString();
            truck1 = truckno.getText().toString();
            latitude1=Latitude.getText().toString();
            longitude1 = Longitude.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
            request.addProperty("billno",billno1);
            request.addProperty("date",date1);
            request.addProperty("time",time1);
            request.addProperty("container",container1);
            request.addProperty("truck",truck1);
            request.addProperty("latitude",latitude1);
            request.addProperty("longitude",longitude1);
            request.addProperty("eseal",tagg);
            request.addProperty("entryby",username);
            //request.addProperty("iec",ieccode);

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
                Toast.makeText(getActivity(),"Enter the field",Toast.LENGTH_LONG).show();
            }
            else
            {

                new MaterialStyledDialog.Builder(getActivity())
                        .setDescription("Your Data is Save......")
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
               /* Intent intent =new Intent(Register.this,MainActivity.class);
                intent.putExtra("companyname",companyname);
                intent.putExtra("username",username);
                intent.putExtra("mobileno",monno);
                intent.putExtra("emailid",emailid);
                intent.putExtra("imei",imei);
                startActivity(intent);*/
                // new SendSmsTask().execute();
            }
        }
    }


}
