package com.umitytsr.peti.view.authentication.sign_up_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.umitytsr.peti.databinding.FragmentSignUpScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpScreenFragment : Fragment() {
    private lateinit var binding: FragmentSignUpScreenBinding
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initUI()
    }

    private fun initUI(){
        with(binding){
            binding.signInTextButton.setOnClickListener {
                findNavController().navigate(
                    SignUpScreenFragmentDirections.actionSignUpScreenFragmentToSignInScreenFragment()
                )
            }
            continueButton.setOnClickListener {
                val email = eMailTextField.editText?.text.toString()
                val password = passwordTextField.editText?.text.toString()
                val confirmPassword = confirmPasswordTextField.editText?.text.toString()
                viewModel.signUp(email, password, confirmPassword)
            }
        }
    }
}