package com.enpeck.RFID.common;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by ABC on 10/30/2017.
 */

public class Bean implements KvmSerializable {
    public String serial;
    public String iec;
    public String bill;
    public String vechicle;
    public String container;
    public String dest;
    public String sealingdate;
    public String sealingtime;
    public String shippingdate;
    public String Sealno;

    public Bean(String serial, String iec, String bill, String vechicle, String container, String dest, String sealingdate, String sealingtime, String shippingdate,String Sealno) {
        this.serial = serial;
        this.iec = iec;
        this.bill = bill;
        this.vechicle = vechicle;
        this.container = container;
        this.dest = dest;
        this.sealingdate = sealingdate;
        this.sealingtime = sealingtime;
        this.shippingdate = shippingdate;
        this.Sealno =Sealno;
    }

    public Bean(){}


    public Object getProperty(int arg0) {
        switch(arg0)
        {
            case 0:
                return serial;
            case 1:
                return iec;
            case 2:
                return bill;
            case 3:
                return vechicle;
            case 5:
                return container;
            case 6:
                return dest;
            case 7:
                return sealingdate;
            case 8:
                return sealingtime;
            case 9:
                return shippingdate;
            case 10:
                return Sealno;
        }
        return null;
    }

    public int getPropertyCount() {
        return 10;
    }

    @SuppressWarnings("rawtypes")
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "serial";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "iec";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "bill";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "vechicle";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "container";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "dest";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "sealingdate";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "sealingtime";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "shippingdate";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Sealno";
                break;
            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index)
        {
            case 0:
                serial = value.toString();
                break;
            case 1:
                iec = value.toString();
                break;
            case 2:
                bill = value.toString();
                break;
            case 3:
                vechicle = value.toString();
                break;
            case 4:
                container = value.toString();
                break;
            case 5:
                dest = value.toString();
                break;
            case 6:
                sealingdate = value.toString();
                break;
            case 7:
                sealingtime = value.toString();
                break;
            case 8:
                shippingdate = value.toString();
                break;
            case 9:
                Sealno = value.toString();
                break;
            default:
                break;
        }
    }
}
