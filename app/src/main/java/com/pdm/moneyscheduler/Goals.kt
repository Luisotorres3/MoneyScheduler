package com.pdm.moneyscheduler

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.moneyscheduler.Adapter.Goal
import com.pdm.moneyscheduler.Adapter.GoalAdapter
import com.pdm.moneyscheduler.Adapter.Opcion
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Goals.newInstance] factory method to
 * create an instance of this fragment.
 */
class Goals : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var root: View

    private lateinit var adapter: GoalAdapter
    private lateinit var recycler: RecyclerView

    private var listaGoals: ArrayList<Goal> = ArrayList()

    private lateinit var progressBar : ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var relView: RelativeLayout

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
        root= inflater.inflate(R.layout.fragment_goals, container, false)

        inicializar()
        val goals: FloatingActionButton = root.findViewById(R.id.buttonGoals)
        goals.setOnClickListener {
            cambiarFragment(AddGoal.newInstance("",""))
        }
        getData()

        return root
    }
    fun cambiarFragment(fragment: Fragment) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrame, fragment).addToBackStack(null).commit()

    }

    fun inicializar() {
        loadingText=root.findViewById(R.id.loadingText)
        progressBar=root.findViewById(R.id.progressBar)
        relView=root.findViewById(R.id.idRelativeCargando)

        recycler = root.findViewById(R.id.recyclerGoals)
        login()
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

        /*
        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
        relView.visibility= View.VISIBLE
        */



        adapter = GoalAdapter(root.context)
        recycler.adapter = adapter
        //recycler.layoutManager = (LinearLayoutManager(root.context))
        recycler.layoutManager = (GridLayoutManager(root.context,2))
        if(FirebaseAuth.getInstance().currentUser==null) login()
        FirebaseFirestore
            .getInstance()
            .collection("Goals")
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                adapter.clear()
                val dataList: List<DocumentSnapshot> = it.documents
                dataList.forEach {
                    val goal: Goal? = it.toObject(Goal::class.java)
                    adapter.add(goal)
                }
                progressBar.visibility = View.GONE
                loadingText.visibility = View.GONE
                relView.visibility= View.GONE

                recycler.visibility= View.VISIBLE

            }


    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Goals.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Goals().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}