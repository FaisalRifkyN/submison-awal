package com.dicoding.appstory.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.appstory.R
import com.dicoding.appstory.databinding.ActivityRegisterBinding
import com.dicoding.appstory.view.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.dicoding.appstory.data.Result
import com.dicoding.appstory.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpAction()
        playAnimation()
    }

    private fun setUpAction() {
        binding?.buttonRegister?.setOnClickListener {
            inputData()
        }
        binding?.textViewLogin?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun inputData() {
        val name = binding?.edtTextUsername?.text.toString().trim()
        val email = binding?.editTextEmail?.text.toString().trim()
        val password = binding?.edtTextPassword?.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showErrorDialog()
        } else {
            viewModel.userRegister(name, email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        val successData = result.data
                        Toast.makeText(this, successData.message, Toast.LENGTH_SHORT).show()
                        showSuccessDialog(email)
                        showLoading(false)
                    }

                    is Result.Error -> {
                        val errorMessage = result.error ?: getString(R.string.unknown_error)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        showErrorDataIsAlreadyTaken(email)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showErrorDataIsAlreadyTaken(email: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.failed_register))
            .setMessage(resources.getString(R.string.email_is_already_taken, email))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            }
            .create()
            .show()
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.failed_register))
            .setMessage(resources.getString(R.string.dialog_failed_register))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            }
            .create()
            .show()
    }

    private fun showSuccessDialog(email: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.success_register))
            .setMessage(resources.getString(R.string.dialog_success_register, email))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                navigateToLogin()
            }
            .create()
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        val title =
            ObjectAnimator.ofFloat(binding?.txtViewSubTitleSignUp, View.ALPHA, 1f).setDuration(300)
        val textViewName =
            ObjectAnimator.ofFloat(binding?.txtViewUsernameLabel, View.ALPHA, 1f).setDuration(300)
        val nameInputText =
            ObjectAnimator.ofFloat(binding?.edtTextUsername, View.ALPHA, 1f).setDuration(300)
        val textViewEmail =
            ObjectAnimator.ofFloat(binding?.txtViewEmailLabel, View.ALPHA, 1f).setDuration(300)
        val emailInputText =
            ObjectAnimator.ofFloat(binding?.editTextEmail, View.ALPHA, 1f).setDuration(300)
        val textViewPassword =
            ObjectAnimator.ofFloat(binding?.txtViewPasswordLabel, View.ALPHA, 1f).setDuration(300)
        val passwordInputText =
            ObjectAnimator.ofFloat(binding?.edtTextPassword, View.ALPHA, 1f).setDuration(300)
        val btnRegister =
            ObjectAnimator.ofFloat(binding?.buttonRegister, View.ALPHA, 1f).setDuration(300)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            title,
            textViewName,
            nameInputText,
            textViewEmail,
            emailInputText,
            textViewPassword,
            passwordInputText,
            btnRegister
        )
        animatorSet.start()
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
