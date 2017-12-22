package com.enpeck.RFID;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enpeck.RFID.common.SessionManagement;
import com.enpeck.RFID.home.MainActivity;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

import static com.enpeck.RFID.R.id.companyname;
import static com.enpeck.RFID.R.id.email;

public class Register extends AppCompatActivity {
    Button next;
    EditText Companyname,Add,Emailid,Mobno,Ieccode,Cinno,Gstno,Companyshortcode,Username,Password;
   // AutoCompleteTextView State;
    ArrayAdapter autoadapter,brancharray;
  //  Spinner State;
    TextView State,txtGetImei;
    private static final String URL = "http://www.accountsandtaxminers.com/Service.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String Soap_ACTION = "http://tempuri.org/GetState";
    private static final String METHOD_NAME= "GetState";
    private static final String Soap_ACTIONreg = "http://tempuri.org/InsertRegisterCompany";
    private static final String METHOD_NAMEreg= "InsertRegisterCompany";
    private static final String Soap_ACTIONsms = "http://tempuri.org/InsertRegisterCompany";
    private static final String METHOD_NAMEsms= "InsertRegisterCompany";
    private static final String Soap_ACTIONusername= "http://tempuri.org/CheckUserName";
    private static final String METHOD_NAMEusername= "CheckUserName";
    private static final String Soap_ACTIONcompany = "http://tempuri.org/CheckCompanyName";
    private static final String METHOD_NAMEcompany= "CheckCompanyName";
    Boolean serverissue = false;
    private ArrayAdapter<String> adapterBrand;
     String strName,otpId,otpPwd,monno,str;
    String statename,deviceIMEI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        Companyname = (EditText) findViewById(companyname);
        Add = (EditText) findViewById(R.id.Address);
        Emailid = (EditText) findViewById(email);
        Mobno = (EditText) findViewById(R.id.mob);
        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);
        Ieccode = (EditText) findViewById(R.id.iec);
        State = (TextView) findViewById(R.id.state);
        Cinno = (EditText) findViewById(R.id.cin);
        Gstno = (EditText) findViewById(R.id.gst);
        Companyshortcode = (EditText) findViewById(R.id.companys);
        txtGetImei =(TextView) findViewById(R.id.txtgetimei);


        monno =Mobno.getText().toString();
       // new AsynStateData().execute();
        GetBrandStock();

        TelephonyManager tManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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

    /*    Username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckComapnyname(Companyname.getText().toString());
            }
        });

        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUsername(Username.getText().toString());
            }
        });*/

