package com.pdm.moneyscheduler

import android.animation.LayoutTransition
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.pdm.moneyscheduler.Adapter.Opcion
import com.pdm.moneyscheduler.Adapter.OpcionAdapter
import java.math.BigDecimal
import java.math.RoundingMode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var pieChart: PieChart
    private lateinit var barChart: HorizontalBarChart
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var textDinero: TextView
    private lateinit var textTotal: TextView

    private lateinit var selected: String

    private lateinit var adapter: OpcionAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var root: View

    private var listaOpcionesIngresos: ArrayList<Opcion> = ArrayList()
    private var listaOpcionesGastos: ArrayList<Opcion> = ArrayList()

    private lateinit var progressBar : ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var relView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            selected= param1.toString()
            if(selected==""||selected=="Empty")selected="Gastos"
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)

        login()

        inicializar(root)


        if(selected=="Ingresos"){
            selected = "Ingresos"
            getData(selected)
            obtenerDocumentosGastosIngresos()
            val lista = obtenerListaFloat(listaOpcionesIngresos)
            crearPieChart(obtenerListaValores(lista))
            pieChart.centerText = totalLista(listaOpcionesIngresos).toString()
            crearBarChart(lista)
        }else{
            selected = "Gastos"
            getData(selected)
            obtenerDocumentosGastosIngresos()
            val lista = obtenerListaFloat(listaOpcionesGastos)
            crearPieChart(obtenerListaValores(lista))
            pieChart.centerText = totalLista(listaOpcionesGastos).toString()
            crearBarChart(lista)
        }


        funcionesBotones()



        return root
    }
    fun totalLista(lista: ArrayList<Opcion>): Float{
        var sum: Float= 0.0F
        lista.forEach {
            sum+=it.cantidad.toFloat()
        }
        return sum
    }
    fun inicializar(root: View) {

        loadingText=root.findViewById(R.id.loadingText)
        progressBar=root.findViewById(R.id.progressBar)
        relView=root.findViewById(R.id.idRelativeCargando)

        textTotal = root.findViewById(R.id.textTotal)
        pieChart = root.findViewById(R.id.pieChart)
        barChart = root.findViewById(R.id.barChartGoal)
        buttonAdd = root.findViewById(R.id.button_add)
        val textTipo = root.findViewById<TextView>(R.id.textTipo)
        textTipo.setText(selected)
        val cardPie: CardView = root.findViewById(R.id.cardPieChart)
        cardPie.setOnClickListener {
            expand(root)
        }
        textDinero = root.findViewById(R.id.textViewDinero)    }

    fun funcionesBotones() {


        buttonAdd.setOnClickListener {
            cambiarFragment(AddOption.newInstance(selected.toString(), null))
        }


    }

    private fun obtenerListaFloat(lista: ArrayList<Opcion>): ArrayList<Float>{
        var listaResultado: ArrayList<Float> = ArrayList()
        var listaAux: ArrayList<Opcion> = ArrayList()
        var encontrado=false
        for(opt in lista){
            encontrado=false
            for (opcion in listaAux) {
                if (opcion.categoria == opt.getCategoria()) {
                    val nuevaCantidad: Double = (opcion.cantidad + opt.getCantidad()).toDouble()
                    opcion.cantidad = nuevaCantidad.toDouble()
                    encontrado = true
                }
            }
            if (!encontrado) {
                listaAux.add(opt)
            }
        }
        listaAux.forEach {
            listaResultado.add(it.cantidad.toFloat())
        }


        return listaResultado
    }

    private fun getData(tipo: String){

        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
        relView.visibility= View.VISIBLE

        recycler = root.findViewById(R.id.recyclerElementos)

        adapter = OpcionAdapter(root.context, resources)
        recycler.adapter = adapter
        recycler.layoutManager = (LinearLayoutManager(root.context))

        if(FirebaseAuth.getInstance().currentUser==null) login()
        FirebaseFirestore
            .getInstance()
            .collection(tipo)
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                adapter.clear()
                val dataList: List<DocumentSnapshot> = it.documents
                dataList.forEach {
                    val opt: Opcion? = it.toObject(Opcion::class.java)
                    adapter.add(opt)
                }
                progressBar.visibility = View.GONE
                loadingText.visibility = View.GONE
                relView.visibility= View.GONE

                recycler.visibility= View.VISIBLE
                textTotal.visibility=View.VISIBLE
                val card:CardView = root.findViewById(R.id.cardPieChart)
                card.visibility=View.VISIBLE

            }
    }
    fun obtenerDocumentosGastosIngresos() {
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

                // Obtener los documentos de la colección "Gastos"
                val gastosSnapshot = resultList[0] as QuerySnapshot
                gastosList.addAll(gastosSnapshot.documents)

                // Obtener los documentos de la colección "Ingresos"
                val ingresosSnapshot = resultList[1] as QuerySnapshot
                ingresosList.addAll(ingresosSnapshot.documents)

                var sumaIng = 0.0
                var sumaGast = 0.0

                ingresosList.forEach {
                    val opt: Opcion? = it.toObject(Opcion::class.java)
                    if (opt != null) {
                        addToLista(listaOpcionesIngresos,opt)
                        sumaIng += opt.cantidad
                    }
                }

                gastosList.forEach {
                    val opt: Opcion? = it.toObject(Opcion::class.java)
                    if (opt != null) {
                        addToLista(listaOpcionesGastos,opt)
                        sumaGast += opt.cantidad
                    }
                }

                if(selected=="Ingresos"){
                    textDinero.setText(sumaIng.toString())
                    pieChart.centerText = sumaIng.toString()

                }else{
                    textDinero.setText(sumaGast.toString())
                    pieChart.centerText = sumaGast.toString()

                }
                val res = BigDecimal((sumaIng-sumaGast))
                    .setScale(2, RoundingMode.DOWN)
                    .toDouble()
                calcularTotalTexto(res)
            } else {
                // Manejar el error en caso de que ocurra
            }
        }
    }

    fun addToLista(lista: ArrayList<Opcion>, opt: Opcion){
        var encontrado=false
        for (opcion in lista) {
            if (opcion.categoria == opt.categoria) {
                val nuevaCantidad = opcion.cantidad + opt.cantidad
                opcion.cantidad = nuevaCantidad
                encontrado = true
            }
        }
        if (!encontrado) {
            lista.add(opt)
        }
    }

    fun calcularTotalTexto(valor: Double){
        textTotal.setText(valor.toString())
        if(valor<0.0){
            textTotal.setTextColor(resources.getColor(R.color.red))
        }else{
            textTotal.setTextColor(resources.getColor(R.color.blue))
        }
        if(selected=="Ingresos"){
            var lista = obtenerListaFloat(listaOpcionesIngresos)
            crearPieChart(obtenerListaValores(lista))
            crearBarChart(lista)
        }else{
            var lista = obtenerListaFloat(listaOpcionesGastos)
            crearPieChart(obtenerListaValores(lista))
            crearBarChart(lista)
        }
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
    private fun getColorOpcion(opt: String): Int {
        var color: Int = -1
        when (opt) {
            "Trabajo" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.entertainment_color).toLong()
                    )
                )
            }

            "Regalos" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.food_color).toLong()
                    )
                )
            }

            "Ahorros" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.transport_color).toLong()
                    )
                )
            }

            "Otros" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.others_color).toLong()
                    )
                )
            }

            "Ocio" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.entertainment_color).toLong()
                    )
                )
            }

            "Alimentacion" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.food_color).toLong()
                    )
                )
            }

            "Transporte" -> {
                color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and resources.getColor(R.color.transport_color).toLong()
                    )
                )
            }


        }
        return color
    }


    private fun crearPieChart(listaValores: ArrayList<PieEntry>) {
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
        var listAux: ArrayList<Opcion> = ArrayList()
        if(selected=="Gastos"){
            listAux= listaOpcionesGastos
        }else{
            listAux=listaOpcionesIngresos
        }
        listAux.forEach {
            colors.add(getColorOpcion(it.categoria))
        }

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
        pieChart.setTouchEnabled(false)


        // loading chart
        pieChart.invalidate()

    }

    private fun crearBarChart(listaValores: ArrayList<Float>) {
        val l = FloatArray(listaValores.size)
        if (listaValores.isNotEmpty()) {
            listaValores.sortDescending()
            for (i in 0..listaValores.size - 1) {
                l[i] = listaValores.get(i)
            }
        }
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0.toFloat(), l))

        val dataSet = BarDataSet(entries, null)
        dataSet.setDrawValues(false) // Ocultar los valores en las barras
        dataSet.setDrawIcons(false) // Ocultar los iconos en las barras

        val colors: ArrayList<Int> = ArrayList()
        var listAux: ArrayList<Opcion> = ArrayList()
        if(selected=="Gastos"){
            listAux= listaOpcionesGastos
        }else{
            listAux=listaOpcionesIngresos
        }
        if(listAux.isNotEmpty()){
            val listaOrdenadaAscendente= listAux.sortedByDescending { opcion -> opcion.cantidad }
            listaOrdenadaAscendente.forEach {
                val color = Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFFL and getColorOpcion(it.categoria).toLong()
                    )
                )
                colors.add(color)
            }
        }

        if(colors.isNotEmpty())
            dataSet.colors=colors.toList()


        val barData = BarData(dataSet)
        barData.setValueTextSize(15f)

        barChart.data = barData


        barChart.setDrawValueAboveBar(false) // Ocultar los valores encima de las barras
        barChart.axisLeft.isEnabled = false // Ocultar el eje Y izquierdo
        barChart.axisRight.isEnabled = false // Ocultar el eje Y derecho
        barChart.xAxis.isEnabled = false // Ocultar el eje X
        barChart.description.isEnabled = false // Ocultar la descripción
        barChart.legend.isEnabled = false // Ocultar la leyenda
        barChart.setTouchEnabled(false)


        barChart.setFitBars(true)
        barChart.invalidate()


    }
    private fun obtenerColores(listAux: List<Opcion>): ArrayList<Int> {
        val colors = ArrayList<Int>()
        listAux.forEach { opcion ->
            val color = getColorOpcion(opcion.categoria)
            colors.add(color.toInt())
        }
        return colors
    }


    fun obtenerListaValores(listaValores: ArrayList<Float>): ArrayList<PieEntry> {
        val listaValoresAux: ArrayList<PieEntry> = ArrayList()
        listaValores.forEach {
            listaValoresAux.add(PieEntry(it))
        }
        return listaValoresAux
    }

    fun clearPieChart() {
        val lista: ArrayList<Float> = ArrayList()
        lista.add(0f)
        crearPieChart(obtenerListaValores(lista))
        pieChart.centerText = "No hay valores que mostrar"
    }

    fun clearBarChart() {
        val lista: ArrayList<Float> = ArrayList()
        lista.add(0f)
        crearBarChart((lista))
        barChart.setNoDataText("No hay datos que mostrar")
    }
    fun cambiarFragment(fragment: Fragment) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment).addToBackStack(null).commit()

    }

    fun expand(view: View) {
        val layout: LinearLayout = view.findViewById(R.id.linearAnimation)
        layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        val v = if (pieChart.visibility == View.GONE) View.VISIBLE else View.GONE
        val w = if (barChart.visibility == View.GONE) View.VISIBLE else View.GONE


        TransitionManager.beginDelayedTransition(layout, AutoTransition())
        pieChart.visibility = v
        barChart.visibility = w
        textDinero.visibility = w
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
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}