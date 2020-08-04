package com.kronos.netdata.Domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by farah on 01/nov/2019.
 */
@DatabaseTable(tableName="historial")
public class Historial {

    public static final String PAQUETE_FIELD_NAME = "paquete";
    public static final String ID_PAQUETE_FIELD_NAME = "id_paquete";
    public static final String DATE_FIELD_NAME = "date";
    public static final String MONTH_NAME_FIELD_NAME = "month_name";

    @DatabaseField(generatedId = true)
    private int _id;
    @DatabaseField(columnName=PAQUETE_FIELD_NAME)
    private String paquete;
    @DatabaseField(columnName=ID_PAQUETE_FIELD_NAME)
    private int id_paquete;
    @DatabaseField(columnName=DATE_FIELD_NAME)
    private long date;
    @DatabaseField(columnName=MONTH_NAME_FIELD_NAME)
    private String month_name;


    public Historial() {
    }

    public Historial(String paquete, int id_paquete, long date, String month_name) {
        this.paquete = paquete;
        this.id_paquete = id_paquete;
        this.date = date;
        this.month_name=month_name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }

    public int getId_paquete() {
        return id_paquete;
    }

    public void setId_paquete(int id_paquete) {
        this.id_paquete = id_paquete;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }
}
