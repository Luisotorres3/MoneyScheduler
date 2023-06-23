package com.pdm.moneyscheduler

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.ToggleButton
import androidx.fragment.app.FragmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.moneyscheduler.Adapter.Opcion
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddOption.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddOption : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: Opcion? = null

    private lateinit var spinner: Spinner
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var listCategoria: ArrayList<Categoria>
    private lateinit var listBotones: ArrayList<ToggleButton>
    private lateinit var selected: String
    private lateinit var cantidad: EditText
    private lateinit var radioBtn: RadioGroup
    private lateinit var descripcion: EditText
    private lateinit var fecha: EditText

    private lateinit var btn1: ToggleButton
    private lateinit var btn2: ToggleButton
    private lateinit var btn3: ToggleButton
    private lateinit var btn4: ToggleButton

    private var opcionBorrada: Opcion? = null

    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            selected= param1.toString()
            param2 = it.getSerializable(ARG_PARAM2) as Opcion?
            if(param2!=null)opcionBorrada= param2 as Opcion
            else opcionBorrada=null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root= inflater.inflate(R.layout.fragment_add_option, container, false)

        inicializar(root)

        funcionesBotones(root)

        editarOpcion()

        return root
    }

    private fun inicializar(root: View){
        descripcion= root.findViewById(R.id.descripcion)
        radioBtn= root.findViewById(R.id.radioBtn)
        cantidad= root.findViewById(R.id.cantidad)
        spinner=root.findViewById(R.id.spinnerCurrency)
        btnAdd= root.findViewById(R.id.button)
        fecha= root.findViewById(R.id.date)
        fecha.setOnClickListener {
            showDatePickerDialog(fecha)
        }

        //Crear Spinner
        val opciones = arrayOf("€", "$", "ZLT")
        val adapter = ArrayAdapter(root.context, android.R.layout.simple_spinner_dropdown_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        //Crear Categorias
        listCategoria= ArrayList()
        if(selected=="Ingresos"){
            listCategoria.add(Categoria("Trabajo",resources.getColor(R.color.entertainment_color),resources.getDrawable(R.mipmap.work_icon_foreground),R.mipmap.work_icon_foreground.toString()))
            listCategoria.add(Categoria("Regalos",resources.getColor(R.color.food_color),resources.getDrawable(R.mipmap.gift_icon_foreground),R.mipmap.gift_icon_foreground.toString()))
            listCategoria.add(Categoria("Ahorros",resources.getColor(R.color.transport_color),resources.getDrawable(R.mipmap.savings_icon_foreground),R.mipmap.savings_icon_foreground.toString()))
            listCategoria.add(Categoria("Otros",resources.getColor(R.color.others_color),resources.getDrawable(R.mipmap.others_icon_foreground),R.mipmap.others_icon_foreground.toString()))
        }else if(selected=="Gastos"){
            listCategoria.add(Categoria("Ocio",resources.getColor(R.color.entertainment_color),resources.getDrawable(R.mipmap.popcorn_icon_foreground),R.mipmap.popcorn_icon_foreground.toString()))
            listCategoria.add(Categoria("Alimentacion",resources.getColor(R.color.food_color),resources.getDrawable(R.mipmap.groceries_icon_foreground),R.mipmap.groceries_icon_foreground.toString()))
            listCategoria.add(Categoria("Transporte",resources.getColor(R.color.transport_color),resources.getDrawable(R.mipmap.transport_icon_foreground),R.mipmap.transport_icon_foreground.toString()))
            listCategoria.add(Categoria("Otros",resources.getColor(R.color.others_color),resources.getDrawable(R.mipmap.others_icon_foreground),R.mipmap.others_icon_foreground.toString()))
        }

        //Añadir textWatcher para ver si hay cambio en los componentes
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEditText1Filled = cantidad.text.toString().isNotEmpty()
                val res: ToggleButton?= getButtonChecked()

                btnAdd.isEnabled = isEditText1Filled && res!=null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        //Crear la lista de los botones y añadirles el textchanged
        btn1= root.findViewById(R.id.btn1)
        btn2= root.findViewById(R.id.btn2)
        btn3= root.findViewById(R.id.btn3)
        btn4= root.findViewById(R.id.btn4)

        listBotones= ArrayList()
        listBotones.add(btn1)
        listBotones.add(btn2)
        listBotones.add(btn3)
        listBotones.add(btn4)
        cantidad.addTextChangedListener(textWatcher)
        listBotones.forEach {
            it.addTextChangedListener(textWatcher)
        }

        forzarDosDecimales(cantidad)
    }

    fun forzarDosDecimales(editText: EditText) {
        val decimalFilter = object : InputFilter {
            private val decimalPattern = Regex("^\\d+\\.?\\d{0,2}$")

            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val newInput = dest.toString().substring(0, dstart) +
                        source?.subSequence(start, end).toString() +
                        dest.toString().substring(dend)

                return if (newInput.isNotEmpty() && !newInput.matches(decimalPattern)) {
                    ""
                } else {
                    null
                }
            }
        }

        editText.filters = arrayOf(decimalFilter)
    }

    private fun funcionesBotones(root: View){

        //Crear categorias y configurar botones

        if(listCategoria.isNotEmpty()){
            configurarButton(btn1, listCategoria[0])
            configurarButton(btn2, listCategoria[1])
            configurarButton(btn3, listCategoria[2])
            configurarButton(btn4, listCategoria[3])
            btn1.setOnClickListener {
                configurarButton(btn1, listCategoria[0])
            }
            btn2.setOnClickListener {
                configurarButton(btn2, listCategoria[1])
            }
            btn3.setOnClickListener {
                configurarButton(btn3, listCategoria[2])
            }
            btn4.setOnClickListener {
                configurarButton(btn4, listCategoria[3])
            }
        }

        //Añadir click listener del boton
        btnAdd.setOnClickListener {
            crearOpcion()
            cambiarFragment(Home.newInstance(selected,""))
        }
    }
    //Funcion para cambiar de fragment el frame
    private fun cambiarFragment(fragment: Fragment){
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment).addToBackStack(null).commit()

    }

    fun showDatePickerDialog(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(root.context,
            DatePickerDialog.OnDateSetListener { _, yearSelected, monthOfYear, dayOfMonth ->
                val selectedDate = String.format(
                    Locale.getDefault(), "%02d/%02d/%04d",
                    dayOfMonth, monthOfYear + 1, yearSelected)
                editTextDate.setText(selectedDate)
            }, year, month, dayOfMonth)

        datePickerDialog.show()
    }
    private fun configurarButton(a: ToggleButton, categ: Categoria){
        a.textOn=(categ.tipo)
        a.textOff=(categ.tipo)
        val originalDrawable: Drawable? = categ.icon

        val desiredWidth = 200
        val desiredHeight = 200

        val bitmap = Bitmap.createBitmap(
            desiredWidth,
            desiredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        originalDrawable?.setBounds(0, 0, canvas.width, canvas.height)

        originalDrawable?.draw(canvas)

        val resizedDrawable = BitmapDrawable(resources, bitmap)

        a.setCompoundDrawablesWithIntrinsicBounds(null,resizedDrawable , null, null)

        a.backgroundTintList= ColorStateList.valueOf(categ.color)
        colorBackBoton(a,categ)
    }

    private fun colorBackBoton(a: ToggleButton, categ: Categoria){
        listBotones.forEach {
            if(it!=a){
                it.isChecked=false
            }
            if(it.isChecked){
                it.backgroundTintList= ColorStateList.valueOf(categ.color)
            }else{
                it.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.backgroundTint))
            }
        }
    }

    fun getButtonChecked(): ToggleButton? {
        listBotones.forEach {
            if(it.isChecked) return it
        }

        return null
    }

    private fun crearOpcion(){
        val opcionId: String

        if(opcionBorrada==null) opcionId= UUID.randomUUID().toString()
        else opcionId= opcionBorrada!!.id

        val cant: String= cantidad.text.toString()
        val descripcion: String = descripcion.text.toString()
        val currency: String = spinner.selectedItem.toString()

        var fecha: String =fecha.text.toString()
        if(fecha==""){
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaActual = dateFormat.format(calendar.time)
            fecha=fechaActual
        }



        val opcion = Opcion(
            FirebaseAuth.getInstance().uid,
            opcionId,
            selected,
            getCategoriaBoton()?.tipo.toString(),
            cant.toDouble(),
            currency,
            descripcion,
            fecha
        )

        FirebaseFirestore
            .getInstance()
            .collection(selected)
            .document(opcionId)
            .set(opcion)

    }

    private fun getCategoriaBoton(): Categoria? {
        val btn: ToggleButton? = getButtonChecked()
        var cat = Categoria()
        if (btn != null) {
            when (btn.id) {
                R.id.btn1 -> {
                    cat = listCategoria[0]
                }
                R.id.btn2 -> {
                    cat = listCategoria[1]
                }
                R.id.btn3 -> {
                    cat = listCategoria[2]
                }
                R.id.btn4 -> {
                    cat = listCategoria[3]
                }
            }
            return cat
        }
        return null
    }
    private fun editarOpcion(){
        if(opcionBorrada!=null){
            cantidad.setText(opcionBorrada!!.cantidad.toString())
            descripcion.setText(opcionBorrada!!.descripcion)
            val date: EditText = root.findViewById(R.id.date)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date2 = Date(opcionBorrada!!.fecha)
            val formateada = dateFormat.format(date2)
            date.setText(formateada)

            var res=false
            listCategoria.forEach {
                if(opcionBorrada!!.tipo==it.tipo)res=true
            }
            if(res){
                val buttonSelected: ToggleButton = this.getToggleButtonSelected(opcionBorrada!!.categoria)!!
                if (buttonSelected!=null)buttonSelected.performClick()
            }else{
                invalidarBotones()
            }

        }
    }
    private fun invalidarBotones(){
        listBotones.forEach {
            it.isEnabled=!it.isEnabled
        }

    }

    private fun getToggleButtonSelected(categoria: String?): ToggleButton? {
        listBotones.forEach {
            if(it.textOn==categoria)
                return it
        }
        return null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddOption.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: Opcion?) =
            AddOption().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                }
            }
    }
}