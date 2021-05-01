package com.example.sangallae

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

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


    }
}