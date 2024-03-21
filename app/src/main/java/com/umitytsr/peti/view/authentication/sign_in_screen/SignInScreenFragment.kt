package com.umitytsr.peti.view.authentication.sign_in_screen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.umitytsr.peti.databinding.FragmentSignInScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInScreenFragment : Fragment() {
    private lateinit var binding: FragmentSignInScreenBinding
    private val viewModel: SignInViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        with(binding) {
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        val result = it.toString().replace(" ", "")
                        if (result != it.toString()) {
                            (it as Editable).replace(0, it.length, result)
                        }
                    }
                }
            }

            eMailTextField.editText?.addTextChangedListener(textWatcher)
            passwordTextField.editText?.addTextChangedListener(textWatcher)

            signUpTextButton.setOnClickListener {
                findNavController().navigate(
                    SignInScreenFragmentDirections.actionSignInScreenFragmentToSignUpScreenFragment()
                )
            }
            continueButton.setOnClickListener {
                val email = eMailTextField.editText?.text.toString()
                val password = passwordTextField.editText?.text.toString()
                viewModel.signIn(email, password)
            }
        }
    }
}