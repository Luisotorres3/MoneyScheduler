package com.pdm.moneyscheduler

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.pdm.moneyscheduler.Adapter.Opcion
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Graphs.newInstance] factory method to
 * create an instance of this fragment.
 */
class Graphs : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var root: View
    private lateinit var pieChartInicio: PieChart
    private lateinit var pieChartFin: PieChart
    private lateinit var fechaInicio: EditText
    private lateinit var fechaFin: EditText

    private var cantGastos: Double = 0.0
    private var cantIngresos: Double= 0.0

    private var cantGastosFin: Double = 0.0
    private var cantIngresosFin: Double= 0.0

    private var seguir:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_graphs, container, false)

        inicializar()

        funcionesBotones()


        return root
    }

    fun inicializar(){
        fechaInicio=root.findViewById(R.id.editFechaInicio)
        fechaFin=root.findViewById(R.id.editFechaFin)
        pieChartInicio=root.findViewById(R.id.pieChartInicio)
        pieChartFin=root.findViewById(R.id.pieChartFinal)

    }
    fun funcionesBotones(){
        fechaInicio.setOnClickListener {
            showDatePickerDialog(fechaInicio)
        }
        fechaFin.setOnClickListener {
            showDatePickerDialog(fechaFin)
        }



        //Añadir textWatcher para ver si hay cambio en los componentes
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEditText1Filled = fechaInicio.text.toString()!=""
                val isEditText2Filled = fechaFin.text.toString()!=""


                if(isEditText1Filled)getData(fechaInicio.text.toString(),pieChartInicio,0)
                if(isEditText2Filled )getData(fechaFin.text.toString(),pieChartFin,1)

            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        fechaInicio.addTextChangedListener(textWatcher)
        fechaFin.addTextChangedListener(textWatcher)
    }

    fun showDatePickerDialog(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val monthPickerDialog = DatePickerDialog(
            editTextDate.context,
            null,
            year,
            month,
            1 // Día preseleccionado, puedes cambiarlo si lo deseas
        )

        monthPickerDialog.setOnDateSetListener { _, yearSelected, monthOfYear, _ ->
            val selectedMonth = String.format(
                Locale.getDefault(), "%02d/%04d",
                monthOfYear + 1, yearSelected
            )
            editTextDate.setText(selectedMonth)

        }

        try {
            val datePicker = monthPickerDialog.datePicker
            val datePickerFields = datePicker.javaClass.declaredFields

            for (datePickerField in datePickerFields) {
                if ("mDaySpinner" == datePickerField.name || "mDayPicker" == datePickerField.name) {
                    datePickerField.isAccessible = true
                    val dayPicker = datePickerField.get(datePicker) as ViewGroup
                    dayPicker.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        monthPickerDialog.show()
    }
    fun login(){
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.setTitle("Por favor")
        progressDialog.setMessage("Espere")
        progressDialog.setCancelable(false)
        if (FirebaseAuth.getInstance().currentUser == null) {
            progressDialog.show()
            FirebaseAuth.getInstance().signInAnonymously()
                .addOnSuccessListener {
                    progressDialog.cancel()
                }
                .addOnFailureListener {
                    progressDialog.cancel()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun getData(fecha: String, pieChart: PieChart,tipo: Int){
        if (FirebaseAuth.getInstance().currentUser == null) login()
        if(fecha!="")obtenerDocumentosGastosIngresos(pieChart,fecha,tipo)
    }

    fun obtenerDocumentosGastosIngresos(pieChart: PieChart, fecha: String,tipo: Int) {
        cantGastos=0.0
        cantGastosFin=0.0
        cantIngresos=0.0
        cantIngresosFin=0.0
        val gastosList = ArrayList<DocumentSnapshot>()
        val ingresosList = ArrayList<DocumentSnapshot>()


        if (FirebaseAuth.getInstance().currentUser == null) login()
        val gastosTask: Task<QuerySnapshot> = FirebaseFirestore.getInstance()
            .collection("Gastos")
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()

        val ingresosTask: Task<QuerySnapshot> = FirebaseFirestore.getInstance()
            .collection("Ingresos")
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()

        val combinedTask: Task<List<QuerySnapshot>> = Tasks.whenAllSuccess(gastosTask, ingresosTask)

        combinedTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val resultList = task.result

                val gastosSnapshot = resultList[0] as QuerySnapshot
                gastosList.addAll(gastosSnapshot.documents)

                val ingresosSnapshot = resultList[1] as QuerySnapshot
                ingresosList.addAll(ingresosSnapshot.documents)

                ingresosList.forEach {
                    val opt: Opcion? = it.toObject(Opcion::class.java)
                    if (opt != null && comprobarFechaEnMes(fecha,opt.fecha)) {
                        if(tipo==0){
                            cantIngresos+=opt.cantidad
                        }else if(tipo==1){
                            cantIngresosFin+=opt.cantidad
                        }
                    }
                }

                gastosList.forEach {
                    val opt: Opcion? = it.toObject(Opcion::class.java)
                    if (opt != null&& comprobarFechaEnMes(fecha,opt.fecha)) {
                        if(tipo==0){
                            cantGastos+=opt.cantidad
                        }else if(tipo==1){
                            cantGastosFin+=opt.cantidad
                        }
                    }
                }
            }
            gestionarGraphs(pieChart,tipo)
        }



    }
    fun comprobarFechaEnMes(mes: String, fecha: String): Boolean {
        Log.d(TAG, mes)
        Log.d(TAG, fecha)


        val formatter1 = DateTimeFormatter.ofPattern("MM/yyyy")
        val formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val monthYear1 = YearMonth.parse(mes, formatter1)
        val localDate2 = LocalDate.parse(fecha, formatter2)

        val primerDiaDelMes = monthYear1.atDay(1)
        val ultimoDiaDelMes = monthYear1.atEndOfMonth()

        return !localDate2.isBefore(primerDiaDelMes) && !localDate2.isAfter(ultimoDiaDelMes)
    }


    fun gestionarGraphs(pieChart: PieChart, tipo: Int){
        val entries = java.util.ArrayList<PieEntry>()
        if(tipo==0){
            entries.add(PieEntry(cantGastos.toFloat()))
            entries.add(PieEntry(cantIngresos.toFloat()))
        }else{
            entries.add(PieEntry(cantGastosFin.toFloat()))
            entries.add(PieEntry(cantIngresosFin.toFloat()))
        }


        pieChart.setUsePercentValues(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)
        pieChart.setHoleRadius(80f)
        pieChart.setTransparentCircleRadius(61f)
        pieChart.setDrawCenterText(true)
        pieChart.setRotationAngle(0f)
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.getLegend().setEnabled(false)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.getDescription().setEnabled(false)
        val dataSet = PieDataSet(entries, "")

        val colors = java.util.ArrayList<Int>()
        colors.add(Color.RED)
        colors.add(Color.BLUE)
        dataSet.colors = colors
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        data.setDrawValues(false)

        pieChart.setData(data)
        pieChart.highlightValues(null)
        if(tipo==0){
            pieChart.setCenterText((cantIngresos-cantGastos).toString())
        }else{
            pieChart.setCenterText((cantIngresosFin-cantGastosFin).toString())
        }

        if(tipo==0&& (cantIngresos==0.0 && cantGastos==0.0)) pieChart.centerText="NO HAY VALORES AÚN"
        if(tipo==1&& (cantIngresosFin==0.0 && cantGastosFin==0.0)) pieChart.centerText="NO HAY VALORES AÚN"
        pieChart.setCenterTextSize(16f)
        pieChart.setTouchEnabled(false)
        pieChart.invalidate()

        seguir=true

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Graphs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Graphs().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}