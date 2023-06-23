package com.pdm.moneyscheduler;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

public class Categoria {
    private String tipo;
    private int color;
    private Drawable icon;

    private String idIcon;

    public Categoria(String tipo, int color, Drawable icon, String idIcon) {
        this.tipo = tipo;
        this.color = color;
        this.icon = icon;
        this.idIcon= idIcon;
    }
    public Categoria(String tipo, int color, Drawable icon) {
        this.tipo = tipo;
        this.color = color;
        this.icon = icon;
    }
    public Categoria(){
        this.tipo = null;
        this.color = 0;
        this.icon = null;
        this.idIcon= null;
    }
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getIdIcon() {
        return idIcon;
    }
    public void setIdIcon(String idIcon) {
        this.idIcon = idIcon;
    }
}
