package com.kronos.netdata.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.MonthHistoryData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.kronos.netdata.R;

/**
 * Created by farah on 02/nov/2019.
 */
public class HistoryExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<MonthHistoryData> groupParentList;
    private ArrayList<MonthHistoryData> groupOriginalList;

    public HistoryExpandableListViewAdapter(Context context, ArrayList<MonthHistoryData> groupParentList, ArrayList<MonthHistoryData> groupOriginalList) {
        this.context = context;
        this.groupParentList=new ArrayList<>();
        this.groupParentList.addAll(groupParentList);
        this.groupOriginalList = groupOriginalList;
    }

    @Override
    public int getGroupCount() {
        return groupParentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupParentList.get(groupPosition).getHistorials().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupParentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupParentList.get(groupPosition).getHistorials().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return ((long) groupParentList.get(groupPosition).get_id());
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupParentList.get(groupPosition).getHistorials().get(childPosition).get_id();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final MonthHistoryData monthHistoryData= (MonthHistoryData) getGroup(groupPosition);

        if (convertView==null){
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= layoutInflater.inflate(R.layout.date_historial_row,null);
        }
        TextView textViewMonth= (TextView) convertView.findViewById(R.id.textViewDateHistorial);
        textViewMonth.setText(monthHistoryData.getMonth_name()+": "+cucThisMonth(monthHistoryData)+"CUC");
        return convertView;
    }

    private String cucThisMonth(MonthHistoryData monthHistoryData) {
        int sum=0;
        for (int i = 0; i < monthHistoryData.getHistorials().size(); i++) {
            sum+=monthHistoryData.getHistorials().get(i).getId_paquete();
        }
        return Integer.toString(sum);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Historial historial= (Historial) getChild(groupPosition,childPosition);
        convertView=null;
        if (convertView==null){
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= layoutInflater.inflate(R.layout.historial_row,null);
        }
        TextView paqueteName= (TextView) convertView.findViewById(R.id.textViewHistorialPaqueteName);
        TextView paquetePrice= (TextView) convertView.findViewById(R.id.textViewHistorialPaquetePrice);
        TextView buyDate= (TextView) convertView.findViewById(R.id.textViewHistorialPaqueteBuyDate);

        paqueteName.setText(historial.getPaquete());
        paquetePrice.setText(Integer.toString(historial.getId_paquete())+" CUC");
        buyDate.setText(dateAsString(historial.getDate()));

        return convertView;
    }

    private String dateAsString(long date) {
        String string_date="";
        DateFormat format=new SimpleDateFormat("dd-MM-yyyy");
        string_date=format.format(new Date(date)).toString();
        return string_date;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
