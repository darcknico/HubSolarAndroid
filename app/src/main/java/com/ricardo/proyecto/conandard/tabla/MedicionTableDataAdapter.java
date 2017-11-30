package com.ricardo.proyecto.conandard.tabla;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by nico on 23/11/17.
 */

public class MedicionTableDataAdapter extends TableDataAdapter<FranjaHoraria> {

    public MedicionTableDataAdapter(Context context, List<FranjaHoraria> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        //Log.d("Table","Column Index "+rowIndex);
        FranjaHoraria franjaHoraria = getRowData(rowIndex);
        View renderView = null;
        switch (columnIndex){
            case 0:
                renderView = render(franjaHoraria.getHora());
                break;
            case 1:
                renderView = render(franjaHoraria.getRadiacionSolar());
                break;
            case 2:
                renderView = render(franjaHoraria.getIntensidadCorriente());
                break;
            case 3:
                renderView = render(franjaHoraria.getVoltaje());
                break;
            case 4:
                renderView = render(franjaHoraria.getPotencia());
                break;
        }
        return renderView;
    }

    private int paddingLeft = 20;
    private int paddingTop = 30;
    private int paddingRight = 20;
    private int paddingBottom = 30;
    private int textSize = 12;
    private int typeface = Typeface.BOLD;
    private int textColor = 0x99000000;

    private View render(String text){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        textView.setTypeface(textView.getTypeface(), typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);

        return textView;
    }
}
