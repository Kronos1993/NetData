package com.kronos.netdata.Activities.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.Domain.USSDCode;
import com.kronos.netdata.Util.GeneralUtility;
import com.kronos.netdata.R;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by farah on 02/oct/2019.
 */
public class PaqueteInternetAdapterGridOrList extends BaseAdapter implements ListAdapter{

    private Context context;
    private ArrayList<PaqueteInternet> paqueteInternets;
    private boolean list;
    private int numColumn;
    private Activity activity;

    public PaqueteInternetAdapterGridOrList(Activity activity,Context context, ArrayList paqueteInternets,boolean list,int numColumn)
    {
        this.context = context;
        this.paqueteInternets= paqueteInternets;
        this.list=list;
        this.numColumn=numColumn;
        this.activity = activity;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return paqueteInternets.size();
    }

    @Override
    public PaqueteInternet getItem(int position) {
        return paqueteInternets.get(position);
    }

    @Override
    public long getItemId(int position) {

        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            if(list){
                view = inflater.inflate(R.layout.new_paquete_row, viewGroup, false);
            }else{
                view = inflater.inflate(R.layout.new_paquete_row_grid, viewGroup, false);
            }
        }

        TextView descripcion= (TextView) view.findViewById(R.id.label_description_paquete);
        TextView nombrePlan= (TextView) view.findViewById(R.id.label_paquete_precio);
        descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1),getItem(position).getPaquete())+String.format(context.getString(R.string.pqt_descripcion2),getItem(position).getId()));
        nombrePlan.setText(getItem(position).getPaquete());
        if(getItem(position).getId()==35 || getItem(position).getId()==45 || getItem(position).getId()==4 || getItem(position).getId()==8){
            descripcion.setText(String.format(context.getString(R.string.pqt_descripcion_no_bono),getItem(position).getId()));
        }else if(getItem(position).getId()==1){
            descripcion.setText(String.format(context.getString(R.string.pqt_diario_descripcion),getItem(position).getId()));
        }else if(getItem(position).getId()==5){
            descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), getItem(position).getBono())+String.format(context.getString(R.string.pqt_descripcion2),getItem(position).getId()));
        }else if(getItem(position).getId()==7){
            descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), getItem(position).getBono())+String.format(context.getString(R.string.pqt_descripcion2),getItem(position).getId()));
        }else if(getItem(position).getId()==10){
            descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), getItem(position).getBono())+String.format(context.getString(R.string.pqt_descripcion2),getItem(position).getId()));
        }else if(getItem(position).getId()==20){
            descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), getItem(position).getBono())+String.format(context.getString(R.string.pqt_descripcion2),getItem(position).getId()));
        }else if(getItem(position).getId()==30){
            descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), getItem(position).getBono())+String.format(context.getString(R.string.pqt_descripcion2),getItem(position).getId()));
        }

        view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createConfirmDialog(getItem(position),context);
                }
            });

        return view;
    }

    private void createConfirmDialog(final PaqueteInternet paqueteInternet, final Context context) {
        final Date date=new Date();
        final Historial historial=new Historial();
        final MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.confirm_paquete_dialog);
        String body=String.format(context.getString(R.string.confirm_paquete_dialog_body_part1),paqueteInternet.getPaquete())+String.format(context.getString(R.string.confirm_paquete_dialog_body_part2),paqueteInternet.getId());
        builder.setMessage(body);

        if(paqueteInternet.getId()!=0){
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    historial.setId_paquete(paqueteInternet.getId());
                    historial.setPaquete(paqueteInternet.getPaquete());
                    historial.setDate(date.getTime());
                    historial.setMonth_name(getMonthName(date.getMonth()));
                    GeneralUtility.makeCallUSSD(context, USSDCode.buy(paqueteInternet.getId()),historial);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            }
        });
        alertDialog.show();


/*
        String body=String.format(context.getString(R.string.confirm_paquete_dialog_body_part1),paqueteInternet.getPaquete())+String.format(context.getString(R.string.confirm_paquete_dialog_body_part2),paqueteInternet.getId());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetRoundCorners);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_confirm, activity.findViewById(R.id.bottom_confirm_dialog));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(35, 0, 35, 300);
        ConstraintLayout layout = view.findViewById(R.id.bottom_confirm_dialog);
        layout.setLayoutParams(layoutParams);

        TextView textViewTitle = view.findViewById(R.id.bottom_sheet_title);
        TextView textViewBody = view.findViewById(R.id.bottom_sheet_body);

        textViewTitle.setText(R.string.confirm_paquete_dialog);
        textViewBody.setText(body);

        Button ok = view.findViewById(R.id.bottom_sheet_button_ok);
        Button cancel = view.findViewById(R.id.bottom_sheet_button_cancel);


        if(paqueteInternet.getId()!=0){
            ok.setOnClickListener(view1 -> {
                historial.setId_paquete(paqueteInternet.getId());
                historial.setPaquete(paqueteInternet.getPaquete());
                historial.setDate(date.getTime());
                historial.setMonth_name(getMonthName(date.getMonth()));
                GeneralUtility.makeCallUSSD(context, USSDCode.buy(paqueteInternet.getId()),historial);
                bottomSheetDialog.dismiss();
            });
            cancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        }
        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            ok.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            cancel.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();*/
    }

    private String getMonthName(int month) {
        String month_name="";
        switch (month){
            case 0:month_name=context.getString(R.string.month_january);break;
            case 1:month_name=context.getString(R.string.month_february);break;
            case 2:month_name=context.getString(R.string.month_march);break;
            case 3:month_name=context.getString(R.string.month_april);break;
            case 4:month_name=context.getString(R.string.month_may);break;
            case 5:month_name=context.getString(R.string.month_june);break;
            case 6:month_name=context.getString(R.string.month_july);break;
            case 7:month_name=context.getString(R.string.month_august);break;
            case 8:month_name=context.getString(R.string.month_september);break;
            case 9:month_name=context.getString(R.string.month_october);break;
            case 10:month_name=context.getString(R.string.month_november);break;
            case 11:month_name=context.getString(R.string.month_december);break;
        }
        return month_name;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }





}
