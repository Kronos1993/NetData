package com.kronos.netdata.Domain;

import java.util.Objects;

/**
 * Created by marcos.guerra on 10/05/2019.
 */
public class PaqueteInternet {

    private int id;
    private String paquete;
    private String bono;

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBono() {
        return bono;
    }

    public void setBono(String bono) {
        this.bono = bono;
    }

    public PaqueteInternet(int id, String paquete, String bono) {
        this.id = id;
        this.paquete = paquete;
        this.bono = bono;
    }

    public PaqueteInternet() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaqueteInternet)) return false;
        PaqueteInternet that = (PaqueteInternet) o;
        return getId() == that.getId() &&
                getPaquete().equals(that.getPaquete()) &&
                getBono().equals(that.getBono());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPaquete(), getBono());
    }
}
