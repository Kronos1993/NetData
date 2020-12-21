package com.kronos.netdata.Activities.ViewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kronos.netdata.Activities.MainActivityDrawer;
import com.kronos.netdata.DB.Connection;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.MonthHistoryData;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;

import java.util.ArrayList;
import java.util.Hashtable;

public class HistorialActivityViewModel extends AndroidViewModel {

    private ArrayList<Historial> historials=new ArrayList<>();
    private ArrayList<MonthHistoryData> monthHistoryDatas=new ArrayList<>();
    private Hashtable<Integer, Hashtable<String,Integer>> packageBuy = new Hashtable<>();
    private boolean showingList = false;


    public HistorialActivityViewModel(@NonNull Application application) {
        super(application);

        try {
            QueryBuilder<Historial,Integer> queryBuilder= Connection.getConnection(application).getHistorialDao().queryBuilder();
            queryBuilder.orderBy(Historial.DATE_FIELD_NAME, false);
            PreparedQuery<Historial> preparedQuery = queryBuilder.prepare();
            historials= (ArrayList<Historial>) Connection.getConnection(application).getHistorialDao().query(preparedQuery);
        }catch (Exception e){
            Toast.makeText(application, R.string.error_db, Toast.LENGTH_SHORT).show();
            GeneralUtility.navigate(application, MainActivityDrawer.class);
            e.printStackTrace();
        }

    }

    public ArrayList<Historial> getHistorials() {
        return historials;
    }

    public void setHistorials(ArrayList<Historial> historials) {
        this.historials = historials;
    }

    public ArrayList<MonthHistoryData> getMonthHistoryDatas() {
        return monthHistoryDatas;
    }

    public void setMonthHistoryDatas(ArrayList<MonthHistoryData> monthHistoryDatas) {
        this.monthHistoryDatas = monthHistoryDatas;
    }

    public Hashtable<Integer, Hashtable<String, Integer>> getPackageBuy() {
        return packageBuy;
    }

    public void setPackageBuy(Hashtable<Integer, Hashtable<String, Integer>> packageBuy) {
        this.packageBuy = packageBuy;
    }

    public boolean isShowingList() {
        return showingList;
    }

    public void setShowingList(boolean showingList) {
        this.showingList = showingList;
    }
}
