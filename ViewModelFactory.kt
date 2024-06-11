package com.dicoding.appstory.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.appstory.data.StoryRepository
import com.dicoding.appstory.data.injection.Injection
import com.dicoding.appstory.view.addstory.AddStoryViewModel
import com.dicoding.appstory.view.home.MainViewModel
import com.dicoding.appstory.view.login.LoginviewModel
import com.dicoding.appstory.view.register.RegisterViewModel

class ViewModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginviewModel::class.java) -> {
                LoginviewModel(repository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            synchronized(this) {
                instance = Injection.provideRepository(context)?.let { ViewModelFactory(it) }
            }
            return instance as ViewModelFactory
        }
    }
}