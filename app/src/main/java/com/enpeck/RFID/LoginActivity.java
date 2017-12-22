package com.enpeck.RFID;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.common.Bean;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.home.MainActivity;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.vistrav.ask.Ask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Button login;
    TextView register, company;

    EditText password, username;
    private String uname, pwd, companyname;
    String IMEI_no;
    TextView txtGetImei;
    String deviceIMEI;

    String radiostr ="Custom Officer";
    RadioButton radioButton;
    RadioGroup radiogroup;
    TextView text;
    String model = "";
    String[] Loca;
    String name,cmp;
    Boolean serverissue = false;

    private ProgressDialog pd;
    private Thread SearchTread;

    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String METHOD_NAMEimemi = "Rfidimei1";
    private static final String SOAP_ACTIONimei = "http://tempuri.org/Rfidimei1";
    private static final String METHOD_NAMELogin = "Rfidlogin1";
    private static final String SOAP_ACTIONLogin = "http://tempuri.org/Rfidlogin1";
    SessionManagement session;

    String companyname1, ieccode1, username1, password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.register);
        // company =(TextView)findViewById(R.id.companyname);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login);
        Ask.on(this)
                .forPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE

                )
                .withRationales("Location permission need for map to work properly",
                        "In order to save file you will need to grant storage permission") //optional
                .go();

        if (!isNetworkAvailable()) {
            MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(this)
                    .setTitle("Alert!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setDescription("Netwrok Not Availible..\n")
                    .withIconAnimation(true)
                    .setPositiveText("Retry")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));

                        }
                    }).setNegativeText("Close").onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Log.d("MaterialStyledDialogs", "Do something!");
                            dialog.dismiss();
                            finishAffinity();
                        }
                    })
                    .show();
        }

        /*if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/

        txtGetImei = (TextView) findViewById(R.id.txtgetimei);

        TelephonyManager tManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
        txtGetImei.setText(deviceIMEI);

        new AsynkImei(deviceIMEI).execute();



       /* try{
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            str = getDeviceID(tm);
            txtGetImei.setText(str);
        }catch (Exception e){
            e.printStackTrace();
        }

*/

      ///  new AsynkImei(str).execute();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        username = (EditText) findViewById(R.id.username);
       /* Intent intent = getIntent();
        //  companyname =intent.getStringExtra("companyname");
        uname = intent.getStringExtra("username");
        username.setText(uname);*/
        //   company.setText(companyname);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uname = username.getText().toString();
                pwd = password.getText().toString();





                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    new AsynkLogin(uname).execute();


                } else {

                    new MaterialStyledDialog.Builder(LoginActivity.this)
                            .setTitle("Oops!")
                            .setDescription("You left a Mandatory Field Blank.Please fill it to continue with the Login process.")
                            .setIcon(R.mipmap.sale1)
                            .setHeaderDrawable(R.color.colorPrimary)
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


                // startActivity(new Intent(MainActivity.this,HomePage.class));
            }
        });

     /*   register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Register.class));
            }
        });*/


    /*    session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        companyname1 = user.get(SessionManagement.KEY_company);
        username1 = user.get(SessionManagement.KEY_username);
        ieccode1 = user.get(SessionManagement.KEY_Ieccode);
        password1 = user.get(SessionManagement.KEY_password);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_shutdown) {
            new MaterialStyledDialog.Builder(LoginActivity.this)
                    .setTitle("Exit")
                    .setDescription("Do you really want to exit the application?")
                    .setIcon(R.mipmap.sale1)
                    .setHeaderDrawable(R.color.colorPrimary)
                    .setPositiveText("Yes")
                    .withIconAnimation(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            BluetoothAdapter.getDefaultAdapter().disable();
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .setNegativeText("No")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();

        }


        return super.onOptionsItemSelected(item);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    class AsynkImei extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd = new ProgressDialog(LoginActivity.this);

        String result = "false";
       String str;

        public AsynkImei(String str) {
            this.str = str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEimemi);
                request.addProperty("IMEI",deviceIMEI);

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
                androidHttpTransport.call(SOAP_ACTIONimei, envelope);

                SoapObject response2 = (SoapObject) envelope.getResponse();
                Bean[] personobj = new Bean[response2.getPropertyCount()];
                Bean beanobj = new Bean();

                for (int j = 0; j < personobj.length; j++) {

                    SoapObject pii = (SoapObject) response2.getProperty(j);
                    beanobj.serial = pii.getProperty(0).toString();
                    beanobj.iec = pii.getProperty(1).toString();


                    personobj[j] = beanobj;

                }

                uname = beanobj.serial;
                deviceIMEI = beanobj.iec;

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

            super.onPostExecute(aVoid);
            if (uname == null && deviceIMEI == null ) {
                pd.dismiss();
                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setTitle("Oops!")
                        .setDescription("Your Imei no is not in database....")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
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
            } else {

                pd.dismiss();
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
                map.put("imei", deviceIMEI);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("password", uname);
                map.put("username",deviceIMEI);
                map.put("radiostr", radiostr);
                sessionManagement.createRegisterationSession(deviceIMEI,uname,deviceIMEI,radiostr);
                //   intent.putExtra("radiostr",radiostr);
                startActivity(intent);

            }

        /*    if (result.contentEquals("true")) {
                pd.dismiss();
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
                map.put("imei", deviceIMEI);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("password", uname);
                map.put("username",deviceIMEI);
                map.put("radiostr", radiostr);
                sessionManagement.createRegisterationSession(deviceIMEI,uname,deviceIMEI,radiostr);
                //   intent.putExtra("radiostr",radiostr);
                startActivity(intent);

            } else {
                pd.dismiss();
                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setTitle("Oops!")
                        .setDescription("Your Imei no is not in database....")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
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
                Toast.makeText(LoginActivity.this, "Your Imei no is not in database....", Toast.LENGTH_SHORT).show();
            }*/
        }
    }


    class AsynkLogin extends AsyncTask<Void, Void, Void> {
        String model = "";
        String[] Loca;
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);

        String result = "false";
        String uname , pwd = "";
        String radiostr ="Custom Officer";


        public AsynkLogin(String uname) {
            this.uname = uname;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.show();


           /* Loca = uname.split("@");
            model =Loca[0];
            cmp =Loca[1];*/

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMELogin);
                request.addProperty("username", uname);
                request.addProperty("imei", deviceIMEI);


                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE transportSE = new HttpTransportSE(URL,60*10000);
                transportSE.call(SOAP_ACTIONLogin, envelope);
                SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

                result = soapPrimitive.toString();

            } catch (Exception e) {
                e.printStackTrace();
                pd.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            if (result.contentEquals("true")) {
                pd.dismiss();
                /*Loca =uname.split("@");
                name =Loca[0];
                cmp =Loca[1];
*/
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
                map.put("imei", deviceIMEI);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("password", uname);
                map.put("username",deviceIMEI);
                map.put("radiostr", radiostr);
                sessionManagement.createRegisterationSession(deviceIMEI,uname,deviceIMEI,radiostr);
             //   intent.putExtra("radiostr",radiostr);
                startActivity(intent);

                username.setText("");
                password.setText("");


            } else {
                pd.dismiss();

                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setTitle("Oops!")
                        .setDescription(" Enter correct username and password....Please fill it to continue with the Login process.")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
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
               /* Intent intent =new Intent(LoginActivity.this,Register.class);
                *//*SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
               *//**//* map.put("name", companyname);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("iec", ieccode);
                map.put("username",username);*//**//*
                map.put("radiostr", radiostr);
                sessionManagement.createRegisterationSession1(radiostr);*//*
                startActivity(intent);*/
                Toast.makeText(LoginActivity.this, "Please Enter correct username and password...", Toast.LENGTH_SHORT).show();
                username.setText("");
                password.setText("");
            }
        }
    }

    @Override
    public void onBackPressed() {

    }




   /* String getDeviceID(TelephonyManager phonyManager) {

        String id = phonyManager.getDeviceId();
        try {
            if (id == null) {
                id = "not available";
            }

            int phoneType = phonyManager.getPhoneType();
            switch (phoneType) {
                case TelephonyManager.PHONE_TYPE_NONE:
                    return id;

                case TelephonyManager.PHONE_TYPE_GSM:
                    return id;

                case TelephonyManager.PHONE_TYPE_CDMA:
                    return id;

                default:
                    return id;
            }
        }catch (Exception e){}
        return id;
    }*/



   /* public void onRadioButtonClicked(View v)
    {
        //require to import the RadioButton class
        RadioButton rb1 = (RadioButton) findViewById(R.id.exporter);
        RadioButton rb2 = (RadioButton) findViewById(R.id.custom);
        RadioButton rb3 = (RadioButton) findViewById(R.id.vendor);
        text =(TextView)findViewById(R.id.string);

        //is the current radio button now checked?
        boolean  checked = ((RadioButton) v).isChecked();

        //now check which radio button is selected
        //android switch statement

            switch (v.getId()) {

                case R.id.exporter:
                    if (checked)
                        radiostr = rb1.getText().toString();

                    text.setText(radiostr);
                    break;

                case R.id.custom:
                    if (checked)
                        radiostr = rb2.getText().toString();

                    text.setText(radiostr);
                    break;

                case R.id.vendor:
                    if (checked)
                        radiostr = rb3.getText().toString();

                    text.setText(radiostr);
                    break;


            }
    }
*/

}
