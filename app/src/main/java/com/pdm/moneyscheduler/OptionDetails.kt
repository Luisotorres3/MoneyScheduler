package com.pdm.moneyscheduler

import android.app.ProgressDialog
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.moneyscheduler.Adapter.Opcion
import com.pdm.moneyscheduler.Adapter.OptionDetailsAdapter
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OptionDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class OptionDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var root: View
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: OptionDetailsAdapter

    private lateinit var opcion: Opcion
    private var listaOpciones: ArrayList<Opcion> = ArrayList()


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
        root = inflater.inflate(R.layout.fragment_option_details, container, false)

        opcion = arguments?.getSerializable(ARG_OPCION) as Opcion

        inicializar()
        funcionesBotones()
        getData()



        return root
    }

    private fun inicializar() {


    }
    private fun funcionesBotones() {
        recycler=root.findViewById(R.id.recyclerOptionDetails)
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

    private fun getData(){
        adapter = OptionDetailsAdapter(root.context, resources)
        recycler.adapter = adapter
        recycler.layoutManager = (LinearLayoutManager(root.context))


        if(FirebaseAuth.getInstance().currentUser==null) login()
        FirebaseFirestore
            .getInstance()
            .collection(opcion.tipo)
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                adapter.clear()
                val dataList: List<DocumentSnapshot> = it.documents
                dataList.forEach {
                    val opt: Opcion? = it.toObject(Opcion::class.java)
                    if(opt?.categoria==opcion.categoria){
                        adapter.add(opt)
                        if (opt != null) {
                            listaOpciones.add(opt)
                        }
                    }

                }
            }

        swipeFunction()
    }


    private fun swipeFunction(){
        var opcionBorrada: Opcion

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // Implementa la lógica para el movimiento de elementos si es necesario
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition
                opcionBorrada= listaOpciones[position]
                when (direction){
                    ItemTouchHelper.RIGHT->{
                        cambiarFragment(AddOption.newInstance(opcionBorrada.tipo,opcionBorrada))
                    }
                    ItemTouchHelper.LEFT->{
                        eliminarOpcion(opcionBorrada,position)

                        Snackbar.make(recycler, "Opcion eliminada...", Snackbar.LENGTH_LONG)
                            .setAction("Deshacer") {
                                añadirOpcion(position,opcionBorrada)
                            }
                            .show()
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    .addSwipeLeftBackgroundColor(

                        ContextCompat.getColor(

                            root.context,

                            android.R.color.holo_red_light

                        )

                    )
                    .addSwipeRightBackgroundColor(

                        ContextCompat.getColor(

                            root.context,

                            android.R.color.holo_blue_dark

                        )

                    )


                    .addSwipeLeftActionIcon(R.drawable.delete_icon)  // add any icon of your choice
                    .addSwipeRightActionIcon(R.drawable.edit_icon)

                    .setSwipeRightLabelColor(R.color.white) // behind color on  swiping

                    .setSwipeLeftLabelColor(R.color.white)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            }


        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recycler)
    }




    private fun eliminarOpcion(optBorrar: Opcion,positionAdapter: Int){

        if (FirebaseAuth.getInstance().currentUser == null)login()
        FirebaseFirestore.getInstance()
            .collection(optBorrar.tipo)
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dataList: List<DocumentSnapshot> = querySnapshot.documents
                dataList.forEach { document ->
                    val opt: Opcion? = document.toObject(Opcion::class.java)
                    // Eliminar el elemento (document) si corresponde
                    if (opt != null && opt.id == optBorrar.id) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                val position = adapter.getPosition(opt)
                                if (position != -1) {
                                    listaOpciones.removeAt(position)
                                    adapter.remove(positionAdapter) // Eliminar el elemento del adaptador
                                    adapter.notifyItemRemoved(positionAdapter)
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Manejo de errores en caso de que la eliminación falle
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores en caso de que la consulta falle
            }


    }
    private fun añadirOpcion(pos: Int, opt: Opcion){

        if (FirebaseAuth.getInstance().currentUser == null)login()
        FirebaseFirestore
            .getInstance()
            .collection(opt.tipo)
            .document(opt.id)
            .set(opt)
            .addOnSuccessListener {
                listaOpciones.add(pos,opt)
                adapter.add(opt, pos)
                adapter.notifyItemInserted(pos)

            }
    }
    private fun cambiarFragment(fragment: Fragment){
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment).addToBackStack(null).commit()

    }



    companion object {
        const val ARG_OPCION="opcion"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OptionDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(opcion: Opcion) =
            OptionDetails().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_OPCION,opcion)
                }
            }
    }
}