package com.umitytsr.peti.view.authentication.sign_up_screen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        val spaceRemoverTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val result = it.toString().replace(" ", "")
                    if (result != it.toString()) {
                        (it as Editable).replace(0, it.length, result)
                    }
                }
            }
        }
        with(binding){
            eMailTextField.editText?.addTextChangedListener(spaceRemoverTextWatcher)
            passwordTextField.editText?.addTextChangedListener(spaceRemoverTextWatcher)
            confirmPasswordTextField.editText?.addTextChangedListener(spaceRemoverTextWatcher)

            binding.signInTextButton.setOnClickListener {
                findNavController().navigate(
                    SignUpScreenFragmentDirections.actionSignUpScreenFragmentToSignInScreenFragment()
                )
            }
            continueButton.setOnClickListener {
                val userFullName = fullNameTextField.editText?.text.toString()
                val email = eMailTextField.editText?.text.toString()
                val password = passwordTextField.editText?.text.toString()
                val confirmPassword = confirmPasswordTextField.editText?.text.toString()
                viewModel.signUp(userFullName,email, password, confirmPassword)
            }
        }
    }
}