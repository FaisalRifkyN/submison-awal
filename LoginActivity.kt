package com.dicoding.appstory.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.appstory.R
import com.dicoding.appstory.data.prefence.UserModel
import com.dicoding.appstory.databinding.ActivityLoginBinding
import com.dicoding.appstory.view.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.dicoding.appstory.data.Result
import com.dicoding.appstory.data.response.StoryLoginResponse
import com.dicoding.appstory.view.home.MainActivity
import com.dicoding.appstory.view.register.RegisterActivity


class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginviewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var binding:  ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupUserActions()
        runAnimation()
    }

    private fun setupUserActions(){

        binding?.buttonLogin?.setOnClickListener {
            collectInputData()
        }

        binding?.textViewLogin?.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun collectInputData(){
        val email = binding?.editTextEmail?.text.toString()
        val password = binding?.edtTextPassword?.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            displayErrorDialog()
        }else{
            viewModel.userLogin(email, password).observe(this){result ->
                if (result != null){
                    when(result){
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            handleSuccessfulLogin(email, result)
                        }
                        is Result.Error -> {
                            handleLoginError(result)
                        }
                    }
                }
            }
        }
    }

    private fun handleSuccessfulLogin(email: String, result: Result.Success<StoryLoginResponse>){
        viewModel.saveSession(
            UserModel(
                name = result.data.loginResult.name,
                token = result.data.loginResult.token,
                email = email,
                isLogin = true)
        )
        showLoading(false)
        Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }

    private fun handleLoginError(result: Result.Error){
        showLoading(false)
        displayErrorInvalidPassword()
        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
    }


    private fun displayErrorInvalidPassword(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.failed_login))
            .setMessage(resources.getString(R.string.invalid_password))
            .setPositiveButton(resources.getString(R.string.ok)){_, _ ->
            }
            .create()
            .show()
    }

    private fun displayErrorDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.failed_login))
            .setMessage(resources.getString(R.string.dialog_failed_login))
            .setPositiveButton(resources.getString(R.string.ok)){_, _ ->
            }
            .create()
            .show()
    }

    private fun navigateToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun runAnimation() {
        val title = ObjectAnimator.ofFloat(binding?.txtViewSubTitleSignUp, View.ALPHA, 1f).setDuration(300)
        val textViewEmail = ObjectAnimator.ofFloat(binding?.txtViewEmailLabel, View.ALPHA, 1f).setDuration(300)
        val emailInputText = ObjectAnimator.ofFloat(binding?.editTextEmail, View.ALPHA, 1f).setDuration(300)
        val textViewPassword = ObjectAnimator.ofFloat(binding?.txtViewPasswordLabel, View.ALPHA, 1f).setDuration(300)
        val passwordInputText = ObjectAnimator.ofFloat(binding?.txtViewPasswordLabel, View.ALPHA, 1f).setDuration(300)
        val donthaveacount = ObjectAnimator.ofFloat(binding?.textViewDonTHaveAnAccount, View.ALPHA, 1f).setDuration(300)
        val sign = ObjectAnimator.ofFloat(binding?.textViewLogin, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding?.buttonLogin, View.ALPHA, 1f).setDuration(300)


        AnimatorSet().apply {
            playTogether(title, textViewEmail, emailInputText, textViewPassword, passwordInputText, btnLogin, donthaveacount, sign)
            start()
        }
    }


    private fun showLoading(state: Boolean) { binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
