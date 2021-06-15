package com.example.sangallae.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sangallae.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

//    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.nav_default_enter_anim)
//            .setExitAnim(R.anim.nav_default_exit_anim)
//            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
        navView.setupWithNavController(navController)

        setSupportActionBar(findViewById(R.id.home_toolbar))
        supportActionBar?.elevation = 0f
    }

    var time3: Long = 0
    override fun onBackPressed() {
        val fragmentManager: FragmentManager = supportFragmentManager

        val time1 = System.currentTimeMillis()
        val time2 = time1 - time3
        if (time2 in 0..2000) {
            finish()

        }
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate()
        }
        else {
            super.onBackPressed()
            time3 = time1
            Toast.makeText(applicationContext, R.string.main_back, Toast.LENGTH_SHORT).show()
        }
    }


    fun showUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Log.d("뒤로가기 테스트", "메인에서 supportActionBar: " + supportActionBar.toString())
    }

    fun hideUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }
}