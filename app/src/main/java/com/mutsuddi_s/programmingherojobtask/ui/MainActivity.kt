package com.mutsuddi_s.programmingherojobtask.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.mutsuddi_s.mvvm.viewmodel.MainViewModel
import com.mutsuddi_s.programmingherojobtask.R
import com.mutsuddi_s.programmingherojobtask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private  lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        viewModel= ViewModelProvider(this).get(MainViewModel::class.java)

       // NavigationUI.setupActionBarWithNavController(this,navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}