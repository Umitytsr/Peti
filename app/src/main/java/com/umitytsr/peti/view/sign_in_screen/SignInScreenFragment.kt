package com.umitytsr.peti.view.sign_in_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.umitytsr.peti.databinding.FragmentSignInScreenBinding

class SignInScreenFragment : Fragment() {
    private lateinit var binding : FragmentSignInScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        with(binding){
            continueButton.setOnClickListener {

            }
            signUpTextButton.setOnClickListener {
                findNavController().navigate(
                    SignInScreenFragmentDirections.actionSignInScreenFragmentToSignUpScreenFragment()
                )
            }
        }
    }
}