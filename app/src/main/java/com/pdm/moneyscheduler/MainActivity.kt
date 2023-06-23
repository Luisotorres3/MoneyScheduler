package com.pdm.moneyscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.pdm.moneyscheduler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        var screenSplash= installSplashScreen()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        cambiarFragment(Home.newInstance("Empty",""))
        bottomNav.selectedItemId=R.id.nav_home

        drawerLayout = findViewById(R.id.drawer_layout)


    }

    fun funcionesBotones(){
        bottomNav.setOnNavigationItemSelectedListener {menuItem->
            val fragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
            when (menuItem.itemId) {
                R.id.nav_home-> {
                    if(!(fragment is Expense)){
                        cambiarFragment(Expense.newInstance("",""))
                    }
                    true
                }
                R.id.nav_goals-> {
                    if(!(fragment is Goals)){
                        cambiarFragment(Goals.newInstance("",""))
                    }
                    true
                }
                R.id.nav_income-> {
                    if(!(fragment is Income)){
                        cambiarFragment(Income.newInstance("",""))
                    }
                    true
                }R.id.nav_extras-> {
                    drawerLayout.openDrawer(GravityCompat.END)
                    true
                }
                else -> false
            }
        }

        val navigation_view = findViewById<NavigationView>(R.id.navigation_view)
        navigation_view.setNavigationItemSelectedListener  {menuItem->
            val fragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
            when (menuItem.itemId) {
                R.id.nav_contact-> {
                    if(!(fragment is Contact)){
                        cambiarFragment(Contact.newInstance("",""))
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                    true
                }
                R.id.nav_graphs-> {
                    if(!(fragment is Graphs)){
                        cambiarFragment(Graphs.newInstance("",""))
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                    true
                }
                else -> false
            }
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    fun cambiarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame,fragment).commit()

    }
}