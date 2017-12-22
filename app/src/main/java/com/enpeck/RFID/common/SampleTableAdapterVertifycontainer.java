package com.enpeck.RFID.common;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This class implements the main functionalities of the TableAdapter in
 * Mutuactivos.
 *
 *
 * @author Brais Gabín
 */
public abstract class SampleTableAdapterVertifycontainer extends BaseTableAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    Button button;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public SampleTableAdapterVertifycontainer(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * Returns the context associated with this array adapter. The context is
     * used to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Quick access to the LayoutInflater instance that this Adapter retreived
     * from its Context.
     *
     * @return The shared LayoutInflater.
     */
    public LayoutInflater getInflater() {
        return inflater;
    }

    @Override
    public View getView(final int row, final int column, View converView, ViewGroup parent) {
        // RecyclerView.ViewHolder holder;
        if (converView == null) {
            converView = inflater.inflate(getLayoutResource(row, column), parent, false);
            // holder =new ViewHolder();
        }
        setText(converView, getCellString(row, column),row,column);
        final TextView cellString = (TextView) converView.findViewById(android.R.id.text1);
       /* if(column==0) {
            if()
            cellString.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_black_24dp, 0, 0, 0);

        }*/
        // button=(Button)converView.findViewById(R.id.inventoryButton);
        //  final TextView listView =(TextView) converView.findViewById(R.id.inventoryList);
        try {
            cellString.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, getCellString(row, column), Toast.LENGTH_SHORT).show();
                    if (row == 0) {
                   /* Toast.makeText(getContext(),"NO date is added",Toast.LENGTH_LONG).show();*/
                    }
                    try {
                        if (row == -1) {
                            Toast.makeText(getContext(), getCellString(row, column), Toast.LENGTH_SHORT).show();
                            // button.setEnabled(true);



                        } else {
                            Toast.makeText(getContext(), getCellString(row, column), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {

        }

        return converView;
    }

    /**
     * Sets the text to the view.
     *
     * @param view
     * @param text
     */
    private void setText(View view, String text,int r,int c) {
        ((TextView) view.findViewById(android.R.id.text1)).setText(text);
        Log.d("row_col", "setText: "+text+" position r-"+r+" position c-"+c);
        if(r==0&&c==10&&text.equalsIgnoreCase("NO"))
        {
            ((TextView) view.findViewById(android.R.id.text1)).setBackgroundColor(Color.WHITE);
        }
        if(c==-1 && r != -1)
        {
            ((TextView) view.findViewById(android.R.id.text1)).setBackgroundColor(Color.GREEN);
            ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.WHITE);

        }
    }

    /**
     * @param row    the title of the row of this header. If the column is -1
     *               returns the title of the row header.
     * @param column the title of the column of this header. If the column is -1
     *               returns the title of the column header.
     * @return the string for the cell [row, column]
     */
    public abstract String getCellString(int row, int column);

    public abstract int getLayoutResource(int row, int column);
}