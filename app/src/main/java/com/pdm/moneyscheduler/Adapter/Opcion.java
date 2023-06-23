package com.pdm.moneyscheduler.Adapter;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Opcion implements Serializable {
    private String id;
    private String tipo;

    private String categoria;
    private Double cantidad;
    private String fecha;
    private String descripcion;
    private String currency;

    private String uid;


    public Opcion() {
    }

    public Opcion(String uid,String id,String tipo,String categoria, Double cantidad, String currency, String descripcion, String fecha) {
        this.id = id;
        this.tipo = tipo;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.currency = currency;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Opcion opcion = (Opcion) o;
        return Objects.equals(id, opcion.id) && Objects.equals(tipo, opcion.tipo) && Objects.equals(categoria, opcion.categoria) && Objects.equals(cantidad, opcion.cantidad) && Objects.equals(fecha, opcion.fecha) && Objects.equals(descripcion, opcion.descripcion) && Objects.equals(currency, opcion.currency) && Objects.equals(uid, opcion.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tipo, categoria, cantidad, fecha, descripcion, currency, uid);
    }
}
