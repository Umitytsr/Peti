package com.umitytsr.peti.view.authentication.loading_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.umitytsr.peti.databinding.FragmentLoadingScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingScreenFragment(): Fragment() {
    private lateinit var binding: FragmentLoadingScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            signInButton.setOnClickListener {
                findNavController().navigate(
                    LoadingScreenFragmentDirections.actionLoadingScreenFragmentToSignInScreenFragment()
                )
            }
            signUpButton.setOnClickListener {
                findNavController().navigate(
                    LoadingScreenFragmentDirections.actionLoadingScreenFragmentToSignUpScreenFragment()
                )
            }
        }
    }
}