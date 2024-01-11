package com.umitytsr.peti.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.umitytsr.peti.R
import com.umitytsr.peti.databinding.ActivityHomeBinding
import com.umitytsr.peti.util.LanguageManager
import com.umitytsr.peti.util.setAppLocale
import com.umitytsr.peti.view.authentication.mainActivity.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

        val bottomNavFragments = setOf(R.id.homeFragment, R.id.chatFragment, R.id.addFragment, R.id.profileFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in bottomNavFragments) {
                binding.bottomNavView.visibility = View.VISIBLE
            } else {
                binding.bottomNavView.visibility = View.GONE
            }
        }
        getAppLanguage()
    }

    private fun getAppLanguage() {
        lifecycleScope.launch {
            viewModel.isLanguageString.collectLatest {
                LanguageManager.setSelectedLanguage(this@HomeActivity, it)
                setAppLocale(this@HomeActivity, it)
            }
        }
    }
}
