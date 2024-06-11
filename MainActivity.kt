package com.dicoding.appstory.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.appstory.R
import com.dicoding.appstory.adapter.StoryAdapter
import com.dicoding.appstory.data.prefence.StoryList
import com.dicoding.appstory.databinding.ActivityMainBinding
import com.dicoding.appstory.view.ViewModelFactory
import com.dicoding.appstory.data.Result
import com.dicoding.appstory.data.response.ListStoryItem
import com.dicoding.appstory.view.addstory.AddStoryActivity
import com.dicoding.appstory.view.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var listStoryItem = ArrayList<StoryList>()
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpUserActions()
        setUpStoryList()

        binding?.topAppBar?.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setUpStoryList() {
        viewModel.getAllStory().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val newData = result.data
                        if (newData.isNotEmpty()) {
                            val add = addDataParcelable(newData)
                            listStoryItem.clear()
                            listStoryItem.addAll(add)
                            setListStory()
                        } else {
                            showListNull(true)
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this,
                            "Terjadi Kesalahan " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun addDataParcelable(list: List<ListStoryItem>): ArrayList<StoryList> {
        val NewlistStory = ArrayList<StoryList>()
        for (i in list.indices) {
            val story = StoryList(
                list[i].id.toString(),
                list[i].name.toString(),
                list[i].photoUrl.toString(),
                list[i].description.toString()
            )
            NewlistStory.add(story)
        }
        return NewlistStory
    }

    private fun setListStory() {
        val layoutManager = LinearLayoutManager(this@MainActivity)
        binding?.recycleview?.layoutManager = layoutManager
        binding?.recycleview?.setHasFixedSize(true)
        val adapter = StoryAdapter()
        binding?.recycleview?.adapter = adapter
        adapter.submitList(listStoryItem)
    }


    private fun setUpUserActions() {
        binding?.fabAdd?.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.logout))
            .setMessage(resources.getString(R.string.logout_description))
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                logoutUser()
            }
            .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
            .create()
            .show()
    }

    private fun logoutUser() {
        viewModel.logout()
        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showListNull(state: Boolean) {
        binding?.tvListKosong?.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllStory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}