package com.pdm.moneyscheduler

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.moneyscheduler.Adapter.Goal
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
 * Use the [AddGoal.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddGoal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var root: View

    private lateinit var meta: EditText
    private lateinit var precio: EditText
    private lateinit var fechaInicio: EditText
    private lateinit var fechaFin: EditText

    private lateinit var buttonAdd: FloatingActionButton

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
        root= inflater.inflate(R.layout.fragment_add_goal, container, false)

        inicializar()
        buttonAdd.setOnClickListener {
            crearGoal()
            cambiarFragment(Goals.newInstance("",""))

        }
        fechaInicio.setOnClickListener{
            showDatePickerDialog(fechaInicio)
        }
        fechaFin.setOnClickListener{
            showDatePickerDialog(fechaFin)
        }



        return root
    }

    fun inicializar() {
        meta=root.findViewById(R.id.editTextMeta)
        precio=root.findViewById(R.id.editTextPrecio)
        fechaInicio=root.findViewById(R.id.editTextFechaInicio)
        fechaFin=root.findViewById(R.id.editTextFechaFin)

        buttonAdd=root.findViewById(R.id.buttonAddGoal)
    }

    fun cambiarFragment(fragment: Fragment) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment).addToBackStack(null).commit()

    }

    private fun crearGoal(){
        val goalId: String
        goalId= UUID.randomUUID().toString()
        val metaString: String= meta.text.toString()
        val precioDouble: Double = precio.text.toString().toDouble()
        val precioAcumulado: Double= 0.0
        val fechaInicioDate: String = fechaInicio.text.toString()
        val fechaFinDate: String = fechaFin.text.toString()
        val porcentajeDouble: Double= 0.0



        val goal = Goal(
            FirebaseAuth.getInstance().uid,
            goalId,
            metaString,
            precioDouble,
            precioAcumulado,
            fechaInicioDate,
            fechaFinDate,
            porcentajeDouble
        )

        FirebaseFirestore
            .getInstance()
            .collection("Goals")
            .document(goalId)
            .set(goal)

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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddGoal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddGoal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}