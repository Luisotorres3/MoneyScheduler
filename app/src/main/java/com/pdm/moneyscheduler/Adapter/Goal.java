package com.pdm.moneyscheduler.Adapter;

import java.io.Serializable;
import java.util.Date;

public class Goal implements Serializable {
    private String idMeta,uid;
    private String meta;
    private Double precioInicial,precioAcumulado;
    private String fechaInicio,fechaFinal;
    private Double porcentajeCompletado;

    public Goal(){

    }
    public Goal(String uid,String idMeta, String meta, Double precioInicial, Double precioAcumulado, String fechaInicio, String fechaFinal, Double porcentajeCompletado) {
        this.uid=uid;
        this.idMeta = idMeta;
        this.meta = meta;
        this.precioInicial = precioInicial;
        this.precioAcumulado=precioAcumulado;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.porcentajeCompletado = porcentajeCompletado;
    }

    public String getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(String idMeta) {
        this.idMeta = idMeta;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Double getPrecio() {
        return precioInicial;
    }

    public void setPrecio(Double precio) {
        this.precioInicial = precio;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Double getPorcentajeCompletado() {
        return porcentajeCompletado;
    }

    public void setPorcentajeCompletado(Double porcentajeCompletado) {
        this.porcentajeCompletado = porcentajeCompletado;
    }

    public Double getPrecioAcumulado() {
        return precioAcumulado;
    }

    public void setPrecioAcumulado(Double precioAcumulado) {
        this.precioAcumulado = precioAcumulado;
    }
}