/*
        Companyname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {



                    CheckComapnyname(Companyname.getText().toString());
            }
        });

        Username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckUsername(Username.getText().toString());
            }
        });*/

        Username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckComapnyname(Companyname.getText().toString());
            }
        });


        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckUsername(Username.getText().toString());
            }
        });

       /* Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckUsername(Username.getText().toString());
            }
        });*/
        Mobno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length()>0){
                    if (s.length() == 9){
                        Ieccode.setEnabled(true);
                        Toast.makeText(Register.this,"Please entry 10 digit Mobile no",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


       /* State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                statename =parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


       State.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showBranchDialog("Select State", "brand");
              /* statename= State.getText().toString();
               State.setText(statename);*/
           }
       });

        next =(Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (TextUtils.isEmpty(Companyname.getText()) && TextUtils.isEmpty(Add.getText()) && TextUtils.isEmpty(Emailid.getText())
                       && TextUtils.isEmpty(Ieccode.getText()) && TextUtils.isEmpty(Cinno.getText())
                       && TextUtils.isEmpty(Gstno.getText()) && TextUtils.isEmpty(Companyshortcode.getText())
                       && TextUtils.isEmpty(Username.getText())&& TextUtils.isEmpty(Password.getText())&& TextUtils.isEmpty(Mobno.getText())){
                   new MaterialStyledDialog.Builder(Register.this)
                           .setTitle("Oops!")
                           .setDescription("Please fill all the Field.All Field are Mandatory.")
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
               }else {

                   new ValidateUser().execute();

               }
              //  startActivity(new Intent(Register.this,OTP.class));
            }
        });



    }

 /*   public class AsynStateData extends AsyncTask<Void,Void,Void> {

        private ArrayList<String> itemname = new ArrayList<>();


        ProgressDialog pd = new ProgressDialog(Register.this);
        String info;
        int position=0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request =new SoapObject(NAMESPACE,METHOD_NAME);


                SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet =true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE =new HttpTransportSE(URL,60*10000);
                httpTransportSE.call(Soap_ACTION,envelope);

                SoapObject object = (SoapObject) envelope.getResponse();

                if(object.getPropertyCount() >0){
                    for (int i =0;i<object.getPropertyCount(); i++){
                        String message =object.getProperty(i).toString();
                        itemname.add(message);


                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                pd.dismiss();

                serverissue = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            brancharray =new ArrayAdapter(Register.this,android.R.layout.simple_list_item_1,itemname);
            brancharray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            State.setAdapter(brancharray);

            pd.dismiss();

            super.onPostExecute(aVoid);
            if (serverissue) {
                MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(Register.this)
                        .setTitle("Ooops!!!")
                        .setIcon(R.mipmap.ic_launcher)
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

        }
    }*/


 public void  showBranchDialog(String title,String type){
     AlertDialog.Builder builder =new AlertDialog.Builder(this);
     builder.setTitle(title);

     builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
         }
     });
     if (TextUtils.equals(type,"brand")){
         builder.setAdapter(adapterBrand, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                  strName = adapterBrand.getItem(which);

                 State.setText(strName);
             }
         });

     }
     builder.show();
 }
    protected void GetBrandStock() {


        String[] Loca = new String[]{"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat",
                "Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh"
                ,"Maharashtra" ,"Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu",
                "Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal","Andaman and Nicobar Islands",
                "Chandigarh","Dadra and Nagar Haveli","Daman and Diu","Lakshadweep","Delhi","Puducherry","Other Territory"};

        adapterBrand = new ArrayAdapter<String>(this,R.layout.simple_textview, Loca);

    }


    private void CheckComapnyname(String refered) {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEcompany);

            request.addProperty("methodName", "CheckCompanyName");

            request.addProperty("companyname", Companyname.getText().toString());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(Soap_ACTIONcompany, envelope);

            SoapPrimitive objs = (SoapPrimitive) envelope.getResponse();

            if (objs.toString().equals("true")) {
                new MaterialStyledDialog.Builder(Register.this)
                        .setTitle("Awsome!")
                        .setDescription("Company Name is already exit in database. Please enter Different Company Name")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .setPositiveText("Yes")
                        .withIconAnimation(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
//                                pdialog = new ProgressDialog(RegisterNew.this);
//                                pdialog.setMessage("Please wait while we update your registration details..");
//                                pdialog.setCancelable(false);
//                                pdialog.show();
                            //    new SendSMSTask().execute();

                            }
                        })

                        .setCancelable(false)
                        .show();
            } else {

               // GetFirstReferalNumber(edtMobile.getText().toString());
                Username.requestFocus();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void CheckUsername(String refered) {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEusername);

            request.addProperty("methodName", "CheckUserName");

            request.addProperty("username", Username.getText().toString());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(Soap_ACTIONusername, envelope);

            SoapPrimitive objs = (SoapPrimitive) envelope.getResponse();

            if (objs.toString().equals("true")) {
                new MaterialStyledDialog.Builder(Register.this)
                        .setTitle("Awsome!")
                        .setDescription("User Name is already exit in database. Please enter Different User Name")
                        .setIcon(R.mipmap.sale1)
                        .setHeaderDrawable(R.color.colorPrimary)
                        .setPositiveText("Yes")
                        .withIconAnimation(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
//                                pdialog = new ProgressDialog(RegisterNew.this);
//                                pdialog.setMessage("Please wait while we update your registration details..");
//                                pdialog.setCancelable(false);
//                                pdialog.show();
                                //    new SendSMSTask().execute();

                            }
                        })

                        .setCancelable(false)
                        .show();
            } else {

                // GetFirstReferalNumber(edtMobile.getText().toString());
                Password.requestFocus();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   /* public class AsynCheckCompanyname extends AsyncTask<Void,Void,Void>{

        ProgressDialog pd = new ProgressDialog(Register.this);

        String result= "false";
        String  companyname;
        public AsynCheckCompanyname(String companyname) {
            this.companyname=companyname;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.show();

            companyname =Companyname.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{

            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEcompany);
            request.addProperty("companyname",companyname);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(Soap_ACTIONcompany,envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            result=soapPrimitive.toString();

        } catch (Exception e) {
            e.printStackTrace();
            pd.dismiss();
        }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result.contentEquals("true"))
            {
                pd.dismiss();
                new AsynUsername(Username.getText().toString()).execute();
            }
            else {


                Toast.makeText(Register.this,"Company Name is already exit in database. Please enter Different Company Name",Toast.LENGTH_LONG).show();

            }
            }





    }

    public class AsynUsername extends AsyncTask<Void,Void,Void>{
        ProgressDialog pd = new ProgressDialog(Register.this);

        String result= "false";
        String  username;
        public AsynUsername(String username) {
            this.username=username;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.show();

            username =Username.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEusername);
            request.addProperty("username",username);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL);
            transportSE.call(Soap_ACTIONusername,envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            result=soapPrimitive.toString();

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
                pd.dismiss();
                new AsynUsername(Username.getText().toString()).execute();
            }
            else {

                Toast.makeText(Register.this,"Company Name is already exit in database. Please enter Different Company Name",Toast.LENGTH_LONG).show();

            }
        }


    }*/
    public class ValidateUser extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
        TelephonyManager tm ;
        boolean success = false;
        String  companyname,address,emailid,monno,ieccode,cinno,gstno,companyshortcode,state,imei,username,password;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Register.this);
            progressDialog.setMessage("Please Wait while we Board you in..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            companyname =Companyname.getText().toString();
            username =Username.getText().toString();
            password =Password.getText().toString();
            address = Add.getText().toString();
            emailid =Emailid.getText().toString();
            monno = Mobno.getText().toString();
            ieccode =Ieccode.getText().toString();
            cinno = Cinno.getText().toString();
            gstno =Gstno.getText().toString();
            state =State.getText().toString();
            companyshortcode =Companyshortcode.getText().toString();
            imei = txtGetImei.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAMEreg);
            request.addProperty("Comapany_name",companyname);
            request.addProperty("Username",username);
            request.addProperty("Password",password);
            request.addProperty("ConformPass",password);
            request.addProperty("Address",address);
            request.addProperty("Email_id",emailid);
            request.addProperty("Mob_no",monno);
            request.addProperty("IEC_code",ieccode);
            request.addProperty("State",state);
            request.addProperty("CIN_no",cinno);
            request.addProperty("GST_no",gstno);
            request.addProperty("Company_scode",companyshortcode);
            request.addProperty("imei",deviceIMEI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet= true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE =new HttpTransportSE(URL);
            try{
                httpTransportSE.call(Soap_ACTIONreg,envelope);
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
                Toast.makeText(Register.this,"Enter the field",Toast.LENGTH_LONG).show();
            }
            else
            {

                Intent intent =new Intent(Register.this,MainActivity.class);
                SessionManagement sessionManagement =new SessionManagement(getApplicationContext());
                Map<String, String> map = new HashMap<>();
               /* map.put("name", companyname);   //  ("name=(that use from web service in paas string name ", name=(that use from database colom name for this)
                map.put("iec", ieccode);
                map.put("username",username);*/
                map.put("password", imei);
                sessionManagement.createRegisterationSession1(imei);
               /* intent.putExtra("companyname",companyname);
                intent.putExtra("username",username);
                intent.putExtra("mobileno",monno);
                intent.putExtra("emailid",emailid);
                intent.putExtra("imei",imei);*/
                startActivity(intent);
               // new SendSmsTask().execute();
            }
        }
    }

   /* public class SendSmsTask extends AsyncTask<Void,Void,Void>{
        private ProgressDialog ringProgressDialog;
        boolean success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEsms);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            SoapObject response = null;
            try{
                androidHttpTransport.call(Soap_ACTIONsms + METHOD_NAMEsms, envelope);

                response = (SoapObject) envelope.getResponse();
                if (response != null){
                    otpId = response.getProperty("Otp_ID").toString();
                    otpPwd = response.getProperty("Otp_Password").toString();

                    HttpClient client =new DefaultHttpClient();
                    String SetServerString = "";

                    String msg = URLEncoder.encode("Your OTP for registering with RHINO is " + otpPwd, "UTF-8");
                   // String URL = "http://fast.admarksolution.com/vendorsms/pushsms.aspx?user=Guptapower&password=abc123&msisdn=91"+monno+"&sid=ERHINO&msg="+msg+"&fl=0&gwid=2";
                    String URL = "http://fast.admarksolution.com/vendorsms/pushsms.aspx?user=MUMATM&password=abc123&msisdn=91"+monno+"&sid=MUMATM&msg="+msg+"&fl=0&gwid=2";
                    // Create Request to server and get response

                    HttpGet httpget = new HttpGet(URL);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    SetServerString = client.execute(httpget, responseHandler);
                    SetServerString += "";

                }
                success = true;
            }catch (Exception e){

                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(success){
                Toast.makeText(Register.this, "Message Sent Successfully. Please wait while you receive the message..", Toast.LENGTH_LONG).show();
                Intent i = new Intent(Register.this,MainActivity.class);
                i.putExtra("otpid",otpId);
                i.putExtra("otppwd",otpPwd);
                i.putExtra("mobile",Mobno.getText().toString());
                i.putExtra("imei",str);
                startActivity(i);

            }else{
                Toast.makeText(Register.this, "Problem in sending message..Please try again..", Toast.LENGTH_LONG).show();
            }

        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        /*if (id == R.id.action_info) {
            new MaterialStyledDialog.Builder(Register.this)
                    .setTitle("Exit")
                    .setDescription("Do you really want to exit the application?")
                    .setHeaderDrawable(R.color.colorPrimary)
                    .setPositiveText("Ok")
                    .withIconAnimation(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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

                    Intent intent =new Intent(Register.this,LoginActivity.class);
                    startActivity(intent);



        }else */if (id == R.id.action_shutdown) {
            new MaterialStyledDialog.Builder(Register.this)
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

    @Override
    public void onBackPressed() {

    }
}
