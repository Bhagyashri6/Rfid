package com.enpeck.RFID.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by ABC on 01/16/2018.
 */

public class OpenCellID {
    String mcc;  //Mobile Country Code
    String mnc;  //mobile network code
    String cellid; //Cell ID
    String lac;  //Location Area Code

    Boolean error;
    String strURLSent;
    String GetOpenCellID_fullresult;

    String latitude;
    String longitude;

    public Boolean isError() {
        return error;
    }

    public void setMcc(String value) {
        mcc = value;
    }

    public void setMnc(String value) {
        mnc = value;
    }

    public void setCallID(int value) {
        cellid = String.valueOf(value);
    }

    public void setCallLac(int value) {
        lac = String.valueOf(value);
    }

    public String getLocation() {
        return (latitude + " : " + longitude);
    }

    public void groupURLSent() {
        strURLSent =
                "http://www.opencellid.org/cell/get?mcc=" + mcc
                        + "&mnc=" + mnc
                        + "&cellid=" + cellid
                        + "&lac=" + lac
                        + "&fmt=txt";
    }

    public String getstrURLSent() {
        return strURLSent;
    }

    public String getGetOpenCellID_fullresult() {
        return GetOpenCellID_fullresult;
    }

    public void GetOpenCellID() throws Exception {


        groupURLSent();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(strURLSent);
        HttpResponse response = client.execute(request);
        GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
        spliteResult();
    }

    private void spliteResult() {
        if (GetOpenCellID_fullresult.equalsIgnoreCase("err")) {
            error = true;
        } else {
            error = false;
            String[] tResult = GetOpenCellID_fullresult.split(",");
            latitude = tResult[0];
            longitude = tResult[1];
        }


    }
}
