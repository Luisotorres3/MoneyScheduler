package com.pdm.moneyscheduler.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pdm.moneyscheduler.Categoria;
import com.pdm.moneyscheduler.OptionDetails;
import com.pdm.moneyscheduler.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class OpcionAdapter extends RecyclerView.Adapter<OpcionAdapter.ViewHolder>{

    private List<Opcion> listaOpciones;
    private Context context;
    private Resources resources;


    public OpcionAdapter() {

    }

    public OpcionAdapter(Context context, Resources resources) {
        this.listaOpciones = new ArrayList<>();
        this.context = context;
        this.resources= resources;
    }


    public OpcionAdapter(List<Opcion> listaOpciones, Context context, Resources resources) {
        this.listaOpciones = listaOpciones;
        this.context = context;
        this.resources= resources;
    }

    public void add(Opcion opt){
        Boolean encontrado=false;
        for(Opcion opcion: listaOpciones){
            if(opcion.getCategoria().equals(opt.getCategoria())){
                double nuevaCantidad= opcion.getCantidad() + opt.getCantidad();
                opcion.setCantidad((Double) nuevaCantidad);
                encontrado=true;
            }
        }
        if(!encontrado){
            listaOpciones.add(opt);
        }
        notifyDataSetChanged();
    }
    public void clear(){
        this.listaOpciones.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OpcionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_income,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpcionAdapter.ViewHolder holder, int position) {
        Collections.sort(listaOpciones, new Comparator<Opcion>() {
            @Override
            public int compare(Opcion o1, Opcion o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    Date fecha1 = sdf.parse(o1.getFecha());
                    Date fecha2 = sdf.parse(o2.getFecha());
                    return fecha2.compareTo(fecha1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
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

        ColorStateList colorStateList = ColorStateList.valueOf(color);
        holder.icono.setBackgroundTintList(colorStateList);

        holder.itemView.setOnClickListener(v -> {
            // Creamos un nuevo fragmento de ProductoDetalle
            OptionDetails detalleFragment = new OptionDetails();

            if(opt!=null){
                // Creamos un bundle y le añadimos el objeto Producto
                Bundle bundle = new Bundle();
                bundle.putSerializable("opcion",opt);
                // Añadimos el bundle al fragmento
                detalleFragment.setArguments(bundle);
            }

            // Obtenemos el FragmentManager y comenzamos la transacción
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Reemplazamos el contenido actual del contenedor por el fragmento ProductoDetalle
            transaction.replace(R.id.mainFrame, detalleFragment);
            // Agregamos la transacción al back stack para poder volver al fragmento anterior
            transaction.addToBackStack(null);
            // Confirmamos la transacción
            transaction.commit();
        });

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
        private final TextView categoria, porcentaje;

        private final LinearLayout linearIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icono=itemView.findViewById(R.id.icon);
            categoria=itemView.findViewById(R.id.textNombreCategoria);
            porcentaje=itemView.findViewById(R.id.textPorcentaje);
            linearIcon= itemView.findViewById(R.id.linearIcon);



        }

    }
}
