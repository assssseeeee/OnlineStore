package com.example.onlinestore.ui.activities

import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.onlinestore.R
import com.example.onlinestore.databinding.ActivityDashBoardBinding


class DashBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashBoardActivity,
                R.drawable.app_gradient_color_background
            )
        )

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dash_board)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_products, R.id.navigation_orders
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}