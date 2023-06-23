package com.pdm.moneyscheduler

import android.animation.LayoutTransition
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var pieChart: PieChart
    private lateinit var barChart: HorizontalBarChart
    private lateinit var cardIngresos: CardView
    private lateinit var cardGastos: CardView
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var textDinero: TextView

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
        var root: View

        root= inflater.inflate(R.layout.fragment_login, container, false)

        inicializar(root)
        funcionesBotones()

        clearBarChart()
        clearPieChart()



        return root
    }



    private fun crearPieChart(listaValores: ArrayList<PieEntry>){
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        listaValores.forEach {
            entries.add(it)
        }

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.orange))
        colors.add(resources.getColor(R.color.light_orange))
        colors.add(resources.getColor(R.color.dark_orange))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        pieChart.setCenterTextSize(20f)

        // loading chart
        pieChart.invalidate()

    }

    private fun crearBarChart(listaValores: ArrayList<Float>){
        val l= FloatArray(listaValores.size)
        if(listaValores.isNotEmpty()){
            listaValores.sortDescending()
            for(i in 0..listaValores.size-1){
                l[i]= listaValores.get(i)
            }
        }
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0.toFloat(), l))

        val dataSet = BarDataSet(entries, null)
        dataSet.setDrawValues(true) // Ocultar los valores en las barras
        dataSet.setDrawIcons(false) // Ocultar los iconos en las barras

        dataSet.colors = listOf(resources.getColor(R.color.orange),resources.getColor(R.color.light_orange),resources.getColor(R.color.dark_orange))

        val barData = BarData(dataSet)
        barData.setValueTextSize(15f)

        barChart.data = barData


        barChart.setDrawValueAboveBar(false) // Ocultar los valores encima de las barras
        barChart.axisLeft.isEnabled = false // Ocultar el eje Y izquierdo
        barChart.axisRight.isEnabled = false // Ocultar el eje Y derecho
        barChart.xAxis.isEnabled = false // Ocultar el eje X
        barChart.description.isEnabled = false // Ocultar la descripción
        barChart.legend.isEnabled = false // Ocultar la leyenda

        barChart.setFitBars(true)
        barChart.invalidate()


    }

    fun obtenerListaValores(listaValores: ArrayList<Float>): ArrayList<PieEntry> {
        val listaValoresAux:ArrayList<PieEntry> = ArrayList()
        listaValores.forEach {
            listaValoresAux.add(PieEntry(it))
        }
        return listaValoresAux
    }

    fun clearPieChart(){
        val lista:ArrayList<Float> = ArrayList()
        lista.add(0f)
        crearPieChart(obtenerListaValores(lista))
        pieChart.centerText="No hay valores que mostrar"
    }

    fun clearBarChart(){
        val lista:ArrayList<Float> = ArrayList()
        lista.add(0f)
        crearBarChart((lista))

        barChart.setNoDataText("No hay datos que mostrar")
    }

    fun inicializar(root: View){
        pieChart = root.findViewById(R.id.pieChart)
        barChart=root.findViewById(R.id.barChartGoal)
        cardIngresos=root.findViewById(R.id.cardIngresos)
        cardGastos=root.findViewById(R.id.cardGastos)
        buttonAdd=root.findViewById(R.id.button_add)
        val cardPie: CardView= root.findViewById(R.id.cardPieChart)
        cardPie.setOnClickListener {
            expand(root)

        }

        val textIngresos: TextView = root.findViewById(R.id.textViewIngresos)
        var spannableString = SpannableString(textIngresos.text)
        var underlineSpan = UnderlineSpan()
        var foregroundColorSpan = ForegroundColorSpan(Color.BLACK)

        spannableString.setSpan(underlineSpan, 0, spannableString.length, 0)
        spannableString.setSpan(foregroundColorSpan, 0, spannableString.length, 0)

        textIngresos.text = spannableString

        val textGastos: TextView = root.findViewById(R.id.textViewGastos)
        spannableString = SpannableString(textGastos.text)
        underlineSpan = UnderlineSpan()
        foregroundColorSpan = ForegroundColorSpan(Color.BLACK)

        spannableString.setSpan(underlineSpan, 0, spannableString.length, 0)
        spannableString.setSpan(foregroundColorSpan, 0, spannableString.length, 0)

        textGastos.text = spannableString

        textDinero=root.findViewById(R.id.textViewDinero)


    }

    fun funcionesBotones(){
        cardIngresos.setOnClickListener {
            val lista:ArrayList<Float> = ArrayList()
            lista.add(50f)
            lista.add(40f)
            lista.add(10f)
            crearPieChart(obtenerListaValores(lista))
            pieChart.centerText="Ingresos"
            crearBarChart(lista)
        }
        cardGastos.setOnClickListener {
            val lista:ArrayList<Float> = ArrayList()
            lista.add(30f)
            lista.add(30f)
            lista.add(40f)
            crearPieChart(obtenerListaValores(lista))
            pieChart.centerText="Gastos"
            crearBarChart(lista)
        }

        buttonAdd.setOnClickListener {
            cambiarFragment(AddOption.newInstance("",null))
        }




    }

    fun cambiarFragment(fragment: Fragment){
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment).addToBackStack(null).commit()

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun expand(view: View) {
        val layout: LinearLayout= view.findViewById(R.id.linearAnimation)
        layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        val v = if (pieChart.visibility == View.GONE) View.VISIBLE else View.GONE
        val w = if (barChart.visibility == View.GONE) View.VISIBLE else View.GONE


        TransitionManager.beginDelayedTransition(layout,AutoTransition())
        pieChart.visibility=v
        barChart.visibility=w
        textDinero.visibility=w
    }


}