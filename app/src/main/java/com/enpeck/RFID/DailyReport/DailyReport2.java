package com.enpeck.RFID.DailyReport;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpeck.RFID.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyReport2 extends Fragment {

    TextView billno,port,containerr,truck,date,iec,serial,eseal,sealingdate,sealingtime;
    String billno1,port1,container1,truck1,serialno1,date1,iec1,eseal1,sealingdate1,sealingtime1;
    TextView datetime,time;
    public DailyReport2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_daily_report2,container,false);

        billno = (TextView) view.findViewById(R.id.billnoo);
        port = (TextView) view.findViewById(R.id.destt);
        truck = (TextView) view.findViewById(R.id.trucknoo);
        containerr = (TextView) view.findViewById(R.id.containerr);
        date =(TextView)view.findViewById(R.id.date);
        iec =(TextView)view.findViewById(R.id.iec);
        serial=(TextView)view.findViewById(R.id.serial);
        eseal=(TextView)view.findViewById(R.id.eseal);

        sealingdate=(TextView)view.findViewById(R.id.esealdate);
        sealingtime=(TextView)view.findViewById(R.id.esealtime);




        billno1 =getArguments().getString("bill");
        date1 =getArguments().getString("date");
        port1 =getArguments().getString("port");
        truck1 =getArguments().getString("truck");
        serialno1 =getArguments().getString("serialno");
        container1 =getArguments().getString("cont");
        iec1 =getArguments().getString("ieccode");
        eseal1 =getArguments().getString("eseal");
        sealingdate1 =getArguments().getString("sealingdate");
        sealingtime1 =getArguments().getString("sealingtime");
    /*    systemdate =getArguments().getString("sysdate");
        systemtime =getArguments().getString("systime");*/


        billno.setText(billno1);
        port.setText(port1);
        truck.setText(truck1);
        containerr.setText(container1);
        date.setText(date1);
        iec.setText(iec1);
        eseal.setText(eseal1);
        sealingdate.setText(sealingdate1);
        sealingtime.setText(sealingtime1);

        return view;
    }

}
