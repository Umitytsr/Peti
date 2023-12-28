package com.umitytsr.peti.view.sign_up_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.umitytsr.peti.databinding.FragmentSignUpScreenBinding

class SignUpScreenFragment : Fragment() {
    private lateinit var binding: FragmentSignUpScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        with(binding){
            signInTextButton.setOnClickListener {
                findNavController().navigate(
                    SignUpScreenFragmentDirections.actionSignUpScreenFragmentToSignInScreenFragment()
                )
            }
        }
    }
}