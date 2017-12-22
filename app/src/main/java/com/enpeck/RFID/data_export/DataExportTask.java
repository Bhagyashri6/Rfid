package com.enpeck.RFID.data_export;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.enpeck.RFID.inventory.InventoryListItem;

/**
 * class to export inventory Data in CSV format
 */
public class DataExportTask extends AsyncTask<Void, Void, Boolean> {

    private static final String FILE_EXTENSION = ".csv";
    private static final String HEADERS = "TAG ID,COUNT";
    private static final String INVENTORY_SUMMARY = "INVENTORY SUMMARY";
    private static final String UNIQUE_COUNT = "UNIQUE COUNT:";
    private static final String TOTAL_COUNT = "TOTAL COUNT:";
    private static final String SEPARATOR = ",";
    private static final String TAG = "DataExportTask";
    private static final String READ_TIME = "READ TIME:";
    private final ArrayList<InventoryListItem> inventoryList;
    private final Context context;
    private final String connectedReader;
    private final int totalTags;
    private final int uniqueTags;
    private final String readTime;
    private FileOutputStream fos;

    public DataExportTask(Context context, ArrayList<InventoryListItem> inventoryList, String connectedReader, int totalTags, int uniqueTags, long rrStartedTime) {
        this.inventoryList = inventoryList;
        this.context = context;
        this.connectedReader = connectedReader;
        this.totalTags = totalTags;
        this.uniqueTags = uniqueTags;
        this.readTime = getReadTime(rrStartedTime);
    }

    private String getReadTime(long rrStartedTime) {

        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(rrStartedTime),
                TimeUnit.MILLISECONDS.toMinutes(rrStartedTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(rrStartedTime) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, "Exporting inventory data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (isExternalStorageWritable()) {
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/inventory");
                dir.mkdirs();
                File file = new File(dir, getFilename());
                if (!file.exists())
                    file.createNewFile();
                fos = new FileOutputStream(file, false);
                addInventorySummary();
                addHeaders();
                exportData(inventoryList);
            } else {
                Log.e(TAG, "External storage not writable");
                return false;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return true;
    }

    private void addInventorySummary() throws IOException {
        fos.write((INVENTORY_SUMMARY + "\n").getBytes());
        fos.write(UNIQUE_COUNT.getBytes());
        fos.write((SEPARATOR + uniqueTags + "\n").getBytes());
        fos.write(TOTAL_COUNT.getBytes());
        fos.write((SEPARATOR + totalTags + "\n").getBytes());
        fos.write(READ_TIME.getBytes());
        fos.write((SEPARATOR + readTime + "\n\n").getBytes());
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result)
            Toast.makeText(context, "Inventory Data has been exported", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Failed to export inventory data", Toast.LENGTH_SHORT).show();
    }

    /**
     * method to add headers to the file
     */
    private void addHeaders() throws IOException {
        fos.write((HEADERS + "\n").getBytes());
    }

    /**
     * method to export data to file in csv format
     *
     * @param inventoryList inventory data to be export
     */
    private void exportData(ArrayList<InventoryListItem> inventoryList) throws IOException {
        for (InventoryListItem item : inventoryList) {
            fos.write((item.getTagID() + SEPARATOR + item.getCount() + "\n").getBytes());
        }
    }

    /**
     * method to know whether file existed are not
     */
    public boolean fileExistance(String fname) {
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }

    /**
     * Checks if external storage is available for read and write
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * get filename
     */
    private String getFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");
        return connectedReader + "_" + sdf.format(new Date()) + FILE_EXTENSION;
    }
}
