package com.ricardo.proyecto.conandard.tabla;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.codecrafters.tableview.TableHeaderAdapter;

/**
 * Created by nico on 23/11/17.
 */

public class MedicionTableHeaderAdapter extends TableHeaderAdapter {

    private int paddingLeft = 20;
    private int paddingTop = 30;
    private int paddingRight = 20;
    private int paddingBottom = 30;
    private int textSize = 13;
    private int typeface = Typeface.BOLD;
    private int textColor = 0x99000000;

    public MedicionTableHeaderAdapter(Context context) {
        super(context);
    }

    @Override
    public View getHeaderView(int columnIndex, ViewGroup parentView) {
        View renderView = null;
        switch (columnIndex){
            case 0:
                renderView = renderString("Hora");
                break;
            case 1:
                renderView = renderString("Radiacion\nSolar");
                break;
            case 2:
                renderView = renderString("Intencidad\nCorriente");
                break;
            case 3:
                renderView = renderString("Voltaje");
                break;
            case 4:
                renderView = renderString("Potencia\nW");
                break;
        }
        return renderView;
    }

    private View renderString(String text){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        //textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        //textView.setTypeface(textView.getTypeface(), typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //textView.setSingleLine();
        //textView.setEllipsize(TextUtils.TruncateAt.END);
        return textView;
    }
}
