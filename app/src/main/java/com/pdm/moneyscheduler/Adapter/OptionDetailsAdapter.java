package com.pdm.moneyscheduler.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pdm.moneyscheduler.Categoria;
import com.pdm.moneyscheduler.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OptionDetailsAdapter extends RecyclerView.Adapter<OptionDetailsAdapter.ViewHolder>{

    private List<Opcion> listaOpciones;
    private Context context;
    private Resources resources;

    public OptionDetailsAdapter() {
    }

    public OptionDetailsAdapter(Context context) {
        this.context = context;
        this.listaOpciones = new ArrayList<>();
    }

    public OptionDetailsAdapter(Context context, Resources resources) {
        this.listaOpciones = new ArrayList<>();
        this.context = context;
        this.resources = resources;
    }

    public OptionDetailsAdapter(List<Opcion> listaOpciones, Context context, Resources resources) {
        this.listaOpciones = listaOpciones;
        this.context = context;
        this.resources = resources;
    }
    public void add(Opcion opt){
        listaOpciones.add(opt);
        notifyDataSetChanged();
    }
    public void add(Opcion opt,int pos){
        listaOpciones.add(pos,opt);
        notifyDataSetChanged();
    }
    public void clear(){
        this.listaOpciones.clear();
        notifyDataSetChanged();
    }

    public int getPosition(Opcion opt){
        for(int i=0;i<listaOpciones.size();i++){
            if(listaOpciones.get(i).equals(opt)){
                return listaOpciones.indexOf(opt);
            }
        }
        return -1;
    }


    public void remove(int position){
        if(listaOpciones.get(position)!=null){
            listaOpciones.remove(listaOpciones.get(position));
        }
    }

    @NonNull
    @Override
    public OptionDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_income_details,parent,false);
        return new OptionDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionDetailsAdapter.ViewHolder holder, int position) {
        Opcion opt = listaOpciones.get(position);

        Categoria categ = new Categoria();
        switch (opt.getCategoria()){
            case "Trabajo":
                categ=new Categoria("Trabajo",resources.getColor(R.color.entertainment_color),resources.getDrawable(R.mipmap.work_icon_foreground));
                break;
            case "Regalos":
                categ=new Categoria("Regalos",resources.getColor(R.color.food_color),resources.getDrawable(R.mipmap.gift_icon_foreground));
                break;
            case "Ahorros":
                categ=new Categoria("Ahorros",resources.getColor(R.color.transport_color),resources.getDrawable(R.mipmap.savings_icon_foreground));
                break;
            case "Otros":
                categ=new Categoria("Otros",resources.getColor(R.color.others_color),resources.getDrawable(R.mipmap.others_icon_foreground));
                break;
            case "Ocio":
                categ=new Categoria("Ocio",resources.getColor(R.color.entertainment_color),resources.getDrawable(R.mipmap.popcorn_icon_foreground));
                break;
            case "Alimentacion":
                categ=new Categoria("Alimentacion",resources.getColor(R.color.food_color),resources.getDrawable(R.mipmap.groceries_icon_foreground));
                break;
            case "Transporte":
                categ=new Categoria("Transporte",resources.getColor(R.color.transport_color),resources.getDrawable(R.mipmap.transport_icon_foreground));
                break;
        }

        int color = categ.getColor();
        Drawable drawable= categ.getIcon();

        holder.icono.setImageDrawable(configurarDrawable(drawable));
        holder.categoria.setText(opt.getCategoria());
        holder.porcentaje.setText(opt.getCantidad().toString()+ " "+opt.getCurrency());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date2 = new Date(opt.getFecha());
        String textFecha = dateFormat.format(date2);

        holder.fecha.setText(textFecha);

        ColorStateList colorStateList = ColorStateList.valueOf(color);
        holder.icono.setBackgroundTintList(colorStateList);
    }
    private Drawable configurarDrawable(Drawable drawable){
        Drawable originalDrawable = drawable;

        int desiredWidth = 200;
        int desiredHeight = 200;

        Bitmap bitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        if (originalDrawable != null) {
            originalDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            originalDrawable.draw(canvas);
        }

        BitmapDrawable resizedDrawable = new BitmapDrawable(resources, bitmap);

        return resizedDrawable;

    }

    @Override
    public int getItemCount() {
        return listaOpciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView icono;
        private final TextView categoria, porcentaje,fecha;

        private final LinearLayout linearIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icono=itemView.findViewById(R.id.icon);
            categoria=itemView.findViewById(R.id.textNombreCategoria);
            porcentaje=itemView.findViewById(R.id.textPorcentaje);
            linearIcon= itemView.findViewById(R.id.linearIcon);
            fecha=itemView.findViewById(R.id.textDate);
        }

    }
}
