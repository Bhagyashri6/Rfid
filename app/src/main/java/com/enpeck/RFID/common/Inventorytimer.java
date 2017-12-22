package com.enpeck.RFID.common;

import android.app.Activity;
import android.widget.TextView;

import com.enpeck.RFID.R;
import com.enpeck.RFID.application.Application;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by qvfr34 on 2/16/2015.
 */
public class Inventorytimer {
    private static final int INV_UPDATE_INTERVAL = 500;
    private static Inventorytimer inventorytimer;
    private static long startedTime;
    private Activity activity;
    private Timer rrTimer;

    public static Inventorytimer getInstance() {
        if (inventorytimer == null)
            inventorytimer = new Inventorytimer();
        return inventorytimer;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void startTimer() {
        if (isTimerRunning())
            stopTimer();
        startedTime = System.currentTimeMillis();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //ReadRate = (No Of Tags Read / Inventory Duration)
                Application.mRRStartedTime += (System.currentTimeMillis() - startedTime);
                if (Application.mRRStartedTime == 0)
                    Application.TAG_READ_RATE = 0;
                else
                    Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS * 1000 / Application.mRRStartedTime);
                startedTime = System.currentTimeMillis();
                updateUI();
            }
        };
        rrTimer = new Timer();
        rrTimer.scheduleAtFixedRate(task, 0, INV_UPDATE_INTERVAL);
    }

    public void stopTimer() {
        if (rrTimer != null) {
            //Stop the timer
            rrTimer.cancel();
            rrTimer.purge();
            //ReadRate = (No Of Tags Read / Inventory Duration)
            Application.mRRStartedTime += (System.currentTimeMillis() - startedTime);
            if (Application.mRRStartedTime == 0)
                Application.TAG_READ_RATE = 0;
            else
                Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS * 1000 / Application.mRRStartedTime);
        }
        rrTimer = null;
        updateUI();
    }

    public boolean isTimerRunning() {
        if (rrTimer != null)
            return true;
        return false;
    }

    void updateUI() {
        activity.runOnUiThread(new Runnable() {
            StringBuilder min;
            StringBuilder sec;

            @Override
            public void run() {
                TextView readRate = (TextView) activity.findViewById(R.id.readRateContent);
                TextView timeText = (TextView) activity.findViewById(R.id.readTimeContent);
                TextView uniqueTags = (TextView) activity.findViewById(R.id.uniqueTagContent);
                TextView totalTags = (TextView) activity.findViewById(R.id.totalTagContent);

                if (readRate != null) {
                    readRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
                }
                if (uniqueTags != null) {
                    uniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
                    if (uniqueTags.getTextScaleX() > 0.5 && uniqueTags.getText().length() > 4)
                        uniqueTags.setTextScaleX(uniqueTags.getTextScaleX() - (float) 0.1);
                    else if (uniqueTags.getTextScaleX() > 0.4 && uniqueTags.getText().length() > 5)
                        uniqueTags.setTextScaleX(uniqueTags.getTextScaleX() - (float) 0.03);
                }
                if (totalTags != null)
                    totalTags.setText(String.valueOf(Application.TOTAL_TAGS));
                if (timeText != null) {
                    long displayTime = Application.mRRStartedTime;
                    min = new StringBuilder(String.format("%d", TimeUnit.MILLISECONDS.toMinutes(displayTime)));
                    sec = new StringBuilder(String.format("%d", TimeUnit.MILLISECONDS.toSeconds(displayTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(displayTime))));
                    if (min.length() == 1) {
                        min = min.insert(0, "0");
                    }
                    if (sec.length() == 1) {
                        sec = sec.insert(0, "0");
                    }
                    timeText.setText(min + ":" + sec);
                }
                min = null;
                sec = null;
            }
        });
    }


}
