package com.umitytsr.peti.view.authentication.sign_up_screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.umitytsr.peti.R
import com.umitytsr.peti.data.repository.PetiRepository
import com.umitytsr.peti.view.home.HomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val petiRepository : PetiRepository
) : ViewModel() {

    fun signUp(userFullName: String ,email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            Toast.makeText(context, context.getString(R.string.the_password_does_not_match), Toast.LENGTH_SHORT).show()
            return
        } else {
            if (email.isNotEmpty() && password.isNotEmpty() && userFullName.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        petiRepository.createUser(userFullName)
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(context, context.getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }
}