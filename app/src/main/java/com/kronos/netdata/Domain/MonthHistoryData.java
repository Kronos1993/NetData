package com.kronos.netdata.Domain;

import java.util.ArrayList;

/**
 * Created by farah on 02/nov/2019.
 */
public class MonthHistoryData {

    private double _id;
    private String month_name;
    private ArrayList<Historial> historials;

    public MonthHistoryData(String month_name, ArrayList<Historial> historials) {
        this.month_name = month_name;
        this.historials = historials;
        this._id=Math.random();
    }

    public MonthHistoryData() {
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public ArrayList<Historial> getHistorials() {
        return historials;
    }

    public void setHistorials(ArrayList<Historial> historials) {
        this.historials = historials;
    }

    public double get_id() {
        return _id;
    }

    public void set_id(double _id) {
        this._id = _id;
    }
}
