package com.pdm.moneyscheduler.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pdm.moneyscheduler.Goals;
import com.pdm.moneyscheduler.OptionDetails;
import com.pdm.moneyscheduler.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder>{
    private List<Goal> listaMetas;
    private Context context;

    public GoalAdapter(Context context) {
        this.context = context;
        this.listaMetas=new ArrayList<>();
    }

    public GoalAdapter( Context context,List<Goal> listaMetas) {
        this.listaMetas = listaMetas;
        this.context = context;
    }

    public void add(Goal goal){
        listaMetas.add(goal);
        notifyDataSetChanged();
    }
    public void clear(){
        this.listaMetas.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.goals_individual,parent,false);
        return new GoalAdapter.ViewHolder(view);
    }

    private int convertDpToPx(float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder holder, int position) {
        Goal goal= listaMetas.get(position);
        holder.texto1.setText(goal.getMeta());
        holder.texto2.setText(goal.getPrecioAcumulado()+"/"+goal.getPrecio().toString()+"€");

        holder.pieChart.setUsePercentValues(true);
        holder.pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
        holder.pieChart.setDragDecelerationFrictionCoef(0.95f);
        holder.pieChart.setHoleRadius(80f);
        holder.pieChart.setTransparentCircleRadius(61f);
        holder.pieChart.setDrawCenterText(true);
        holder.pieChart.setRotationAngle(0f);
        holder.pieChart.setRotationEnabled(true);
        holder.pieChart.setHighlightPerTapEnabled(true);
        holder.pieChart.getLegend().setEnabled(false);
        holder.pieChart.setEntryLabelTextSize(12f);
        holder.pieChart.getDescription().setEnabled(false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        float porcentajeCompletado = (float) (goal.getPrecioAcumulado()/goal.getPrecio()*100);
        entries.add(new PieEntry(porcentajeCompletado));
        entries.add(new PieEntry(100f - porcentajeCompletado));

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.GRAY);
        dataSet.setColors(colors);
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        holder.pieChart.setData(data);
        holder.pieChart.highlightValues(null);
        holder.pieChart.setCenterText(String.format("%.1f%%", porcentajeCompletado));
        holder.pieChart.setCenterTextSize(16f);
        holder.pieChart.setTouchEnabled(false);
        holder.pieChart.invalidate();

        if(goal.getPrecio()!=goal.getPrecioAcumulado()){
            holder.itemView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Spinner);
                builder.setTitle("Indica la cantidad a introducir:");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        setAcumulado(goal,Double.parseDouble(m_Text));
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            });
        }





    }

    public void setAcumulado(Goal goal,Double valor){
        Double finalValor = valor;
        if(valor+goal.getPrecioAcumulado()>goal.getPrecio())valor=goal.getPrecio();
        else valor=valor+goal.getPrecioAcumulado();

        FirebaseFirestore.getInstance()
                .collection("Goals")
                .document(goal.getIdMeta())
                .update("precioAcumulado", valor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addOption(goal, finalValor);

                        Goals detalleFragment = new Goals();
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.mainFrame, detalleFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al actualizar el campo
                    }
                });

    }

    public void addOption(Goal goal,Double valor){
        String opcionId = UUID.randomUUID().toString();

        String cant = valor.toString();
        String descripcion = goal.getMeta();
        String currency = "EUR";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


        Opcion opcion = new Opcion(
                FirebaseAuth.getInstance().getUid(),
                opcionId,
                "Gastos",
                goal.getMeta(),
                Double.parseDouble(cant),
                currency,
                descripcion,
                goal.getFechaInicio()
        );

        FirebaseFirestore.getInstance()
                .collection("Gastos")
                .document(opcionId)
                .set(opcion);

    }

    @Override
    public int getItemCount() {
        return listaMetas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView texto1,texto2;
        private final PieChart pieChart;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            texto1=itemView.findViewById(R.id.texto1);
            texto2=itemView.findViewById(R.id.texto2);
            pieChart=itemView.findViewById(R.id.pieChartGoals);
        }

    }
}
