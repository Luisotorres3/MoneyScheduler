package com.pdm.moneyscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_MoneyScheduler)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cambiarFragment(Home.newInstance("",""))

        inicializar()
        funcionesBotones()


    }

    override fun onStop(){
        super.onStop()
    }

    override fun onPause(){
        super.onPause()
    }

    fun inicializar(){
        bottomNav= findViewById<BottomNavigationView>(R.id.bottom_navigation)
    }

    fun funcionesBotones(){
        bottomNav.setOnNavigationItemSelectedListener {menuItem->
            val fragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
            when (menuItem.itemId) {
                R.id.nav_home-> {
                    if(!(fragment is Home)){
                        cambiarFragment(Home.newInstance("",""))
                    }
                    true
                }
                R.id.nav_profile-> {
                    if(!(fragment is Profile)){
                        cambiarFragment(Profile.newInstance("",""))
                    }
                    true
                }
                R.id.nav_goals-> {
                    if(!(fragment is Goals)){
                        cambiarFragment(Goals.newInstance("",""))
                    }
                    true
                }
                R.id.nav_contact-> {
                    if(!(fragment is Contact)){
                        cambiarFragment(Contact.newInstance("",""))
                    }
                    true
                }
                else -> false
            }
        }



    }

    fun cambiarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame,fragment).commit()
    }
}