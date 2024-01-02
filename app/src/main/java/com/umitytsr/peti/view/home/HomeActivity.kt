package com.umitytsr.peti.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.umitytsr.peti.R
import com.umitytsr.peti.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}