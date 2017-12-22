package com.enpeck.RFID.common;

/**
 * Created by ABC on 09/21/2017.
 */

public class ModelDeailyReport {
    private String Serialno;
    private String IECno;
    private String Billno;
    private String E_sealno;
    private String Vehicleno;
    private String Containerno;
    private String Destination;
    private String S_Bill_no;
    private String S_billDate;
    private String s_billtime;
    private String Source;
    private String Tag;
    private String Count;

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getS_billDate() {
        return S_billDate;
    }

    public void setS_billDate(String s_billDate) {
        S_billDate = s_billDate;
    }

    public String getS_billtime() {
        return s_billtime;
    }

    public void setS_billtime(String s_billtime) {
        this.s_billtime = s_billtime;
    }

    public ModelDeailyReport(){

    }

    public ModelDeailyReport(String serialno, String IECno, String billno, String s_Bill_no, String vehicleno, String source, String destination, String containerno, String e_sealno) {
        Serialno = serialno;
        this.IECno = IECno;
        Billno = billno;
        E_sealno = e_sealno;
        Vehicleno = vehicleno;
        Containerno = containerno;
        Destination = destination;
        S_Bill_no =s_Bill_no;
        Source =source;
    }

    public ModelDeailyReport(String serialno, String IECno, String billno, String s_Bill_no, String vehicleno, String source, String destination, String containerno, String e_sealno,String Sealingdate,String Sealingtime) {
        Serialno = serialno;
        this.IECno = IECno;
        Billno = billno;
        E_sealno = e_sealno;
        Vehicleno = vehicleno;
        Containerno = containerno;
        Destination = destination;
        S_Bill_no =s_Bill_no;
        Source =source;
        S_billDate =Sealingdate;
        s_billtime =Sealingtime;
    }

    public ModelDeailyReport(String IECno ,String billno,String s_billDate,String e_sealno,String Sealingdate,String Sealingtime,String destination,String containerno,String vehicleno,String source,String  serialno,String tag ,String count){
        this.IECno =IECno;
        Billno =billno;
        S_Bill_no =s_billDate;
        E_sealno =e_sealno;
        S_billDate =Sealingdate;
        s_billtime =Sealingtime;
        Destination =destination;
        Containerno =containerno;
        Vehicleno =vehicleno;
        Serialno =serialno;
        Source =source;
        Tag =tag;
        Count =count;

    }


    public ModelDeailyReport(String serialno, String IECno, String billno,String s_Bill_no) {
        Serialno = serialno;
        this.IECno = IECno;
        Billno = billno;
        S_Bill_no =s_Bill_no;

    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getS_Bill_no() {
        return S_Bill_no;
    }

    public void setS_Bill_no(String s_Bill_no) {
        S_Bill_no = s_Bill_no;
    }

    public String getSerialno() {
        return Serialno;
    }

    public void setSerialno(String serialno) {
        Serialno = serialno;
    }

    public String getIECno() {
        return IECno;
    }

    public void setIECno(String IECno) {
        this.IECno = IECno;
    }

    public String getBillno() {
        return Billno;
    }

    public void setBillno(String billno) {
        Billno = billno;
    }

    public String getE_sealno() {
        return E_sealno;
    }

    public void setE_sealno(String e_sealno) {
        E_sealno = e_sealno;
    }

    public String getVehicleno() {
        return Vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        Vehicleno = vehicleno;
    }

    public String getContainerno() {
        return Containerno;
    }

    public void setContainerno(String containerno) {
        Containerno = containerno;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }
}
